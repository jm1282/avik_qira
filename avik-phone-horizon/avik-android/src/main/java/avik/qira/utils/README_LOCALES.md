# Locale-aware Qira page detection

The Motorola Qira master-capture suite is designed to run unchanged in any
locale Qira ships translations for. Page objects still declare anchors
using plain English literals (e.g. `"I agree"`, `"A few notes about
Motorola Qira"`), but at match time every anchor is expanded into the
current-locale form by looking up **Qira's own `R.string` resource IDs**.
Those IDs are the stable "unique ID" shared across every locale Qira
supports, so no hand-written translations are required for anything Qira
itself ships.

## TL;DR

Run the master capture suite in Brazilian Portuguese:

```bash
adb shell am instrument -w \
    -e class avik.qira.scripts.MotorolaQiraMasterCaptureTest \
    -e qira.locale pt-BR \
    com.motorola.avikscripts/androidx.test.runner.AndroidJUnitRunner
```

Switch to German, Arabic, Japanese, Simplified Chinese by changing the
`qira.locale` value:

| Argument            | Result                                  |
|---------------------|-----------------------------------------|
| `-e qira.locale de` | German                                  |
| `-e qira.locale ar-EG` | Arabic (Egypt)                      |
| `-e qira.locale ja` | Japanese                                |
| `-e qira.locale zh-CN` | Simplified Chinese                   |
| `-e qira.locale fr-CA` | French (Canada)                      |
| (no flag)           | Use the device's existing system locale |

The instrumentation will:

1. Flip Qira's per-app locale via
   `cmd locale set-app-locales --user current com.lenovo.qira --locales <tag>`
   (Android 13+). Non-destructive — only Qira's locale changes.
2. Send `LOCALE_CHANGED` so Qira re-reads its Resources immediately.
3. Flip the test instrumentation process to the same locale so
   `QiraStrings` resolves every anchor through the correct Qira
   resource-binding.

Pass `-e qira.applySystemLocale true` to also try to change the device
system locale (requires elevated permissions — best-effort).

### Screenshot string-link extraction flags

Qira captures now enable Avik string-link metadata with:

- `includeText=true` (default)
- `includeDescription=false` (default)

This default avoids a common Compose failure mode where a giant root
`content-desc` turns into one full-screen string-link entry and hides the
actual per-text/per-field/per-button links.

You can override at runtime:

```bash
-e qira.includeScreenshotText true|false
-e qira.includeScreenshotDescription true|false
```

For normal Qira/SLAP runs (including `en-XM`), keep the defaults.

## Pseudo-locales (`en-XM`, `en-XA`, `ar-XB`)

Android ships a small set of *pseudo-localization* locales that wrap every
string resource with Unicode bidi isolate markers (`U+2066 LRI` …
`U+2069 PDI`, plus `U+200E/F` LRM/RLM inside) so apps can be stress-tested
for bidi correctness. Motorola's `en-XM` variant additionally encodes
**SLAP string-link markers** in those wrappers — they are exactly what the
screen-capture pipeline audits to figure out which `R.string` backed each
rendered label.

The capture suite therefore treats `en-XM` as a *first-class* run mode,
not as a synonym for `en-US`:

* **Qira's locale is left untouched.** When the device is in `en-XM` and
  no `-e qira.locale` was passed, the suite logs the pseudo-locale and
  honours it. We do **not** silently flip Qira to `en-US` — doing so would
  strip the SLAP markers from the captured UI.
* **Qira's `R.string` resources are read in the device locale.**
  `QiraStrings` opens its `qiraResourcesCurrent` against `en-XM`, so the
  resolver returns the pseudo-localized form alongside the English anchor.
* **Matching is bidi-tolerant.** `BaseQiraPage`'s `BIDI_WHITESPACE_CLASS`
  regex absorbs the bidi marks between/around tokens, so a literal anchor
  like `"Chat"` still matches the on-screen `"Chat\u2066…\u2069"`. The
  small number of helpers that compare content-descriptions with
  `String.equals` (e.g. `QiraFocusZonePage.isBubbleBarVisible()`) call
  `QiraStrings.stripBidiControls()` first so plain-equality keeps working.

If you ever need to run the suite explicitly in `en-XM` (or switch from
some other locale into it) just pass it through the existing argument:

```bash
adb shell am instrument -w \
    -e class avik.qira.scripts.MotorolaQiraMasterCaptureTest \
    -e qira.locale en-XM \
    com.motorola.avikscripts/androidx.test.runner.AndroidJUnitRunner
```

## Why Qira `R.string` IDs are the right "unique ID"

Qira's UI is rendered through Jetpack Compose / WebView. UiAutomator dumps
show that view-level `resource-id` attributes are almost entirely absent —
every clickable surface is a bare `android.view.View` with only
`content-desc` and `text` populated. The only real `resource-id` anywhere
in the tree is the root `android:id/content`, which is useless for
targeting.

But Qira's APK **does** ship a full Android string-resource table. Every
locale it supports is a different column of that same table, keyed by
integer resource IDs (`R.string.*`) that never change across locales. Those
IDs are exactly the stable "unique id used even when other languages
change" that test code needs.

We bridge from page-object English anchors to those IDs at resolve time.

## Selector priority from `qira-ui-dumps`

Use the most locale-stable selector the dump exposes:

1. **System `resource-id` first.** Android permission prompts expose stable
   IDs such as `com.android.permissioncontroller:id/permission_allow_all_button`.
   Never choose permission answers by translated text when an ID exists.
2. **Dump-proven icon `content-desc` next.** Qira Compose surfaces usually do
   not expose view IDs, but repeated icon affordances do expose exact semantic
   descriptions such as `Menu`, `More options`, `Send`, `Add image`,
   `Choose Image option`, and `take photo option`. Page objects should use
   `BaseQiraPage.findByStableDescription()` / `clickByStableDescription()`
   for those anchors so they are treated as raw accessibility IDs, not as text
   to translate.
3. **Structure/geometry for localized controls.** Some controls localize both
   text and `content-desc`. The response-language picker is the key example:
   the language names, the back affordance, and the continue button all change
   by locale, but the first radio row remains English (United States) and the
   continue control remains the bottom-right clickable in every dump.
4. **Qira `R.string` expansion for text-only buttons.** When no stable ID or
   semantic description exists, keep using English anchors and let
   `QiraStrings` resolve them through Qira's string resources.

## How `QiraStrings.resolve(anchor)` works

On first launch after a Qira APK update,
`QiraStrings.enableRuntimeResourceScan()` walks the Qira resource-ID range
(`0x7f010000…0x7f2f0000`), reads the English value of every string
resource, and indexes it under a **normalised** key
(lower-cased, whitespace collapsed, trailing punctuation stripped). The
result — a `normalize(englishText) → resId` map covering the entire Qira
string table — is cached to
`/sdcard/avik/qira-strings-cache/qira-strings-v2-<versionCode>.json` and
reused on every subsequent run until Qira's versionCode changes. First
run costs ~40 s on a cold device; every run after that is near-zero cost.

When a page object calls `QiraStrings.resolve("I agree")` the helper
returns, in order:

1. **Runtime scan (primary, automatic).** Normalise `"I agree"`, look it
   up in the scan index, fetch the current-locale value of the matching
   resource ID via `qiraResourcesCurrent.getString(resId)`. On pt-BR this
   returns `"Concordo"`; on de it returns `"Ich stimme zu"`; on ar it
   returns `"أوافق"`. No translation table involved.
2. **Explicitly registered resource name.** If a page object called
   `registerQiraResource("Confirm", "qira_confirm_btn")` at startup, Qira
   Resources are asked for the current-locale value of that resource.
   Used as a deterministic override where English text alone is
   ambiguous (two Qira resources with identical English).
3. **Static catalog fallback.** `loadCatalog()` in `QiraStrings.java`
   is **intentionally empty**. The hand-written per-locale tables that
   used to live there (600+ entries across 15 languages) were removed
   because they drifted out of sync with Qira's real resource table
   every time Qira reworded a string. Framework permission dialogs
   (`Allow` / `Don't allow` / `Only this time` / `Allow only while using
   the app`) are not handled here either — `BaseQiraPage.handleSystemPermissionPrompt`
   clicks them directly by their stable `com.android.permissioncontroller:id/…`
   resource IDs, which are locale-agnostic.

Every `findBy*` helper in `BaseQiraPage` iterates over the full alias
list returned by `resolve`, so a match from any tier is valid.

## Adding a new Qira surface (zero-code)

1. Run the capture once in any locale. The exhaustive scan will index
   every string Qira ships. Nothing to write by hand.
2. If a page object fails to detect a surface, confirm the English
   anchor in the page object matches **Qira's own R.string value**
   (case/punctuation-insensitive). You can verify by pulling the UI
   dump for a run in en-US and cross-referencing the on-screen label.
3. Fix the anchor if it's stale (e.g. Qira renamed `"I acknowledge"` to
   `"Accept"`) — this is a one-line change in the page object, not a
   per-locale translation.

## Adding a non-Qira string

For the rare case that a non-Qira (framework / OEM) label needs to be
matched by text, prefer:

1. **Click by resource ID.** System permission dialogs, device policy
   dialogs, etc. all expose stable `resource-id`s. Use
   `By.res("com.android.permissioncontroller:id/permission_allow_button")`
   rather than text. See `BaseQiraPage.handleSystemPermissionPrompt` for
   the canonical pattern.
2. **Bind to a Qira resource.** If Qira itself happens to own the English
   equivalent under a different resource name, call
   `QiraStrings.registerQiraResource("My Anchor", "qira_res_name")`.
   The resolver will then fetch the current-locale value directly from
   Qira's `Resources` — again, no hand-written translation needed.

## UI-dump discovery workflow

Even without resource-id targeting, UiAutomator dumps are the best tool
for understanding an unfamiliar Qira surface:

```bash
adb shell am instrument -w \
    -e class avik.qira.scripts.MotorolaQiraMasterCaptureTest \
    -e qira.locale pt-BR \
    -e qira.dumpUi true \
    com.motorola.avikscripts/androidx.test.runner.AndroidJUnitRunner
adb pull /sdcard/avik/qira-ui-dumps ./qira-ui-dumps
```

Each screenshot has a matching sub-directory under `qira-ui-dumps/`
(e.g. `MotorolaQiraHome_Onboarding_Start/`) containing a UiAutomator XML
and a tab-separated resource-id inventory. The fourth column of the TSV
is the on-screen `text`, the third is `content-desc`.

## Files

| File | Role |
|------|------|
| `QiraConfig.java` | Parses `-e qira.locale <tag>` and `-e qira.applySystemLocale true`. |
| `QiraStrings.java` | Resolver. Exhaustive Qira `R.string` scan, normalised lookup. Static catalog is intentionally empty. |
| `BaseQiraPage.java` | Every `findBy*` helper calls `QiraStrings.expandAll(labels)` before matching. |
| `BaseQiraCaptureScript.java` | Applies `qira.locale` in `setUp()`, kicks off the exhaustive scan. |

## Troubleshooting

If a capture fails in a new locale with `Unable to detect …`:

1. Check the diagnostic line in the log
   (`[diag waitForSurface … timeout] visible text/desc: …`). It prints
   every visible label at the moment the timeout fired.
2. Pick the label you expected the anchor to match. Pull the matching
   UI dump XML to cross-check.
3. If the label is rendered by Qira, confirm that **the English anchor
   in the page object is the exact English wording Qira ships** (not a
   paraphrase). Fix the anchor if Qira renamed the label — a single
   line in the page object now covers every locale.
4. Watch for log lines of the form
   `QiraStrings: tier-1 MISS for anchor "X" in locale Y` — those are
   anchors the exhaustive scan couldn't resolve via any Qira R.string.
   Fix them by calling `registerQiraResource("X", "<qira_res_name>")`
   from the page object (prefer this over text translations).
5. If the scan itself seems stale after a Qira upgrade, delete
   `/sdcard/avik/qira-strings-cache/` on the device and rerun — the
   scanner will rebuild the cache on the next launch.
6. Confirm the locale flip actually happened by searching logs for
   `QiraLocale: set-app-locales result:` and
   `QiraStrings: reopened Qira resources for locale <tag>`. If those
   lines are missing, the `-e qira.locale …` argument didn't reach the
   instrumentation — double-check your `am instrument` command line.

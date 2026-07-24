# Qira v2 Avik Capture — Logic & Implementation

Localization screen-capture automation for **Qira** (`com.lenovo.qira`) on Horizon.
This document explains how the `qira_v2` capture pipeline works, how it decides *what*
to capture, how it stays robust across locales, and — honestly — what its coverage does
and does not guarantee. Written to answer the recurring questions: *"how confident are we
that all UIs are captured?"* and *"how do we know Qira has ~104 screens?"*

---

## TL;DR (the short answer)

- Avik captures screens from the **running app on a real device** (APK → UI Automator →
  SLAP metadata → RWS/Workbench), not from Figma and not from source.
- Coverage is a **deterministic, version-controlled contract of named screen states**
  (currently **104 canonical tags**, en‑XM as the structural reference), *driven* by
  scripts — not an exhaustive/magic crawl. The "~104" is an **engineering contract we
  enumerate and maintain**, not a discovered count of every possible screen.
- Each element is located by **stable identifiers only** (SLAP message ID → Qira
  string/Compose resource ID → Android resource ID → accessibility), never by translated
  text or fixed coordinates. Where no stable ID exists, we resolve the *live node's own*
  bounds or **fail loud with a diagnostic dump** — we never silently guess.
- A run is only "good" when it produces **exactly the 104 paired PNG+JSON tags** and passes
  the deterministic string‑link audit. Missing/foreign/duplicate data is a hard failure,
  not a silent gap.
- **Error/warning/transient states are captured only where a script explicitly navigates
  to and classifies them.** New flows need script updates. That is the real coverage
  boundary, and it is exactly where a source‑level / MCP approach (below) could help.

---

## 1. Where this fits in the pipeline

```
Qira APK on device  ──►  Avik (Android UiAutomator instrumentation)  ──►  SLAP metadata  ──►  RWS / Workbench
                          │                                              │
                          │ per screen state:                            │ <hash>.png  +  <hash>.json
                          │  • navigate & classify (stable IDs)          │  (screenshot + avikTexts[] with
                          │  • capture screenshot                        │   text, bounds, messageIds[])
                          │  • owner-scope + link message IDs            │
```

- The Workbench-facing entry points live under `avik.qira.scripts` so executions import
  into the existing **QIRA** app tile:
  - Onboarding only: `avik.qira.scripts.MotorolaQiraHome_Onboarding_Start`
  - Full suite: `avik.qira.scripts.MotorolaQiraMasterCaptureV2`
- The actual logic lives under `avik.qira_v2.*`; the `avik.qira.*` classes are thin
  delegating wrappers (`MotorolaQiraMasterCaptureV2 extends
  avik.qira_v2.scripts.MotorolaQiraMasterCapture`).

## 2. The master flow

`avik.qira_v2.scripts.MotorolaQiraMasterCapture` runs the whole app in **one
instrumentation process** as an ordered pipeline of six sub-flows:

1. Home + full onboarding (`MotorolaQiraHome_Onboarding_Start`)
2. Focus Zone (`MotorolaQiraFocusZoneCapture`)
3. Creator Zone (`MotorolaQiraCreatorZoneCaptureV2`)
4. Knowledge (`MotorolaQiraKnowledgeCaptureV2`)
5. Chat History (`MotorolaQiraChatHistoryCaptureV2`)
6. Settings (`MotorolaQiraSettingsCaptureV2`)

Between steps it force-stops Qira, re-verifies the device is unlocked, and returns to a
clean state (`resetBetweenSteps`). Each sub-flow's failure is captured and **aggregated**;
if any sub-flow fails the whole run throws with a per-step summary (`all sub-flows OK` is
logged only on full success). SLAP flags are always on:
`qira.dumpUi=true`, `qira.includeScreenshotText=true`,
`qira.includeScreenshotDescription=true` (`QiraV2InstrumentationDefaults.apply()`).

## 3. What "coverage" means here (answering *"are all UIs captured?"*)

Coverage is **defined**, not discovered. `tools/avik-string-link-audit/contract.json` (and
`SCOPE.md`) enumerate the **104 canonical screen-state tags** — e.g.
`MotorolaQiraFocusZone_Chat_Processing`, `..._Chat_Answer`, `..._Chat_Result`,
`MotorolaQiraCreatorZone_CreateImage_Styles` vs `..._StyleFantasy`,
`MotorolaQiraHome_Onboarding_PermissionsScrolled` variants, etc.

For each tag the scripts:

1. **Navigate** to that state using stable selectors (section 4).
2. **Prove the screen's own identity is visible before capturing** (deterministic
   classification — e.g. `Onboarding_Language` is only tagged when
   `language_screen_pill_text` is visible). We classify *before* the shutter, and never
   dedupe/rename/overwrite after.
3. **Capture** the screenshot + SLAP metadata.
4. **Owner-scope and link** the metadata (section 5).

This is why the number is stable and defensible: it is a curated set of states the scripts
are written to reach and distinguish, including many "sub-screens" 3–4 levels deep
(permission scroll positions, quota popups, style pickers, chat processing/answer/result,
RTL carousel variants). It deliberately **excludes** `MotorolaQiraFocusZone_Live_Camera`
(a live camera surface that cannot be captured deterministically).

**Honest limitation:** transient errors, warnings, and edge dialogs are captured **only
where a script explicitly drives and classifies them**. Avik does not brute-force every
possible state, and new/changed UI requires script updates. A screen that cannot be reached
or identified by a stable identifier is reported with evidence and the failing transition —
never hidden behind a heuristic.

## 4. Selector architecture (answering *"we mostly don't have resource/string IDs"*)

Every element is resolved by the **highest available stable identifier**, in this order:

| Priority | Source | Code |
|---|---|---|
| 1 | **SLAP message ID** (from Qira's shipped en‑XM Compose catalog) | `QiraV2SlapTextDump.findByMessageId` / `resolveCatalogMessageIdsForVisibleTexts` |
| 2 | **Qira string resource ID** — Android `R.string`, then Qira **Compose** catalog | `QiraV2SlapTextDump.findByResolvedQiraStringResource` / `...ComposeStringResource`, `QiraV2ComposeStrings` |
| 3 | **Android resource ID** (`pkg:id/…`) | `QiraV2Selectors.findByResourceId` |
| 4 | **Accessibility-backed** resolved identifier (text/contentDescription of the resolved node) | `QiraV2SlapTextDump.dumpVisibleAccessibility*` |

Key robustness rules (all implemented, all locale-safe):

- **No locale-fragile selectors.** Visible/translated text, locale string tables, fixed
  coordinates, and OCR are *not* used as primary selectors. We match a runtime‑resolved
  resource *value* (bidi/diacritic/whitespace-normalized) to on-screen evidence — i.e. we
  resolve the ID first, then find the node.
- **Clicks prefer semantics.** We invoke the resolved node's accessibility `ACTION_CLICK`
  on its clickable ancestor; if a tap is unavoidable it targets the **live resolved node's
  own bounds**, never a constant coordinate. Directional gestures derive left/right from
  `isCurrentLocaleRtl()` and live geometry (`trailingEdgeX`), never a hard-coded side.
- **Placeholder guard.** A resource whose value is placeholder-only (e.g. `%1$s`) must
  never match or link a node (`isPlaceholderOnlyResourceValue`) — otherwise it would attach
  a message ID to unrelated dynamic text.
- **RTL recovery.** The stock Workbench SLAP parser throws and drops an entire window when
  Arabic text embeds a Latin run (e.g. "Motorola"), which would erase every Qira string on
  ar‑* screens. For RTL we read the accessibility tree directly and merge back the dropped
  nodes (LTR path stays byte-identical). This is why *"we mostly don't have resource/string
  IDs"* is manageable: the message-ID bridge + Compose catalog + accessibility recovery
  cover the cases where the plain hierarchy has no usable ID.
- **Fail loud.** If nothing stable resolves, we dump UI (`QiraUiDumper`) and throw with an
  actionable message rather than continue.

## 5. Owner-scoped capture & metadata integrity (why the data is trustworthy)

Implemented in `QiraV2CaptureArtifacts`:

- Each tag has exactly **one evidence-backed owner package** (`OwnerPolicy`): Qira
  `com.lenovo.qira`; PermissionController `com.(google.)android.permissioncontroller` for
  the Android permission tags; Action Core `com.motorola.actioncore` for
  `Live_EnablePermission`. Owner is checked **before, during, and after** capture; a
  mismatch **quarantines** the screenshot (moves it out of the importable `.png/.json`
  namespace) and fails loud.
- After the synchronous Avik call we rebuild a **single-frame, owner-filtered
  accessibility snapshot** and retain only metadata records whose exact text/description +
  bounds belong to the owner — stripping foreign-window contamination (launcher `Home`,
  status bar, other apps).
- Message IDs are linked from Qira's **en‑XM Compose catalog** (`QiraV2SlapMessageIdIndex` +
  `QiraV2ComposeStrings`) bridging the decoded en‑XM marker to the current-locale value.
  For external-owner (permission/action-core) screens we keep the text with **empty**
  `messageIds` — we never invent Qira IDs.
- Description-only accessibility ancestors that merely wrap an independently linked child
  are suppressed from linkage (via `qiraAccessibilityNodePath`) but preserved as unlinked
  evidence — no whole-screen/parent-container link.
- Metadata is replaced **atomically** (staged temp file, identity re-checked, atomic move);
  a truncated/mismatched file can never overwrite good metadata.

## 6. Validation gate (the "done" bar)

A run is accepted only when all hold (see
`.cursor/rules/senior-avik-qira-validation-protocol.mdc`):

- Builds/installs clean; audit unit tests green.
- Device run ends `OK (1 test)` with "all sub-flows OK".
- **Exactly 104 paired PNG+JSON tags == `contract.json`** — zero missing / extra /
  duplicate; no `Live_Camera`.
- `tools/avik-string-link-audit/audit.py` exits `0` (PASS): per-element links only, valid
  on-screen bounds, correct owner/appId/locale, proven state distinctness (Chat
  Processing/Answer/Result; Styles vs StyleFantasy; permission scroll variants).

So confidence is not a subjective "I think we got everything" — it is a hard, reproducible
gate on an enumerated contract.

## 7. Forward-looking: source-level / MCP-Avik (Roy's idea)

Today Avik discovers IDs by inspecting the APK + live device dumps because it captures from
the **running binary** — that is what produces the localized, on-screen screenshots RWS
needs, and it is the part a Figma-only or source-only approach cannot replace.

Because **Helios is a monorepo**, several things Avik currently *reverse-engineers* exist
authoritatively **at source**, which is where "MCP-Avik" could add real value — as a
complement, not a replacement:

- **Measurable coverage.** Enumerate the authoritative screen/route + Compose‑destination
  inventory and string catalog from source, then diff it against the 104-tag contract. That
  directly answers *"how many UIs are there really, and which are we missing?"* — the gap
  that app-crawlers never closed.
- **Stronger selectors, less fallback.** Generate/maintain the message-ID ↔ resource-ID ↔
  Compose-string map from source so fewer screens drop to accessibility/geometry.
- **Agent-driven upkeep.** With Qira integrated into Linear + GitHub, a Cursor + MCP agent
  could extend/repair capture scripts as new flows land, keeping the contract in sync with
  the app.

Net: keep on-device capture + SLAP linkage (it is the ground truth for localized pixels),
and use source-level/MCP to make coverage *measurable* and selectors *more robust*.

## 8. Load-bearing code map

| Concern | File |
|---|---|
| Master pipeline (6 sub-flows) | `avik/qira_v2/scripts/MotorolaQiraMasterCapture.java` |
| Workbench-facing wrappers (`appId=qira`) | `avik/qira/scripts/MotorolaQiraMasterCaptureV2.java`, `...MotorolaQiraHome_Onboarding_Start` |
| Selector primitives | `avik/qira_v2/utils/QiraV2Selectors.java` |
| SLAP dump, message-ID linkage, placeholder guard, semantic/resolved clicks, RTL recovery | `avik/qira_v2/utils/QiraV2SlapTextDump.java` |
| Compose catalog / message-ID index | `avik/qira_v2/utils/QiraV2ComposeStrings.java`, `QiraV2SlapMessageIdIndex.java` |
| Owner-scoped capture + atomic metadata + quarantine | `avik/qira_v2/utils/QiraV2CaptureArtifacts.java` |
| Onboarding classification + adaptive permission scroll | `avik/qira_v2/utils/QiraV2HomeOnboardingFlow.java` |
| Action Core reset before launch | `avik/qira_v2/utils/QiraV2App.java` |
| Tag contract + deterministic audit + tests | `tools/avik-string-link-audit/{contract.json,audit.py,tests/}` |
| Scope, tag taxonomy, release metrics | `artifacts/avik-string-linking-investigation/SCOPE.md` |
| Decisions of record (D‑001..D‑012) | `artifacts/avik-string-linking-investigation/DECISIONS.md` |

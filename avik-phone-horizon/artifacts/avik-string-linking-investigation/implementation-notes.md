# AViK/Qira v2 string-link correction implementation notes

## Changed files

1. `avik-android/src/main/java/avik/qira_v2/utils/QiraV2CaptureArtifacts.java`
   - Applies canonical-tag owner policy for Qira, Android PermissionController,
     and Action Core.
   - Verifies the foreground package before and after the synchronous AViK
     capture and verifies metadata package/tag/hash identity.
   - Filters `avikTexts` by exact NFC/bidi-normalized value plus clipped bounds
     from all accessibility windows owned by the verified package.
   - Appends missing accessibility nodes and performs Compose Message-ID
     enrichment only for Qira-owned captures.
   - Rejects empty owner evidence, logs required counts/candidates, quarantines
     rejected pairs outside AViK's importable extensions, and replaces corrected
     metadata with same-directory `ATOMIC_MOVE` + `REPLACE_EXISTING` (with an
     explicit logged fallback/failure path).
   - Retrieves `dumpsys window windows` without a shell pipeline and parses
     `mCurrentFocus` / `mFocusedApp` in Java for owner-failure activity evidence.
   - Reconstructs metadata with exact accessibility source-kind/value/bounds/path
     keys and clears IDs only from proven DESCRIPTION strict ancestors of an
     independently linked child. Suppressed descriptions remain as unlinked
     accessibility evidence and are counted in diagnostics.
2. `avik-android/src/main/java/avik/qira_v2/utils/QiraV2SlapTextDump.java`
   - Rejects placeholder-only resource values before exact or wildcard matching.
   - Preserves parameterized values with literal letter/digit anchors and
     reordered numbered placeholders.
   - Logs the number of ambiguous catalog entries skipped.
   - Preserves TEXT versus DESCRIPTION source kind plus deterministic
     root-prefixed child-index paths in owner accessibility snapshots; equal
     text/description values are text-owned.
3. `avik-android/src/main/java/avik/qira_v2/utils/QiraV2FocusZoneFlow.java`
   - Stops after required `Live_Active`; no camera switch, camera permission, or
     `Live_Camera` capture occurs in the user-approved 104-tag flow.
   - Proves the Qira enable-permission prompt through Compose/resource-backed
     SLAP and invokes only the resource-backed Qira CTA.
   - Requires exact `com.motorola.actioncore` foreground before capture, never
     activates Action Core's visible Enable action, then uses Android Back and
     proves both Qira foreground and the preserved Qira prompt.
4. `avik-android/src/main/java/avik/qira_v2/pages/QiraV2FocusZonePage.java`
   - Provides the bounded exact-package wait with two stable polls for Action
     Core/Qira transitions; camera-only helpers were removed.
5. `avik-android/src/main/java/avik/qira_v2/utils/QiraV2HomeOnboardingFlow.java`
   - Classifies Intro, response-language, and Device Assurance surfaces by
     owner-backed Compose catalog identity before capture.
   - Retries an unchanged Intro through the stable Next selector without another
     capture, emits Language and Device Assurance exactly once, and fails/dumps
     any unclassified Next-only surface.
   - Adds a fail-only canonical capture guard and en-XM assertions requiring
     both Language and Device Assurance before downstream success.
6. `avik-android/src/main/java/avik/qira_v2/utils/QiraV2SlapCatalog.java`
   - Adds the authoritative `language_screen_pill_text` catalog identity with
     Message ID `1bnG5By6KeLRvwwkliANfx` and parameterized source value.
7. `avik-android/src/main/java/avik/qira_v2/utils/QiraV2App.java`
   - Clears only `com.motorola.actioncore` in addition to Qira before launch,
     requires trimmed output `Success`, logs success, and fails otherwise.
8. `tools/avik-string-link-audit/audit.py`
   - Adds the standard-library-only read-only artifact audit and CSV/JSON/Markdown
     reporting command.
   - Enforces the user-approved authoritative 104-tag default contract.
   - Strictly validates the TagsDropdown dynamic/unscoped exact set, promotes
     linked parent containers to release-gate failures, and limits
     duplicate-bounds analysis to linked overlays.
   - Uses same-root strict ancestry paths for proven parent containers; pathless
     geometry is a named warning and malformed populated paths fail.
9. `tools/avik-string-link-audit/contract.json`
   - Checks in the exact 104-tag `SCOPE.md` contract, excluding only
     `MotorolaQiraFocusZone_Live_Camera`, plus app/script and placeholder-only
     leakage constraints.
   - Schema version 2 declares the exact four-value TagsDropdown
     dynamic/unscoped classification with a no-Message-ID requirement.
10. `tools/avik-string-link-audit/README.md`
   - Documents command, report, exit-code, dynamic classification, and linked
     overlay release gates.
11. `tools/avik-string-link-audit/tests/fixtures/cases.json`
   - Provides sanitized artifact cases for linkage, identity, bounds, locale,
     dynamic text, parameterized text, RTL, placeholder leakage, strict
     TagsDropdown classification, parent containers, and linked bounds.
12. `tools/avik-string-link-audit/tests/test_audit.py`
   - Covers the required fixture scenarios, both accepted execution layouts,
     report generation, compressed-PNG corruption, the exact 104-tag scope, and
     explicit exclusion of `Live_Camera`.
   - Covers exact/missing/extra/unexpected/linked dynamic rows, unrelated
     no-link failure, parent-container gating, and linked-only bounds handling.
13. `artifacts/avik-string-linking-investigation/DECISIONS.md`
   - Adds D-012, superseding required-Live-Camera/105-tag wording while keeping
     D-011 Action Core reset active.
14. `artifacts/avik-string-linking-investigation/SCOPE.md`
   - Removes only `MotorolaQiraFocusZone_Live_Camera`.
15. `artifacts/avik-string-linking-investigation/PROGRESS.md`
   - Records the 104-tag scope and next validation target.
16. `artifacts/avik-string-linking-investigation/implementation-notes.md`
   - Records this scoped implementation and validation.

No production flow/page source outside the authorized qira_v2 Live camera and
Home onboarding files, qira v1 source, Workbench/Review source, Gradle
configuration, existing tag name, translation, or credential was changed.

## Validation

- Python:
  - Command: `python -m unittest discover -s ".\tools\avik-string-link-audit\tests" -p "test_*.py" -v`
  - Final result after ancestry/D-011/104-tag update: exit code `0`; `29` tests
    run; `29` passed.
- Android compile:
  - Command: `$env:GRADLE_USER_HOME="C:\Users\BLR-USER\.gradle"; & "C:\Users\BLR-USER\.gradle\wrapper\dists\gradle-8.7-bin\bhs2wmbdwecv87pi65oeuq5iu\gradle-8.7\bin\gradle.bat" :avik-android:compileDebugJavaWithJavac --offline --no-daemon -PartifactoryUsername=unused -PartifactoryPassword=unused`
  - Final result: exit code `0`; `BUILD SUCCESSFUL in 30s`; `16`
    actionable tasks (`2` executed, `14` up-to-date).
  - One intermediate compile exited `1` because the published `AvikUtility`
    lacks `getCurrentActivityName()`. Activity diagnostics were changed to the
    already-proven device `dumpsys window` path; both subsequent compiles passed.
- IDE diagnostics: no errors in the changed Java/Python files.
- Final install:
  - Used the same cached Gradle 8.7 executable with `--offline --no-daemon` and
    dummy Artifactory properties for `:avik-android:installDebug` and
    `:avik-android:installDebugAndroidTest`.
  - Exit code `0`; `BUILD SUCCESSFUL in 32s`; 61 actionable tasks; both APKs
    installed on the sole real device `N2HT430016`.
- Final instrumentation:
  - Entry/script:
    `avik.qira.scripts.MotorolaQiraMasterCaptureV2`.
  - Build label: `Qira_StringLinkFix6_en-XM_20260719`; locale and
    `qira.locale=en-XM`; `qira.applySystemLocale=false`; SLAP/UI-dump flags all
    enabled; `app_id=qira`.
  - Full log:
    `artifacts/avik-string-linking-investigation/logs/postfix-master-en-XM-run6-20260719.log`
    (642,998 bytes; SHA-256
    `f7e2bcbc7f5b58d7a9c206b197a61d84ec545c3f2506c286d206292fdda922d0`).
  - Result: exit code `0`, all six sub-flows completed,
    `qira_v2 master capture suite finished; all sub-flows OK.`, and
    `OK (1 test)`.
  - The log contains 104 owner-scoped captures / 104 unique tags, no duplicate
    guard failure, and no `Live_Camera` mention.
  - Action Core reset logged exact `output=Success` before Qira launch.
    `Live_Active` then proved the resource/SLAP prompt; Action Core foreground
    was stable for two polls; `Live_EnablePermission` was captured under exact
    Action Core ownership; Back returned to Qira and re-proved the prompt.
- Final artifacts and audit:
  - Fresh pull:
    `artifacts/avik-string-linking-investigation/before-after/postfix-en-XM-run6`
    contains 208 files: 104 PNG + 104 JSON.
  - The 104 metadata files have 104 unique tags, unique orders 1-104,
    `appId=qira`, the required script, and the run-6 build label.
  - Report:
    `before-after/postfix-en-XM-run6-audit/avik-string-link-audit.{json,csv,md}`.
    The JSON SHA-256 is
    `ff34365d3f98f0e73a7629fb6434b16d1b52b8aced8f83eeab4c288f875e065b`.
  - Audit status **PASS**: 104 artifacts/pairs, 1,615 linked records, 239
    static-unresolved records, four excluded dynamic/unscoped rows, 546 unique
    Message IDs, zero release-gate failures, and two investigated warnings.
  - Missing/extra/duplicate tags, invalid/out-of-screen bounds, near-full
    links, placeholder leakage, exact duplicate links, linked ancestry
    containers, legacy pathless geometry warnings, and malformed paths are all
    zero.
  - TagsDropdown exactly matches `Identity`, `Contact`, `Education`, `Work`;
    all four rows have no Message IDs and classification passes.
- Source/path inspection:
  - All 1,858 records have valid source-kind and node-path metadata.
  - 69 ID associations were removed from strict DESCRIPTION ancestors on seven
    expected screens. The Focus slide-2 page description is unlinked while its
    granular child text and icon descriptions such as `Menu` remain linked.
  - Fifty geometry-only candidates remained linked because same-root paths
    prove they are not ancestors. The historical foreign launcher Message ID
    and near-full `Home` record are absent.
  - The two warnings are same-bounds Red/Purple portal-animation sibling
    descriptions on Chat and Catch Me Up onboarding; text, IDs, and paths are
    distinct, so they are not duplicate exact links.

## Workbench en-XM exception remediation (2026-07-20)

- Failure evidence retained under
  `live-evidence/workbench-en-XM-20260719` proved two over-strict state gates:
  - Live displayed the resource-backed
    `language_model_download_failed` notice in the same content slot as the
    Action Core prompt. Its stable `download_close` control was present.
  - Create Avatar had successfully returned from the picker and exposed the
    stable `replace_label` and `generate_image_button` controls, but the
    optional `cd_selected_photo_preview` accessibility description was absent.
- Implementation:
  - `QiraV2FocusZonePage` now detects and dismisses only that model-download
    notice through exact Qira Compose string IDs. The Live flow waits for the
    authoritative permission prompt before capturing `Live_Active`.
  - Avatar and Style Sync now prove their shared selected-photo surface from
    the required `replace_label` + `generate_image_button` pair in one SLAP
    snapshot. `cd_selected_photo_preview` is retained as diagnostic evidence,
    not an AND-gate.
  - No visible-text, OCR, fixed-coordinate, or sleeps-only fallback was added.
- Validation:
  - Audit tests: 29/29 passed; offline Gradle 8.7 compile/install passed.
  - Direct-class reproduction then captured all 104 screens and resolved the
    reported Live and Avatar exceptions; it was intentionally not accepted as
    a Workbench artifact gate because direct `avik.qira_v2.*` provenance yields
    `appId=qira_v2`.
  - Final Workbench-facing run
    `Qira_EnXM_FinalFix_20260720` used
    `avik.qira.scripts.MotorolaQiraMasterCaptureV2`, completed all six
    sub-flows, and ended `OK (1 test)`.
  - `before-after/postfix-en-XM-finalfix` contains exactly 104 PNG/JSON pairs.
    Its audit is **PASS** with zero release gates, 1,628 linked records, four
    excluded dynamic rows, 545 unique Message IDs, and one investigated
    same-bounds portal-animation sibling warning.

## Residual validation risk

- The current evidence proves the Google PermissionController package; the AOSP
  platform-equivalent package is narrowly accepted because the existing Qira
  permission implementation already identifies it. Its use on another target
  build must still be observed in that build's package/activity evidence.
- The required owner snapshot is taken immediately after AViK returns. A
  genuinely changing dynamic node can therefore differ from the screenshot-time
  hierarchy; exact matches are retained and Qira-only missing nodes are appended,
  and run 6 found no gate failure attributable to frame drift.
- Under the final 104-tag/schema-v3 audit, immutable run 4 has 105 paired
  artifacts and fails only for the historical extra `Live_Camera` metadata/tag
  (2 gates); it has 0 proven ancestry containers, 80 legacy geometry overlaps,
  and 28 warnings total.
- Immutable run 5 has 104 paired artifacts and fails for historical extra
  `Live_Camera` plus missing required `Live_EnablePermission` (3 gates); it has
  0 proven ancestry containers, 43 legacy geometry overlaps, and 19 warnings.
- Local Workbench is available, but the exact unique run-6 round is absent
  (HTTP 404), its script query is empty, and the API has no round-create route.
  The exact run was not imported into an unrelated round; no `qira_v2` module
  was created.
- Multi-locale and authenticated remote Review validation remain outside this
  final en-XM device gate.

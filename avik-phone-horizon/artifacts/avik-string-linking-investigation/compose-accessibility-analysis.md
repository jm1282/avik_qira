# Compose and accessibility analysis

## Executive finding

**Fact:** The dominant parent-area record is not a Qira Compose root. In the scoped en-XM Workbench export, 52 of 58 present affected tags have the same launcher-owned `Home` accessibility description at `[0,123][1080,2394]` (90.119% of the display) with message ID `2wGy6ZtecYu8S5DJUvTEHV`. Only 1 of 44 present controls fails the same gate: `MotorolaQiraSettings_Drawer_Menu`, which is also launcher-backed. Evidence: `before-after/en-XM-scope-overlink.json:7-23,26-48` and `before-after/en-XM-scope-overlink.csv:2-60,149-165`.

**Fact:** A failed Chat History hierarchy contains granular Qira children before SLAP extraction: 21 Qira text nodes and 16 Qira content-description nodes. The near-full-screen `Home` node is owned by `com.motorola.launcher3`, while Qira is a separate `ComposeView` window. Evidence: `before-after/forensic-pair-metrics.md:25-45` and `logs/candidate-en-XM-20260716-195459-ManageChats_Selected.xml:70,150-250`.

**Conclusion:** The first evidence-backed difference is **P — capture-window scoping / foreign-window contamination**, not missing Compose child semantics. AViK walks every visible accessibility window, qira_v2 explicitly disables its available foreground-package filter, and `includeDescription=true` serializes the launcher's large `Home` description into Qira metadata. Child Qira links remain present; they are not collapsed away.

The en-XM export is structurally strong but not release-authoritative: it uses execution `3544956f-bef9-422d-9f76-3600fb0397b2`, app version `QT-01.01.550`, geometry `1080x2520`, and historical `app_id=qira_v2`; its saved XML is supplemental rather than execution-paired. A confirming run must use the required `appId=qira`.

## What UiAutomator and AViK can see

**Facts**

- `AndroidHierarchy` reads `UiAutomation.windowsOnAllDisplays`, recursively walks each `AccessibilityNodeInfo`, and therefore sees the Android accessibility tree for every interactive window on display 0. It does not read the Compose layout tree or Compose's unmerged test-semantics tree. Source: `workbench/avik-clients/android/src/main/java/com/motorola/g11n/tools/avik/client/android/screenshot/hierarchy/AndroidHierarchy.kt:61-81,151-201`.
- AViK emits every nonblank node `text` and, when requested, every nonblank `contentDescription`. Bounds come from `getBoundsInScreen`; parsing only clips them to the display and does not expand them. Source: `HierarchyAccessibilityNode.kt:24-53` and `workbench/avik-workbench-common/src/main/java/com/motorola/g11n/tools/avik/common/parsers/ParseSLAPDump.kt:30-65,79-107,134-156`.
- qira_v2 forces `includeText=true` and `includeDescription=true`. It then performs later accessibility snapshots to append missing Qira nodes and resolve Compose catalog IDs. Those later passes retain accessibility bounds and do not reconstruct leaf geometry. Source: `avik-phone-horizon/avik-android/src/main/java/avik/qira_v2/utils/QiraV2CaptureArtifacts.java:33-86,138-260,277-363`.
- Screenshot pixels and hierarchy are produced on separate service threads, although `takeScreenshot` blocks until all capture services finish. A small pixel/tree timing difference is possible, but no race is needed to explain the repeated launcher node. Source: `AbstractScreenshotService.kt:10-35`, `ScreenshotController.kt:50-70`, and `AndroidScreenshotService.kt:26-59`.
- Qira UI dumps are separate snapshots taken after the AViK call. They are excellent structural evidence but are not automatically the exact hierarchy serialized into a prior screenshot. Source: `QiraUiDumper.java:84-133`.

AViK cannot see text painted only into a `Canvas`, `TextureView`, `SurfaceView`, or image; hidden/pruned nodes; unmerged Compose semantics; or test tags that are not exported as Android view IDs.

## Affected versus known-good controls

### Compose/View hierarchy

**Fact:** Both affected and known-good Qira screens are predominantly Compose. Home, Knowledge, and Settings all have a full-screen `androidx.compose.ui.platform.ComposeView` followed by virtual `android.view.View` / `android.widget.TextView` nodes. Evidence:

- Home: `.scratch/validation-evidence/sol-bg-BG-20260716/ui-dumps/MotorolaQiraHome_Home/20260716-181713.xml:60-129`
- Knowledge: `.scratch/validation-evidence/sol-bg-BG-20260716/ui-dumps/MotorolaQiraKnowledge_Main_FileList/20260716-182734.xml:3-163`
- Settings: `_dbg_pull/evidence-id-settings-default.xml:47-136`

Therefore “Compose versus Views” does not separate failures from controls.

System permission surfaces are different: they are classic PermissionController views in `GrantPermissionsActivity`, with stable IDs such as `permission_message`, `permission_allow_foreground_only_button`, `permission_allow_one_time_button`, and `permission_deny_button`. Evidence: `.scratch/qira-ui-dumps-device/qira-ui-dumps/MotorolaQiraHome_Onboarding_AndroidLocationPermission/20260703-081800.xml:27-39`.

### Merged/unmerged semantics and `clearAndSetSemantics`

**Fact:** UiAutomator exposes only the accessibility-facing tree. The repository contains no Qira application Compose source and no occurrences of `clearAndSetSemantics`, `mergeDescendants`, or `testTagsAsResourceId`; those implementation choices cannot be proved from this checkout.

**Fact:** Granular child text exists before SLAP extraction on the failed Chat History sample and on current Home, Focus, Creator, Knowledge, and Settings dumps. This rejects a general stage-E “Compose exposed no children” explanation.

**Hypothesis:** A few isolated visual regions may use merged or cleared semantics, but proving that requires a Qira debug/test build that dumps both merged and unmerged Compose semantics. Absence from UiAutomator alone is insufficient proof.

### Content-description aggregation

**Fact:** Some affected Compose controls expose both child text and a larger parent description:

- Focus carousel page: parent `Page 1 of 5…` at `[42,294][1038,1156]` plus the title child.
- Home tiles: parent descriptions such as `Creator Zone …` plus separate title/subtitle children.
- Knowledge rows: a row description aggregates filename/date/source while a filename child remains.

Evidence: `.scratch/qira-ui-dumps-device/qira-ui-dumps/MotorolaQiraFocusZone_BubbleBar/20260703-102555.txt:9-22` and `.scratch/validation-evidence/sol-bg-BG-20260716/ui-dumps/MotorolaQiraKnowledge_Main_FileList/20260716-182734.txt:9-44`.

**Fact:** These Qira parent descriptions are secondary to the observed 90.1% defect. The repeated near-full-screen record is the launcher's `Home` description, not one of these Qira containers.

**Hypothesis:** After foreign windows are removed, some mid-sized duplicate parent links may remain on carousel/cards. They should be measured before changing Qira semantics.

### Custom graphics and View/Compose boundaries

**Facts**

- Creator and Live onboarding use `androidx.compose.ui.viewinterop.ViewFactoryHolder` around an `android.view.TextureView`; text remains in sibling Compose nodes. Evidence: `.scratch/dumps/MotorolaQiraCreatorZone_CreatorHome_Grid/20260707-171055.xml:215-221` and `_dbg_pull/live_de/MotorolaQiraFocusZone_Live_agreement_not_visible/20260708-192914.xml:78-87`.
- No relevant scoped dump proves a Qira `WebView` or `SurfaceView`.
- Scribble's drawing area exposes no localizable text, while its toolbar labels do. No evidence shows static localizable text painted inside the canvas. Evidence: the bg-BG `MotorolaQiraCreatorZone_Scribble_CanvasActive` metadata at `.scratch/validation-evidence/sol-bg-BG-20260716/screenshots/screenshots/avik.qira.scripts.MotorolaQiraMasterCaptureV2/cz7pbgpidc32wgmo17o37mk9u.json`.

### Scrollable/Lazy content

**Fact:** Knowledge, Settings, Creator, and Chat History expose scrollable accessibility nodes and only currently materialized/visible children. Runtime class names cannot distinguish `LazyColumn` from another Compose scroll container. The visible children are individually extractable, so Lazy virtualization is not the broad failure discriminator.

### Popups, dialogs, sheets, navigation roots, and multiple windows

**Facts**

- Known-good Knowledge dropdowns and menus expose a small bounded popup root while background content is accessibility-pruned. Evidence: `.scratch/validation-evidence/sol-bg-BG-20260716/ui-dumps/MotorolaQiraKnowledge_Main_CategoriesDropdown/20260716-182736.txt:9-13` and `.scratch/qira-ui-dumps-validation6/qira-ui-dumps/MotorolaQiraKnowledge_Main_MoreOptionsMenu/20260601-151715.txt:9-11`.
- Creator flows can expose multiple Qira `android:id/content` roots simultaneously. The Scribble exit dump contains Creator Home, Scribble, and dialog roots; current metadata also includes launcher content. Evidence: `.scratch/validation-evidence/sol-bg-BG-20260716/ui-dumps/MotorolaQiraCreatorZone_Scribble_ExitPopup/20260716-182616.txt:9-47`.
- Launcher-backed Home, Focus Zone, Creator, Chat History, and Settings Drawer retain the full launcher workspace in addition to ordinary system chrome. Full-screen Onboarding, Knowledge, and Settings pages may still expose status/taskbar windows, but generally do not expose the launcher's near-full-screen `Home` workspace node. This topology matches the 52 affected failures and the single failed control.
- `MotorolaQiraCreatorZone_EditImage_Editor` in the bg-BG validation metadata is actually `com.google.android.photopicker/com.android.photopicker.MainActivity`, not Qira's editor. This is a separate stage-A/H naming/state issue, not a Qira semantics failure. Evidence: `.scratch/validation-evidence/sol-bg-BG-20260716/screenshots/screenshots/avik.qira.scripts.MotorolaQiraMasterCaptureV2/688er279k35euqp09zoklfcdj.json`.

### Resource IDs and `testTagsAsResourceId`

**Fact:** Qira virtual nodes in the sampled dumps normally have an empty `resource-id`; `android:id/content` is often the only Qira ID. There is no runtime evidence that Compose test tags are exported as view IDs.

**Hypothesis:** Either `testTagsAsResourceId` is disabled or the relevant nodes have no test tags. This cannot be distinguished without Qira source or a debug semantics dump.

### Accessibility pruning

**Fact:** Pruning differs by window type, not simply by feature:

- Modal Knowledge popups prune the background.
- Launcher-backed overlays retain the launcher accessibility window.
- Texture/canvas visuals are textless unless explicit semantics are supplied.
- System permission dialogs expose only their own View text/buttons plus system chrome.

## System-owned permission surfaces

Treat these independently from Qira SLAP:

- `MotorolaQiraHome_Onboarding_Android*` and `MotorolaQiraFocusZone_Live_AndroidMicrophonePermission` are owned by `com.google.android.permissioncontroller`. Their text records correctly have no Qira message IDs in product locales. Evidence: `.scratch/validation-evidence/sol-bg-BG-20260716/screenshots/screenshots/avik.qira.scripts.MotorolaQiraMasterCaptureV2/djf80qta997fcxheuuhgu6537.json` and `.scratch/validation-evidence/sol-bg-BG-20260716/screenshots/screenshots/avik.qira.scripts.MotorolaQiraMasterCaptureV2/7lzooyuumtwjg45n1bd25swsu.json`.
- `MotorolaQiraFocusZone_Live_EnablePermission` is owned by `com.motorola.actioncore/.screenawareness.MotoAiPermissionActivity`, not Qira or PermissionController. Evidence: `.scratch/validation-evidence/sol-bg-BG-20260716/screenshots/screenshots/avik.qira.scripts.MotorolaQiraMasterCaptureV2/2rzoxi9thjl0e597sytqptj90.json`.
- System surfaces must be selected by their foreground package and stable Android resource IDs. They must not be forced through the Qira Compose catalog.

The scope audit passes the Android microphone permission and blocks the absent ActionCore row; it does not show the launcher-parent defect on PermissionController.

## Current source versus committed HEAD

**Facts**

- HEAD `726751cda59f32925ea3d0939d6cd5e36d81d38a` already collects every display window. Current `AndroidHierarchy.kt` adds an opt-in package filter, but it is off by default.
- Current untracked qira_v2 code explicitly clears `avik.foregroundWindowOnly`, so the current effective behavior remains all-window capture. Source: `QiraV2InstrumentationDefaults.java:43-50`.
- Current qira_v1 defaults to text extraction with descriptions off because of prior parent-description overlink observations; qira_v2 forces descriptions on. Source: `BaseQiraCaptureScript.java:190-207` and `QiraConfig.java:30-43`.
- The compatible qira_v2 run uses the same forced flags for affected and control tags, so the affected/control difference is not an `includeDescription` configuration mismatch. Historical v1 outputs with descriptions off are not compatible controls.
- Qira production Compose source is absent. Exact app composable files using semantics APIs cannot be named from this repository.

## Smallest safe correction

### Recommended first: qira_v2 test-only window/package scoping

Use the existing AViK foreground-window filter for qira_v2 captures, but set the expected package per capture:

- Qira-owned tags: `com.lenovo.qira`
- Android runtime permission tags: current PermissionController package
- ActionCore permission tag: `com.motorola.actioncore`
- Photo Picker tags, only if intentionally captured: `com.google.android.photopicker`

This should retain non-focused Qira overlay windows because the filter can be explicitly pinned to Qira, while excluding launcher, status-bar, widget, IME, and unrelated app windows. Do not globally pin every tag to Qira.

This is smaller and safer than changing Compose semantics: it uses already-present capture-framework support, is qira_v2-scoped, preserves `includeDescription=true`, preserves icon-only descriptions, and does not change user-visible or TalkBack behavior.

### Secondary, only after a scoped rerun

If Qira-owned parent links remain, add a tree-aware capture rule that distinguishes:

- leaf text;
- icon-only/unique content descriptions, which must be retained;
- container descriptions that duplicate or concatenate exposed descendant text.

Do not apply a flat size threshold or disable descriptions globally. Carousel page-state descriptions and genuine merged controls could otherwise be lost.

### Test-only Compose bridge for unresolved cases

If a scoped tag visibly contains text but UiAutomator still has no child node, use a Qira debug/test build to export merged and unmerged semantics and optionally expose stable `testTag(stringId)` values through `testTagsAsResourceId`. This is diagnostic/identity support; it does not itself create missing localizable text.

### Production semantics correction

Only consider removing redundant parent `contentDescription`, changing merge policy, or changing `clearAndSetSemantics` after unmerged-tree and TalkBack evidence proves the app semantics are wrong. Such changes can alter focus order, announcement grouping, action targets, and accessibility behavior.

## Regression risks

- Package scoping could drop a legitimate Qira overlay if the package is inferred from the focused launcher window instead of explicitly pinned.
- Globally pinning Qira would erase PermissionController, ActionCore, and Photo Picker text.
- Removing all parent descriptions would lose icon-only controls and meaningful merged-control labels.
- Deduplicating only by text would collapse repeated labels on distinct controls.
- Production merge/clear changes can regress TalkBack navigation and announcements.
- A later accessibility snapshot used for qira_v2 metadata enrichment can observe a different transient frame; window/package identity should be captured with the original hierarchy where possible.

## Required validation

1. Re-run en-XM under `appId=qira` with unchanged `dumpUi/includeText/includeDescription=true`.
2. Compare at minimum:
   - failed overlay: `MotorolaQiraChatHistory_Main_ManageChats_Selected`;
   - failed control: `MotorolaQiraSettings_Drawer_Menu`;
   - full-screen controls: `MotorolaQiraHome_Onboarding_PermissionsScrolled1`, `MotorolaQiraKnowledge_Main_FileList`, `MotorolaQiraSettings_Settings_Default`;
   - Qira aggregate semantics: `MotorolaQiraFocusZone_BubbleBar`;
   - multi-root dialog: `MotorolaQiraCreatorZone_Scribble_ExitPopup`;
   - external surfaces: Android microphone permission and ActionCore permission.
3. Assert:
   - launcher `Home` ID `2wGy6ZtecYu8S5DJUvTEHV` is absent from Qira-owned metadata;
   - affected near-full-screen and foreign-package link counts are zero;
   - Qira leaf text/message-ID counts do not regress;
   - icon-only descriptions remain;
   - system-owned surfaces retain their own text and resource IDs but receive no Qira IDs;
   - screenshot, metadata, UI dump, tag, package, and activity are paired.
4. Add parser/window tests for:
   - Qira overlay above launcher;
   - popup that prunes its background;
   - multiple same-package Qira windows;
   - IME present;
   - PermissionController and ActionCore foreground packages;
   - parent description plus child text;
   - icon-only description;
   - repeated labels at different bounds.

## Affected implementation files

- `workbench/avik-clients/android/src/main/java/com/motorola/g11n/tools/avik/client/android/screenshot/hierarchy/AndroidHierarchy.kt`
- `workbench/avik-clients/android/src/main/java/com/motorola/g11n/tools/avik/client/android/screenshot/hierarchy/HierarchyAccessibilityNode.kt`
- `workbench/avik-workbench-common/src/main/java/com/motorola/g11n/tools/avik/common/parsers/ParseSLAPDump.kt`
- `avik-phone-horizon/avik-android/src/main/java/avik/qira_v2/utils/QiraV2InstrumentationDefaults.java`
- `avik-phone-horizon/avik-android/src/main/java/avik/qira_v2/utils/QiraV2CaptureArtifacts.java`
- `avik-phone-horizon/avik-android/src/main/java/avik/qira_v2/utils/QiraV2SlapTextDump.java`
- `avik-phone-horizon/avik-android/src/main/java/avik/qira/scripts/BaseQiraCaptureScript.java`
- `avik-phone-horizon/avik-android/src/main/java/avik/qira/utils/QiraConfig.java`

No exact Qira production composable file can be identified because that source is not present.

## Primary evidence paths

- `artifacts/avik-string-linking-investigation/before-after/en-XM-scope-overlink.json`
- `artifacts/avik-string-linking-investigation/before-after/en-XM-scope-overlink.csv`
- `artifacts/avik-string-linking-investigation/before-after/forensic-pair-metrics.md`
- `artifacts/avik-string-linking-investigation/logs/candidate-en-XM-20260716-195459-ManageChats_Selected.xml`
- `artifacts/avik-string-linking-investigation/logs/candidate-20260716-195459-ManageChats_Selected.txt`
- `.scratch/runs/qira-v2-en-XM-slap.txt`
- `.scratch/runs/sol-validation-bg-BG-20260716.log`
- `.scratch/validation-evidence/sol-bg-BG-20260716/ui-dumps/MotorolaQiraHome_Home/20260716-181713.xml`
- `.scratch/validation-evidence/sol-bg-BG-20260716/ui-dumps/MotorolaQiraKnowledge_Main_FileList/20260716-182734.xml`
- `.scratch/validation-evidence/sol-bg-BG-20260716/ui-dumps/MotorolaQiraCreatorZone_Scribble_ExitPopup/20260716-182616.txt`

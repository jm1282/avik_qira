# Repository and Pipeline Map

Generated: 2026-07-19  
Repository root: `C:\Users\BLR-USER\Avik_repo\avik`  
Active project: `C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon`

## 1. Executive findings

1. The current working implementation is not the committed repository state. `HEAD` is `726751cda59f32925ea3d0939d6cd5e36d81d38a` on `master` (`ahead 1, behind 1`), while all `avik.qira_v2.*` implementation, all Workbench-facing V2 wrappers, and `AppIdAlias.kt` are untracked. Relevant qira v1 and Workbench files are also unstaged, and the legacy `MotorolaQiraHomeCapture.java` is simultaneously staged as deleted and re-created as an untracked V2 shim.
2. The Android capture call is synchronous at its public boundary. `ScreenshotController.startScreenshot()` blocks until screenshot, string-link, and screen-metadata workers have all called `notifyScreenshotFinished()`. `AndroidScreenProcessService` then serializes and closes `<hash>.json` before `AvikHandler.takeScreenshot(...)` returns. The comments in the two master scripts that describe `takeScreenshot()` as merely queuing work are stale relative to the current service code.
3. Stock AViK string collection is an Android accessibility-window traversal, not an unmerged Compose semantics dump. Compose contributes whatever it exposes through `AccessibilityNodeInfo`; merged parent semantics can therefore arrive as large parent records. The parser emits both text and, when requested, content description for every available node and does not suppress parents when children exist.
4. qira_v2 adds a second linkage layer after stock metadata serialization. It reads the just-written JSON by the latest screenshot hash, appends missing Qira accessibility nodes, resolves current-locale Compose values through stable Compose string IDs, maps those IDs to message IDs decoded from Qira's shipped `values-en-rXM` catalog, and atomically replaces the same JSON.
5. qira_v2 deliberately captures all visible accessibility windows. The current unstaged `AndroidHierarchy` can opt into foreground-package scoping, but `QiraV2InstrumentationDefaults.apply()` clears `avik.foregroundWindowOnly`. This is consistent with current evidence containing Qira, launcher, System UI, Google, and other-window strings in one screenshot.
6. Screenshot/metadata pairing on device is hash-based: `<hash>.png` and `<hash>.json` under the instrumentation `script` directory. Workbench imports every JSON recursively and reconstructs `<hash>.png`; it does not explicitly reject a missing or mismatched PNG before database insertion.
7. There is no standalone Android-side upload manifest. The effective registration manifest is Workbench's serialized `ScreenUploadInfo` (`enable_info`) containing screen name, deterministic registration hash, image hash, bounds/message IDs, locale, script, and app ID. PNG/audio files are uploaded separately. Metadata JSON is intentionally not uploaded to GCS; Review receives `mtexts` from the Workbench database.
8. Review App does not invent or merge link geometry. It stores the submitted `mtexts`, returns the latest screenshot's records, discards records with no message IDs in the Angular model, scales the remaining bounds to the displayed source image, and draws one absolutely positioned overlay per record. A parent-sized source record therefore renders as a parent-sized highlight.
9. The authoritative newest completed execution found is Workbench execution `1cc5e264-bd1d-47e9-9371-e744995b531c` (`uk-UA`), not a filename-derived choice. Its exported execution record has explicit start/end/duration, 104 ordered unique tags, 104 valid PNGs, and the final order-104 Settings Feedback capture six seconds before execution end. Workbench database reconciliation also provides reviewable payloads and Review URLs for that execution.
10. That execution represents the older V2 identity contract: `script=avik.qira_v2.scripts.MotorolaQiraMasterCapture`, `appId=qira_v2`. It does not prove the current required wrapper/app ID contract (`avik.qira.scripts.MotorolaQiraHome_Onboarding_Start`, `appId=qira`).

No fix is proposed in this report.

## 2. Evidence and trust boundaries

### Repository state

- Branch: `master...origin/master [ahead 1, behind 1]`.
- HEAD: `726751cda59f32925ea3d0939d6cd5e36d81d38a`, authored `2026-05-12T12:18:11+05:30`, subject `QTL10NTLS-85 Fix Repo Init for avik pc`.
- Staged pipeline change: deletion of the 242-line committed legacy `avik.qira.scripts.MotorolaQiraHomeCapture`.
- Untracked replacement at the same path: a 63-line compatibility shim delegating to `QiraV2HomeOnboardingFlow`.
- No qira_v2 source, V2 wrapper, onboarding wrapper, or `AppIdAlias.kt` exists in `HEAD`; `git ls-tree` returned no entries for those paths.
- Review App source is present and has no material working-tree changes in the scoped status.

### Current source versus historical run

The map below describes the current working files unless explicitly labeled `HEAD` or historical run. The Round1 Workbench evidence was captured with an earlier uncommitted V2 state:

- Round1 metadata uses `appId=qira_v2` and the original qira_v2 master script.
- Current untracked defaults force `app_id=qira`, and current untracked Workbench wrappers live under `avik.qira.scripts`.
- The Round1 export has no Git commit or dependency-lock manifest, so it cannot prove which exact working-tree snapshot generated it.

## 3. End-to-end current call graph

```text
Instrumentation / Workbench
  AndroidExecution.executeScript()
    extras: build_label, script, screen_folder, locale, isDelta
    -> AndroidJUnitRunner
       -> AvikHandler JUnit rule / AvikScriptStatement.before()
          -> start AndroidScreenProcessService workers
          -> clear screenshot root

Current Workbench-facing V2 master
  avik.qira.scripts.MotorolaQiraMasterCaptureV2            [untracked]
    -> avik.qira_v2.scripts.MotorolaQiraMasterCapture      [untracked]
       -> qira Home onboarding wrapper
       -> qira_v2 Focus Zone flow
       -> qira V2 Creator wrapper
       -> qira V2 Knowledge wrapper
       -> qira V2 Chat History wrapper
       -> qira V2 Settings wrapper

Home / Focus flow
  QiraV2...Flow.capture(..., QiraV2ScreenshotSink)
    -> sink.capture(full canonical tag)
       -> QiraV2CaptureArtifacts.captureSlapScreenshot(...)

Creator / Knowledge / Chat History / Settings flow
  inherited avik.qira legacy script calls takeScreenshot(suffix)
    runtime class is qira_v2 wrapper
    -> overridden takeScreenshot()
       -> QiraV2CaptureArtifacts.captureSlapScreenshot(full canonical tag)

QiraV2CaptureArtifacts
  -> waitForIdle(1000)
  -> AvikHandler.takeScreenshot(tag, true, true)
     -> AndroidScreenProcessService.startScreenshot()
        -> ScreenshotController.startScreenshot() barrier
           parallel workers:
             AndroidScreenshotService
               -> UiAutomation.takeScreenshot()
               -> temporary <screenName>.png
               -> hash(image + tag + buildLabel + locale + currentTimeMillis)
               -> rename to <hash>.png
             AndroidStringLinkService
               -> AndroidHierarchy(all visible windows)
               -> recursive AccessibilityNodeInfo traversal
               -> parseDeviceDump(text + descriptions)
               -> StringLinkageUtils.decipherMessageId(value)
             AndroidScreenMetadataService
               -> focused activity/package/app version/device/build/order/time
        -> AndroidScreenProcessService.saveScreenMetadata()
           -> Metadata(..., appId, avikTexts.toSet())
           -> FileWriter(<hash>.json).use/close
  -> QiraV2CaptureArtifacts.enrichMetadataWithComposeMessageIds()
     -> latest screenHash
     -> read <hash>.json
     -> append missing Qira accessibility nodes
     -> QiraV2SlapMessageIdIndex:
          decode message IDs from Qira values-en-rXM Compose catalog by stringId
     -> QiraV2ComposeStrings:
          resolve same stringId in current locale
     -> match localized visible value + exact bounds to Qira accessibility node
     -> merge messageIds
     -> write <hash>.json.qira-v2.tmp
     -> same-directory rename over <hash>.json
  -> QiraV2SlapTextDump validation
  -> QiraUiDumper XML + inventory

Workbench collection/import
  AndroidExecution.pullOutputFiles()
    /sdcard/Pictures/Screenshots/Avik/<script>/
      -> executionDir/<locale>/<script>/
  AvikJsonParser recursively parses every *.json
  AvikExecutionResultsImporter
    -> ScriptEntity(appId/script/locale/run/device/app version)
    -> ScreenEntity(hash/name/order/dimensions)
    -> ScreenStringEntity(text/bounds) + MessageIdEntity

Upload / registration
  ScreenInfoGenerator
    -> Mtext(text, "[l,t][r,b]", message_ids)
    -> ScreenInfo(name, registration hash, screenhash, app_id, script, ...)
  ScreensAPI
    -> multipart field enable_info = ScreenUploadInfo JSON
    -> Review server screen-enable endpoint
  ScreenFilesAPI
    -> upload PNG/audio to GCS
    -> skip FileType.METADATA

Review server
  updateScreens.generateScreenObject()
    query key: name + build_label + mapped app name + locale
    screenshot object: build/locale/script/<screenhash>.png
    mtexts: submitted records unchanged
    replace same object or append screenshot history
  screenScreenidGET
    -> latest screenshot's mtexts

Review Angular client
  Screenshot.deserialize()
    -> retain only mtexts with messageIds
  ScreenMetadataService.screenHasLinks()
    -> task string-link enabled + at least one valid-ID/nonzero-bounds record
  ScreenReviewContainerComponent
    -> source screenshot only
    -> scale [l,t,r,b] by rendered/natural image ratios
    -> absolute .messageIdLayer per retained record
```

## 4. Capture API variants and proven behavior

### Generic AViK APIs

Source: `workbench/avik-clients/android/.../AvikHandler.kt`.

| Variant | Defaults/behavior | Qira scope use |
|---|---|---|
| `takeScreenshot(name)` | `includeText=true`, `includeDescription=false`, display `0` | Generic scripts; not the current V2 helper |
| `takeScreenshot(name, includeText, includeDescription)` | Explicit hierarchy attributes | Both qira helpers ultimately use this |
| `takeScreenshot(name, marqueeResources)` | Default text, descriptions off; marks configured resources | Not used by scoped Qira calls |
| Physical-display overloads | Captures display `0` with UiAutomation or nonzero display with `screencap -d` | Scoped Qira calls use display `0` |
| Audio overloads | Plays/records audio around capture and renames audio to the screen hash | Voice validation only, not SCOPE tags |
| Bitmap overload | Uses provided bitmap and forces text off | Not used by SCOPE tags |
| `takeScreenshotByImage` | Imports an existing image file | Not used by SCOPE tags |
| `takeScreenshot(AvikScreenshotAction)` | Conditional/locale and scrolling orchestration | Not used by SCOPE tags |
| `scrollAndTakeScreenshot(...)` | Emits indexed scrolling captures through the action handler | Not used by SCOPE tags |

All direct variants enter the same controller barrier. `ScreenshotController` wakes three workers and waits until all three finish. Metadata serialization occurs after that barrier, making the public capture call synchronous in the current source.

### Qira helper variants

| Helper | Flags | Failure semantics | Artifact behavior |
|---|---|---|---|
| `BaseQiraCaptureScript.takeScreenshot(suffix)` | Reads `QiraConfig`; current defaults are text `true` except pseudo-locales default `false`, description `false` | Retries twice; logs and continues without image after final failure; UI dump only when config says so | Builds `<prefix>_<suffix>`; stock AViK JSON only |
| `QiraV2ScreenshotSink.capture(fullName)` | Interface only | Defined by entrypoint | Decouples flows from capture implementation |
| `QiraV2CaptureArtifacts.captureSlapScreenshot(fullName)` | Forces `true, true`; ignores QiraConfig capture flags | Retries twice; throws if screenshot failed or the later all-window SLAP dump is empty | Stock JSON, then qira_v2 in-place enrichment, then unconditional Qira UI dump |
| `QiraV2HomeOnboardingFlow.captureStep(...)` | V2 sink | Requires catalog/resource/message-ID evidence before capture | Used for stable onboarding controls |
| `captureStepBestEffort(...)` | V2 sink | Logs catalog evidence but does not require it | Used when a transient/optional surface is preserved |
| `captureSystemDialogBestEffort(...)` | V2 sink | Swallows capture exceptions | Supplementary Android-owned permission surfaces |
| V2 zone script `takeScreenshot(suffix)` overrides | V2 forced path | Inherits V2 throw behavior | Creator, Knowledge, Chat History, Settings |

Additional proven details:

- `QiraV2SlapTextDump.dumpVisibleText(includeDescription, ...)` currently sets `effectiveIncludeDescription=true`; its parameter does not disable descriptions.
- Its nonempty check is across all windows, not target-package scoped. System/launcher strings can satisfy it even if the Qira window contributes no records.
- Exact duplicate `AvikText` objects are collapsed by `toSet()` during metadata serialization; parent and child records with different text/bounds remain.
- The qira_v2 metadata replacement is same-directory and hash-preserving. It does not create a second metadata filename.

## 5. Scoped tag-to-call-site map

Legend:

- **V2 sink**: flow passes the full tag to `QiraV2CaptureArtifacts`.
- **V2 override / v1 base**: the call site is in a shared legacy script. A qira_v2 runtime class dispatches to its overridden V2 helper; a qira v1 entry dispatches to `BaseQiraCaptureScript.takeScreenshot`.

### Home and onboarding

Source: `avik-android/src/main/java/avik/qira_v2/utils/QiraV2HomeOnboardingFlow.java`.

| SCOPE tag | Current call site | Route/guard |
|---|---:|---|
| `MotorolaQiraHome_Home` | 1609-1612 | V2 sink after feature-grid/bubble-bar Home verification |
| `MotorolaQiraHome_Onboarding_Start` | 430-457, capture at 443 | V2 sink after ID-backed Start evidence |
| `..._IntroArrow` | 149-159 via `captureStep` | Strict catalog evidence |
| `..._Language` | 188-198 and fallback 666-668 | Strict or best-effort capture depending discovered surface |
| `..._DeviceAssurance` | 209-216 via `advanceFooterCardIfVisible`; sink in shared helper 736+ | Stable Compose catalog entry |
| `..._ContinueAs` | 2289-2308 | Best-effort capture of Continue-as or localized sign-in sheet |
| `..._Acknowledge` | 231-250 and fallback 633-638 | Strict/best-effort catalog path |
| `..._Acknowledge_Scrolled` | 243-247 and 637 | Always emits once when acknowledgement path is used |
| `..._Permissions` | 939-970 | Captures permission panel before enabling master toggle |
| `..._ContextualReadingPermission` | 1325-1328 | V2 sink when contextual-reading dialog is detected |
| `..._ContextualReadingPermissionScrolled1` | 1342-1344, `pass=1` | Dynamic fixed three-pass sequence |
| `..._ContextualReadingPermissionScrolled2` | 1342-1344, `pass=2` | Same |
| `..._ContextualReadingPermissionScrolled3` | 1342-1344, `pass=3` | Same |
| `..._ContextualReadingPermissionAccept` | 1446-1449 | Immediately before ID-backed Enable action |
| `..._PermissionsToggleEnabled` | 968-972 | After accessibility verification of enabled toggles |
| `..._PermissionsScrolled1` | 1255-1258 | Requires changed resource-backed viewport signature |
| `..._PermissionsScrolled` | 1274-1278 | Requires final viewport distinct from Scrolled1 |
| `..._AndroidLocationPermission` | 1644-1647 | Android permission-controller surface |
| `..._AndroidLocationPermissionPrecise` | 1647-1649 | Same prompt after selecting precise when available |
| `..._AndroidSystemPermission` | 1658-1662 | Stable permission-controller allow-resource path |

Entrypoints reaching these calls:

- Required Workbench-facing current wrapper: `avik.qira.scripts.MotorolaQiraHome_Onboarding_Start` (untracked).
- Compatibility shim: `avik.qira.scripts.MotorolaQiraHomeCapture` (staged deletion plus untracked replacement).
- Original implementation entry: `avik.qira_v2.scripts.MotorolaQiraHome_Onboarding_Start` (untracked).
- Both current V2 masters.

### Focus Zone affected tags

Source: `avik-android/src/main/java/avik/qira_v2/utils/QiraV2FocusZoneFlow.java`; all are V2-sink calls.

| Tags | Call site(s) | Guard/state |
|---|---:|---|
| `MotorolaQiraFocusZone_BubbleBar` | 155-163 | Bubble bar verified |
| `..._FocusZone_Slide_1` through `_Slide_5` | 199-245; capture at 221 | Dynamic page number from settled carousel accessibility text |
| `..._Chat_Onboarding` | 301-312 | Chat intro or recovered composer |
| `..._Chat_Composer` | 322-328 | Captured before missing-composer failure |
| `..._Chat_Composer_Input` | 330-336 | Exact prompt verified |
| `..._Chat_Thinking` | 354-356 or canonical fallback 395-403 | Resource-backed streaming state or verified response transition |
| `..._Chat_Processing` | 359-373 or canonical fallback 405-414 | Resource-backed processing state or verified response transition |
| `..._Chat_Answer` | 376-417 | Completed answer action row |
| `..._Chat_Result` | 430-448 | Result probe or final verified answer |
| `..._Live_AndroidMicrophonePermission` | 451-457, common sink at 526-536 | Only when permission controller is foreground |
| `..._Live_Onboarding` | 458-465 | Live intro/agreement surface reached |
| `..._Live_Agreement` | 466-475 | Stable agreement/share-screen state |
| `..._Live_ShareScreen` | 478-480 | Message ID or SLAP surface |
| `..._Live_Active` | 486-489 | After runtime-permission handling |
| `..._Live_EnablePermission` | 503-513 | Only if contextual Action Core permission CTA appears |
| `..._CatchMeUp_Onboarding` | 562-566 | First-run path only |
| `..._CatchMeUp_Agreement` | 568-572 | First-run path only |
| `..._CatchMeUp_ManageApps` | 597-603 | Compose `cmu_edu_manage_apps` reached |
| `..._PayAttention_Onboarding` | 632-643 | Captured only when intro is actually visible |
| `..._PayAttention_Agreement` | 649-651 | Agreement visible before consent |
| `..._PayAttention_ByProceeding` | 655-663 | Consent text reached |
| `..._PayAttention_Recording` | 680-682 | Consent closed and recording active |
| `..._PayAttention_Summary` | 696-700 | Summary tab selected |
| `..._PayAttention_Transcript` | 701-703 | Tab selection succeeded |
| `..._PayAttention_AudioRecording` | 709-711 | Tab selection succeeded |

Entrypoints:

- Current Workbench-facing: `avik.qira.scripts.MotorolaQiraFocusZoneCaptureV2`.
- Original: `avik.qira_v2.scripts.MotorolaQiraFocusZoneCapture`.
- Current V2 master uses the original Focus class internally.
- Legacy `avik.qira.scripts.MotorolaQiraFocusZoneCapture` has separate v1 call sites and routes through the base helper; it does not implement the V2 Chat Thinking/Processing/Result state model.

### Creator Zone affected tags

Shared call sites: `avik/qira/scripts/MotorolaQiraCreatorZoneCapture.java`. The qira_v2 subclass overrides `takeScreenshot` at lines 153-162 and requires verified canonical surfaces. The qira v1 class uses the base helper.

| Tags | Current call site(s) |
|---|---:|
| `..._Onboarding_InformationQuota` | V2 corrected sequence: qira_v2 script 85-101 |
| `..._Onboarding_1_CreatorZone` | V2 script 104-105 |
| `..._Onboarding_2_ImaginationRunFree` | V2 script 107-117 |
| `..._Onboarding_3_MakeItYourOwn` | V2 script 119-129 |
| `..._CreatorHome_Grid` | V2 script 137-143 |
| `..._CreateImage_Composer` | shared script 709-724 |
| `..._CreateImage_QuotaInfoPopup` | 1076-1106 |
| `..._CreateImage_Styles` | 736-745 |
| `..._CreateImage_StyleFantasy` | 829-848; strict failure before mis-tagging |
| `..._CreateImage_PromptReady` | 850-862 |
| `..._CreateImage_Generating_Preparing` | strict 986-999; v1/fallback 908 and 1012 |
| `..._CreateImage_Generating_Generating` | strict 986-999; v1/fallback 913 and 1013 |
| `..._CreateImage_GeneratedImage` | 919, 930-942, fallback 1014 |
| `..._EditImage_ConfirmUsage` | 1131-1157 |
| `..._EditImage_Editor` | 1222-1249 |
| `..._CreateAvatar_Main` | 1266-1309 |
| `..._CreateAvatar_PreviewConfirm` | 1384-1424 |
| `..._CreateSticker_Main` | 1484-1492 |
| `..._CreateSticker_Templates_Slide2` | 1494-1502, `slide=2` |
| `..._CreateSticker_Templates_Slide3` | 1494-1502, `slide=3` |
| `..._Scribble_CanvasActive` | strict normal path 1547-1553; v1 fallback 1530-1538 |
| `..._Scribble_ExitPopup` | 1555-1576; v1 fallback 1538 |
| `..._StyleSync_PostPicker` | 1588-1637 |
| `..._CreatorHome_ViewMore` | 221-265, captures at 248 or 265 |

The shared script also contains v1 "best available" fallback rows at 534-568. In strict qira_v2 mode, canonical surface failures are raised instead of silently producing those fallback rows.

### Chat History affected tags

Shared source: `avik/qira/scripts/MotorolaQiraChatHistoryCapture.java`; V2 override is in `avik/qira_v2/scripts/MotorolaQiraChatHistoryCapture.java:52-62`.

| Tag | Call site |
|---|---:|
| `MotorolaQiraChatHistory_Main_ChatList` | 222-256 |
| `..._Main_ManageChats` | 276-294 |
| `..._Main_ManageChats_Selected` | 301-334 |
| `..._Main_ManageChats_DeleteAction` | 336-390 |
| `..._Detail_MoreOptionsMenu` | 440-480 |

### Knowledge known-good controls

Shared source: `avik/qira/scripts/MotorolaQiraKnowledgeCapture.java`; V2 override is at `avik/qira_v2/scripts/MotorolaQiraKnowledgeCapture.java:52-68`.

| Tag | Call site |
|---|---:|
| `MotorolaQiraKnowledge_Onboarding_1_Knowledge` | 177-206 |
| `..._Onboarding_2_Permissions` | 209-230 |
| `..._Main_FileList` | 234-246 |
| `..._Main_CategoriesDropdown` | 302-321 |
| `..._Main_TagsDropdown` | 331-350 |
| `..._Main_MoreOptionsMenu` | 362-374 |
| `..._Main_ManageSettingsPopup` | 375-400 |
| `..._Main_FabMenu` | 419-431 |
| `..._Main_CreateMemory_Dialog` | 419-450 |

### Settings known-good controls

Shared source: `avik/qira/scripts/MotorolaQiraSettingsCapture.java`; V2 override is at `avik/qira_v2/scripts/MotorolaQiraSettingsCapture.java:46-56`.

| Tag(s) | Call site |
|---|---:|
| `MotorolaQiraSettings_Drawer_Menu` | 118-119 |
| `..._Settings_Default` | 121-128 |
| `..._Settings_Account` | suffix defined 168-171; emitted by `captureOption` at 347 or 380 |
| `..._Settings_Devices` | 174-177; emitted 347/380 |
| `..._Settings_SmartConnect` | 180-183; emitted 347/380 |
| `..._Settings_Language` | 190-193; emitted 347/380 |
| `..._Settings_LaunchOptions` | 196-199; emitted 347/380 |
| `..._Settings_Voice` | 202-205; emitted 347/380 |
| `..._Settings_LockScreenDisplay` | 208-212; emitted 347/380 |
| `..._Settings_SyncData` | 219-222; emitted 347/380 |
| `..._Settings_PersonalizedAnswers` | 229-233; emitted 347/380 |
| `..._Settings_CatchMeUp` | 236-239; emitted 347/380 |
| `..._Settings_Connectors` | 242-245; emitted 347/380 |
| `..._Settings_About` | 252-255; emitted 347/380 |
| `..._Settings_SupportPage` | 258-262; emitted 347/380 |
| `..._Settings_LegalNotices` | 265-268; emitted 347/380 |
| `..._Settings_Feedback` | 275-278; emitted 347/380 |

## 6. Hierarchy, Compose, SLAP, and ID resolution

### Stock hierarchy path

`AndroidHierarchy`:

1. Enables `FLAG_RETRIEVE_INTERACTIVE_WINDOWS`.
2. Reads `UiAutomation.windowsOnAllDisplays`.
3. For the requested display, recursively wraps every accessibility node as `HierarchyAccessibilityNode`.
4. A node is available when visible, nonempty, and its top/left begin inside the display.
5. `parseDeviceDump` clips bounds to display size, filters zero-area post-clip records, and emits:
   - node text;
   - node content description when enabled.
6. Every emitted value is passed to `StringLinkageUtils.decipherMessageId`.
7. An exception in one window is caught by `AndroidHierarchy.getAvikTextsFromWindow`, which drops that entire window's records.

The current unstaged foreground-window filter is off by default and explicitly cleared by qira_v2. It is therefore not active for the current V2 path.

### Compose behavior

- No Compose testing API or unmerged semantics tree is collected.
- Compose appears only through Android accessibility semantics.
- If Compose exposes one merged parent description/text, stock AViK emits that parent's bounds. If it also exposes child nodes, both parent and children are emitted.
- `includeDescription=true` can add a large parent description in addition to leaf text.
- The parser clips rather than rejects out-of-screen bounds; this is why serialized bounds are normally in range even when source accessibility bounds extend beyond the image.

### qira_v2 additions

`QiraV2SlapTextDump`:

- Uses stock `AndroidHierarchy` with descriptions forced on.
- On RTL only, directly traverses all accessibility windows to recover text/descriptions lost when `decipherMessageId` throws. Recovered nodes intentionally have no message IDs.
- Direct recovery deduplicates by bidi-stripped text plus a coarse 24-pixel center bucket.

Compose message-ID enrichment:

- `QiraV2SlapMessageIdIndex` reads the authoritative `values-en-rXM/strings.commonMain.cvr`.
- It decodes each catalog value with `MessageIdParser` and indexes `Compose stringId -> message IDs`.
- `QiraV2ComposeStrings` resolves the same string ID from current-locale folders, then language-only, then default.
- Visible localized strings are matched after bidi removal/diacritic normalization and placeholder-aware value comparison.
- Only nodes whose text/bounds key exists in the current Qira accessibility snapshot are enriched. Existing native message IDs are retained.
- Missing direct Qira accessibility nodes are appended with empty message IDs before resolution.

Selector/string ID resolution:

- `QiraStrings` builds an exhaustive `normalized English R.string value -> Android resource ID` index, cached by Qira APK version code.
- Current resolver order is runtime `R.string` scan, template substitution, explicitly registered resource names, empty static catalog, then qira_v2 Compose supplemental resolver.
- The Compose supplemental resolver uses English Compose values as the pivot to stable string IDs, then reads current-locale values.
- `QiraV2SlapCatalog` contains audited onboarding string IDs/message IDs for strict selector evidence.

## 7. Metadata, flush, overwrite, and pairing

### Serialization contract

`Metadata` contains tag, build label, script, locale, hash, dimensions/orientation, order, focused package/activity, app ID/version, device fields, timestamp/window type/watermark, and the `avikTexts` set. It does not contain:

- execution/run ID;
- Git commit;
- AViK framework version;
- density;
- Core or Action Core package versions as independent run fields.

### Flush behavior

- Three capture workers complete before `saveScreenMetadata`.
- `FileWriter(...).use` closes/flushed the initial JSON.
- qira_v2 reads and atomically replaces that JSON before returning.
- Master `drainPendingAvikWork()` waits are conservative; current source does not require them to flush an already-returned capture.

### Naming and overwrite behavior

- Temporary image: `<screenName>.png`.
- Final image hash includes image bytes, screen name, build label, locale, and current time.
- Final pair: `<hash>.png` + `<hash>.json`.
- Repeating a tag produces a new hash and normally leaves both pairs.
- qira_v2 replaces only the JSON for the most recently exposed screenshot hash.
- Workbench identifies a local screen by `(screen hash, script execution)`, not by tag, so duplicate tags can coexist locally.
- Review server identifies the logical screen by `(tag, build, mapped app, locale)` and keeps screenshot history; same object paths are replaced, new object paths appended.

### Diagnostic artifacts

`QiraUiDumper` writes:

```text
/sdcard/avik/qira-ui-dumps/<sanitized-tag>/<yyyyMMdd-HHmmss>.xml
/sdcard/avik/qira-ui-dumps/<sanitized-tag>/<yyyyMMdd-HHmmss>.txt
```

These are separate from the AViK screenshot folder and are not pulled by the standard Workbench `AndroidExecution.pullOutputFiles()` path.

## 8. Workbench import, grouping, and upload

### Import

- Workbench passes `screen_folder=/sdcard/Pictures/Screenshots/Avik`.
- Device output is `<screen_folder>/<script>/<hash>.*`.
- Pull result is moved to `<executionDir>/<locale>/<script>`.
- `AvikJsonParser` recursively parses every JSON.
- The importer creates/fetches one `ScriptEntity` from metadata and execution times, then one `ScreenEntity` and child string/message-ID rows per metadata object.
- It does not explicitly validate the matching PNG before database creation.

### Current app ID paths

Committed behavior:

```text
appId = scriptName.split(".")[1]
```

Thus `avik.qira_v2...` imports as `qira_v2`.

Current unstaged/untracked behavior:

- `ScreenshotProperties.appIdOverride` and `AvikProperties`'s `app_id` reader are unstaged.
- `QiraV2InstrumentationDefaults` is untracked and sets `app_id=qira`.
- Required qira wrapper is untracked.
- `AppIdAlias.kt` is untracked; `Round.kt` and `ScriptController.kt` are unstaged. They canonicalize old `qira_v2` local executions into the `qira` tile/query.
- This alias changes local Workbench display/query, not historical Review server `app_id`.

### Upload registration

1. `ScreenInfoGenerator` converts each database string to:
   - text;
   - bound string `[left,top][right,bottom]`;
   - message ID list;
   - marquee flag.
2. It computes a deterministic registration hash from tag/build/locale/app ID and carries the image hash separately as `screenhash`.
3. `ScreensAPI` posts `ScreenUploadInfo` as multipart form field `enable_info`, in chunks of 20.
4. The Review server upserts logical screen records and returns Review URLs.
5. `ScreenFilesAPI` uploads screen/attachment files to GCS. `FileType.METADATA` is explicitly skipped.

The standard offline export similarly packs screenshot files plus a top-level `execution.json`; it does not pack each `<hash>.json`. Workbench database `ScreenStringEntity` rows are therefore the durable source of uploaded `mtexts`.

## 9. Review App ingestion and rendering

Server:

- `Screens.js` stores `mtexts` as `{message_ids, mtext, bound, is_marquee}`.
- `updateScreens.js` maps upload data directly into a screenshot history entry.
- Screen lookup/upsert key is tag, round/build, mapped app name, and locale.
- The image object is `buildLabel/locale/script/screenhash.png`.
- `screenScreenidGET.js` returns the latest screenshot's `mtexts`.

Client:

- `MetaText` parses `[l,t][r,b]` into four numbers.
- `Screenshot.deserializeProperty` retains only records with nonempty message IDs.
- String-link display is enabled only when the task enables string linking and at least one source record has IDs and positive dimensions.
- `ScreenReviewContainerComponent` builds overlays only for `ScreenshotType.SOURCE`.
- Coordinates are independently scaled by rendered/natural width and height and floored.
- No client-side parent/leaf classification, overlap suppression, or image-bound clipping occurs.

Therefore a correct API response containing a near-full-screen parent record will render a near-full-screen parent overlay. The renderer is not the stage that creates that geometry.

## 10. qira legacy versus qira_v2

| Concern | qira legacy | qira_v2 current working tree |
|---|---|---|
| Source ownership | `avik.qira.*` committed plus large unstaged edits | `avik.qira_v2.*` entirely untracked |
| Screenshot helper | Base helper, config-driven flags | Forced text+description, metadata enrichment, fail-loud SLAP validation |
| Home | Committed legacy flow is staged for deletion | Untracked qira shim delegates to V2 Home flow |
| Other zones | Legacy scripts/pages | Reuse legacy flow classes through strict subclasses and override capture |
| Message IDs | Inline marker decoding only | Inline decoding plus Compose catalog enrichment |
| RTL parser recovery | None | Direct accessibility merge for RTL |
| Workbench app ID | Derived from script package (`qira`) | Forced `qira` in current defaults; historical runs were `qira_v2` |
| Workbench-facing entry | Legacy class names | Required wrapper `avik.qira.scripts.MotorolaQiraHome_Onboarding_Start` |
| Master | Legacy six-step master | V2 six-step master plus Workbench wrapper |

## 11. Locale and Tier configuration

### Runtime locale fields

- `locale`: AViK metadata/Workbench review locale (`AvikProperties.localeInReview`).
- `qira.locale`: Qira automation locale used for per-app locale and instrumentation `Locale`.
- Workbench `AndroidExecution` currently passes `locale`, but not `qira.locale`.
- Current `QiraV2InstrumentationDefaults.apply()` defaults missing `qira.locale` to `en-XM`.
- The repository's dedicated locale runner passes both fields with the same tag, plus `qira.applySystemLocale=false` after an explicit device-wide locale switch.

This distinction is material: a Workbench run that selects a target locale but supplies no `qira.locale` follows the current V2 defaulting code, while the dedicated batch runner explicitly aligns device, Qira, and metadata locales.

### Repository batch lists

Operational Tier-1 batch (`.scratch/run-qira-v2-tier1-locales.bat`):

```text
en-XM en-US en-GB en-IN es-US es-ES fr-FR it-IT de-DE
pt-BR pl-PL ro-RO ja-JP zh-CN ar-EG
```

Operational Tier-2 batch (`.scratch/run-qira-v2-tier2-locales.bat`):

```text
nl-NL el-GR hu-HU pt-PT fr-CA sk-SK bg-BG cs-CZ
sr-Latn-RS sr-Cyrl-RS sv-SE da-DK fi-FI hr-HR uk-UA ru-RU hi-IN
```

Workbench does not hard-code these lists. `getLocalesByTierNumber()` reads a `Tier <n>` alias from the local Workbench database and intersects it with task locales; if no alias exists, it returns all task locales. The Round1 export explicitly reports `tier=UNSPECIFIED` and `No per-locale tier field in current export` for every locale, so repository batch labels cannot be treated as proven Workbench tier membership.

The Round1 evidence contains 26 locales:

```text
ar-EG, bg-BG, cs-CZ, da-DK, de-DE, el-GR, en-GB, en-IN, en-US, en-XM,
es-ES, es-US, fr-CA, fr-FR, hi-IN, id-ID, it-IT, ja-JP, pl-PL, pt-BR,
pt-PT, ro-RO, ru-RU, sk-SK, uk-UA, zh-CN
```

This set differs from the union of the two repository batch defaults (for example, it contains `id-ID` and omits several Tier-2 batch entries).

## 12. Authoritative most recent completed AViK execution

### Selection method

The selection is based on:

1. Workbench `execution.json` records, not directory or log filename order.
2. `avik_round_discovery.json` reconciliation of 26 execution IDs.
3. Explicit execution start/end/duration.
4. Ordered captures with valid PNG integrity.
5. A final Settings Feedback capture at order 104 immediately before execution end.
6. Workbench database/payload reconciliation with Review URLs.

The export's global execution window ends at this execution's end time; no later execution record exists in that evidence set.

### Run record

| Field | Value/evidence |
|---|---|
| Execution/run ID | `1cc5e264-bd1d-47e9-9371-e744995b531c` |
| Round/build label | `Qira_Horizon_Tier1_Tier2_Round1` |
| Start UTC | `2026-07-18T00:23:52.755Z` |
| End UTC | `2026-07-18T00:41:53.275Z` |
| Duration | `1,080,520 ms` |
| Locale | `uk-UA` (LTR); campaign locale list is the 26-locale set above |
| Script | `avik.qira_v2.scripts.MotorolaQiraMasterCapture` |
| appId | `qira_v2` |
| Qira APK | `QT-01.01.550`, version code `460101550` |
| Core version | **Unavailable.** Neither `Metadata`, the execution export, nor the reconciled Workbench schema has an independent Core package/version field. |
| Action Core version | **Unavailable for this execution.** Order 39 is package `com.motorola.actioncore`, but Workbench payload displays the script-level Qira version because per-screen app versions are discarded during import. Separate non-authoritative bg-BG metadata from 2026-07-16 records Action Core `3.00.1213` / `353001213`; it is not assigned to this run. |
| Git commit | **Unavailable.** No commit field exists in Android metadata or Workbench execution export. Current HEAD and the run's uncommitted implementation cannot be equated. |
| Device | `motorola razr fold 2026`; `android_nosdcard` |
| Device build | `blanc_gu-userdebug 16 W3WB36.36-48-5 fb58d2-883e52 intcfg,test-keys MW-445` |
| Resolution | `1080 x 2520` for all run PNGs |
| Orientation | metadata `0`, which current screenshot utility defines as portrait (`height >= width`) |
| Density | **Unavailable.** Not serialized; all 104 referenced metadata/dump JSON files are absent from the offline export. |
| Capture framework version | **Unavailable in run metadata.** Current project config pins Android client `1.0.0-543`, screenshot service `1.0.0-540`, common `1.0.0-539`, interaction `1.0.0-546`, utils `1.0.0-406`; no run manifest proves that exact dependency set. |
| Capture count | 104 screens, 104 unique tags, 104 valid PNGs, no duplicate locale/tag group |
| Baseline delta | Missing `MotorolaQiraFocusZone_Live_Camera`; extras `..._Live_EnablePermission` and `MotorolaQiraHome_Onboarding_DeviceAssurance` versus the 103-tag en-XM discovery baseline |
| Final capture | `MotorolaQiraSettings_Settings_Feedback`, order 104, `2026-07-18T00:41:47Z` |
| Artifact directory | `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\input\current_export\executions\Qira_Horizon_Tier1_Tier2_Round1\2026-07-18_04.05.36.283\uk-UA\avik.qira_v2.scripts.MotorolaQiraMasterCapture` |
| Offline export state | `uploadState=NONE`, screen flags false in the export snapshot |
| Workbench/Review state | Reconciled database payload marks screens reviewable and supplies Review URLs; the export/database screen sets have zero mismatch |

The apparent upload-state disagreement is preserved rather than normalized: the offline execution snapshot says `NONE`, while the later Workbench database extract contains reviewable rows and Review URLs.

## 13. Existing build/test/runtime reports and failures

### Build

- `.scratch/compile-rtl.log`: `BUILD SUCCESSFUL in 28s`; Java/Kotlin compile.
- `.scratch/compile-act.log`: `BUILD SUCCESSFUL in 1m 1s`.
- `.scratch/compile-check3.log`: `BUILD SUCCESSFUL in 24s`.
- `.scratch/build.out`: assemble/debug Android test `BUILD SUCCESSFUL in 37s`.
- `.scratch/build11.log`: assemble/debug Android test `BUILD SUCCESSFUL in 36s`.
- `build/reports/problems/problems-report.html`: Gradle deprecation warnings; no test-result report.
- No files exist under `build/test-results`, and no standalone unit/instrumentation HTML test report was found.

The build logs include deprecation notices and, in older logs, AGP/build-tools compatibility warnings. The current unstaged tree changes AGP from committed `9.0.1` to `8.6.1`; AViK dependency versions are unchanged from HEAD.

### Runtime failures retained in the repository

- `.scratch/runs/sol-validation-bg-BG-20260716.log`: progressed through master step 6/6 to Settings SmartConnect, then `INSTRUMENTATION_RESULT: shortMsg=Process crashed` and `INSTRUMENTATION_CODE: 0`; not completed.
- `.scratch/runs/onb-uk-UA-tier2-20260715.log`: `IllegalStateException: Unable to advance Qira onboarding to home`; not completed.
- `.scratch/runs/onb-en-XM-validate.log`: same onboarding-to-home failure.
- `.scratch/runs/fz-ar-EG-validate.log`: Catch Me Up manage-apps sheet not reached.
- `.scratch/runs/fz-de-DE-validate.log`: Live agreement not reached.
- `.scratch/runs/master-en-XM-2.log`: explicit successful older full run (`qira_v2 master capture suite finished; all sub-flows OK`, `OK (1 test)`), but its screenshot JSON set is not retained.

### Round1 QA status

The external Workbench QA package records:

- 26 locales;
- 103 qualified en-XM tags;
- 94.40% aggregate capture coverage;
- overall recommendation `FAIL`;
- validation state `INCOMPLETE`;
- 2,566 database screens with zero export/database mismatch;
- 114 screens without message IDs;
- 2,566 missing per-screen JSON files in the offline export;
- incomplete locale runs, notably `zh-CN` with 15 captures and `ar-EG` with 71.

## 14. Material affected-file map by Git state

### Committed HEAD foundation

- `workbench/avik-clients/android/.../AvikHandler.kt`
- `.../AndroidScreenProcessService.kt`
- `.../AndroidScreenshotService.kt`
- `.../AndroidScreenMetadataService.kt`
- `.../AndroidStringLinkService.kt`
- `workbench/avik-clients/screenshotService/...`
- `workbench/avik-workbench-common/.../ParseSLAPDump.kt`
- Workbench importer/entity/upload code
- Review App server/client code
- qira legacy scripts/pages/utilities (their current contents may additionally be unstaged)

### Staged

- `D avik-phone-horizon/avik-android/src/main/java/avik/qira/scripts/MotorolaQiraHomeCapture.java`

### Unstaged material implementation

- qira v1 pages/scripts and `QiraStrings.java`.
- `BaseQiraCaptureScript.java` adds V2 onboarding recovery only; its screenshot helper itself is unchanged from HEAD.
- `AndroidHierarchy.kt`: optional foreground-window scope.
- `AvikProperties.kt` and `ScreenshotProperties.kt`: `app_id` override.
- `Round.kt` and `ScriptController.kt`: qira/qira_v2 local grouping/query alias use.
- `gradle/libs.versions.toml`: AGP change only; AViK component versions unchanged.

### Untracked material implementation

- Entire `avik-android/src/main/java/avik/qira_v2/**`.
- Workbench-facing V2 wrappers under `avik/qira/scripts/*V2.java`.
- `avik/qira/scripts/MotorolaQiraHome_Onboarding_Start.java`.
- Re-created `avik/qira/scripts/MotorolaQiraHomeCapture.java`.
- `workbench/avik-workbench-entities/.../AppIdAlias.kt`.

### Clean Review App source

- `reviewapp/server/app/services/screenFile/screenEnable/updateScreens.js`
- `reviewapp/server/app/model/Screens.js`
- `reviewapp/server/app/services/screens/screenScreenidGET.js`
- `reviewapp/client/src/app/shared/screen/{screenshot,meta-text}.ts`
- `reviewapp/client/src/app/shared/screen-metadata.service.ts`
- `reviewapp/client/src/app/screen-review/screen-comparison/screen-container/*`

## 15. Pipeline-stage evidence map

| SCOPE stage | Authoritative inspection point |
|---|---|
| A/B | Page/flow guard immediately before each call site; run log/UI dump |
| C | `Capture Screen: <tag>` log and metadata tag/order |
| D/E | `AndroidHierarchy`, raw UiAutomator XML, accessibility direct dump |
| F | `parseDeviceDump`, parent/child `AvikText` geometry, Compose accessibility exposure |
| G | Controller three-worker barrier, `saveScreenMetadata`, closed FileWriter |
| H | Shared `<hash>` in Metadata and pair filenames |
| I | qira_v2 latest-hash JSON replacement; duplicate tag/hash inventory |
| J | `ScreenUploadInfo`/`ScreenInfo`, GCS related-file list, Review registration response |
| K | Workbench importer DB rows and Review server `updateScreens` |
| L | Review API latest `mtexts` versus Angular scaled overlay |
| M | device locale, `locale`, `qira.locale`, per-app locale logs |
| N | message-ID/static-ID classification; dynamic records generally remain unlinked unless catalog matching links them |
| O | permission-controller or Action Core package/activity and best-effort/system-specific capture helper |
| P | Git-state/run-provenance differences documented above |

## 16. Evidence paths

Repository evidence:

- `C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon\artifacts\avik-string-linking-investigation\SCOPE.md`
- `C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon\.cursor\skills\avik-qira-automation\HANDOFF.md`
- `C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon\.scratch\runs\master-en-XM-2.log`
- `C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon\.scratch\runs\sol-validation-bg-BG-20260716.log`
- `C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon\.scratch\runs\onb-uk-UA-tier2-20260715.log`
- `C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon\.scratch\round2_export\execution.json`
- `C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon\artifacts\avik-string-linking-investigation\before-after\forensic-pair-metrics.json`
- `C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon\build\reports\problems\problems-report.html`

Authoritative Workbench export/database QA evidence:

- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\input\current_export\execution.json`
- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\avik_round_discovery.json`
- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\avik_raw_capture_index.csv`
- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\workbench_screen_payload_index.csv`
- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\workbench_screen_strings.csv`
- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\input\workbench_api\workbench_db_extract.json`
- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\structured\report_metrics.json`
- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\structured\locale_summary.csv`

## 17. Proven mapping gaps

These are evidence gaps, not fix proposals:

1. No retained run artifact binds any capture to a Git commit.
2. No run manifest serializes the AViK dependency versions.
3. Density is absent.
4. Core and per-screen Action Core versions are absent after Workbench import.
5. Offline exports omit per-screen metadata JSON by design; the Round1 export also contains no QiraUiDumper XML/inventory.
6. Round1 tier membership is not serialized; all entries are `UNSPECIFIED`.
7. The newest completed run proves the historical `qira_v2` app/script identity, not the current required `qira` wrapper identity.
8. The current untracked/unstaged app-ID grouping path has compile evidence in local logs but no completed full Workbench import/Review execution matching the release contract.

## 18. Exact SCOPE tag inventory

This appendix removes any ambiguity introduced by abbreviated names in the tables above. Every exact SCOPE tag is assigned to its proven current call-site family.

### Home affected — V2 sink, `QiraV2HomeOnboardingFlow.java:1609-1612`

- `MotorolaQiraHome_Home`

### Focus Zone affected — V2 sink, `QiraV2FocusZoneFlow.java:144-717`

- `MotorolaQiraFocusZone_BubbleBar`
- `MotorolaQiraFocusZone_FocusZone_Slide_2`
- `MotorolaQiraFocusZone_FocusZone_Slide_3`
- `MotorolaQiraFocusZone_FocusZone_Slide_4`
- `MotorolaQiraFocusZone_FocusZone_Slide_5`
- `MotorolaQiraFocusZone_FocusZone_Slide_1`
- `MotorolaQiraFocusZone_Chat_Onboarding`
- `MotorolaQiraFocusZone_Chat_Composer`
- `MotorolaQiraFocusZone_Chat_Composer_Input`
- `MotorolaQiraFocusZone_Chat_Thinking`
- `MotorolaQiraFocusZone_Chat_Processing`
- `MotorolaQiraFocusZone_Chat_Answer`
- `MotorolaQiraFocusZone_Chat_Result`
- `MotorolaQiraFocusZone_Live_AndroidMicrophonePermission`
- `MotorolaQiraFocusZone_Live_Onboarding`
- `MotorolaQiraFocusZone_Live_Agreement`
- `MotorolaQiraFocusZone_Live_ShareScreen`
- `MotorolaQiraFocusZone_Live_Active`
- `MotorolaQiraFocusZone_Live_EnablePermission`
- `MotorolaQiraFocusZone_CatchMeUp_Onboarding`
- `MotorolaQiraFocusZone_CatchMeUp_Agreement`
- `MotorolaQiraFocusZone_CatchMeUp_ManageApps`
- `MotorolaQiraFocusZone_PayAttention_Onboarding`
- `MotorolaQiraFocusZone_PayAttention_Agreement`
- `MotorolaQiraFocusZone_PayAttention_ByProceeding`
- `MotorolaQiraFocusZone_PayAttention_Recording`
- `MotorolaQiraFocusZone_PayAttention_Summary`
- `MotorolaQiraFocusZone_PayAttention_Transcript`
- `MotorolaQiraFocusZone_PayAttention_AudioRecording`

### Creator Zone affected — shared legacy call sites with V2 override

Source families: `MotorolaQiraCreatorZoneCapture.java:221-265, 534-568, 709-1106, 1131-1637` and corrected V2 onboarding at `avik/qira_v2/scripts/MotorolaQiraCreatorZoneCapture.java:73-143`.

- `MotorolaQiraCreatorZone_Onboarding_InformationQuota`
- `MotorolaQiraCreatorZone_Onboarding_1_CreatorZone`
- `MotorolaQiraCreatorZone_Onboarding_2_ImaginationRunFree`
- `MotorolaQiraCreatorZone_Onboarding_3_MakeItYourOwn`
- `MotorolaQiraCreatorZone_CreatorHome_Grid`
- `MotorolaQiraCreatorZone_CreateImage_Composer`
- `MotorolaQiraCreatorZone_CreateImage_QuotaInfoPopup`
- `MotorolaQiraCreatorZone_CreateImage_Styles`
- `MotorolaQiraCreatorZone_CreateImage_StyleFantasy`
- `MotorolaQiraCreatorZone_CreateImage_PromptReady`
- `MotorolaQiraCreatorZone_CreateImage_Generating_Preparing`
- `MotorolaQiraCreatorZone_CreateImage_Generating_Generating`
- `MotorolaQiraCreatorZone_CreateImage_GeneratedImage`
- `MotorolaQiraCreatorZone_EditImage_ConfirmUsage`
- `MotorolaQiraCreatorZone_EditImage_Editor`
- `MotorolaQiraCreatorZone_CreateAvatar_Main`
- `MotorolaQiraCreatorZone_CreateAvatar_PreviewConfirm`
- `MotorolaQiraCreatorZone_CreateSticker_Main`
- `MotorolaQiraCreatorZone_CreateSticker_Templates_Slide2`
- `MotorolaQiraCreatorZone_CreateSticker_Templates_Slide3`
- `MotorolaQiraCreatorZone_Scribble_CanvasActive`
- `MotorolaQiraCreatorZone_Scribble_ExitPopup`
- `MotorolaQiraCreatorZone_StyleSync_PostPicker`
- `MotorolaQiraCreatorZone_CreatorHome_ViewMore`

### Chat History affected — shared legacy call sites with V2 override

Source: `MotorolaQiraChatHistoryCapture.java:222-480`.

- `MotorolaQiraChatHistory_Main_ChatList`
- `MotorolaQiraChatHistory_Main_ManageChats`
- `MotorolaQiraChatHistory_Main_ManageChats_Selected`
- `MotorolaQiraChatHistory_Main_ManageChats_DeleteAction`
- `MotorolaQiraChatHistory_Detail_MoreOptionsMenu`

### Home known-good controls — V2 sink

Source: `QiraV2HomeOnboardingFlow.java:133-250, 430-540, 939-970, 1255-1449, 1609-1662`.

- `MotorolaQiraHome_Onboarding_Start`
- `MotorolaQiraHome_Onboarding_IntroArrow`
- `MotorolaQiraHome_Onboarding_Language`
- `MotorolaQiraHome_Onboarding_DeviceAssurance`
- `MotorolaQiraHome_Onboarding_ContinueAs`
- `MotorolaQiraHome_Onboarding_Acknowledge`
- `MotorolaQiraHome_Onboarding_Acknowledge_Scrolled`
- `MotorolaQiraHome_Onboarding_Permissions`
- `MotorolaQiraHome_Onboarding_ContextualReadingPermission`
- `MotorolaQiraHome_Onboarding_ContextualReadingPermissionScrolled1`
- `MotorolaQiraHome_Onboarding_ContextualReadingPermissionScrolled2`
- `MotorolaQiraHome_Onboarding_ContextualReadingPermissionScrolled3`
- `MotorolaQiraHome_Onboarding_ContextualReadingPermissionAccept`
- `MotorolaQiraHome_Onboarding_PermissionsToggleEnabled`
- `MotorolaQiraHome_Onboarding_PermissionsScrolled1`
- `MotorolaQiraHome_Onboarding_PermissionsScrolled`
- `MotorolaQiraHome_Onboarding_AndroidLocationPermission`
- `MotorolaQiraHome_Onboarding_AndroidLocationPermissionPrecise`
- `MotorolaQiraHome_Onboarding_AndroidSystemPermission`

### Knowledge known-good controls — shared legacy call sites with V2 override

Source: `MotorolaQiraKnowledgeCapture.java:177-450`.

- `MotorolaQiraKnowledge_Onboarding_1_Knowledge`
- `MotorolaQiraKnowledge_Onboarding_2_Permissions`
- `MotorolaQiraKnowledge_Main_FileList`
- `MotorolaQiraKnowledge_Main_CategoriesDropdown`
- `MotorolaQiraKnowledge_Main_TagsDropdown`
- `MotorolaQiraKnowledge_Main_MoreOptionsMenu`
- `MotorolaQiraKnowledge_Main_ManageSettingsPopup`
- `MotorolaQiraKnowledge_Main_FabMenu`
- `MotorolaQiraKnowledge_Main_CreateMemory_Dialog`

### Settings known-good controls — shared legacy call sites with V2 override

Source: `MotorolaQiraSettingsCapture.java:97-380`.

- `MotorolaQiraSettings_Drawer_Menu`
- `MotorolaQiraSettings_Settings_Default`
- `MotorolaQiraSettings_Settings_Account`
- `MotorolaQiraSettings_Settings_Devices`
- `MotorolaQiraSettings_Settings_SmartConnect`
- `MotorolaQiraSettings_Settings_Language`
- `MotorolaQiraSettings_Settings_LaunchOptions`
- `MotorolaQiraSettings_Settings_Voice`
- `MotorolaQiraSettings_Settings_LockScreenDisplay`
- `MotorolaQiraSettings_Settings_SyncData`
- `MotorolaQiraSettings_Settings_PersonalizedAnswers`
- `MotorolaQiraSettings_Settings_CatchMeUp`
- `MotorolaQiraSettings_Settings_Connectors`
- `MotorolaQiraSettings_Settings_About`
- `MotorolaQiraSettings_Settings_SupportPage`
- `MotorolaQiraSettings_Settings_LegalNotices`
- `MotorolaQiraSettings_Settings_Feedback`

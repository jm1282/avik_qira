# Investigation Scope and Release Contract

## Authoritative baseline

- Structural baseline locale: `en-XM`
- Workbench-facing app ID: `qira`
- Workbench-facing onboarding entry point: `avik.qira.scripts.MotorolaQiraHome_Onboarding_Start`
- Preserve `qira.dumpUi=true`, `qira.includeScreenshotText=true`, and `qira.includeScreenshotDescription=true`
- Do not rename screen tags.

## Affected tags

- `MotorolaQiraHome_Home`
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
- `MotorolaQiraChatHistory_Main_ChatList`
- `MotorolaQiraChatHistory_Main_ManageChats`
- `MotorolaQiraChatHistory_Main_ManageChats_Selected`
- `MotorolaQiraChatHistory_Main_ManageChats_DeleteAction`
- `MotorolaQiraChatHistory_Detail_MoreOptionsMenu`

## Known-good controls

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
- `MotorolaQiraKnowledge_Onboarding_1_Knowledge`
- `MotorolaQiraKnowledge_Onboarding_2_Permissions`
- `MotorolaQiraKnowledge_Main_FileList`
- `MotorolaQiraKnowledge_Main_CategoriesDropdown`
- `MotorolaQiraKnowledge_Main_TagsDropdown`
- `MotorolaQiraKnowledge_Main_MoreOptionsMenu`
- `MotorolaQiraKnowledge_Main_ManageSettingsPopup`
- `MotorolaQiraKnowledge_Main_FabMenu`
- `MotorolaQiraKnowledge_Main_CreateMemory_Dialog`
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

## Required stage taxonomy

- A: Screen navigation did not reach target.
- B: Screen reached but not stable.
- C: Capture call never executed.
- D: Screenshot captured but hierarchy absent.
- E: Hierarchy existed but child strings were not exposed.
- F: Child strings existed but SLAP extraction collapsed them into a parent.
- G: Correct SLAP records generated but not flushed before completion.
- H: Screenshot and metadata received different names/tags.
- I: Metadata overwritten by another capture.
- J: Upload manifest omitted or mismatched metadata.
- K: AViK ingestion/indexing dropped mappings.
- L: Review API returned correct mappings but UI rendering was wrong.
- M: Locale-specific selector or navigation failed.
- N: Dynamic content incorrectly treated as static localizable text.
- O: System-owned permission UI followed a different capture path.
- P: Other evidence-backed stage.

## Release metrics

Required: 100% en-XM/Tier-1/Tier-2 tag coverage, 100% artifact pairing, 100% expected static localizable element linking, 100% expected authoritative ID resolution, zero invalid/out-of-screen bounds, zero parent-only whole-layout links, zero duplicate tags/unintended links, zero missing metadata, zero unexplained known-good regression delta, zero introduced compile/lint/test failures, zero unexplained runtime capture exceptions, and zero locale-dependent visible-text selectors in affected paths.

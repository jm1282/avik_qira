# AViK/Qira String-Linking Investigation Progress

Last updated: 2026-07-19 IST

## Completed

- Loaded the repository Qira automation rules and project handoff.
- Confirmed repository root `C:/Users/BLR-USER/Avik_repo/avik`.
- Recorded branch `master` and HEAD `726751cda59f32925ea3d0939d6cd5e36d81d38a`.
- Confirmed the branch is one commit ahead and one commit behind `origin/master`.
- Captured the dirty working-tree inventory. Existing changes are extensive and include Qira, qira_v2, AViK client, Workbench, IDE, build, and generated evidence files.
- Established that no production source will be edited until the first failing stage and an evidence-backed implementation plan are documented.
- Located the current round snapshot from `export_screens_2026-07-18_18.07.20.369.zip` metadata. The latest completed execution is `1cc5e264-bd1d-47e9-9371-e744995b531c` (`uk-UA`, 2026-07-18T00:23:52.755Z–00:41:53.275Z), Qira `QT-01.01.550`, device `motorola razr fold 2026`, build `blanc_gu-userdebug 16 W3WB36.36-48-5 fb58d2-883e52`, 1080x2520 at 420 dpi.
- Discovered the repository execution definitions: 15 Tier-1 locales and 17 Tier-2 locales. The current round manifest has only 26 executions, omits seven Tier-2 locales, and adds unclassified `id-ID`; the export itself stores every tier as `UNSPECIFIED`.
- Confirmed en-XM has 102 of the 104 requested scope tags. `MotorolaQiraHome_Onboarding_DeviceAssurance` and `MotorolaQiraFocusZone_Live_EnablePermission` are absent; `MotorolaQiraFocusZone_Live_Camera` is outside the requested scope.
- Produced the authoritative matrix: 33 locales (Tier union plus unclassified run target) x 104 tags = 3,432 rows; 2,704 `FAIL`, 728 `BLOCKED`, zero `PASS`.
- Produced a same-execution en-XM Workbench comparison between `MotorolaQiraChatHistory_Main_ManageChats_Selected` and `MotorolaQiraHome_Onboarding_PermissionsScrolled1`.
- Proved that the failed tag contains 48 element-level specific-ID records plus one foreign launcher `Home` record spanning 90.119% of the image. The control contains 24 element-level specific-ID records and no near-full-screen record.
- Audited the complete scoped en-XM set: 52 of 58 present affected tags contain the same near-full-screen overlink; one of 44 present controls (`MotorolaQiraSettings_Drawer_Menu`) also contains it and is therefore not a clean regression control in this run.
- Opened the remote Review task through Cursor. Authenticated DOM/network inspection is still unavailable. Local Workbench port 8321 is not listening.
- Confirmed a connected matching device and retained hierarchy evidence. Current Qira, Qira Core, and Action Core packages have not changed since before the round executions.
- Confirmed all 2,566 PNG/metadata JSON pairs remain under the local Workbench execution root and match the database. The offline export omits those JSON files by design.
- Completed the baseline Java compilation with Gradle 8.7 offline: exit 0, `BUILD SUCCESSFUL in 2m 10s`, 16/16 tasks executed.
- Implemented and independently revalidated owner-scoped metadata plus placeholder-only match rejection: 15/15 audit tests passed and Java compilation passed.
- Installed debug and Android-test APKs successfully on the connected device.
- Switched the device, Qira app, and instrumentation locale to `en-XM` with a verified system-runtime restart.
- Ran the complete Workbench-facing master capture on device. It produced 103 synchronized `appId=qira` PNG/JSON pairs; all were preserved under `before-after/postfix-en-XM-run1`.
- Post-fix audit reduced historical 2,014 release-gate findings to two: missing `Live_EnablePermission` and one zero-specific-ID `Knowledge_Main_TagsDropdown`.
- Confirmed zero near-full-screen links, zero placeholder-wildcard leakage, zero app/script identity errors, and zero invalid/out-of-screen bounds in the post-fix run.
- The owner gate exposed a real pre-existing flow error: `Live_Camera` attempted capture while Android's camera PermissionController was foreground. The run therefore ended with one Focus Zone sub-flow/JUnit failure despite completing the remaining feature groups.
- Corrected the camera-permission transition by stable PermissionController IDs and reran the full en-XM master.
- En-XM run 2 passed all six subflows and JUnit (`OK (1 test)`), producing 104 synchronized `appId=qira` pairs with zero near-full-screen, wildcard, identity, or bounds failures.
- `Live_Active` and `Live_Camera` are byte- and visually distinct. The camera capture is Qira-owned and contains the specific linked `Starting Camera...` state plus stable Live/camera controls.
- The 105-tag audit now has only two gates: missing Action Core `Live_EnablePermission`, and the previously false-wildcard-only Knowledge Tags dropdown with no specific IDs.
- En-XM run 3 captured Action Core under exact `com.motorola.actioncore`, returned with Android Back, re-proved Qira and the preserved prompt, and again passed all six subflows/JUnit.
- Audit run 3 found 106 paired artifacts but exposed a separate onboarding state-machine defect: three `Language` tags and no `DeviceAssurance`. Visual/metadata evidence proves the duplicates are Intro, actual Language, and Device Assurance surfaces.
- En-XM run 4 passed instrumentation with 105/105 unique then-current tags and proved exact onboarding classification. Its immutable output includes the now-removed historical Live Camera tag.
- En-XM run 5 produced 104 tags but correctly failed because persisted Action Core state hid required `Live_EnablePermission`. It also proved source-kind capture works and that geometry alone overstates hierarchy.
- The user removed only `Live_Camera` from the canonical scope. The active contract is 104 tags and still requires `Live_Active` plus `Live_EnablePermission`.
- Re-ran the final audit suite after D-009/D-010/D-011/D-012: 29/29
  tests passed. Installed debug and debugAndroidTest with the cached offline
  Gradle 8.7 executable; 61 tasks completed successfully and both APKs were
  installed on the sole connected real device `N2HT430016`.
- Completed final en-XM run 6 with build label
  `Qira_StringLinkFix6_en-XM_20260719` and Workbench-facing script
  `avik.qira.scripts.MotorolaQiraMasterCaptureV2`. All six master sub-flows
  completed and instrumentation ended `OK (1 test)`.
- Run 6 logged `pm clear com.motorola.actioncore` output exactly `Success`
  before the first Qira launch. It then captured `Live_Active`, proved the
  resource/SLAP enable-permission prompt, proved Action Core foreground with
  two stable polls, captured `Live_EnablePermission` under the exact Action
  Core owner, returned with Android Back, and re-proved the preserved Qira
  prompt. No `Live_Camera` capture or log mention occurred.
- Pulled exactly 208 fresh files (104 PNG + 104 JSON) to
  `before-after/postfix-en-XM-run6`. The metadata has 104 unique tags and
  unique orders 1-104, and every record uses `appId=qira`, the required script,
  and the run-6 build label.
- Final schema-v3 audit at `before-after/postfix-en-XM-run6-audit` is **PASS**:
  104 artifacts/pairs/unique tags, zero missing/extra/duplicate tags, zero gate
  failures, and two investigated warnings. It reports 1,615 linked records,
  239 static-unresolved records, four excluded exact dynamic/unscoped rows,
  546 unique Message IDs, zero malformed paths, near-full links, exact
  duplicate links, placeholder leakage, invalid/out-of-screen bounds, proven
  linked ancestry containers, or legacy pathless geometry overlaps.
- All 1,858 run-6 metadata records have valid source-kind and
  `qiraAccessibilityNodePath` provenance. Strict ancestry suppression removed
  69 ID associations on seven expected screens while linked text and icon-only
  descriptions remained. Fifty geometry-only candidates remained linked
  because their populated paths prove they are not ancestors.
- The two warnings are distinct Red/Purple portal-animation DESCRIPTION
  siblings on Chat and Catch Me Up onboarding. They share bounds but have
  different text, Message IDs, and same-root sibling paths; exact duplicate
  links remain zero.
- Representative PNG/metadata checks covered Home, Focus slide 2, Knowledge
  file list, Creator home, Chat History, TagsDropdown, `Live_Active`, and
  `Live_EnablePermission`. Child bounds remain granular, the Focus description
  ancestor is unlinked, icon descriptions remain linked, unrelated geometric
  overlap remains linked, and the historical launcher `Home` Message ID/bounds
  are absent.
- Under the current 104-tag audit, immutable run 4 remains historical FAIL
  (105 pairs, extra `Live_Camera`, two gates, 80 pathless geometry candidates,
  28 warnings). Run 6 is 104 pairs with no extra tag, complete ancestry paths,
  zero pathless geometry candidates, zero gates, and two explained warnings.
- Diagnosed the later Workbench en-XM failure from retained live dumps. The
  Live model-download notice was masking the Action Core prompt, while Avatar
  and Style Sync exposed their required Replace/Create controls without the
  optional selected-photo accessibility description.
- Added exact Compose-resource handling for `language_model_download_failed`
  / `download_close`, and changed Avatar/Style Sync verification to the stable
  `replace_label` + `generate_image_button` pair.
- Recompiled, installed, and ran the complete Workbench-facing master again as
  `Qira_EnXM_FinalFix_20260720`. All six sub-flows completed and JUnit ended
  `OK (1 test)`.
- Pulled exactly 104 PNG + 104 JSON artifacts to
  `before-after/postfix-en-XM-finalfix`. The final audit is **PASS** with zero
  release gates, no missing/extra/duplicate tags, and one investigated
  same-bounds portal sibling warning.

## In progress

- Final authorized en-XM on-device and artifact validation is complete.
- The previously launched locale-matrix driver is closed and will not be
  resumed, per the user's instruction to focus on en-XM.
- Local Workbench import remains intentionally unmodified pending a matching
  round: the API is listening, but
  `Qira_StringLinkFix6_en-XM_20260719` returns HTTP 404 from `/api/rounds` and
  no scripts. The API exposes round read/sync routes but no safe create route,
  so importing into a different existing round would not preserve exact run
  provenance.

## Next

- Run the normal Workbench execution with
  `avik.qira.scripts.MotorolaQiraMasterCaptureV2` (not the internal
  `avik.qira_v2.*` implementation class) so captures retain `appId=qira`.
- The final en-XM 104-tag release gate is PASS. No locale matrix is pending.

## Evidence produced

- Git status and diff-stat outputs in the active Cursor command transcript.
- Existing handoff evidence: `.cursor/skills/avik-qira-automation/HANDOFF.md`.
- This investigation directory.
- `before-after/forensic-pair-metrics.{json,md}`
- `before-after/en-XM-scope-overlink.{csv,json}`
- `logs/postfix-master-en-XM-run6-20260719.log`
- `before-after/postfix-en-XM-run6`
- `before-after/postfix-en-XM-run6-audit/avik-string-link-audit.{json,csv,md}`
- `logs/postfix-master-en-XM-finalfix-20260720.log`
- `before-after/postfix-en-XM-finalfix`
- `before-after/postfix-en-XM-finalfix-audit/avik-string-link-audit.{json,csv,md}`
- `live-evidence/workbench-en-XM-20260719`
- Retained en-XM and uk-UA UIAutomator evidence under `logs/`.
- Current round QA snapshot under `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA`.

## Blockers

- The requested Plan Mode UI switch was not accepted. Plan-only behavior is being enforced manually: investigation and reporting are allowed; production source edits are not.
- The working tree contains substantial pre-existing staged, unstaged, and untracked work. All existing work must be preserved and ownership separated before implementation.
- The offline export omits all 2,566 per-screen metadata JSON files, but the local Workbench execution root retains and validates every pair.
- Local Workbench is now listening on ports 8321/8221, but the exact unique
  run-6 round is absent and the read-only API/source check found no round-create
  route. Import was therefore not forced into an unrelated round.
- The round contains no per-locale tier field; repository batch definitions and the round target manifest disagree.
- The checked-in Gradle wrapper checksum is `544c...`, but the downloaded Gradle 9.1.0 distribution is `a17d...`; the exact wrapper command fails before configuration. Validation therefore uses the installed Gradle 8.7 cache without modifying the wrapper or credentials.
- Remote AViK Review remains authentication/tooling blocked; no browser
  automation was used.

## Exact command to resume

```powershell
Set-Location 'C:\Users\BLR-USER\Avik_repo\avik\avik-phone-horizon'
git -C .. status --short --branch
```

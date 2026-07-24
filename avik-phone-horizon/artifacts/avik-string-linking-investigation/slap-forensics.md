# SLAP and UI-Semantics Forensics

Generated: 2026-07-19

## Executive conclusion

The first failing stage is **P — foreign accessibility-window contamination during hierarchy extraction**.

AViK traverses every visible accessibility window. On launcher-backed Qira surfaces, it serializes the background launcher's pseudo-localized `Home` content description into the Qira screenshot metadata. That record spans `[0,123][1080,2394]`, or 90.119% of a 1080x2520 image, and carries Message ID `2wGy6ZtecYu8S5DJUvTEHV`. Review App renders submitted message-ID records unchanged as absolute overlays, so this foreign record becomes the observed near-full-screen selectable region.

Qira child semantics are not generally missing or collapsed. The failed representative screen contains 48 element-level records with specific Message IDs and its retained hierarchy exposes 21 Qira text nodes plus 16 Qira content-description nodes.

A second proven defect is **N — placeholder-only resource overmatching**. Qira Compose resource `wakeup_word_display` has authoritative value `%1$s`. The bulk matcher converts that to a pattern matching every nonempty visible value, causing its Message ID `5HQQfXNKx50UU0Nn1RHqH5` to be attached to static, dynamic, user, and server-generated text indiscriminately.

## Compatible comparison

| Field | Failed | Control |
|---|---|---|
| Execution | `3544956f-bef9-422d-9f76-3600fb0397b2` | Same |
| Locale | `en-XM` | Same |
| Build | `Qira_Horizon_Tier1_Tier2_Round1` | Same |
| Qira version | `QT-01.01.550` / `460101550` | Same |
| Device | `motorola razr fold 2026` | Same |
| Device build | `blanc_gu-userdebug 16 W3WB36.36-48-5 fb58d2-883e52 intcfg,test-keys MW-445` | Same |
| Geometry | 1080x2520, portrait | Same |
| appId/script | `qira_v2` / `avik.qira_v2.scripts.MotorolaQiraMasterCapture` | Same historical identity |
| Tag | `MotorolaQiraChatHistory_Main_ManageChats_Selected` | `MotorolaQiraHome_Onboarding_PermissionsScrolled1` |
| Screen ID | `91804b92-a845-4e5b-bc91-23eda734955c` | `199eb98f-8737-4998-9b90-a4659a90d4c5` |
| Screenshot hash | `5osw5d30vxufpym2zritpu9zs` | `founkob6n9kpd886546yuahd` |
| Timestamp UTC | `2026-07-16T19:21:27Z` | `2026-07-16T19:09:07Z` |
| PNG SHA-256 | `78f56cdd058785dcd8e77a027cf8861dc0532734f7bce5ab40ba53c5701b90ef` | `2f3590b2c68cc725511a55c0b83e2e89adf966903efe51f3f1deb22f11449cca` |
| PNG integrity | Valid | Valid |

Raw metadata:

- `C:\Users\BLR-USER\avikWorkbench\executions\Qira_Horizon_Tier1_Tier2_Round1\2026-07-17_12.18.45.964\en-XM\avik.qira_v2.scripts.MotorolaQiraMasterCapture\5osw5d30vxufpym2zritpu9zs.json`
- `C:\Users\BLR-USER\avikWorkbench\executions\Qira_Horizon_Tier1_Tier2_Round1\2026-07-17_12.18.45.964\en-XM\avik.qira_v2.scripts.MotorolaQiraMasterCapture\founkob6n9kpd886546yuahd.json`

## Quantitative comparison

| Metric | Failed | Control |
|---|---:|---:|
| Workbench link records | 73 | 27 |
| Leaf link records | 61 | 26 |
| Records with any Message ID | 53 | 24 |
| Records with non-global specific IDs | 48 | 24 |
| Unique non-global specific IDs | 43 | 42 |
| Records carrying `5HQQ…` | 37 | 15 |
| Near-full-screen links | 1 | 0 |
| Parent/container links | 1 | 0 |
| Invalid bounds | 0 | 0 |
| Out-of-screen bounds | 0 | 0 |
| Duplicate bounds | 1 | 0 |
| Retained hierarchy nodes | 176 | 43 |
| Qira hierarchy nodes | 80 | 5 |
| Qira text nodes | 21 | 0 |
| Qira content-description nodes | 16 | 0 |

The UiAutomator dumps are supplemental snapshots retained from the matching build/device sequence. The raw JSON and Workbench rows are the execution-paired evidence.

## First causal difference

The failed raw metadata array contains:

1. Qira child records with granular bounds.
2. System UI records.
3. Launcher/widget/search records.
4. The launcher-owned `Home` record appended after the Qira children.

Review App creates one absolutely positioned layer per message-ID record in array order, with no ownership or parent suppression. The launcher record is therefore both invalid for the Qira capture and capable of masking earlier child layers.

The control lacks the launcher `Home` record. Its largest linked region is only 8.35% of the image.

## Pipeline hypothesis results

| Hypothesis | Result |
|---|---|
| Capture API inconsistency | Rejected. Failed and control use the same forced `takeScreenshot(name, true, true)` path. |
| Asynchronous metadata race | Rejected for this defect. The public capture call blocks through all workers, metadata is closed before return, and both pairs are complete. |
| Missing child Compose semantics | Rejected as the broad cause. Granular failed-screen children exist before SLAP extraction. |
| Parent/container extraction | Confirmed as a secondary mechanism: descriptions and children are both emitted without suppression. |
| Foreign-window contamination | Confirmed primary cause. Launcher, widgets, Google, System UI, and Qira are flattened into one record set. |
| Coordinate transform error | Rejected. Bounds are valid and match screenshot geometry. |
| Filename/tag mismatch | Rejected for the representative pair. Hash, metadata, PNG, database, tag, and dimensions agree. |
| Metadata overwrite | Rejected for the representative pair. Unique hash pairs remain intact. |
| Workbench ingestion loss | Rejected. Raw metadata and Workbench counts/bounds/IDs match. |
| Review UI creates bad bounds | Rejected. Review renders the submitted `[0,123][1080,2394]` record. |
| Placeholder-only wildcard linking | Confirmed secondary stage N. `%1$s` is treated as `.+?` and matches every nonempty value. |
| System permission ownership | Separate path. PermissionController and Action Core records require their own package scope and must not receive Qira IDs. |

## Full-scope corroboration

The en-XM scope audit finds the same launcher `Home` overlink on 52 of 58 present affected tags and on one control, `MotorolaQiraSettings_Drawer_Menu`, which is also launcher-backed. There are 53 near-full-screen records and 58 parent/container records across the present scoped set.

Six present affected tags do not have the launcher overlink and require separate ownership/static-dynamic validation:

- `MotorolaQiraFocusZone_Live_AndroidMicrophonePermission`
- `MotorolaQiraCreatorZone_CreateImage_Generating_Preparing`
- `MotorolaQiraCreatorZone_CreateImage_Generating_Generating`
- `MotorolaQiraCreatorZone_CreateImage_GeneratedImage`
- `MotorolaQiraCreatorZone_EditImage_Editor`
- `MotorolaQiraCreatorZone_CreatorHome_ViewMore`

`MotorolaQiraFocusZone_Live_EnablePermission` is absent from en-XM.

## Artifact and ingestion result

- Local PNG/JSON pairing: 2,566/2,566.
- Metadata/Workbench field and string reconciliation: exact for 2,566 screens.
- Invalid/out-of-screen bounds: zero.
- Historical identity: all data uses `appId=qira_v2`; this does not satisfy the required `appId=qira`.
- Remote Review hover/click/API verification: blocked by Google IAP and unavailable authenticated browser tooling.

## Required correction boundary

The fix must:

1. Scope each capture to its evidence-backed owner package while retaining multiple windows from that package.
2. Select Qira, PermissionController, or Action Core ownership according to the actual screen contract.
3. Reject a mismatched foreground owner instead of silently capturing another app.
4. Preserve `includeText=true` and `includeDescription=true`.
5. Preserve icon-only descriptions while suppressing foreign windows.
6. Reject placeholder-only values as generic metadata matches; parameterized strings with literal anchors remain eligible.
7. Re-audit Qira-owned parent descriptions after package scoping before adding any tree-aware suppression.

## Evidence

- `before-after/forensic-pair-metrics.{json,md}`
- `before-after/en-XM-scope-overlink.{csv,json}`
- `repo-pipeline-map.md`
- `compose-accessibility-analysis.md`
- `browser-review-evidence.md`
- User-provided Review screenshots, including selection of `2wGy6ZtecYu8S5DJUvTEHV`

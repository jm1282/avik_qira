# Evidence Index

| ID | Evidence | Scope | Status |
|---|---|---|---|
| E-001 | `.cursor/skills/avik-qira-automation/HANDOFF.md` | Prior qira_v2 implementation and RTL findings | Collected |
| E-002 | Git status at HEAD `726751cda59f32925ea3d0939d6cd5e36d81d38a` | Working-tree preservation baseline | Collected |
| E-003 | User-provided AViK Review screenshots | Parent-area highlighting and locale summary | Collected; visual evidence only |
| E-004 | `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\run_manifest.json` | Current export provenance and hashes | Collected |
| E-005 | `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\avik_round_discovery.json` | 26 executions, run identity, tags, artifact integrity | Collected |
| E-006 | `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\input\workbench_api\workbench_db_extract.json` | Uploaded/reviewable Workbench screen and string records | Collected |
| E-007 | `before-after/forensic-pair-metrics.{json,md}` | Same-execution en-XM failed/control quantitative comparison | Collected |
| E-008 | `before-after/en-XM-scope-overlink.{csv,json}` | Full affected/control overlink audit | Collected |
| E-009 | `logs/candidate-en-XM-20260716-195459-ManageChats_Selected.{xml,txt}` | Retained en-XM failed-screen hierarchy | Supplemental; raw export pairing absent |
| E-010 | `logs/candidate-en-XM-20260716-194252-PermissionsScrolled1.{xml,txt}` | Retained en-XM control hierarchy | Supplemental; raw export pairing absent |
| E-011 | Review App client `screen-review-container.component.{ts,html,scss}` | Every message-ID record is rendered as an absolute interactive layer; no owner/container filter | Collected |
| E-012 | Connected device package/build queries | 1080x2520, 420 dpi, matching build; Qira 550, Qira Core 1.0.110, Action Core 3.00.1213 | Collected |
| E-013 | `repo-pipeline-map.md` | Complete capture-to-Review call graph and Git-state boundaries | Collected |
| E-014 | `compose-accessibility-analysis.md` | Window ownership, Compose semantics, and system-surface analysis | Collected |
| E-015 | `tag-locale-matrix.{csv,json}` / `locale-flow-analysis.md` | 33-locale x 104-tag strict release matrix | Collected |
| E-016 | `browser-review-evidence.md` | Workbench pairing/grouping and remote-access status | Collected |
| E-017 | `C:\Users\BLR-USER\avikWorkbench\executions\Qira_Horizon_Tier1_Tier2_Round1` | 2,566 retained PNG/JSON pairs | Collected and validated |
| E-018 | Baseline Gradle 8.7 compile | 16/16 tasks; exit 0 | Passed |
| E-019 | Final audit tests and offline Gradle 8.7 install | 29/29 tests; 61 install tasks; debug + debugAndroidTest installed on sole device `N2HT430016` | Passed |
| E-020 | `logs/postfix-master-en-XM-run6-20260719.log` | Run-6 instrumentation, Action Core reset/owner sequence, six sub-flows, no Live Camera; 642,998 bytes; SHA-256 `f7e2bcbc7f5b58d7a9c206b197a61d84ec545c3f2506c286d206292fdda922d0` | `OK (1 test)` |
| E-021 | `before-after/postfix-en-XM-run6` | Build `Qira_StringLinkFix6_en-XM_20260719`; 208 files; 104 PNG/JSON pairs; 104 unique tags/orders; `appId=qira` | Collected and validated |
| E-022 | `before-after/postfix-en-XM-run6-audit/avik-string-link-audit.{json,csv,md}` | Schema-v3 exact 104-tag gate; JSON SHA-256 `ff34365d3f98f0e73a7629fb6434b16d1b52b8aced8f83eeab4c288f875e065b` | PASS; 0 gates, 2 investigated warnings |
| E-023 | Run-6 representative PNG/metadata hashes `eylk2sitk2xclgf1g05shvqrt`, `chx68yumpawbu8z4xqny0jj19`, `5edju7zwat4i9ls0062on4x2d`, `behwdjzp1t7m0ddj8d16y35sn`, `86oi8xw3bzbhxjmbsupzwij9v`, `cmsa5wbojr6vbho28pbhj488x`, `6vverge0za58misnvc428bzom`, `lrfdpllqli3q9xpvb0sghqa1` | Home, Focus, Knowledge, Creator, Chat History, TagsDropdown, Live Active, Action Core visual/linkage evidence | Inspected |
| E-024 | Local Workbench read-only API probes on 8321/8221 | API available; exact run-6 round HTTP 404, script query empty, no round-create route | Import blocked without mutating unrelated round |
| E-025 | `live-evidence/workbench-en-XM-20260719` | Live model-download notice and Avatar Replace/Create post-picker hierarchy at the reported failures | Collected; exact selector evidence |
| E-026 | `logs/postfix-master-en-XM-finalfix-20260720.log` | Final Workbench-facing rerun after Live/Avatar/Style Sync corrections | `OK (1 test)`; all six sub-flows |
| E-027 | `before-after/postfix-en-XM-finalfix` | Build `Qira_EnXM_FinalFix_20260720`; 208 files; 104 PNG/JSON pairs; `appId=qira`; no Live Camera | Collected and validated |
| E-028 | `before-after/postfix-en-XM-finalfix-audit/avik-string-link-audit.{json,csv,md}` | Final exact 104-tag en-XM release gate after exception remediation | PASS; 0 gates, 1 investigated warning |

Additional evidence will be indexed with source path, run identity, timestamp, and compatibility constraints.

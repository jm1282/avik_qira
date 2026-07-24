# AViK Review and Workbench Evidence

Generated: 2026-07-19

## Access status

- Local Workbench is currently offline: ports `8321` and `8221` refuse connections.
- A retained read-only probe from 2026-07-18 records HTTP 200 for both the Workbench round and API.
- The remote AViK Review URL redirects to Google IAP in the available non-Edge tooling. The Cursor opener does not expose authenticated DOM or network inspection, and no Edge-session inheritance was available.
- No Review status was changed and `FINISH` was not clicked.

## Local Workbench evidence

Authoritative sources:

- `C:\Users\BLR-USER\avikWorkbench\data.db`
- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\evidence\browser_access\workbench_access_proof.json`
- `C:\Users\BLR-USER\Downloads\AViK_Horizon_Tier1_Tier2_Round1_QA\input\workbench_api\workbench_db_extract.json`
- `C:\Users\BLR-USER\avikWorkbench\executions\Qira_Horizon_Tier1_Tier2_Round1`

Findings:

- Round ID: `04b39e1c-d7a8-4fd8-8e07-1d77dd9cbea8`.
- The round contains 26 executions, 2,566 screens, and 105,925 string records.
- All 2,566 local PNG/JSON artifact pairs exist and match Workbench dimensions, tags, locale metadata, string/message-ID counts, and bounds.
- All 2,566 screens are uploaded, locally reviewable, and have distinct remote Review URLs.
- Every execution and Review URL uses raw `appId=qira_v2` / `apps=qira_v2`, violating the required native `appId=qira` contract.
- Current uncommitted alias code could present historical `qira_v2` data under a QIRA tile, but this is not runtime-validated and does not correct stored/uploaded metadata.

## Failed versus control evidence

Failed sample:

- Tag: `MotorolaQiraChatHistory_Main_ManageChats_Selected`
- en-XM screen ID: `91804b92-a845-4e5b-bc91-23eda734955c`
- Element-level specific-ID records: 48
- Foreign launcher record: `Home`, Message ID `2wGy6ZtecYu8S5DJUvTEHV`, bounds `[0,123][1080,2394]` (90.119% of the image)

Known-good sample:

- Tag: `MotorolaQiraHome_Onboarding_Start`
- Expected Start Message ID: `3HZqzQOSH074ww76j8WdvK`
- Bounds: `[497,1899][584,1952]`

The Workbench/server-side record already contains the oversized launcher mapping. Review App source renders submitted `mtexts` unchanged as absolute overlays and does not create this geometry. The user-provided Review screenshot independently shows `2wGy6ZtecYu8S5DJUvTEHV` selected with the near-full-screen region.

## Coverage

- Requested affected scope: 1,445 of 1,534 executed locale/tag pairs present.
- Requested control scope: 1,115 of 1,170 executed locale/tag pairs present.
- The broader authoritative Tier-1/Tier-2 matrix is documented in `locale-flow-analysis.md`.

## Remote Review result

Status: `BLOCKED — AUTHENTICATED REVIEW ACCESS REQUIRED`

Not validated:

- Hover/click behavior after a fix
- Review API ordering of overlapping records
- String/Message ID search after a fix
- Remote ingestion under native `appId=qira`

No remote result is inferred from Workbench data.

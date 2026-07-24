# Root Cause

Status: Primary and secondary causes corrected; final en-XM 104-tag gate PASS.

## Executive summary

The first failing stage is **P — foreign accessibility-window contamination during AViK hierarchy extraction**.

AViK flattens every visible accessibility window into one `avikTexts` array. Launcher-backed Qira screens therefore include the background launcher's pseudo-localized `Home` description. In en-XM that record carries Message ID `2wGy6ZtecYu8S5DJUvTEHV` and bounds `[0,123][1080,2394]`, covering 90.119% of the screenshot. Review App faithfully renders the submitted record as an absolute overlay.

Granular Qira child links are already present. The failed representative contains 48 element-level specific-ID records, so the defect is not a general loss or collapse of Qira child semantics.

## Secondary proven cause

**N — placeholder-only resource overmatching.**

Qira Compose resource `wakeup_word_display` has value `%1$s`. The current generic matcher converts that value to `^.+?$`, so its Message ID `5HQQfXNKx50UU0Nn1RHqH5` is added to every nonempty Qira accessibility value, including dynamic/user/server text. A placeholder-only value has no literal identity and cannot safely identify a visible element.

## Later flow exceptions

- Live: the prompt was not absent due to an unknown transition. The retained
  dump shows Qira's `language_model_download_failed` notice occupying the same
  slot, with the exact `download_close` Compose control. Dismissing that
  resource-backed notice exposes the required Action Core prompt.
- Avatar and Style Sync: picker selection succeeded. Their post-picker surface
  exposed `replace_label` and `generate_image_button`; only the optional
  `cd_selected_photo_preview` description was absent. Requiring all three
  created a false failure. The two-action resource pair is the authoritative
  surface identity.

## Why specific feature groups are affected

Home, Focus Zone, Creator Zone, Chat History, and Settings Drawer frequently render as launcher-backed overlays, leaving the launcher accessibility window visible. Full-screen Onboarding, Knowledge, and Settings pages generally do not expose the launcher `Home` window. The scope audit finds the contaminating record on 52 of 58 present affected tags and one launcher-backed control.

## Rejected hypotheses

- Different screenshot helper: failed and control use the same V2 helper and flags.
- Asynchronous flush race: capture is synchronous through all workers and the representative pairs are complete.
- Missing Qira children: failed hierarchy and metadata contain granular children.
- Coordinate transform expansion: the 90.119% bounds originate in the launcher hierarchy and are valid screen coordinates.
- Filename/tag mismatch or overwrite: hash, tag, PNG, JSON, database, and Workbench records agree.
- Workbench/Review creates the geometry: raw metadata already contains it.

## Locale and identity contributors

- Historical round metadata uses `appId=qira_v2` and the original qira_v2 master script instead of the required `appId=qira` wrapper contract.
- The historical en-XM round has only 102 of 104 requested scope tags. Final
  run 6 has exactly 104/104 after authoritative Device Assurance and Action
  Core `Live_EnablePermission` capture.
- Seven configured Tier-2 locales have no execution, and `id-ID` is an unclassified round target.
- Current-run logs are absent for zh-CN, ar-EG, and ru-RU divergences, so their low-level selector/runtime causes remain blocked rather than inferred.

## Fix architecture

1. Owner-scope metadata in the central qira_v2 capture helper.
2. Preserve all accessibility windows belonging to the expected Qira, PermissionController, or Action Core package.
3. Fail on package/tag ownership mismatch.
4. Preserve text and icon-only descriptions, attach deterministic same-root
   accessibility paths, and unlink only DESCRIPTION records proven to be strict
   ancestors of independently linked children.
5. Reject placeholder-only bulk resource matches while retaining parameterized strings with literal anchors.
6. Run under the Workbench-facing `avik.qira.*` wrapper so new metadata is natively `appId=qira`.

## Final on-device proof

- Final build label: `Qira_StringLinkFix6_en-XM_20260719`.
- Instrumentation log:
  `logs/postfix-master-en-XM-run6-20260719.log`.
- The log records the Action Core clear result exactly as `Success` before Qira
  launch, all six master sub-flows completed, and `OK (1 test)`.
- The Live sequence is ordered and owner-proven: Qira `Live_Active` and its
  resource/SLAP prompt, exact `com.motorola.actioncore` foreground,
  `Live_EnablePermission`, Android Back to Qira, and the preserved prompt.
  `Live_Camera` is absent.
- The fresh artifact folder contains exactly 104 PNG/JSON pairs, 104 unique
  canonical tags, and unique orders 1-104. All 104 metadata files carry
  `appId=qira`, script
  `avik.qira.scripts.MotorolaQiraMasterCaptureV2`, and the run-6 build label.
- The schema-v3 audit is PASS with zero release gates. It finds no missing,
  extra, or duplicate tags; no invalid/out-of-screen bounds, near-full links,
  placeholder wildcard leakage, exact duplicate links, or proven linked
  ancestry containers.
- Every one of 1,858 metadata records has a valid
  `qiraAccessibilityNodePath`; malformed paths are zero. D-010 suppressed 69
  Message-ID associations on seven expected screens. Granular TEXT children
  and icon-only DESCRIPTION records remain linked.
- Fifty linked geometric containment candidates remain by design because their
  populated paths prove they are siblings/unrelated rather than ancestors.
  This includes the Home `dismiss Qira`/content geometry, Knowledge row/delete
  geometry, and Chat History Search geometry.
- The former launcher contamination is absent: no record has launcher Message
  ID `2wGy6ZtecYu8S5DJUvTEHV` or the historical `Home`
  `[0,123][1080,2394]` combination.
- TagsDropdown passes the strict exact dynamic/unscoped rule with only
  `Identity`, `Contact`, `Education`, and `Work`, all with no Message IDs.
- The audit's two warnings are fully explained: Red and Purple portal-animation
  DESCRIPTION sibling nodes share bounds on Chat and Catch Me Up onboarding,
  but have distinct text, Message IDs, and node paths. Exact duplicate links
  remain zero.

Immutable run 4 remains useful historical evidence: under the current 104-tag
contract it has 105 pairs, the removed `Live_Camera` extra tag, two gates, no
path provenance, 80 legacy geometry candidates, and 28 warnings. Run 6 has 104
pairs, complete path provenance, zero legacy geometry candidates, zero gates,
and two explained warnings.

Local Workbench is listening, but exact import was not forced: the unique run-6
round returns HTTP 404, its script query is empty, and the API exposes no
round-create route. Importing into another existing round would falsify run
provenance. No `qira_v2` module was created.

The post-exception Workbench-facing run
`Qira_EnXM_FinalFix_20260720` also completed all six sub-flows and produced
104/104 artifacts under `appId=qira`. Its schema-v3 audit is **PASS** with
zero release gates and one investigated same-bounds portal sibling warning.
The final en-XM 104-tag remediation gate is **PASS**.

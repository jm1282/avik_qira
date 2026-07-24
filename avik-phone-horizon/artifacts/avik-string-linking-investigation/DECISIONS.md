# Investigation Decisions

## D-001 — Preserve the current working tree

- Status: Active
- Decision: Do not reset, stash, discard, or overwrite existing staged, unstaged, or untracked work.
- Basis: The repository contains extensive pre-existing Qira, qira_v2, AViK client, Workbench, build, and evidence changes.

## D-002 — Evidence gate before production edits

- Status: Active
- Decision: Production source changes are blocked until a compatible failed/known-good comparison identifies the first failing pipeline stage and the implementation plan is documented.
- Basis: Screenshots alone do not distinguish capture, extraction, serialization, upload, ingestion, or Review UI defects.

## D-003 — en-XM structural authority

- Status: Active
- Decision: Use en-XM as the structural baseline. Other locales will be compared by stable tag and authoritative IDs rather than literal text.
- Basis: Release contract and repository Qira automation rules.

## D-004 — Approve the first production correction

- Status: Approved for implementation
- Decision: Correct qira_v2 capture metadata centrally by retaining only records from the evidence-backed screen-owner package, while preserving all windows belonging to that package. Enforce Qira, PermissionController, and Action Core ownership through one capture policy and fail on mismatches.
- Basis: A compatible en-XM pair proves that the launcher-owned `Home` record is the first causal difference; Qira child records already exist.
- Constraint: The phone project consumes a published AViK Android client rather than the modified local Workbench client. The release fix must therefore be effective in the qira_v2 capture helper without depending on an unpublished client artifact.

## D-005 — Reject placeholder-only wildcard linking

- Status: Approved for implementation
- Decision: A resource whose value contains only placeholders, such as `wakeup_word_display=%1$s`, cannot identify arbitrary visible text and must not participate in bulk metadata matching. Parameterized resources with literal anchors remain eligible.
- Basis: The current matcher turns `%1$s` into `^.+?$`, attaching `5HQQfXNKx50UU0Nn1RHqH5` to dynamic chat titles, timestamps, and unrelated static strings.

## D-006 — Defer screen-flow changes until post-fix en-XM evidence

- Status: Active
- Decision: Do not alter navigation, permission preconditions, or scrolling in the first production patch. Run the corrected en-XM capture and deterministic audit first, then diagnose any remaining missing tags or byte-identical states from current evidence.
- Basis: The historical round predates portions of the current uncommitted flow logic and lacks matching instrumentation logs. Editing flow behavior now would be speculative.

## D-007 — Preserve and correctly reach Live Camera

- Status: Superseded by D-012
- Decision: Keep `MotorolaQiraFocusZone_Live_Camera` as an existing expected tag. After tapping the camera bubble, handle the Android camera-permission dialog by stable PermissionController resource IDs, wait boundedly for Qira to regain foreground, and only then capture the Qira-owned camera state.
- Basis: The 2026-07-19 en-XM rerun proved the old flow attempted `Live_Camera` while `com.google.android.permissioncontroller` owned the foreground. The owner gate correctly rejected that mis-tag. Dropping the existing tag would create a regression and would hide the state error.
- Contract impact: The deterministic expected-tag contract is 105 tags: the 104 requested affected/control tags plus the existing `Live_Camera` tag.

## D-008 — Remove generic Next-to-Language tagging

- Status: Approved after en-XM run-3 evidence
- Decision: Capture `Onboarding_Language` only when `language_screen_pill_text` / Message ID `1bnG5By6KeLRvwwkliANfx` is visible, and capture `Onboarding_DeviceAssurance` only when its own stable catalog identity is visible. An unchanged Intro card must be retried without another tag; any other Next-gated surface must dump and fail.
- Basis: Run 3 produced three `Language` artifacts: the Intro card, the actual response-language picker, and the Device Assurance card. The audit correctly reported duplicate Language and missing Device Assurance even though JUnit passed.
- Constraint: Do not deduplicate after capture or overwrite artifacts. The state must be classified correctly before each capture.

## D-009 — Classify Knowledge tag values as dynamic/unscoped data

- Status: Approved from corpus and APK evidence
- Decision: Treat the exact `Identity`, `Contact`, `Education`, and `Work` values on `MotorolaQiraKnowledge_Main_TagsDropdown` as non-localizable dynamic/unscoped tag data. Require that exact set with no Message IDs; any different value or authoritative ID remains a release-gate finding.
- Basis: The same four English values occur unchanged in every one of 24 available locale executions, including en-XM and RTL ar-EG. They have no en-XM marker, no Android/Compose resource entry, and historically carried only the proven false `%1$s` wildcard ID. Qira application source is unavailable, so no authoritative resource can be synthesized.
- Impact: These four rows are excluded from static-localizable linkage denominators; the screen itself remains required and paired.

## D-010 — Suppress redundant description-only parent links

- Status: Approved after owner-scoped en-XM evidence
- Decision: Clear linkage only from owner-package accessibility records proven to originate solely from `contentDescription` and to be an actual accessibility-tree ancestor of an independently linked owner child. Preserve the record as unlinked accessibility evidence.
- Basis: Run 4 retains linked description containers such as `dismiss Qira`, carousel page aggregations, Knowledge row aggregations, and overlapping portal-animation descriptions. Their link geometry contains separately linked visible text.
- Constraints: No flat area threshold, no text-based denylist, no global description disablement, and no geometry-only inference. Text nodes, icon-only descriptions, and descriptions without an independently linked descendant remain unchanged.

## D-011 — Reset Moto Action Core companion state for the master contract

- Status: Approved from repeat-run evidence
- Decision: Before a qira_v2 fresh master launch, clear `com.motorola.actioncore` in addition to Qira and require the shell command to report success.
- Basis: Historical run 4 exposed the Qira-owned Live enable-permission prompt and completed the then-current 105-tag contract, which included Live Camera. Run 5 began with the same Qira clear-data path but the prompt was absent because companion-owned consent state persisted. The current user-approved 104-tag contract removes only Live Camera and still requires the Action Core screen. On-device `pm clear com.motorola.actioncore` returns `Success`.
- Impact: The Action Core capture remains reachable and deterministic across repeated master runs without text, OCR, coordinates, or sleeps. qira v1 is untouched.

## D-012 — Remove Live Camera from the canonical contract

- Status: User-approved scope override; active
- Decision: Remove only `MotorolaQiraFocusZone_Live_Camera` from capture and the structural contract. Keep the broader Live flow, `MotorolaQiraFocusZone_Live_Active`, and `MotorolaQiraFocusZone_Live_EnablePermission`.
- Basis: The user explicitly determined that the Live camera screenshot is not necessary and prioritized making the remaining Run-4 screens deterministic and exact.
- Impact: The authoritative contract is 104 tags. Historical runs 4 and 5 retain their immutable Live Camera evidence and therefore audit with one extra historical tag; they are not final release runs.
- Supersedes: D-007 and any prior 105-tag/required-Live-Camera wording. D-011 remains active for deterministic Action Core state.

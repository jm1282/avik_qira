# AGENTS.md — avik-phone-horizon

Cross-tool orientation for AI agents working in this workspace (Qira v2 Avik Android
localization automation). This is a concise bridge; the authoritative, always-applied
standard for Cursor is `.cursor/rules/senior-avik-qira-automation-rules.mdc`. Follow it.

## Sources of truth

- Standard / invariants: `.cursor/rules/senior-avik-qira-automation-rules.mdc`
- Build/run/validation runbook: `.cursor/rules/senior-avik-qira-validation-protocol.mdc`
- Operational skill (discovered IDs, method): `.cursor/skills/avik-qira-automation/SKILL.md`
- Tag contract (104 tags): `tools/avik-string-link-audit/contract.json`
- Decisions of record (D-001..D-012): `artifacts/avik-string-linking-investigation/DECISIONS.md`
- Live status + active blocker: `.cursor/skills/avik-qira-automation/HANDOFF.md`

## Non-negotiables (summary — full text in the rule)

- A full run produces EXACTLY 104 paired PNG+JSON tags == `contract.json`; zero
  missing/extra/duplicate; `MotorolaQiraFocusZone_Live_Camera` excluded.
- `appId=qira`; provenance prefix `avik.qira.scripts.`; qira_v2 imports under the existing
  `qira` module; `avik.qira_v2.*` logic with thin `avik.qira.*` wrappers.
- Stable-ID selectors only (SLAP message ID > Qira/Android resource ID > accessibility). No
  visible-text, coordinate, OCR, or sleep-only fallbacks. Fail loud with a dump; never guess.
- Owner-scoped capture (`com.lenovo.qira` / permissioncontroller / `com.motorola.actioncore`);
  strip foreign-window text; never invent Qira IDs for external-owner screens.
- Keep SLAP flags on. Never break qira v1.
- Verified on device is the only "done": build + install + run to `OK (1 test)` + `audit.py`
  PASS with 104 tags and proven state distinctness. Compiling is NOT done. Defect budget <= 0.01%.

## Build / run / validate

See `.cursor/rules/senior-avik-qira-validation-protocol.mdc` for the exact commands, the
device-health preflight, and the audit gate.

# AViK/Qira string-link audit

Read-only, standard-library-only release gate for completed AViK screenshot
executions. It accepts either a historical Workbench execution root or one
directory containing completed `<hash>.png` / `<hash>.json` pairs.

Run from `avik-phone-horizon`:

```powershell
python .\tools\avik-string-link-audit\audit.py `
  C:\path\to\execution `
  --output-dir C:\path\to\audit-output
```

The command writes `avik-string-link-audit.csv`, `.json`, and `.md`, and exits
with `1` for release-gate findings (`2` for invalid input/tool errors). The
checked-in contract contains all 104 canonical `SCOPE.md` tags.
`MotorolaQiraFocusZone_Live_Camera` is intentionally excluded by the
user-approved scope decision; `Live_Active` and `Live_EnablePermission` remain
required.

Contract schema version 2 also declares one strict dynamic/unscoped data
classification: `MotorolaQiraKnowledge_Main_TagsDropdown` must contain exactly
`Identity`, `Contact`, `Education`, and `Work`, with no Message IDs. Only that
exact validated set is exempt from `no_resolved_links`; missing, extra,
unexpected, or linked values fail the release gate.

Audit report schema version 3 treats `parent_container_candidate` as a release gate only
when `qiraAccessibilityNodePath` proves a strict same-root accessibility
ancestor relationship between linked records. Legacy artifacts without paths
report `legacy_geometric_overlap_unproven` warnings; geometry alone is never
called hierarchy. Malformed populated paths fail. Duplicate bounds are
calculated only among linked overlays, so preserved unlinked accessibility
evidence does not create a false duplicate-bounds warning.

Run the sanitized fixture suite:

```powershell
python -m unittest discover `
  -s .\tools\avik-string-link-audit\tests `
  -p "test_*.py" -v
```

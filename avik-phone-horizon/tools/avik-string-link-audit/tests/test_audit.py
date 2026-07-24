from __future__ import annotations

import binascii
import importlib.util
import json
import re
import struct
import tempfile
import unittest
import zlib
from pathlib import Path


TOOL_DIR = Path(__file__).resolve().parents[1]
FIXTURES = Path(__file__).resolve().parent / "fixtures" / "cases.json"
SPEC = importlib.util.spec_from_file_location("avik_string_link_audit", TOOL_DIR / "audit.py")
assert SPEC and SPEC.loader
AUDIT = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(AUDIT)


def png_chunk(kind: bytes, payload: bytes) -> bytes:
    return (
        struct.pack(">I", len(payload))
        + kind
        + payload
        + struct.pack(">I", binascii.crc32(kind + payload) & 0xFFFFFFFF)
    )


def write_png(path: Path, width: int, height: int) -> None:
    row = b"\x00" + (b"\x00\x00\x00\xff" * width)
    raw = row * height
    data = (
        AUDIT.PNG_SIGNATURE
        + png_chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
        + png_chunk(b"IDAT", zlib.compress(raw))
        + png_chunk(b"IEND", b"")
    )
    path.write_bytes(data)


class AuditTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        fixture = json.loads(FIXTURES.read_text(encoding="utf-8"))
        cls.defaults = fixture["defaults"]
        cls.cases = fixture["cases"]

    def write_contract(
        self,
        root: Path,
        *,
        tags: list[str] | None = None,
        dynamic_rules: list[dict] | None = None,
    ) -> Path:
        contract = {
            "contractVersion": 1,
            "tags": tags or ["GoodTag"],
            "expectedAppId": "qira",
            "requiredScriptPrefix": "avik.qira.scripts.",
            "forbiddenPlaceholderOnlyResources": [
                {
                    "stringId": "wakeup_word_display",
                    "expectedValue": "%1$s",
                    "messageId": "5HQQfXNKx50UU0Nn1RHqH5",
                }
            ],
        }
        if dynamic_rules is not None:
            contract["dynamicUnscopedData"] = dynamic_rules
        path = root / "contract.json"
        path.write_text(json.dumps(contract), encoding="utf-8")
        return path

    def materialize(
        self,
        root: Path,
        case_name: str,
        *,
        stem: str | None = None,
        direct: bool = False,
        historical: bool = False,
        write_json: bool = True,
        write_image: bool = True,
    ) -> tuple[Path, Path, Path]:
        case = dict(self.cases[case_name])
        metadata = dict(self.defaults)
        metadata.update(
            {
                key: value
                for key, value in case.items()
                if key not in {"texts", "directoryLocale"}
            }
        )
        metadata["name"] = metadata.pop("tag")
        if stem is not None:
            metadata["hash"] = stem
        pair_stem = metadata["hash"]
        metadata["avikTexts"] = []
        for fixture_text in case.get("texts", []):
            text = dict(fixture_text)
            left, top, right, bottom = text.pop("bounds")
            text.update(
                {
                    "left": left,
                    "top": top,
                    "right": right,
                    "bottom": bottom,
                    "isMarquee": False,
                }
            )
            metadata["avikTexts"].append(text)
        locale_dir = case.get("directoryLocale", metadata["locale"])
        if direct:
            artifact_dir = root
        elif historical:
            artifact_dir = (
                root
                / "executions"
                / "SanitizedBuild"
                / "2026-07-19_10.00.00.000"
                / locale_dir
                / metadata["script"]
            )
        else:
            artifact_dir = root / locale_dir / metadata["script"]
        artifact_dir.mkdir(parents=True, exist_ok=True)
        png_path = artifact_dir / f"{pair_stem}.png"
        json_path = artifact_dir / f"{pair_stem}.json"
        if write_image:
            write_png(png_path, metadata["width"], metadata["height"])
        if write_json:
            json_path.write_text(
                json.dumps(metadata, ensure_ascii=False), encoding="utf-8"
            )
        return artifact_dir, png_path, json_path

    def run_case(
        self,
        temp_root: Path,
        case_name: str,
        *,
        require_en_xm: bool = False,
        direct: bool = False,
        historical: bool = False,
        write_reports: bool = False,
    ) -> dict:
        input_root = temp_root / "input"
        input_root.mkdir()
        self.materialize(
            input_root, case_name, direct=direct, historical=historical
        )
        contract = self.write_contract(temp_root)
        return AUDIT.run_audit(
            input_root,
            contract_path=contract,
            output_dir=temp_root / "reports",
            require_en_xm=require_en_xm,
            write_reports=write_reports,
        )

    def run_dynamic_case(self, temp_root: Path, case_name: str) -> dict:
        input_root = temp_root / "input"
        input_root.mkdir()
        self.materialize(input_root, case_name, direct=True)
        tag = "MotorolaQiraKnowledge_Main_TagsDropdown"
        contract = self.write_contract(
            temp_root,
            tags=[tag],
            dynamic_rules=[
                {
                    "tag": tag,
                    "classification": "dynamic_unscoped_non_localizable",
                    "exactValues": ["Identity", "Contact", "Education", "Work"],
                    "requireNoMessageIds": True,
                }
            ],
        )
        return AUDIT.run_audit(
            input_root,
            contract_path=contract,
            require_en_xm=False,
            write_reports=False,
        )

    @staticmethod
    def codes(report: dict) -> set[str]:
        return {issue["code"] for issue in report["issues"]}

    def test_checked_in_contract_has_exact_scope(self) -> None:
        contract = AUDIT.load_contract(TOOL_DIR / "contract.json")
        scope_path = (
            TOOL_DIR.parents[1]
            / "artifacts"
            / "avik-string-linking-investigation"
            / "SCOPE.md"
        )
        scope_tags = re.findall(
            r"^- `([^`]+)`$",
            scope_path.read_text(encoding="utf-8"),
            flags=re.MULTILINE,
        )
        self.assertEqual(104, len(contract["tags"]))
        self.assertEqual(104, len(set(contract["tags"])))
        self.assertEqual(scope_tags, contract["tags"])

    def test_checked_in_contract_excludes_live_camera(self) -> None:
        contract = AUDIT.load_contract(TOOL_DIR / "contract.json")
        self.assertNotIn(
            "MotorolaQiraFocusZone_Live_Camera", contract["tags"]
        )

    def test_checked_in_contract_has_strict_tags_dropdown_classification(self) -> None:
        contract = AUDIT.load_contract(TOOL_DIR / "contract.json")
        rule = contract["dynamicUnscopedData"][0]
        self.assertEqual(
            "MotorolaQiraKnowledge_Main_TagsDropdown", rule["tag"]
        )
        self.assertEqual(
            ["Identity", "Contact", "Education", "Work"],
            rule["exactValues"],
        )
        self.assertTrue(rule["requireNoMessageIds"])

    def test_known_good_leaf_links_and_report_formats(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            report = self.run_case(
                root,
                "known_good_leaf",
                require_en_xm=True,
                direct=True,
                write_reports=True,
            )
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertEqual(1, report["summary"]["resolvedNodeCount"])
            for extension in ("csv", "json", "md"):
                self.assertTrue(
                    (root / "reports" / f"avik-string-link-audit.{extension}").is_file()
                )

    def test_historical_workbench_layout_discovers_en_xm(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(
                Path(temp),
                "known_good_leaf",
                require_en_xm=True,
                historical=True,
            )
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertEqual(["en-XM"], report["summary"]["locales"])

    def test_parent_only_whole_screen_link_fails(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "parent_only_whole_screen")
            self.assertIn("near_full_screen_link", self.codes(report))
            self.assertEqual("FAIL", report["summary"]["status"])

    def test_missing_metadata_fails(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            input_root = root / "input"
            input_root.mkdir()
            self.materialize(
                input_root,
                "known_good_leaf",
                direct=True,
                write_json=False,
            )
            report = AUDIT.run_audit(
                input_root,
                contract_path=self.write_contract(root),
                require_en_xm=False,
                write_reports=False,
            )
            self.assertIn("missing_metadata", self.codes(report))

    def test_tag_mismatch_fails(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "tag_mismatch")
            codes = self.codes(report)
            self.assertIn("metadata_tag_contract_mismatch", codes)
            self.assertIn("missing_tags", codes)
            self.assertIn("extra_tags", codes)

    def test_duplicate_tag_fails(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            input_root = root / "input"
            input_root.mkdir()
            self.materialize(input_root, "known_good_leaf", stem="hash_a")
            self.materialize(input_root, "known_good_leaf", stem="hash_b")
            report = AUDIT.run_audit(
                input_root,
                contract_path=self.write_contract(root),
                require_en_xm=False,
                write_reports=False,
            )
            self.assertIn("duplicate_tag", self.codes(report))

    def test_invalid_and_out_of_screen_bounds_fail(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(
                Path(temp), "invalid_and_out_of_screen_bounds"
            )
            codes = self.codes(report)
            self.assertIn("invalid_bounds", codes)
            self.assertIn("out_of_screen_bounds", codes)

    def test_cross_locale_metadata_mismatch_fails(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(
                Path(temp), "cross_locale_metadata_mismatch"
            )
            self.assertIn("metadata_locale_mismatch", self.codes(report))

    def test_parameterized_string_with_literal_anchor_passes(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "parameterized_string")
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertNotIn("placeholder_wildcard_leakage", self.codes(report))

    def test_dynamic_nonlocalizable_text_is_unresolved_not_failed(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "dynamic_nonlocalizable_text")
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertEqual(1, report["summary"]["unresolvedNodeCount"])

    def test_tags_dropdown_exact_dynamic_set_passes(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_dynamic_case(
                Path(temp), "tags_dropdown_dynamic_exact"
            )
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertNotIn("no_resolved_links", self.codes(report))
            self.assertEqual(0, report["summary"]["unresolvedNodeCount"])
            self.assertEqual(
                4, report["summary"]["excludedDynamicUnscopedNodeCount"]
            )
            classification = report["artifacts"][0]["dataClassification"]
            self.assertEqual("PASS", classification["status"])
            self.assertEqual(
                "dynamic_unscoped_non_localizable",
                classification["classification"],
            )

    def test_tags_dropdown_missing_extra_and_unexpected_values_fail(self) -> None:
        scenarios = (
            "tags_dropdown_dynamic_missing",
            "tags_dropdown_dynamic_extra",
            "tags_dropdown_dynamic_unexpected",
        )
        for scenario in scenarios:
            with self.subTest(scenario=scenario), tempfile.TemporaryDirectory() as temp:
                report = self.run_dynamic_case(Path(temp), scenario)
                self.assertEqual("FAIL", report["summary"]["status"])
                self.assertIn(
                    "dynamic_unscoped_exact_set_mismatch", self.codes(report)
                )

    def test_tags_dropdown_dynamic_value_with_id_fails(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_dynamic_case(
                Path(temp), "tags_dropdown_dynamic_with_id"
            )
            self.assertEqual("FAIL", report["summary"]["status"])
            self.assertIn("dynamic_unscoped_has_message_ids", self.codes(report))

    def test_unrelated_no_link_screen_still_fails(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "unrelated_no_link_screen")
            self.assertEqual("FAIL", report["summary"]["status"])
            self.assertIn("no_resolved_links", self.codes(report))

    def test_linked_parent_container_is_release_gate(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "linked_parent_container")
            self.assertEqual("FAIL", report["summary"]["status"])
            issue = next(
                issue
                for issue in report["issues"]
                if issue["code"] == "parent_container_candidate"
            )
            self.assertTrue(issue["releaseGate"])

    def test_geometric_containment_with_unrelated_paths_is_not_parent(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(
                Path(temp), "geometric_containment_unrelated_paths"
            )
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertNotIn("parent_container_candidate", self.codes(report))
            self.assertNotIn(
                "legacy_geometric_overlap_unproven", self.codes(report)
            )

    def test_different_accessibility_roots_are_never_ancestors(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(
                Path(temp), "geometric_containment_different_roots"
            )
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertNotIn("parent_container_candidate", self.codes(report))

    def test_suppressed_unlinked_ancestor_is_ignored(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "suppressed_unlinked_ancestor")
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertNotIn("parent_container_candidate", self.codes(report))

    def test_legacy_geometry_is_explicit_unproven_warning(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "legacy_geometric_containment")
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertIn(
                "legacy_geometric_overlap_unproven", self.codes(report)
            )
            issue = next(
                issue
                for issue in report["issues"]
                if issue["code"] == "legacy_geometric_overlap_unproven"
            )
            self.assertFalse(issue["releaseGate"])

    def test_malformed_accessibility_path_is_release_gate(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "malformed_accessibility_path")
            self.assertEqual("FAIL", report["summary"]["status"])
            self.assertIn(
                "malformed_accessibility_node_path", self.codes(report)
            )

    def test_unlinked_accessibility_record_does_not_duplicate_linked_bounds(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(
                Path(temp), "linked_and_unlinked_same_bounds"
            )
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertNotIn("duplicate_bounds", self.codes(report))

    def test_duplicate_linked_bounds_remain_reported(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "duplicate_linked_bounds")
            self.assertIn("duplicate_bounds", self.codes(report))
            self.assertEqual(
                1, report["artifacts"][0]["duplicateBoundsCount"]
            )

    def test_rtl_coordinates_are_valid(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(Path(temp), "rtl_coordinates")
            self.assertEqual("PASS", report["summary"]["status"])
            self.assertNotIn("out_of_screen_bounds", self.codes(report))

    def test_corrupt_and_empty_artifacts_fail(self) -> None:
        scenarios = (
            "corrupt_png",
            "corrupt_idat",
            "empty_png",
            "corrupt_metadata",
            "empty_metadata",
        )
        for scenario in scenarios:
            with self.subTest(scenario=scenario), tempfile.TemporaryDirectory() as temp:
                root = Path(temp)
                input_root = root / "input"
                input_root.mkdir()
                _, png_path, json_path = self.materialize(
                    input_root, "known_good_leaf", direct=True
                )
                if scenario == "corrupt_png":
                    png_path.write_bytes(b"not-a-png")
                elif scenario == "corrupt_idat":
                    png_path.write_bytes(
                        AUDIT.PNG_SIGNATURE
                        + png_chunk(
                            b"IHDR",
                            struct.pack(">IIBBBBB", 100, 200, 8, 6, 0, 0, 0),
                        )
                        + png_chunk(b"IDAT", b"invalid-zlib-stream")
                        + png_chunk(b"IEND", b"")
                    )
                elif scenario == "empty_png":
                    png_path.write_bytes(b"")
                elif scenario == "corrupt_metadata":
                    json_path.write_text("{broken", encoding="utf-8")
                else:
                    json_path.write_bytes(b"")
                report = AUDIT.run_audit(
                    input_root,
                    contract_path=self.write_contract(root),
                    require_en_xm=False,
                    write_reports=False,
                )
                expected_code = (
                    "corrupt_png" if scenario == "corrupt_idat" else scenario
                )
                self.assertIn(expected_code, self.codes(report))
                self.assertEqual("FAIL", report["summary"]["status"])

    def test_app_id_and_script_mismatch_fail(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(
                Path(temp), "app_id_and_script_mismatch", direct=True
            )
            codes = self.codes(report)
            self.assertIn("app_id_mismatch", codes)
            self.assertIn("script_mismatch", codes)

    def test_placeholder_wildcard_leakage_fails(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            report = self.run_case(
                Path(temp), "placeholder_wildcard_leakage"
            )
            self.assertIn("placeholder_wildcard_leakage", self.codes(report))


if __name__ == "__main__":
    unittest.main()

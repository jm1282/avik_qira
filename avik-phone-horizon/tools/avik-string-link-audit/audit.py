#!/usr/bin/env python3
"""Deterministic, read-only AViK/Qira screenshot string-link audit."""

from __future__ import annotations

import argparse
import binascii
import csv
import datetime as dt
import json
import os
import re
import struct
import sys
import unicodedata
import zlib
from collections import Counter, defaultdict
from pathlib import Path
from typing import Any, Iterable


TOOL_DIR = Path(__file__).resolve().parent
DEFAULT_CONTRACT = TOOL_DIR / "contract.json"
REPORT_BASENAME = "avik-string-link-audit"
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"
LOCALE_RE = re.compile(
    r"^[a-z]{2,3}(?:[-_](?:[A-Za-z]{2,8}|[0-9]{3}))+$"
)
SCREEN_JSON_EXCLUSIONS = {
    "execution.json",
    "audit.json",
    f"{REPORT_BASENAME}.json",
}
NEAR_FULL_RATIO = 0.80
PAIR_MTIME_TOLERANCE_SECONDS = 300
ACCESSIBILITY_NODE_PATH_RE = re.compile(
    r"^(?:window--?\d+|active)(?:/\d+)*$"
)


def load_contract(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as stream:
        contract = json.load(stream)
    tags = contract.get("tags")
    if not isinstance(tags, list) or not tags or not all(
        isinstance(tag, str) and tag for tag in tags
    ):
        raise ValueError(f"Contract {path} has no valid non-empty tags list")
    duplicates = [tag for tag, count in Counter(tags).items() if count > 1]
    if duplicates:
        raise ValueError(f"Contract {path} contains duplicate tags: {duplicates}")
    dynamic_rules = contract.get("dynamicUnscopedData", [])
    if not isinstance(dynamic_rules, list):
        raise ValueError(f"Contract {path} dynamicUnscopedData must be an array")
    dynamic_tags: set[str] = set()
    for rule in dynamic_rules:
        if not isinstance(rule, dict):
            raise ValueError(f"Contract {path} has a non-object dynamic data rule")
        tag = rule.get("tag")
        values = rule.get("exactValues")
        if tag not in tags or tag in dynamic_tags:
            raise ValueError(
                f"Contract {path} has invalid/duplicate dynamic rule tag {tag!r}"
            )
        if (
            not isinstance(values, list)
            or not values
            or not all(isinstance(value, str) and value for value in values)
            or len(values) != len(set(values))
        ):
            raise ValueError(
                f"Contract {path} dynamic rule {tag!r} has invalid exactValues"
            )
        if rule.get("requireNoMessageIds") is not True:
            raise ValueError(
                f"Contract {path} dynamic rule {tag!r} must require no Message IDs"
            )
        dynamic_tags.add(tag)
    return contract


def _is_relative_to(path: Path, parent: Path) -> bool:
    try:
        path.relative_to(parent)
        return True
    except ValueError:
        return False


def discover_artifact_directories(
    root: Path, output_dir: Path | None = None
) -> list[Path]:
    """Find leaf-like directories containing screen PNG/JSON artifacts."""
    root = root.resolve()
    excluded = output_dir.resolve() if output_dir else None
    directories: list[Path] = []
    for dirpath, dirnames, filenames in os.walk(root):
        current = Path(dirpath).resolve()
        if excluded and _is_relative_to(current, excluded):
            dirnames[:] = []
            continue
        if excluded:
            dirnames[:] = [
                name
                for name in dirnames
                if not _is_relative_to((current / name).resolve(), excluded)
            ]
        names = set(filenames)
        pngs = [name for name in names if name.lower().endswith(".png")]
        jsons = [
            name
            for name in names
            if name.lower().endswith(".json")
            and name.lower() not in SCREEN_JSON_EXCLUSIONS
        ]
        if pngs or jsons:
            directories.append(current)
    return sorted(set(directories), key=lambda value: str(value).lower())


def read_png_dimensions(path: Path) -> tuple[int, int]:
    """Validate PNG framing/chunk CRCs and return IHDR dimensions."""
    data = path.read_bytes()
    if not data:
        raise ValueError("file is empty")
    if not data.startswith(PNG_SIGNATURE):
        raise ValueError("invalid PNG signature")
    offset = len(PNG_SIGNATURE)
    width = height = None
    saw_idat = False
    saw_iend = False
    idat_payloads: list[bytes] = []
    chunk_index = 0
    while offset < len(data):
        if offset + 12 > len(data):
            raise ValueError("truncated PNG chunk header")
        length = struct.unpack(">I", data[offset : offset + 4])[0]
        chunk_type = data[offset + 4 : offset + 8]
        payload_start = offset + 8
        payload_end = payload_start + length
        crc_end = payload_end + 4
        if crc_end > len(data):
            raise ValueError(
                f"truncated {chunk_type.decode('ascii', 'replace')} chunk"
            )
        payload = data[payload_start:payload_end]
        expected_crc = struct.unpack(">I", data[payload_end:crc_end])[0]
        actual_crc = binascii.crc32(chunk_type + payload) & 0xFFFFFFFF
        if expected_crc != actual_crc:
            raise ValueError(
                f"CRC mismatch in {chunk_type.decode('ascii', 'replace')} chunk"
            )
        if chunk_index == 0 and chunk_type != b"IHDR":
            raise ValueError("IHDR is not the first PNG chunk")
        if chunk_type == b"IHDR":
            if length != 13:
                raise ValueError("invalid IHDR length")
            width, height = struct.unpack(">II", payload[:8])
            if width <= 0 or height <= 0:
                raise ValueError("non-positive PNG dimensions")
        elif chunk_type == b"IDAT":
            saw_idat = True
            idat_payloads.append(payload)
        elif chunk_type == b"IEND":
            if length != 0:
                raise ValueError("invalid IEND length")
            saw_iend = True
            offset = crc_end
            break
        offset = crc_end
        chunk_index += 1
    if width is None or height is None:
        raise ValueError("missing IHDR")
    if not saw_idat:
        raise ValueError("missing IDAT")
    if not saw_iend:
        raise ValueError("missing IEND")
    if offset != len(data):
        raise ValueError("unexpected bytes after IEND")
    try:
        if not zlib.decompress(b"".join(idat_payloads)):
            raise ValueError("decompressed PNG image data is empty")
    except zlib.error as failure:
        raise ValueError(f"invalid compressed PNG image data: {failure}") from failure
    return width, height


def normalized_text(value: Any) -> str:
    if value is None:
        return ""
    text = unicodedata.normalize("NFC", str(value))
    return "".join(
        character
        for character in text
        if unicodedata.category(character) != "Cf"
    )


def parse_timestamp(value: Any) -> dt.datetime | None:
    if not isinstance(value, str) or not value.strip():
        return None
    raw = value.strip()
    candidates = [raw, raw.replace("Z", "+00:00")]
    for candidate in candidates:
        try:
            return dt.datetime.fromisoformat(candidate)
        except ValueError:
            pass
    for pattern in ("%Y-%m-%d %H:%M:%S", "%Y/%m/%d %H:%M:%S"):
        try:
            return dt.datetime.strptime(raw, pattern)
        except ValueError:
            pass
    return None


def infer_locale(directory: Path, root: Path) -> str | None:
    current = directory
    while True:
        if LOCALE_RE.match(current.name):
            return current.name.replace("_", "-")
        if current == root or current.parent == current:
            break
        current = current.parent
    return None


def is_external_owner_tag(tag: str) -> bool:
    return (
        tag.startswith("MotorolaQiraHome_Onboarding_Android")
        or tag == "MotorolaQiraFocusZone_Live_AndroidMicrophonePermission"
        or tag == "MotorolaQiraFocusZone_Live_EnablePermission"
    )


def _integer(value: Any) -> int | None:
    return value if isinstance(value, int) and not isinstance(value, bool) else None


def _issue(
    issues: list[dict[str, Any]],
    local_codes: list[str],
    code: str,
    message: str,
    *,
    gate: bool,
    path: Path | str | None = None,
    tag: str | None = None,
    locale: str | None = None,
) -> None:
    issues.append(
        {
            "code": code,
            "severity": "error" if gate else "warning",
            "releaseGate": gate,
            "message": message,
            "path": str(path) if path else None,
            "tag": tag,
            "locale": locale,
        }
    )
    local_codes.append(code)


def _message_ids(record: dict[str, Any]) -> tuple[list[str], bool]:
    raw = record.get("messageIds", [])
    if not isinstance(raw, list):
        return [], False
    valid = [
        value
        for value in raw
        if isinstance(value, str) and value and not any(ch.isspace() for ch in value)
    ]
    return valid, len(valid) == len(raw)


def _contains(parent: tuple[int, int, int, int], child: tuple[int, int, int, int]) -> bool:
    return (
        parent != child
        and parent[0] <= child[0]
        and parent[1] <= child[1]
        and parent[2] >= child[2]
        and parent[3] >= child[3]
    )


def _strict_path_ancestor(parent: str, child: str) -> bool:
    return bool(parent and child and child.startswith(parent + "/"))


def _dynamic_unscoped_rule(
    contract: dict[str, Any], tag: Any
) -> dict[str, Any] | None:
    if not isinstance(tag, str):
        return None
    for rule in contract.get("dynamicUnscopedData", []):
        if isinstance(rule, dict) and rule.get("tag") == tag:
            return rule
    return None


def audit_artifact(
    stem: str,
    png_path: Path | None,
    json_path: Path | None,
    directory: Path,
    root: Path,
    contract: dict[str, Any],
    expected_script: str | None,
    issues: list[dict[str, Any]],
) -> dict[str, Any]:
    local_codes: list[str] = []
    inferred = infer_locale(directory, root)
    result: dict[str, Any] = {
        "directory": str(directory),
        "stem": stem,
        "png": str(png_path) if png_path else None,
        "json": str(json_path) if json_path else None,
        "tag": None,
        "hash": None,
        "locale": inferred,
        "appId": None,
        "script": None,
        "pngWidth": None,
        "pngHeight": None,
        "metadataWidth": None,
        "metadataHeight": None,
        "textCount": 0,
        "resolvedNodeCount": 0,
        "rawUnresolvedNodeCount": 0,
        "unresolvedNodeCount": 0,
        "excludedDynamicUnscopedNodeCount": 0,
        "resolvedMessageIds": [],
        "duplicateBoundsCount": 0,
        "duplicateLinkCount": 0,
        "nearFullLinkedCount": 0,
        "containerCandidateCount": 0,
        "legacyGeometryOverlapCount": 0,
        "dataClassification": None,
        "issueCodes": local_codes,
        "order": None,
        "modtime": None,
    }

    if png_path is None:
        _issue(
            issues,
            local_codes,
            "missing_png",
            f"Metadata {json_path} has no matching {stem}.png",
            gate=True,
            path=json_path,
            locale=inferred,
        )
    else:
        try:
            png_width, png_height = read_png_dimensions(png_path)
            result["pngWidth"] = png_width
            result["pngHeight"] = png_height
        except (OSError, ValueError) as failure:
            code = "empty_png" if png_path.exists() and png_path.stat().st_size == 0 else "corrupt_png"
            _issue(
                issues,
                local_codes,
                code,
                f"PNG {png_path} is not usable: {failure}",
                gate=True,
                path=png_path,
                locale=inferred,
            )

    if json_path is None:
        _issue(
            issues,
            local_codes,
            "missing_metadata",
            f"Screenshot {png_path} has no matching {stem}.json",
            gate=True,
            path=png_path,
            locale=inferred,
        )
        return result

    metadata: dict[str, Any] | None = None
    try:
        if json_path.stat().st_size == 0:
            raise ValueError("file is empty")
        loaded = json.loads(json_path.read_text(encoding="utf-8"))
        if not isinstance(loaded, dict):
            raise ValueError("top-level JSON is not an object")
        metadata = loaded
    except (OSError, UnicodeError, json.JSONDecodeError, ValueError) as failure:
        code = "empty_metadata" if json_path.exists() and json_path.stat().st_size == 0 else "corrupt_metadata"
        _issue(
            issues,
            local_codes,
            code,
            f"Metadata {json_path} is not usable: {failure}",
            gate=True,
            path=json_path,
            locale=inferred,
        )
        return result

    tag = metadata.get("name")
    metadata_hash = metadata.get("hash")
    metadata_locale = metadata.get("locale")
    app_id = metadata.get("appId")
    script = metadata.get("script")
    result.update(
        {
            "tag": tag if isinstance(tag, str) else None,
            "hash": metadata_hash if isinstance(metadata_hash, str) else None,
            "locale": metadata_locale if isinstance(metadata_locale, str) else inferred,
            "appId": app_id if isinstance(app_id, str) else None,
            "script": script if isinstance(script, str) else None,
            "order": metadata.get("order"),
            "modtime": metadata.get("modtime"),
        }
    )
    issue_tag = result["tag"]
    issue_locale = result["locale"]

    expected_tags = set(contract["tags"])
    if not isinstance(tag, str) or not tag:
        _issue(
            issues,
            local_codes,
            "missing_metadata_tag",
            f"Metadata {json_path} has no non-empty name/tag",
            gate=True,
            path=json_path,
            locale=issue_locale,
        )
    elif tag not in expected_tags:
        _issue(
            issues,
            local_codes,
            "metadata_tag_contract_mismatch",
            f"Metadata tag {tag!r} is not in the checked-in capture contract",
            gate=True,
            path=json_path,
            tag=tag,
            locale=issue_locale,
        )

    if metadata_hash != stem:
        _issue(
            issues,
            local_codes,
            "metadata_hash_mismatch",
            f"Metadata hash {metadata_hash!r} does not match pair filename {stem!r}",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )

    if inferred and metadata_locale != inferred:
        _issue(
            issues,
            local_codes,
            "metadata_locale_mismatch",
            f"Metadata locale {metadata_locale!r} conflicts with directory locale {inferred!r}",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )

    expected_app_id = contract.get("expectedAppId")
    if expected_app_id and app_id != expected_app_id:
        _issue(
            issues,
            local_codes,
            "app_id_mismatch",
            f"Metadata appId {app_id!r}; expected {expected_app_id!r}",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )

    required_prefix = contract.get("requiredScriptPrefix")
    if expected_script:
        script_matches = script == expected_script
        expected_description = repr(expected_script)
    else:
        script_matches = isinstance(script, str) and (
            not required_prefix or script.startswith(required_prefix)
        )
        expected_description = f"prefix {required_prefix!r}"
    if not script_matches:
        _issue(
            issues,
            local_codes,
            "script_mismatch",
            f"Metadata script {script!r}; expected {expected_description}",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )
    if directory.name.startswith("avik.") and script != directory.name:
        _issue(
            issues,
            local_codes,
            "metadata_script_directory_mismatch",
            f"Metadata script {script!r} conflicts with directory {directory.name!r}",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )

    metadata_width = _integer(metadata.get("width"))
    metadata_height = _integer(metadata.get("height"))
    result["metadataWidth"] = metadata_width
    result["metadataHeight"] = metadata_height
    if not metadata_width or not metadata_height or metadata_width <= 0 or metadata_height <= 0:
        _issue(
            issues,
            local_codes,
            "invalid_metadata_dimensions",
            f"Metadata dimensions are invalid: {metadata_width}x{metadata_height}",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )
    elif (
        result["pngWidth"] is not None
        and (metadata_width, metadata_height)
        != (result["pngWidth"], result["pngHeight"])
    ):
        _issue(
            issues,
            local_codes,
            "dimension_mismatch",
            "Metadata dimensions "
            f"{metadata_width}x{metadata_height} do not match PNG "
            f"{result['pngWidth']}x{result['pngHeight']}",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )

    texts = metadata.get("avikTexts")
    if not isinstance(texts, list):
        _issue(
            issues,
            local_codes,
            "missing_avik_texts",
            "Metadata avikTexts is missing or is not an array",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )
        return result
    result["textCount"] = len(texts)
    if not texts:
        _issue(
            issues,
            local_codes,
            "empty_owner_evidence",
            "Metadata contains no owner-scoped text/description records",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )

    forbidden_ids = {
        entry.get("messageId")
        for entry in contract.get("forbiddenPlaceholderOnlyResources", [])
        if isinstance(entry, dict) and isinstance(entry.get("messageId"), str)
    }
    dynamic_rule = _dynamic_unscoped_rule(contract, tag)
    dynamic_observed: list[tuple[str, list[str], int]] = []
    linked_bounds_counter: Counter[tuple[int, int, int, int]] = Counter()
    link_counter: Counter[tuple[Any, ...]] = Counter()
    valid_records: list[dict[str, Any]] = []
    unique_ids: set[str] = set()
    for index, raw_record in enumerate(texts):
        if not isinstance(raw_record, dict):
            _issue(
                issues,
                local_codes,
                "invalid_text_record",
                f"avikTexts[{index}] is not an object",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )
            continue
        coordinates = tuple(
            _integer(raw_record.get(name))
            for name in ("left", "top", "right", "bottom")
        )
        if any(value is None for value in coordinates):
            _issue(
                issues,
                local_codes,
                "invalid_bounds",
                f"avikTexts[{index}] has non-integer bounds {coordinates}",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )
            continue
        left, top, right, bottom = coordinates
        if right <= left or bottom <= top:
            _issue(
                issues,
                local_codes,
                "invalid_bounds",
                f"avikTexts[{index}] has non-positive bounds {coordinates}",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )
        if (
            metadata_width
            and metadata_height
            and (
                left < 0
                or top < 0
                or right > metadata_width
                or bottom > metadata_height
            )
        ):
            _issue(
                issues,
                local_codes,
                "out_of_screen_bounds",
                f"avikTexts[{index}] bounds {coordinates} exceed "
                f"0,0,{metadata_width},{metadata_height}",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )
        ids, ids_valid = _message_ids(raw_record)
        if not ids_valid:
            _issue(
                issues,
                local_codes,
                "invalid_message_ids",
                f"avikTexts[{index}] has malformed messageIds",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )
        if ids:
            result["resolvedNodeCount"] += 1
            unique_ids.update(ids)
        else:
            result["rawUnresolvedNodeCount"] += 1
            result["unresolvedNodeCount"] += 1
        if dynamic_rule is not None:
            dynamic_observed.append(
                (normalized_text(raw_record.get("text")), ids, index)
            )
        leaked = sorted(forbidden_ids.intersection(ids))
        if leaked:
            _issue(
                issues,
                local_codes,
                "placeholder_wildcard_leakage",
                f"avikTexts[{index}] carries placeholder-only Message ID(s) {leaked}",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )
        bounds = (left, top, right, bottom)
        source_kind = raw_record.get("qiraAccessibilitySourceKind", "")
        raw_node_path = raw_record.get("qiraAccessibilityNodePath")
        node_path_present = raw_node_path is not None
        node_path = raw_node_path if isinstance(raw_node_path, str) else ""
        node_path_valid = (
            isinstance(raw_node_path, str)
            and bool(ACCESSIBILITY_NODE_PATH_RE.fullmatch(raw_node_path))
        )
        if node_path_present and not node_path_valid:
            _issue(
                issues,
                local_codes,
                "malformed_accessibility_node_path",
                f"avikTexts[{index}] has malformed qiraAccessibilityNodePath "
                f"{raw_node_path!r}",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )
        if ids:
            linked_bounds_counter[bounds] += 1
        signature = (
            normalized_text(raw_record.get("text")),
            source_kind,
            bounds,
            tuple(sorted(ids)),
        )
        if ids:
            link_counter[signature] += 1
        valid_records.append(
            {
                "bounds": bounds,
                "ids": ids,
                "text": normalized_text(raw_record.get("text")),
                "sourceKind": source_kind,
                "nodePath": node_path,
                "nodePathPresent": node_path_present,
                "nodePathValid": node_path_valid,
                "index": index,
            }
        )

    dynamic_classification_valid = False
    if dynamic_rule is not None:
        expected_values = [
            normalized_text(value) for value in dynamic_rule["exactValues"]
        ]
        observed_values = [value for value, _, _ in dynamic_observed]
        expected_counter = Counter(expected_values)
        observed_counter = Counter(observed_values)
        values_valid = observed_counter == expected_counter
        rows_with_ids = [
            {"index": index, "text": value, "messageIds": ids}
            for value, ids, index in dynamic_observed
            if ids
        ]
        ids_valid = not rows_with_ids
        dynamic_classification_valid = values_valid and ids_valid
        missing_values = sorted((expected_counter - observed_counter).elements())
        extra_values = sorted((observed_counter - expected_counter).elements())
        result["dataClassification"] = {
            "classification": dynamic_rule.get("classification"),
            "status": "PASS" if dynamic_classification_valid else "FAIL",
            "expectedValues": expected_values,
            "observedValues": observed_values,
            "missingValues": missing_values,
            "extraValues": extra_values,
            "rowsWithMessageIds": rows_with_ids,
        }
        if dynamic_classification_valid:
            excluded_count = len(dynamic_observed)
            result["excludedDynamicUnscopedNodeCount"] = excluded_count
            result["unresolvedNodeCount"] = max(
                0, result["unresolvedNodeCount"] - excluded_count
            )
        if not values_valid:
            _issue(
                issues,
                local_codes,
                "dynamic_unscoped_exact_set_mismatch",
                "Dynamic/unscoped classification requires exact values "
                f"{expected_values}; missing={missing_values}, extra={extra_values}",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )
        if not ids_valid:
            _issue(
                issues,
                local_codes,
                "dynamic_unscoped_has_message_ids",
                "Dynamic/unscoped rows must have no Message IDs: "
                f"{rows_with_ids}",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )

    result["resolvedMessageIds"] = sorted(unique_ids)
    duplicate_bounds = sum(
        count - 1 for count in linked_bounds_counter.values() if count > 1
    )
    duplicate_links = sum(count - 1 for count in link_counter.values() if count > 1)
    result["duplicateBoundsCount"] = duplicate_bounds
    result["duplicateLinkCount"] = duplicate_links
    if duplicate_bounds:
        _issue(
            issues,
            local_codes,
            "duplicate_bounds",
            f"{duplicate_bounds} duplicate linked-overlay bounds occurrence(s)",
            gate=False,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )
    if duplicate_links:
        _issue(
            issues,
            local_codes,
            "duplicate_link",
            f"{duplicate_links} exact duplicate text/bounds/Message-ID link(s)",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )

    if metadata_width and metadata_height:
        screen_area = metadata_width * metadata_height
        for record in valid_records:
            left, top, right, bottom = record["bounds"]
            area = max(0, right - left) * max(0, bottom - top)
            if record["ids"] and area / screen_area >= NEAR_FULL_RATIO:
                result["nearFullLinkedCount"] += 1
                _issue(
                    issues,
                    local_codes,
                    "near_full_screen_link",
                    f"Linked node {record['index']} covers {area / screen_area:.3%} "
                    f"of the screenshot at {record['bounds']}",
                    gate=True,
                    path=json_path,
                    tag=issue_tag,
                    locale=issue_locale,
                )
            actual_ancestor = False
            legacy_geometry = False
            if record["ids"]:
                for other in valid_records:
                    if other is record or not other["ids"]:
                        continue
                    if (
                        record["nodePathValid"]
                        and other["nodePathValid"]
                        and _strict_path_ancestor(
                            record["nodePath"], other["nodePath"]
                        )
                    ):
                        actual_ancestor = True
                    elif (
                        not record["nodePathPresent"]
                        or not other["nodePathPresent"]
                    ) and _contains(record["bounds"], other["bounds"]):
                        legacy_geometry = True
            if actual_ancestor:
                result["containerCandidateCount"] += 1
            elif legacy_geometry:
                result["legacyGeometryOverlapCount"] += 1
        if result["containerCandidateCount"]:
            _issue(
                issues,
                local_codes,
                "parent_container_candidate",
                f"{result['containerCandidateCount']} linked geometric parent/container "
                "candidate(s) contain independently linked children",
                gate=True,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )
        if result["legacyGeometryOverlapCount"]:
            _issue(
                issues,
                local_codes,
                "legacy_geometric_overlap_unproven",
                f"{result['legacyGeometryOverlapCount']} linked legacy record(s) "
                "geometrically contain linked records but have no ancestry path; "
                "hierarchy relationship is unproven",
                gate=False,
                path=json_path,
                tag=issue_tag,
                locale=issue_locale,
            )

    if (
        isinstance(tag, str)
        and tag
        and not is_external_owner_tag(tag)
        and texts
        and result["resolvedNodeCount"] == 0
        and not dynamic_classification_valid
    ):
        _issue(
            issues,
            local_codes,
            "no_resolved_links",
            "Qira-owned screen has accessibility evidence but no resolved Message IDs",
            gate=True,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )

    if png_path and png_path.exists():
        try:
            mtime_delta = abs(
                png_path.stat().st_mtime - json_path.stat().st_mtime
            )
            if mtime_delta > PAIR_MTIME_TOLERANCE_SECONDS:
                _issue(
                    issues,
                    local_codes,
                    "stale_pair_timestamp",
                    f"PNG/JSON filesystem timestamps differ by {mtime_delta:.0f}s",
                    gate=False,
                    path=json_path,
                    tag=issue_tag,
                    locale=issue_locale,
                )
        except OSError:
            pass
    if metadata.get("modtime") is not None and parse_timestamp(metadata.get("modtime")) is None:
        _issue(
            issues,
            local_codes,
            "invalid_capture_timestamp",
            f"Metadata modtime {metadata.get('modtime')!r} is not parseable",
            gate=False,
            path=json_path,
            tag=issue_tag,
            locale=issue_locale,
        )
    return result


def _group_stale_order_issues(
    artifacts: list[dict[str, Any]], issues: list[dict[str, Any]]
) -> None:
    ordered: list[tuple[int, dt.datetime, dict[str, Any]]] = []
    for artifact in artifacts:
        order = _integer(artifact.get("order"))
        timestamp = parse_timestamp(artifact.get("modtime"))
        if order is not None and timestamp is not None:
            ordered.append((order, timestamp, artifact))
    ordered.sort(key=lambda item: item[0])
    for previous, current in zip(ordered, ordered[1:]):
        if current[1] < previous[1] - dt.timedelta(seconds=5):
            local_codes = current[2]["issueCodes"]
            _issue(
                issues,
                local_codes,
                "stale_capture_order",
                f"Capture order {current[0]} timestamp {current[1].isoformat()} "
                f"predates order {previous[0]} timestamp {previous[1].isoformat()}",
                gate=True,
                path=current[2].get("json"),
                tag=current[2].get("tag"),
                locale=current[2].get("locale"),
            )


def run_audit(
    input_path: Path,
    *,
    contract_path: Path = DEFAULT_CONTRACT,
    output_dir: Path | None = None,
    expected_script: str | None = None,
    require_en_xm: bool = True,
    write_reports: bool = True,
) -> dict[str, Any]:
    root = input_path.resolve()
    if not root.exists() or not root.is_dir():
        raise ValueError(f"Input directory does not exist: {root}")
    contract = load_contract(contract_path.resolve())
    directories = discover_artifact_directories(root, output_dir)
    issues: list[dict[str, Any]] = []
    artifacts: list[dict[str, Any]] = []
    groups: list[dict[str, Any]] = []

    if not directories:
        local: list[str] = []
        _issue(
            issues,
            local,
            "no_artifacts",
            f"No screen PNG/JSON artifact directories found under {root}",
            gate=True,
            path=root,
        )

    expected_tags = set(contract["tags"])
    for directory in directories:
        png_by_stem = {
            path.stem: path
            for path in directory.iterdir()
            if path.is_file() and path.suffix.lower() == ".png"
        }
        json_by_stem = {
            path.stem: path
            for path in directory.iterdir()
            if path.is_file()
            and path.suffix.lower() == ".json"
            and path.name.lower() not in SCREEN_JSON_EXCLUSIONS
        }
        group_artifacts: list[dict[str, Any]] = []
        for stem in sorted(
            set(png_by_stem).union(json_by_stem), key=str.lower
        ):
            artifact = audit_artifact(
                stem,
                png_by_stem.get(stem),
                json_by_stem.get(stem),
                directory,
                root,
                contract,
                expected_script,
                issues,
            )
            artifacts.append(artifact)
            group_artifacts.append(artifact)

        tags = [
            artifact["tag"]
            for artifact in group_artifacts
            if isinstance(artifact.get("tag"), str) and artifact["tag"]
        ]
        tag_counts = Counter(tags)
        locale_values = sorted(
            {
                artifact["locale"]
                for artifact in group_artifacts
                if isinstance(artifact.get("locale"), str)
                and artifact["locale"]
            }
        )
        group_locale = (
            infer_locale(directory, root)
            or (locale_values[0] if len(locale_values) == 1 else None)
        )
        group_issue_codes: list[str] = []
        for tag, count in sorted(tag_counts.items()):
            if count > 1:
                _issue(
                    issues,
                    group_issue_codes,
                    "duplicate_tag",
                    f"Tag {tag!r} occurs {count} times in execution directory",
                    gate=True,
                    path=directory,
                    tag=tag,
                    locale=group_locale,
                )
        missing_tags = sorted(expected_tags.difference(tag_counts))
        extra_tags = sorted(set(tag_counts).difference(expected_tags))
        if missing_tags:
            _issue(
                issues,
                group_issue_codes,
                "missing_tags",
                f"Execution is missing {len(missing_tags)} contract tag(s): "
                + ", ".join(missing_tags),
                gate=True,
                path=directory,
                locale=group_locale,
            )
        if extra_tags:
            _issue(
                issues,
                group_issue_codes,
                "extra_tags",
                f"Execution has {len(extra_tags)} out-of-contract tag(s): "
                + ", ".join(extra_tags),
                gate=True,
                path=directory,
                locale=group_locale,
            )
        if len(locale_values) > 1:
            _issue(
                issues,
                group_issue_codes,
                "mixed_metadata_locales",
                f"One artifact directory contains multiple metadata locales: {locale_values}",
                gate=True,
                path=directory,
                locale=group_locale,
            )
        _group_stale_order_issues(group_artifacts, issues)
        groups.append(
            {
                "directory": str(directory),
                "locale": group_locale,
                "artifactCount": len(group_artifacts),
                "uniqueTagCount": len(tag_counts),
                "missingTags": missing_tags,
                "extraTags": extra_tags,
                "duplicateTags": {
                    tag: count for tag, count in tag_counts.items() if count > 1
                },
                "issueCodes": group_issue_codes,
            }
        )

    locales = sorted(
        {
            artifact["locale"]
            for artifact in artifacts
            if isinstance(artifact.get("locale"), str) and artifact["locale"]
        }
    )
    if require_en_xm and "en-XM" not in locales:
        local = []
        _issue(
            issues,
            local,
            "missing_en_xm",
            "No en-XM structural-baseline execution was discovered",
            gate=True,
            path=root,
        )

    gate_failures = [issue for issue in issues if issue["releaseGate"]]
    warnings = [issue for issue in issues if not issue["releaseGate"]]
    summary = {
        "status": "FAIL" if gate_failures else "PASS",
        "contractTagCount": len(contract["tags"]),
        "artifactDirectoryCount": len(directories),
        "artifactCount": len(artifacts),
        "pairedArtifactCount": sum(
            1 for artifact in artifacts if artifact["png"] and artifact["json"]
        ),
        "locales": locales,
        "resolvedNodeCount": sum(
            artifact["resolvedNodeCount"] for artifact in artifacts
        ),
        "rawUnresolvedNodeCount": sum(
            artifact["rawUnresolvedNodeCount"] for artifact in artifacts
        ),
        "unresolvedNodeCount": sum(
            artifact["unresolvedNodeCount"] for artifact in artifacts
        ),
        "excludedDynamicUnscopedNodeCount": sum(
            artifact["excludedDynamicUnscopedNodeCount"]
            for artifact in artifacts
        ),
        "uniqueResolvedMessageIdCount": len(
            {
                message_id
                for artifact in artifacts
                for message_id in artifact["resolvedMessageIds"]
            }
        ),
        "validatedDynamicUnscopedScreenCount": sum(
            1
            for artifact in artifacts
            if isinstance(artifact.get("dataClassification"), dict)
            and artifact["dataClassification"].get("status") == "PASS"
        ),
        "failedDynamicUnscopedScreenCount": sum(
            1
            for artifact in artifacts
            if isinstance(artifact.get("dataClassification"), dict)
            and artifact["dataClassification"].get("status") == "FAIL"
        ),
        "linkedAncestryContainerCount": sum(
            artifact["containerCandidateCount"] for artifact in artifacts
        ),
        "legacyGeometryOverlapCount": sum(
            artifact["legacyGeometryOverlapCount"] for artifact in artifacts
        ),
        "releaseGateFailureCount": len(gate_failures),
        "warningCount": len(warnings),
    }
    report = {
        "schemaVersion": 3,
        "generatedAtUtc": dt.datetime.now(dt.timezone.utc)
        .isoformat()
        .replace("+00:00", "Z"),
        "input": str(root),
        "contract": str(contract_path.resolve()),
        "readOnlyArtifactAudit": True,
        "summary": summary,
        "groups": groups,
        "artifacts": artifacts,
        "issues": issues,
    }
    if write_reports:
        destination = (
            output_dir.resolve()
            if output_dir
            else (Path.cwd() / f"{REPORT_BASENAME}-report").resolve()
        )
        write_report_files(report, destination)
        report["reportDirectory"] = str(destination)
    return report


def write_report_files(report: dict[str, Any], output_dir: Path) -> None:
    output_dir.mkdir(parents=True, exist_ok=True)
    json_path = output_dir / f"{REPORT_BASENAME}.json"
    csv_path = output_dir / f"{REPORT_BASENAME}.csv"
    markdown_path = output_dir / f"{REPORT_BASENAME}.md"
    json_path.write_text(
        json.dumps(report, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )
    fields = [
        "directory",
        "stem",
        "tag",
        "hash",
        "locale",
        "appId",
        "script",
        "pngWidth",
        "pngHeight",
        "metadataWidth",
        "metadataHeight",
        "textCount",
        "resolvedNodeCount",
        "rawUnresolvedNodeCount",
        "unresolvedNodeCount",
        "excludedDynamicUnscopedNodeCount",
        "duplicateBoundsCount",
        "duplicateLinkCount",
        "nearFullLinkedCount",
        "containerCandidateCount",
        "legacyGeometryOverlapCount",
        "dataClassification",
        "issueCodes",
        "png",
        "json",
    ]
    with csv_path.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=fields, extrasaction="ignore")
        writer.writeheader()
        for artifact in report["artifacts"]:
            row = dict(artifact)
            row["issueCodes"] = ";".join(artifact["issueCodes"])
            row["dataClassification"] = (
                json.dumps(
                    artifact["dataClassification"],
                    ensure_ascii=False,
                    sort_keys=True,
                )
                if artifact["dataClassification"] is not None
                else ""
            )
            writer.writerow(row)

    summary = report["summary"]
    lines = [
        "# AViK/Qira String-Link Audit",
        "",
        f"- Status: **{summary['status']}**",
        f"- Input: `{report['input']}`",
        f"- Contract tags: {summary['contractTagCount']}",
        f"- Artifact directories: {summary['artifactDirectoryCount']}",
        f"- Artifacts / paired: {summary['artifactCount']} / "
        f"{summary['pairedArtifactCount']}",
        f"- Locales: {', '.join(summary['locales']) or '<none>'}",
        f"- Resolved / static-unresolved nodes: {summary['resolvedNodeCount']} / "
        f"{summary['unresolvedNodeCount']}",
        "- Raw unresolved / excluded dynamic-unscoped nodes: "
        f"{summary['rawUnresolvedNodeCount']} / "
        f"{summary['excludedDynamicUnscopedNodeCount']}",
        "- Validated / failed dynamic-unscoped screens: "
        f"{summary['validatedDynamicUnscopedScreenCount']} / "
        f"{summary['failedDynamicUnscopedScreenCount']}",
        "- Linked ancestry containers / legacy geometric overlaps: "
        f"{summary['linkedAncestryContainerCount']} / "
        f"{summary['legacyGeometryOverlapCount']}",
        f"- Release-gate failures: {summary['releaseGateFailureCount']}",
        f"- Warnings: {summary['warningCount']}",
        "",
        "## Actionable findings",
        "",
    ]
    if not report["issues"]:
        lines.append("- None.")
    else:
        for issue in report["issues"]:
            label = "FAIL" if issue["releaseGate"] else "WARN"
            location = f" (`{issue['path']}`)" if issue.get("path") else ""
            lines.append(
                f"- **{label} {issue['code']}**: {issue['message']}{location}"
            )
    markdown_path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description=(
            "Read-only release-gate audit for AViK/Qira PNG/JSON execution artifacts"
        )
    )
    parser.add_argument(
        "input",
        type=Path,
        help=(
            "Workbench execution root/layout or one completed script execution directory"
        ),
    )
    parser.add_argument(
        "-o",
        "--output-dir",
        type=Path,
        default=Path.cwd() / f"{REPORT_BASENAME}-report",
        help="Destination for CSV, JSON, and Markdown reports",
    )
    parser.add_argument(
        "--contract",
        type=Path,
        default=DEFAULT_CONTRACT,
        help="Checked-in tag/identity contract",
    )
    parser.add_argument(
        "--expected-script",
        help=(
            "Require one exact metadata script instead of the contract's qira wrapper prefix"
        ),
    )
    parser.add_argument(
        "--allow-missing-en-xm",
        action="store_true",
        help="Do not gate on absence of the en-XM structural baseline",
    )
    return parser


def main(argv: Iterable[str] | None = None) -> int:
    args = build_parser().parse_args(argv)
    try:
        contract = load_contract(args.contract.resolve())
        if args.contract.resolve() == DEFAULT_CONTRACT.resolve():
            if len(contract["tags"]) != 104:
                raise ValueError(
                    f"Checked-in contract must contain exactly 104 tags; "
                    f"found {len(contract['tags'])}"
                )
        report = run_audit(
            args.input,
            contract_path=args.contract,
            output_dir=args.output_dir,
            expected_script=args.expected_script,
            require_en_xm=not args.allow_missing_en_xm,
            write_reports=True,
        )
    except (OSError, ValueError, json.JSONDecodeError) as failure:
        print(f"AUDIT ERROR: {failure}", file=sys.stderr)
        return 2

    summary = report["summary"]
    print(
        f"AViK/Qira audit {summary['status']}: "
        f"artifacts={summary['artifactCount']}, "
        f"paired={summary['pairedArtifactCount']}, "
        f"gate_failures={summary['releaseGateFailureCount']}, "
        f"warnings={summary['warningCount']}"
    )
    print(f"Reports: {report['reportDirectory']}")
    for issue in report["issues"]:
        if issue["releaseGate"]:
            print(
                f"FAIL {issue['code']}: {issue['message']} "
                f"[{issue.get('path') or report['input']}]",
                file=sys.stderr,
            )
    return 1 if summary["releaseGateFailureCount"] else 0


if __name__ == "__main__":
    raise SystemExit(main())

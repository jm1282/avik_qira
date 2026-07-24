#!/usr/bin/env python3
"""Read-only comparison of one failed and one control AViK capture."""

from __future__ import annotations

import argparse
import csv
import json
import xml.etree.ElementTree as ET
from collections import Counter
from pathlib import Path
from typing import Any


GLOBAL_QIRA_MESSAGE_ID = "5HQQfXNKx50UU0Nn1RHqH5"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--strings-csv", required=True, type=Path)
    parser.add_argument("--locale", default="en-XM")
    parser.add_argument("--failed-tag", required=True)
    parser.add_argument("--control-tag", required=True)
    parser.add_argument("--failed-xml", required=True, type=Path)
    parser.add_argument("--control-xml", required=True, type=Path)
    parser.add_argument(
        "--hierarchy-evidence-status",
        default="SUPPLEMENTAL_NOT_EXECUTION_PAIRED",
        choices=("EXECUTION_PAIRED", "SUPPLEMENTAL_NOT_EXECUTION_PAIRED"),
    )
    parser.add_argument("--width", type=int, required=True)
    parser.add_argument("--height", type=int, required=True)
    parser.add_argument("--json-out", required=True, type=Path)
    parser.add_argument("--markdown-out", required=True, type=Path)
    return parser.parse_args()


def parse_ids(value: str) -> list[str]:
    try:
        parsed = json.loads(value)
    except (TypeError, json.JSONDecodeError):
        return []
    return [item for item in parsed if isinstance(item, str) and item]


def bounds(row: dict[str, str]) -> tuple[int, int, int, int]:
    return tuple(int(row[key]) for key in ("left", "top", "right", "bottom"))


def area(rect: tuple[int, int, int, int]) -> int:
    left, top, right, bottom = rect
    return max(0, right - left) * max(0, bottom - top)


def contains(
    outer: tuple[int, int, int, int],
    inner: tuple[int, int, int, int],
) -> bool:
    return (
        outer != inner
        and outer[0] <= inner[0]
        and outer[1] <= inner[1]
        and outer[2] >= inner[2]
        and outer[3] >= inner[3]
    )


def load_screen_rows(
    csv_path: Path,
    locale: str,
    tag: str,
) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []
    with csv_path.open("r", encoding="utf-8-sig", newline="") as handle:
        for raw in csv.DictReader(handle):
            if raw["locale_id"] != locale or raw["tag"] != tag:
                continue
            row: dict[str, Any] = dict(raw)
            row["bounds"] = bounds(raw)
            row["message_ids_parsed"] = parse_ids(raw["message_ids"])
            rows.append(row)
    if not rows:
        raise ValueError(f"No rows found for locale={locale!r}, tag={tag!r}")
    return rows


def screen_metrics(
    rows: list[dict[str, Any]],
    width: int,
    height: int,
) -> dict[str, Any]:
    screen_area = width * height
    rectangles = [row["bounds"] for row in rows]
    message_ids = [item for row in rows for item in row["message_ids_parsed"]]
    specific_ids = [item for item in message_ids if item != GLOBAL_QIRA_MESSAGE_ID]
    invalid = [
        row for row in rows
        if row["bounds"][2] <= row["bounds"][0]
        or row["bounds"][3] <= row["bounds"][1]
    ]
    out_of_screen = [
        row for row in rows
        if row["bounds"][0] < 0
        or row["bounds"][1] < 0
        or row["bounds"][2] > width
        or row["bounds"][3] > height
    ]
    near_full = [
        row for row in rows if area(row["bounds"]) >= screen_area * 0.70
    ]
    containers = [
        row for index, row in enumerate(rows)
        if area(row["bounds"]) >= screen_area * 0.40
        and any(
            contains(row["bounds"], candidate["bounds"])
            for candidate_index, candidate in enumerate(rows)
            if candidate_index != index
        )
    ]
    bounds_counts = Counter(rectangles)
    id_counts = Counter(message_ids)
    leaf_rows = [
        row for index, row in enumerate(rows)
        if not any(
            contains(row["bounds"], candidate["bounds"])
            for candidate_index, candidate in enumerate(rows)
            if candidate_index != index
        )
    ]
    return {
        "screen_id": rows[0]["screen_id"],
        "execution_id": rows[0]["execution_id"],
        "round": rows[0]["round"],
        "app_id": rows[0]["app_id"],
        "locale": rows[0]["locale_id"],
        "tag": rows[0]["tag"],
        "timestamp": rows[0]["screen_timestamp"],
        "order": int(rows[0]["screen_order"]),
        "app_version_name": rows[0]["app_version_name"],
        "link_record_count": len(rows),
        "leaf_link_record_count": len(leaf_rows),
        "records_with_message_ids": sum(
            bool(row["message_ids_parsed"]) for row in rows
        ),
        "records_with_specific_message_ids": sum(
            any(item != GLOBAL_QIRA_MESSAGE_ID for item in row["message_ids_parsed"])
            for row in rows
        ),
        "unique_message_id_count": len(set(message_ids)),
        "unique_specific_message_id_count": len(set(specific_ids)),
        "global_scope_link_count": sum(
            GLOBAL_QIRA_MESSAGE_ID in row["message_ids_parsed"] for row in rows
        ),
        "invalid_bounds_count": len(invalid),
        "out_of_screen_bounds_count": len(out_of_screen),
        "near_full_screen_link_count": len(near_full),
        "parent_container_link_count": len(containers),
        "duplicate_bounds_count": sum(
            count - 1 for count in bounds_counts.values() if count > 1
        ),
        "duplicate_message_id_count": sum(
            count - 1 for count in id_counts.values() if count > 1
        ),
        "largest_records": [
            {
                "text": row["display_text"],
                "bounds": list(row["bounds"]),
                "area_ratio": round(area(row["bounds"]) / screen_area, 6),
                "message_ids": row["message_ids_parsed"],
            }
            for row in sorted(rows, key=lambda item: area(item["bounds"]), reverse=True)[:8]
        ],
    }


def xml_metrics(path: Path, width: int, height: int) -> dict[str, Any]:
    root = ET.parse(path).getroot()
    nodes = list(root.iter("node"))
    qira_nodes = [node for node in nodes if node.get("package") == "com.lenovo.qira"]
    package_counts = Counter(node.get("package", "") for node in nodes)
    screen_area = width * height

    def xml_bounds(node: ET.Element) -> tuple[int, int, int, int]:
        value = node.get("bounds", "")
        values = [
            int(part)
            for part in value.replace("][", ",").replace("[", "").replace("]", "").split(",")
            if part
        ]
        return tuple(values) if len(values) == 4 else (0, 0, 0, 0)

    return {
        "path": str(path),
        "node_count": len(nodes),
        "qira_node_count": len(qira_nodes),
        "qira_text_node_count": sum(bool(node.get("text", "").strip()) for node in qira_nodes),
        "qira_content_description_node_count": sum(
            bool(node.get("content-desc", "").strip()) for node in qira_nodes
        ),
        "qira_resource_id_node_count": sum(
            bool(node.get("resource-id", "").strip()) for node in qira_nodes
        ),
        "qira_near_full_screen_node_count": sum(
            area(xml_bounds(node)) >= screen_area * 0.70 for node in qira_nodes
        ),
        "all_package_counts": dict(sorted(package_counts.items())),
    }


def markdown(result: dict[str, Any]) -> str:
    lines = [
        "# Failed versus control forensic metrics",
        "",
        f"- Locale: `{result['locale']}`",
        f"- Execution: `{result['execution_id']}`",
        f"- Geometry: `{result['width']}x{result['height']}`",
        f"- Compatible Workbench/API pair: `{str(result['compatible_pair']).lower()}`",
        f"- Hierarchy evidence status: `{result['hierarchy_evidence_status']}`",
        "",
        "## Workbench link records",
        "",
        "| Metric | Failed | Control |",
        "|---|---:|---:|",
    ]
    failed = result["failed"]["workbench"]
    control = result["control"]["workbench"]
    metrics = [
        "link_record_count",
        "leaf_link_record_count",
        "records_with_message_ids",
        "records_with_specific_message_ids",
        "unique_specific_message_id_count",
        "global_scope_link_count",
        "near_full_screen_link_count",
        "parent_container_link_count",
        "invalid_bounds_count",
        "out_of_screen_bounds_count",
        "duplicate_bounds_count",
    ]
    for key in metrics:
        lines.append(f"| {key} | {failed[key]} | {control[key]} |")
    lines.extend([
        "",
        "## UI hierarchy",
        "",
        "| Metric | Failed | Control |",
        "|---|---:|---:|",
    ])
    failed_xml = result["failed"]["hierarchy"]
    control_xml = result["control"]["hierarchy"]
    for key in (
        "node_count",
        "qira_node_count",
        "qira_text_node_count",
        "qira_content_description_node_count",
        "qira_resource_id_node_count",
        "qira_near_full_screen_node_count",
    ):
        lines.append(f"| {key} | {failed_xml[key]} | {control_xml[key]} |")
    lines.extend([
        "",
        "## Largest failed-screen records",
        "",
    ])
    for record in failed["largest_records"]:
        lines.append(
            f"- `{record['area_ratio']:.3f}` area, bounds `{record['bounds']}`, "
            f"text `{record['text']}`, IDs `{record['message_ids']}`"
        )
    lines.append("")
    return "\n".join(lines)


def main() -> int:
    args = parse_args()
    failed_rows = load_screen_rows(args.strings_csv, args.locale, args.failed_tag)
    control_rows = load_screen_rows(args.strings_csv, args.locale, args.control_tag)
    failed = screen_metrics(failed_rows, args.width, args.height)
    control = screen_metrics(control_rows, args.width, args.height)
    compatible = (
        failed["execution_id"] == control["execution_id"]
        and failed["round"] == control["round"]
        and failed["locale"] == control["locale"]
        and failed["app_version_name"] == control["app_version_name"]
    )
    result = {
        "schema_version": 1,
        "source_csv": str(args.strings_csv),
        "locale": args.locale,
        "execution_id": failed["execution_id"],
        "width": args.width,
        "height": args.height,
        "compatible_pair": compatible,
        "hierarchy_evidence_status": args.hierarchy_evidence_status,
        "failed": {
            "workbench": failed,
            "hierarchy": xml_metrics(args.failed_xml, args.width, args.height),
        },
        "control": {
            "workbench": control,
            "hierarchy": xml_metrics(args.control_xml, args.width, args.height),
        },
    }
    args.json_out.parent.mkdir(parents=True, exist_ok=True)
    args.markdown_out.parent.mkdir(parents=True, exist_ok=True)
    args.json_out.write_text(
        json.dumps(result, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    args.markdown_out.write_text(markdown(result), encoding="utf-8")
    print(json.dumps({
        "compatible_pair": compatible,
        "failed_near_full_screen_links": failed["near_full_screen_link_count"],
        "control_near_full_screen_links": control["near_full_screen_link_count"],
        "failed_specific_link_records": failed["records_with_specific_message_ids"],
        "control_specific_link_records": control["records_with_specific_message_ids"],
        "json": str(args.json_out),
        "markdown": str(args.markdown_out),
    }, indent=2))
    return 0 if compatible else 2


if __name__ == "__main__":
    raise SystemExit(main())

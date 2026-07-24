#!/usr/bin/env python3
"""Audit near-full-screen and container links for scoped en-XM tags."""

from __future__ import annotations

import argparse
import csv
import json
import re
from collections import Counter, defaultdict
from pathlib import Path
from typing import Any


SECTION_RE = re.compile(r"^## (Affected tags|Known-good controls)$")
TAG_RE = re.compile(r"^- `([^`]+)`$")
GLOBAL_QIRA_MESSAGE_ID = "5HQQfXNKx50UU0Nn1RHqH5"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--scope", required=True, type=Path)
    parser.add_argument("--strings-csv", required=True, type=Path)
    parser.add_argument("--locale", default="en-XM")
    parser.add_argument("--width", required=True, type=int)
    parser.add_argument("--height", required=True, type=int)
    parser.add_argument("--csv-out", required=True, type=Path)
    parser.add_argument("--json-out", required=True, type=Path)
    return parser.parse_args()


def load_scope(path: Path) -> dict[str, list[str]]:
    sections = {"affected": [], "control": []}
    current: str | None = None
    for line in path.read_text(encoding="utf-8").splitlines():
        heading = SECTION_RE.match(line)
        if heading:
            current = "affected" if heading.group(1) == "Affected tags" else "control"
            continue
        if line.startswith("## ") and current:
            current = None
        if current:
            tag = TAG_RE.match(line)
            if tag:
                sections[current].append(tag.group(1))
    return sections


def parse_ids(value: str) -> list[str]:
    try:
        parsed = json.loads(value)
    except (TypeError, json.JSONDecodeError):
        return []
    return [item for item in parsed if isinstance(item, str) and item]


def rectangle(row: dict[str, str]) -> tuple[int, int, int, int]:
    return tuple(int(row[key]) for key in ("left", "top", "right", "bottom"))


def area(rect: tuple[int, int, int, int]) -> int:
    return max(0, rect[2] - rect[0]) * max(0, rect[3] - rect[1])


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


def load_rows(path: Path, locale: str) -> dict[str, list[dict[str, Any]]]:
    grouped: dict[str, list[dict[str, Any]]] = defaultdict(list)
    with path.open("r", encoding="utf-8-sig", newline="") as handle:
        for raw in csv.DictReader(handle):
            if raw["locale_id"] != locale:
                continue
            row: dict[str, Any] = dict(raw)
            row["bounds_parsed"] = rectangle(raw)
            row["message_ids_parsed"] = parse_ids(raw["message_ids"])
            grouped[raw["tag"]].append(row)
    return grouped


def metrics(
    tag: str,
    category: str,
    rows: list[dict[str, Any]],
    width: int,
    height: int,
) -> dict[str, Any]:
    if not rows:
        return {
            "category": category,
            "tag": tag,
            "present": False,
            "status": "BLOCKED",
            "link_record_count": 0,
            "specific_link_record_count": 0,
            "near_full_screen_link_count": 0,
            "parent_container_link_count": 0,
            "duplicate_bounds_count": 0,
            "largest_area_ratio": None,
            "largest_text": "",
            "largest_message_ids": [],
            "execution_id": "",
            "screen_id": "",
        }
    screen_area = width * height
    near_full = [
        row for row in rows if area(row["bounds_parsed"]) >= screen_area * 0.70
    ]
    parents = [
        row for index, row in enumerate(rows)
        if area(row["bounds_parsed"]) >= screen_area * 0.40
        and any(
            contains(row["bounds_parsed"], candidate["bounds_parsed"])
            for candidate_index, candidate in enumerate(rows)
            if candidate_index != index
        )
    ]
    largest = max(rows, key=lambda row: area(row["bounds_parsed"]))
    bounds_counts = Counter(row["bounds_parsed"] for row in rows)
    return {
        "category": category,
        "tag": tag,
        "present": True,
        "status": "FAIL" if near_full or parents else "PASS",
        "link_record_count": len(rows),
        "specific_link_record_count": sum(
            any(item != GLOBAL_QIRA_MESSAGE_ID for item in row["message_ids_parsed"])
            for row in rows
        ),
        "near_full_screen_link_count": len(near_full),
        "parent_container_link_count": len(parents),
        "duplicate_bounds_count": sum(
            count - 1 for count in bounds_counts.values() if count > 1
        ),
        "largest_area_ratio": round(area(largest["bounds_parsed"]) / screen_area, 6),
        "largest_text": largest["display_text"],
        "largest_bounds": list(largest["bounds_parsed"]),
        "largest_message_ids": largest["message_ids_parsed"],
        "execution_id": rows[0]["execution_id"],
        "screen_id": rows[0]["screen_id"],
    }


def summarize(rows: list[dict[str, Any]], category: str) -> dict[str, Any]:
    selected = [row for row in rows if row["category"] == category]
    present = [row for row in selected if row["present"]]
    return {
        "tag_count": len(selected),
        "present_count": len(present),
        "failed_overlink_tag_count": sum(row["status"] == "FAIL" for row in present),
        "near_full_screen_tag_count": sum(
            row["near_full_screen_link_count"] > 0 for row in present
        ),
        "near_full_screen_link_count": sum(
            row["near_full_screen_link_count"] for row in present
        ),
        "container_link_tag_count": sum(
            row["parent_container_link_count"] > 0 for row in present
        ),
        "parent_container_link_count": sum(
            row["parent_container_link_count"] for row in present
        ),
        "tags_with_specific_links": sum(
            row["specific_link_record_count"] > 0 for row in present
        ),
    }


def main() -> int:
    args = parse_args()
    scope = load_scope(args.scope)
    grouped = load_rows(args.strings_csv, args.locale)
    rows = [
        metrics(tag, category, grouped.get(tag, []), args.width, args.height)
        for category in ("affected", "control")
        for tag in scope[category]
    ]
    result = {
        "schema_version": 1,
        "locale": args.locale,
        "width": args.width,
        "height": args.height,
        "source": str(args.strings_csv),
        "summary": {
            "affected": summarize(rows, "affected"),
            "control": summarize(rows, "control"),
        },
        "rows": rows,
    }
    args.csv_out.parent.mkdir(parents=True, exist_ok=True)
    args.json_out.parent.mkdir(parents=True, exist_ok=True)
    columns = [
        "category",
        "tag",
        "present",
        "status",
        "link_record_count",
        "specific_link_record_count",
        "near_full_screen_link_count",
        "parent_container_link_count",
        "duplicate_bounds_count",
        "largest_area_ratio",
        "largest_text",
        "largest_bounds",
        "largest_message_ids",
        "execution_id",
        "screen_id",
    ]
    with args.csv_out.open("w", encoding="utf-8-sig", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=columns)
        writer.writeheader()
        for row in rows:
            writer.writerow({
                **row,
                "largest_bounds": json.dumps(row.get("largest_bounds", [])),
                "largest_message_ids": json.dumps(
                    row.get("largest_message_ids", []), ensure_ascii=False
                ),
            })
    args.json_out.write_text(
        json.dumps(result, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    print(json.dumps(result["summary"], indent=2))
    missing = sum(not row["present"] for row in rows)
    return 2 if missing else 0


if __name__ == "__main__":
    raise SystemExit(main())

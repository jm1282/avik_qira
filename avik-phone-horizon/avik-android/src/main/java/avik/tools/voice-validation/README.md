# Qira Voice Validation harness

Automates the QA "Voice Validation" flow for the locale tabs of
`Voice Validation for ja_JP, zh_CN, ro_RO, pl_PL, ar_SA - SmartPhone.xlsx`.

For every scenario row it: opens a **new chat**, enters the **exact** localized
prompt from the workbook, answers a counter-question if asked, taps the response
**Play** button (with the **Ava-Preview** debug voice + matching **response
language**), captures the per-play **Voice Traceability** folder (Log + SSML +
Audio), and writes the SSML-derived text back into the workbook's **Output Text**
column.

## Pieces

| Part | Location |
|---|---|
| Instrumentation (test scripts) | `avik-android/.../avik/qira_v2/voicevalidation/scripts/` |
| Page objects + utils | `avik-android/.../avik/qira_v2/voicevalidation/{pages,utils}/` |
| Host harness (this folder) | `tools/voice-validation/` |

- `vv_config.py` — per-tab locale contract + Input Text sheet layout + input reader.
- `vv_export_inputs.py` — **Input Text workbook** (`Voice Validation Input Text 1.xlsx`, "Instruction" sheet, one BCP-47 column per locale) → per-tab input JSON → device.
- `vv_fill_results.py` — device manifest + traceability → **results workbook**: Output Text / Output SSML / Audio File / Input Match / Output vs Screen Match / Status, with smart file-completeness waiting and validation.
- `run_voicevalidation.ps1` — end-to-end driver (device-locale reboot per tab → instrumentation → fill+validate).

Two workbooks:
- **Input Text**: `Voice Validation Input Text 1.xlsx` — source of the localized prompts (read-only).
- **Results**: `Voice Validation for ... July 8th.xlsx` — where Output Text + validation is written.

## One-time setup

```powershell
.\gradlew.bat :avik-android:installDebug :avik-android:installDebugAndroidTest
adb shell appops set com.motorola.avikscripts SYSTEM_ALERT_WINDOW allow
pip install openpyxl
```

Qira must be **onboarded/signed in** on the device. The **debug Qira APK** that
exposes the `Ava-Preview (userdebug only)` voice must be installed (that voice is
what writes the traceability folders).

## Run (one tab)

```powershell
pwsh tools/voice-validation/run_voicevalidation.ps1 `
  -Xlsx "C:\Users\BLR-USER\Downloads\Voice Validation for ja_JP, zh_CN, ro_RO, pl_PL, ar_SA -  SmartPhone - July 8th.xlsx" `
  -InputXlsx "C:\Users\BLR-USER\Downloads\Voice Validation Input Text 1.xlsx" `
  -Tabs zh_CN
```

- `-SkipReboot` reuses the current device locale (no reboot; useful when the
  device is already in the target locale).
- `-Tabs zh_CN,ja_JP,...` selects tabs; default is all five.
- `-FileWait 300` caps how long the fill waits for each folder's files to finish.

### Manual, step-by-step

```powershell
# 1) export + push localized prompts (from the Input Text workbook)
python tools/voice-validation/vv_export_inputs.py --input-xlsx "<Input Text 1.xlsx>" --tabs zh_CN --push
# 2) run the instrumentation for the tab
adb shell am instrument -w -e class avik.qira_v2.voicevalidation.scripts.MotorolaQiraVoiceValidation `
  -e vv.tab zh_CN -e app_id qira -e qira.dumpUi true `
  -e qira.includeScreenshotText true -e qira.includeScreenshotDescription true `
  com.motorola.avikscripts/androidx.test.runner.AndroidJUnitRunner
# 3) fill + validate the results workbook
python tools/voice-validation/vv_fill_results.py --xlsx "<results.xlsx>" --input-xlsx "<Input Text 1.xlsx>" --tabs zh_CN
```

## Validation

`vv_fill_results.py` writes and validates, per scenario:
- **Input Match** (`Yes`/`No`): the exact prompt (special characters/symbols
  included) rendered on the device (`inputVerified`) **and** equals the Input
  Text cell (normalized).
- **Output vs Screen Match** (`Yes/No (NN%)`): the SSML output text vs the
  on-screen chat response ("screenshot") text, by normalized similarity
  (letters/digits only, any script; containment counts as 100%).
- **Status**: `Pass` when output present + Input Match + Output Match; else
  `Review`.
- **Smart file wait**: when the trace folder is produced, the fill polls until
  the SSML is valid `<speak>` XML, the Audio file is non-empty and its size is
  stable across polls, and the Log exists (up to `--file-wait`, default 300s).

## Test Run output folder

Each fill also assembles a local **Test Run** folder for audio validation:

```
Test Run/<timestamp>/
  <results workbook>.xlsx                 # filled + validated copy
  zh_CN/  Phone_CH_Weather.ssml  Phone_CH_Weather.txt  Phone_CH_Weather.wav  Phone_CH_Weather.log
          Phone_CH_CMU.*  ...
  ja_JP/  Phone_JP_*   ro_RO/ Phone_RO_*   pl_PL/ Phone_PL_*   ar_SA/ Phone_Ar_*
```

- File names follow the sheet convention `Phone_<code>_<usecase>` (`CH/JP/RO/PL/Ar`)
  and match the workbook's **File Name** / **Audio File Name** columns.
- `.ssml` (spoken SSML), `.txt` (input / output / on-screen response + validation)
  are always written; `.wav`/`.log` are added when the device trace files exist.
- Override the location with `--test-run-dir` (fill) or `-TestRunDir` (driver).

## How Output Text is captured

Qira logs the read-aloud request to logcat when Play is tapped, e.g.:

```
QC.ModelCallHandler MCH input: {"modelName":"cloudtts",...,"prompt":
  "<speak ...><voice name=\"en-US-Ava-preview:DragonHDLatestNeural\">
   <lang xml:lang=\"zh-CN\">…OUTPUT TEXT…</lang></voice></speak>","isSSML":true}
```

The instrumentation clears logcat immediately before each Play, then captures
this SSML (`VoiceSsmlCapture`). The SSML + its plain text are recorded in the
run manifest and written to the workbook. This works **whether or not** the
on-device folder-writing is enabled, and was validated on device (voice
`en-US-Ava-preview:DragonHDLatestNeural`, `locale=zh-CN`).

## Notes

- `zh_CN` and `ja_JP` tabs have no `Output Text` column in the source workbook;
  the filler **adds** `Output Text (Chinese)` / `Output Text (Japanese)` (and
  `Output SSML` for `zh_CN`). `ro_RO` / `pl_PL` / `ar_SA` already have them.
- **Voice Traceability folders** (`/sdcard/Download/Voice Traceability/en-US-Ava-preview_DragonHDLatestNeural/<locale>/<timestamp>/` with Log + SSML + Audio
  files) are produced only when the debug build's trace-write flag is enabled.
  When enabled, the harness also pulls the folder and prefers its SSML file;
  when disabled, Output Text/SSML come from the logcat capture above.
- Tool/permission-gated scenarios (Weather, Catch me up, News, …) are
  interactive: Qira asks confirmations/menus before running the tool. The script
  replies with the configured affirmative up to 3× (`MAX_COUNTER_REPLIES`), then
  plays and captures the latest response. Set a per-locale `affirmative` (or a
  concrete choice like `1`) in `vv_config.py` if a scenario needs it.
- The workbook is modified in place after a timestamped `.bak-*` backup is
  written (use `--output` to write a copy instead).
- SLAP screenshots per response import under `appId=qira`.

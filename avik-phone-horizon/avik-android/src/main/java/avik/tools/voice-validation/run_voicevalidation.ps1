<#
    Voice Validation end-to-end driver.

    Per locale tab it:
      1. (optional) sets the DEVICE system locale and reboots (no Qira data clear)
      2. runs the MotorolaQiraVoiceValidation instrumentation for that tab
    Then it fills the workbook from the pulled manifests + traceability folders.

    Prerequisites (run once):
      .\gradlew.bat :avik-android:installDebug :avik-android:installDebugAndroidTest
      adb shell appops set com.motorola.avikscripts SYSTEM_ALERT_WINDOW allow
      Qira must already be onboarded/signed in on the device.

    Example:
      pwsh tools/voice-validation/run_voicevalidation.ps1 `
        -Xlsx "C:\Users\BLR-USER\Downloads\Voice Validation ... July 8th.xlsx" `
        -Tabs zh_CN
#>
param(
    [Parameter(Mandatory = $true)] [string]$Xlsx,        # results workbook (July 8th)
    [Parameter(Mandatory = $true)] [string]$InputXlsx,   # Input Text 1 workbook
    [string]$Tabs = "zh_CN,ja_JP,ro_RO,pl_PL,ar_SA",
    [switch]$SkipReboot,      # reuse the current device locale (no reboot)
    [switch]$SkipExport,      # do not re-export / push input JSON
    [switch]$SkipFill,        # do not write the workbook at the end
    [int]$FolderTimeoutMs = 45000,  # wait for the trace folder DIR after Play
    [int]$FileWait = 300,     # max seconds to wait for a folder's files to finish
    [string]$TestRunDir = "",  # root for the local Test Run output (default: ./Test Run)
    [string]$Serial = ""
)

$ErrorActionPreference = "Continue"
$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$python = "python"

$deviceLocale = @{
    "zh_CN" = "zh-CN"; "ja_JP" = "ja-JP"; "ro_RO" = "ro-RO";
    "pl_PL" = "pl-PL"; "ar_SA" = "ar-SA"
}

$adb = @("adb")
if ($Serial) { $adb += @("-s", $Serial) }

function Adb { param([string[]]$Args) & $adb[0] @($adb[1..($adb.Count-1)] + $Args) }

function Wait-Boot {
    Adb @("wait-for-device") | Out-Null
    for ($i = 0; $i -lt 90; $i++) {
        $bc = (Adb @("shell", "getprop", "sys.boot_completed")) 2>$null
        if ($bc) { $bc = $bc.Trim() }
        if ($bc -eq "1") { break }
        Start-Sleep -Seconds 2
    }
    Start-Sleep -Seconds 6
    Adb @("shell", "input", "keyevent", "KEYCODE_WAKEUP") | Out-Null
    Adb @("shell", "wm", "dismiss-keyguard") 2>$null | Out-Null
    Adb @("shell", "svc", "power", "stayon", "true") | Out-Null
}

function Set-DeviceLocale {
    param([string]$Tag)
    Write-Host "[locale] Setting device system locale -> $Tag (reboot)"
    Adb @("shell", "am", "force-stop", "com.motorola.avikscripts") | Out-Null
    Adb @("shell", "setprop", "persist.sys.locale", $Tag) | Out-Null
    Adb @("shell", "settings", "put", "system", "system_locales", $Tag) | Out-Null
    Adb @("shell", "am", "broadcast", "-a", "android.intent.action.LOCALE_CHANGED") | Out-Null
    Adb @("reboot") | Out-Null
    Start-Sleep -Seconds 4
    Wait-Boot
    $confirmed = (Adb @("shell", "getprop", "persist.sys.locale")).Trim()
    Write-Host "[locale] persist.sys.locale=$confirmed"
}

$tabList = $Tabs.Split(",") | ForEach-Object { $_.Trim() } | Where-Object { $_ }

if (-not $SkipExport) {
    Write-Host "=== Exporting + pushing input JSON ==="
    $pushArgs = @("$here\vv_export_inputs.py", "--input-xlsx", $InputXlsx, "--tabs", ($tabList -join ","),
                  "--out-dir", "$here\out", "--push")
    if ($Serial) { $pushArgs += @("--serial", $Serial) }
    & $python @pushArgs
}

foreach ($tab in $tabList) {
    if (-not $deviceLocale.ContainsKey($tab)) {
        Write-Host "Skipping unknown tab: $tab"; continue
    }
    Write-Host "=== Tab: $tab ==="
    if (-not $SkipReboot) { Set-DeviceLocale -Tag $deviceLocale[$tab] }

    Write-Host "[run] MotorolaQiraVoiceValidation vv.tab=$tab"
    Adb @("shell", "am", "instrument", "-w",
        "-e", "class", "avik.qira_v2.voicevalidation.scripts.MotorolaQiraVoiceValidation",
        "-e", "vv.tab", $tab,
        "-e", "vv.folderTimeoutMs", "$FolderTimeoutMs",
        "-e", "app_id", "qira",
        "-e", "qira.dumpUi", "true",
        "-e", "qira.includeScreenshotText", "true",
        "-e", "qira.includeScreenshotDescription", "true",
        "com.motorola.avikscripts/androidx.test.runner.AndroidJUnitRunner")
}

if (-not $SkipFill) {
    Write-Host "=== Filling + validating workbook + building Test Run folder ==="
    if (-not $TestRunDir) { $TestRunDir = Join-Path $here "Test Run" }
    $fillArgs = @("$here\vv_fill_results.py", "--xlsx", $Xlsx, "--input-xlsx", $InputXlsx,
                  "--tabs", ($tabList -join ","), "--work-dir", "$here\run",
                  "--test-run-dir", $TestRunDir, "--file-wait", "$FileWait")
    if ($Serial) { $fillArgs += @("--serial", $Serial) }
    & $python @fillArgs
}

Write-Host "Done."

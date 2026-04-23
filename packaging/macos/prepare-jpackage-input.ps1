[CmdletBinding()]
param(
    [string]$TargetDir = "C:\MacOsX\SafesterMacPayload",
    [string]$JdkHome = $env:SAFESTER_JDK_HOME,
    [string]$AppVersion,
    [string]$MavenRepoLocal
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..\..")).Path
$pomPath = Join-Path $repoRoot "pom.xml"
$workspaceTargetDir = Join-Path $repoRoot "target"
$dependencyDir = Join-Path $TargetDir "dependencies"
$inputDir = Join-Path $TargetDir "jpackage-input"
$resourceDir = Join-Path $TargetDir "resources"
$macScriptDir = Join-Path $TargetDir "scripts\macos"
$iconIcns = Join-Path $repoRoot "java.src\net\safester\application\images\files\Safester.icns"

if (-not $JdkHome) {
    $defaultNetBeansJdk = "C:\Program Files\Apache NetBeans\jdk"
    if (Test-Path -LiteralPath $defaultNetBeansJdk) {
        $JdkHome = $defaultNetBeansJdk
    }
}

function Require-CommandPath {
    param([string]$Name)

    $command = Get-Command $Name -ErrorAction SilentlyContinue
    if (-not $command) {
        throw "Unable to locate $Name in PATH."
    }

    return $command.Source
}

function Reset-Directory {
    param([string]$Path)

    if (Test-Path -LiteralPath $Path) {
        Remove-Item -LiteralPath $Path -Recurse -Force
    }

    New-Item -ItemType Directory -Path $Path -Force | Out-Null
}

function Resolve-PomVersion {
    [xml]$pom = Get-Content -LiteralPath $pomPath -Raw
    return $pom.project.version
}

if (-not (Test-Path -LiteralPath $pomPath)) {
    throw "Unable to find pom.xml: $pomPath"
}

if (-not $AppVersion) {
    $AppVersion = Resolve-PomVersion
}

$mavenExe = Require-CommandPath -Name "mvn.cmd"

if ($JdkHome) {
    $javaExe = Join-Path $JdkHome "bin\java.exe"
    if (-not (Test-Path -LiteralPath $javaExe)) {
        throw "Unable to find java.exe under JdkHome: $JdkHome"
    }
    $JdkHome = (Resolve-Path -LiteralPath $JdkHome).Path
}

if (-not (Test-Path -LiteralPath $TargetDir)) {
    New-Item -ItemType Directory -Path $TargetDir -Force | Out-Null
}

Reset-Directory -Path $dependencyDir
Reset-Directory -Path $inputDir
Reset-Directory -Path $resourceDir
Reset-Directory -Path $macScriptDir

$mavenArguments = @(
    "-q",
    "-f", $pomPath,
    "-Dmaven.clean.failOnError=false",
    "-DskipTests",
    "clean",
    "package",
    "dependency:copy-dependencies",
    "-DincludeScope=runtime",
    "-DoutputDirectory=$dependencyDir"
)

if ($MavenRepoLocal) {
    $mavenArguments = @(
        "-q",
        "-f", $pomPath,
        "-Dmaven.clean.failOnError=false",
        "-Dmaven.repo.local=$MavenRepoLocal",
        "-DskipTests",
        "clean",
        "package",
        "dependency:copy-dependencies",
        "-DincludeScope=runtime",
        "-DoutputDirectory=$dependencyDir"
    )
}

Write-Host ""
Write-Host ("> {0} {1}" -f $mavenExe, ($mavenArguments -join " "))

$previousJavaHome = [Environment]::GetEnvironmentVariable("JAVA_HOME", "Process")
$previousPath = [Environment]::GetEnvironmentVariable("Path", "Process")

try {
    if ($JdkHome) {
        [Environment]::SetEnvironmentVariable("JAVA_HOME", $JdkHome, "Process")
        [Environment]::SetEnvironmentVariable("Path", (Join-Path $JdkHome "bin") + ";" + $previousPath, "Process")
    }

    & $mavenExe @mavenArguments
} finally {
    [Environment]::SetEnvironmentVariable("JAVA_HOME", $previousJavaHome, "Process")
    [Environment]::SetEnvironmentVariable("Path", $previousPath, "Process")
}

if ($LASTEXITCODE -ne 0) {
    throw "Maven failed with exit code $LASTEXITCODE."
}

$mainJar = Get-ChildItem -LiteralPath $workspaceTargetDir -Filter "Safester-*.jar" -File |
    Where-Object { $_.Name -notlike "original-*" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $mainJar) {
    throw "Unable to find Safester application jar under $workspaceTargetDir."
}

Copy-Item -LiteralPath $mainJar.FullName -Destination (Join-Path $inputDir "Safester.jar") -Force

Get-ChildItem -LiteralPath $dependencyDir -Filter "*.jar" -File |
    Sort-Object Name |
    ForEach-Object {
        Copy-Item -LiteralPath $_.FullName -Destination (Join-Path $inputDir $_.Name) -Force
    }

$icon80 = Join-Path $repoRoot "java.src\net\safester\application\images\files\safester-icon-80.png"
$icon60 = Join-Path $repoRoot "java.src\net\safester\application\images\files\safester-icon-60.png"

if (Test-Path -LiteralPath $iconIcns) {
    Copy-Item -LiteralPath $iconIcns -Destination (Join-Path $resourceDir "Safester.icns") -Force
}

if (Test-Path -LiteralPath $icon80) {
    Copy-Item -LiteralPath $icon80 -Destination (Join-Path $resourceDir "safester-icon-80.png") -Force
}

if (Test-Path -LiteralPath $icon60) {
    Copy-Item -LiteralPath $icon60 -Destination (Join-Path $resourceDir "safester-icon-60.png") -Force
}

$macScriptNames = @(
    "build-installer.sh",
    "build-unsigned-dmg-from-prepared-input.sh",
    "build-unsigned-dmg.sh",
    "build-from-mounted-volume.sh",
    "GUIDE_INSTALLATEUR_MACOS_SANS_MAVEN.md",
    "README.md"
)

foreach ($macScriptName in $macScriptNames) {
    $sourceFile = Join-Path $repoRoot ("packaging\macos\" + $macScriptName)
    if (-not (Test-Path -LiteralPath $sourceFile)) {
        throw "Unable to find macOS packaging file: $sourceFile"
    }

    Copy-Item -LiteralPath $sourceFile -Destination (Join-Path $macScriptDir $macScriptName) -Force
}

$metadata = @"
Safester macOS jpackage payload
AppVersion=$AppVersion
Created=$(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

If the shared disk is mounted on macOS as /Volumes/MacOsX, you can build directly from:
/Volumes/MacOsX/SafesterMacPayload

Example:
bash /Volumes/MacOsX/SafesterMacPayload/scripts/macos/build-from-mounted-volume.sh --package-type app-image
bash /Volumes/MacOsX/SafesterMacPayload/scripts/macos/build-from-mounted-volume.sh --package-type dmg

This flow does not use Maven on the Mac.
If resources/Safester.icns exists, it is used as the preferred macOS launcher icon.

Fallback copy-based flow:
copy this folder to the Mac as ~/SafesterMacPayload,
then run build-installer.sh with --prepared-input-dir and --launcher-icon-path.
"@

Set-Content -LiteralPath (Join-Path $TargetDir "README-payload.txt") -Value $metadata -Encoding UTF8

Write-Host ""
Write-Host ("Prepared macOS jpackage input: {0}" -f $inputDir)
Write-Host ("Copy this folder to the Mac: {0}" -f $TargetDir)

[CmdletBinding()]
param(
    [string]$TargetDir = "C:\MacOsX\SafesterBuild",
    [string]$JdkHome = "C:\Program Files\Apache NetBeans\jdk",
    [string]$AppVersion = "6.10.1"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$buildScript = Join-Path $scriptRoot "build-installer.ps1"

& $buildScript `
    -TargetDir $TargetDir `
    -JdkHome $JdkHome `
    -PackageType "msi" `
    -AppVersion $AppVersion

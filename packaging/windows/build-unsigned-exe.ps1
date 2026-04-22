[CmdletBinding()]
param(
    [string]$TargetDir = "C:\MacOsX\SafesterBuild",
    [string]$JdkHome = "C:\Program Files\Apache NetBeans\jdk"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$buildScript = Join-Path $scriptRoot "build-installer.ps1"

& $buildScript `
    -TargetDir $TargetDir `
    -JdkHome $JdkHome `
    -PackageType "exe"

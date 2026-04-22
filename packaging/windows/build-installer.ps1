[CmdletBinding()]
param(
    [ValidateSet("app-image", "msi", "exe")]
    [string]$PackageType = "msi",

    [string]$TargetDir = "C:\MacOsX\SafesterBuild",
    [string]$JdkHome = $env:SAFESTER_JDK_HOME,
    [string]$AppVersion,
    [string]$MavenRepoLocal,
    [string]$LauncherIconPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..\..")).Path
$pomPath = Join-Path $repoRoot "pom.xml"
$workspaceTargetDir = Join-Path $repoRoot "target"
$dependencyDir = Join-Path $TargetDir "dependencies"
$inputDir = Join-Path $TargetDir "jpackage-input"
$installerDir = Join-Path $TargetDir "installer"
$resourceSourceDir = Join-Path $PSScriptRoot "resources"
$generatedResourceDir = Join-Path $TargetDir "windows-resources"
$licenseFile = Join-Path $repoRoot "legal\Safester Terms 2017.rtf"
$defaultLauncherIconSource80 = Join-Path $repoRoot "java.src\net\safester\application\images\files\safester-icon-80.png"
$defaultLauncherIconSource60 = Join-Path $repoRoot "java.src\net\safester\application\images\files\safester-icon-60.png"
$nativeRootFiles = @("moyocore_x64.dll")
$upgradeUuid = "9f8b520b-719b-4c7e-b94c-6af0f566b8b0"

function Resolve-ExecutableFromJdk {
    param(
        [string]$CandidateJdkHome,
        [string]$ExecutableName
    )

    if (-not $CandidateJdkHome) {
        return $null
    }

    $candidate = Join-Path $CandidateJdkHome ("bin\" + $ExecutableName)
    if (Test-Path -LiteralPath $candidate) {
        return (Resolve-Path -LiteralPath $candidate).Path
    }

    return $null
}

function Get-JdkReleaseProperties {
    param([string]$CandidateJdkHome)

    if (-not $CandidateJdkHome) {
        return $null
    }

    $releaseFile = Join-Path $CandidateJdkHome "release"
    if (-not (Test-Path -LiteralPath $releaseFile)) {
        return $null
    }

    $properties = @{}
    foreach ($line in Get-Content -LiteralPath $releaseFile) {
        if ($line -match '^([^=]+)="(.*)"$') {
            $properties[$matches[1]] = $matches[2]
        }
    }

    return $properties
}

function Get-JavaMajorVersion {
    param([string]$JavaVersion)

    if (-not $JavaVersion) {
        return $null
    }

    $normalized = $JavaVersion.Trim()
    if ($normalized.StartsWith("1.")) {
        return [int]$normalized.Substring(2, 1)
    }

    $firstToken = ($normalized -split '[\.\+\-_]')[0]
    return [int]$firstToken
}

function Test-OpenJdkWithJpackageHome {
    param([string]$CandidateJdkHome)

    $release = Get-JdkReleaseProperties -CandidateJdkHome $CandidateJdkHome
    if (-not $release) {
        return $false
    }

    $majorVersion = Get-JavaMajorVersion -JavaVersion $release["JAVA_VERSION"]
    if ($majorVersion -lt 17) {
        return $false
    }

    $implementor = $release["IMPLEMENTOR"]
    $buildType = $release["BUILD_TYPE"]
    if ($implementor -match "Oracle" -or $buildType -eq "commercial") {
        return $false
    }

    $jpackagePath = Resolve-ExecutableFromJdk -CandidateJdkHome $CandidateJdkHome -ExecutableName "jpackage.exe"
    return [bool]$jpackagePath
}

function Resolve-OpenJdkHome {
    if ($JdkHome) {
        if (Test-OpenJdkWithJpackageHome -CandidateJdkHome $JdkHome) {
            return (Resolve-Path -LiteralPath $JdkHome).Path
        }

        throw "The provided JdkHome is not a supported OpenJDK installation with jpackage: $JdkHome"
    }

    $candidates = @()

    foreach ($baseDir in @(
        "C:\Program Files\Apache NetBeans",
        "C:\Program Files\Eclipse Adoptium",
        "C:\Program Files\Microsoft",
        "C:\Program Files\OpenJDK",
        "C:\Program Files\Zulu",
        "C:\Program Files\Amazon Corretto",
        "C:\Program Files\Java"
    )) {
        if (-not (Test-Path -LiteralPath $baseDir)) {
            continue
        }

        if (Test-OpenJdkWithJpackageHome -CandidateJdkHome (Join-Path $baseDir "jdk")) {
            return (Resolve-Path -LiteralPath (Join-Path $baseDir "jdk")).Path
        }

        $candidates += Get-ChildItem -LiteralPath $baseDir -Directory -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -match '(^|[-_])((jdk|temurin|zulu|corretto)[-_]?)?(1[7-9]|[2-9][0-9])([\-._].*)?$' }
    }

    if ($env:SAFESTER_JDK_HOME) {
        $candidates = @([pscustomobject]@{ FullName = $env:SAFESTER_JDK_HOME }) + $candidates
    }

    if ($env:JAVA_HOME) {
        $candidates = @([pscustomobject]@{ FullName = $env:JAVA_HOME }) + $candidates
    }

    foreach ($candidate in $candidates) {
        if (Test-OpenJdkWithJpackageHome -CandidateJdkHome $candidate.FullName) {
            return (Resolve-Path -LiteralPath $candidate.FullName).Path
        }
    }

    throw "Unable to locate an OpenJDK 17+ installation with jpackage. Install a distribution such as Temurin 25 and pass -JdkHome."
}

function Resolve-JpackagePath {
    $resolvedJdkHome = Resolve-OpenJdkHome
    $jpackagePath = Resolve-ExecutableFromJdk -CandidateJdkHome $resolvedJdkHome -ExecutableName "jpackage.exe"
    if (-not $jpackagePath) {
        throw "Unable to find jpackage.exe in $resolvedJdkHome."
    }

    return $jpackagePath
}

function Invoke-External {
    param(
        [string]$FilePath,
        [string[]]$Arguments,
        [hashtable]$EnvironmentOverride
    )

    Write-Host ""
    Write-Host ("> {0} {1}" -f $FilePath, ($Arguments -join " "))

    $previousValues = @{}
    if ($EnvironmentOverride) {
        foreach ($entry in $EnvironmentOverride.GetEnumerator()) {
            $previousValues[$entry.Key] = [Environment]::GetEnvironmentVariable($entry.Key, "Process")
            [Environment]::SetEnvironmentVariable($entry.Key, $entry.Value, "Process")
        }
    }

    try {
        & $FilePath @Arguments
        if ($LASTEXITCODE -ne 0) {
            throw "Command failed with exit code $LASTEXITCODE."
        }
    } finally {
        foreach ($entry in $previousValues.GetEnumerator()) {
            [Environment]::SetEnvironmentVariable($entry.Key, $entry.Value, "Process")
        }
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

function Get-FullPath {
    param([string]$Path)
    return [System.IO.Path]::GetFullPath($Path)
}

function Assert-PathUnderTarget {
    param([string]$Path)

    $targetFullPath = Get-FullPath -Path $TargetDir
    $candidateFullPath = Get-FullPath -Path $Path
    $targetPrefix = $targetFullPath.TrimEnd('\') + '\'

    if (-not $candidateFullPath.StartsWith($targetPrefix, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Refusing to reset a directory outside TargetDir: $candidateFullPath"
    }
}

function Reset-Directory {
    param([string]$Path)

    Assert-PathUnderTarget -Path $Path

    if (Test-Path -LiteralPath $Path) {
        Remove-Item -LiteralPath $Path -Recurse -Force
    }

    New-Item -ItemType Directory -Path $Path | Out-Null
}

function Get-ImageSize {
    param([string]$Path)

    if (-not (Test-Path -LiteralPath $Path)) {
        return $null
    }

    Add-Type -AssemblyName System.Drawing
    $image = [System.Drawing.Image]::FromFile($Path)
    try {
        return [pscustomobject]@{
            Width  = $image.Width
            Height = $image.Height
        }
    } finally {
        $image.Dispose()
    }
}

function Convert-To24BppBmp {
    param(
        [string]$SourcePath,
        [string]$OutputPath
    )

    Add-Type -AssemblyName System.Drawing

    $sourceImage = [System.Drawing.Image]::FromFile($SourcePath)
    try {
        $bitmap = New-Object System.Drawing.Bitmap($sourceImage.Width, $sourceImage.Height, [System.Drawing.Imaging.PixelFormat]::Format24bppRgb)
        $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
        try {
            $graphics.Clear([System.Drawing.Color]::White)
            $graphics.DrawImage($sourceImage, 0, 0, $sourceImage.Width, $sourceImage.Height)
        } finally {
            $graphics.Dispose()
        }

        try {
            $bitmap.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Bmp)
        } finally {
            $bitmap.Dispose()
        }
    } finally {
        $sourceImage.Dispose()
    }
}

function Assert-UiBitmapSize {
    param(
        [string]$Path,
        [int]$ExpectedWidth,
        [int]$ExpectedHeight,
        [string]$BitmapLabel
    )

    $imageSize = Get-ImageSize -Path $Path
    if ($imageSize.Width -ne $ExpectedWidth -or $imageSize.Height -ne $ExpectedHeight) {
        throw "$BitmapLabel must be ${ExpectedWidth}x${ExpectedHeight}: $Path"
    }
}

function New-InstallerBitmap {
    param(
        [string]$OutputPath,
        [string]$IconPath,
        [int]$Width,
        [int]$Height,
        [switch]$Dialog
    )

    Add-Type -AssemblyName System.Drawing

    $bitmap = New-Object System.Drawing.Bitmap($Width, $Height, [System.Drawing.Imaging.PixelFormat]::Format24bppRgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
        $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality

        $background = [System.Drawing.Color]::FromArgb(248, 250, 252)
        $ink = [System.Drawing.Color]::FromArgb(24, 38, 48)
        $muted = [System.Drawing.Color]::FromArgb(91, 108, 122)
        $accent = [System.Drawing.Color]::FromArgb(38, 130, 111)
        $line = [System.Drawing.Color]::FromArgb(207, 218, 224)

        $graphics.Clear($background)

        $accentBrush = New-Object System.Drawing.SolidBrush($accent)
        $linePen = New-Object System.Drawing.Pen($line, 1)
        $titleBrush = New-Object System.Drawing.SolidBrush($ink)
        $textBrush = New-Object System.Drawing.SolidBrush($muted)
        $titleFont = New-Object System.Drawing.Font("Segoe UI", 19, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
        $textFont = New-Object System.Drawing.Font("Segoe UI", 12, [System.Drawing.FontStyle]::Regular, [System.Drawing.GraphicsUnit]::Pixel)

        try {
            if ($Dialog) {
                $graphics.FillRectangle($accentBrush, 0, 0, 102, $Height)
                $graphics.DrawLine($linePen, 102, 0, 102, $Height)
                $iconSize = 64
                $iconX = 19
                $iconY = 32
                $textX = 126
                $textY = 38
            } else {
                $graphics.FillRectangle($accentBrush, 0, 0, 8, $Height)
                $graphics.DrawLine($linePen, 8, $Height - 1, $Width, $Height - 1)
                $iconSize = 38
                $iconX = 24
                $iconY = [int](($Height - $iconSize) / 2)
                $textX = 74
                $textY = 9
            }

            if ($IconPath -and (Test-Path -LiteralPath $IconPath)) {
                $icon = [System.Drawing.Image]::FromFile($IconPath)
                try {
                    $graphics.DrawImage($icon, $iconX, $iconY, $iconSize, $iconSize)
                } finally {
                    $icon.Dispose()
                }
            }

            $graphics.DrawString("Safester", $titleFont, $titleBrush, $textX, $textY)
            $graphics.DrawString("Easy OpenPGP encryption for all", $textFont, $textBrush, $textX, $textY + 26)
        } finally {
            $accentBrush.Dispose()
            $linePen.Dispose()
            $titleBrush.Dispose()
            $textBrush.Dispose()
            $titleFont.Dispose()
            $textFont.Dispose()
        }

        $bitmap.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Bmp)
    } finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}

function Initialize-WindowsResourceDirectory {
    param([string]$BrandIconPath)

    Reset-Directory -Path $generatedResourceDir

    if (Test-Path -LiteralPath $resourceSourceDir) {
        Get-ChildItem -LiteralPath $resourceSourceDir -Force | ForEach-Object {
            Copy-Item -LiteralPath $_.FullName -Destination $generatedResourceDir -Recurse -Force
        }
    }

    $customBannerPath = Join-Path $resourceSourceDir "SafesterBanner.bmp"
    $customDialogPath = Join-Path $resourceSourceDir "SafesterDialog.bmp"
    $generatedBannerPath = Join-Path $generatedResourceDir "SafesterBanner_wix.bmp"
    $generatedDialogPath = Join-Path $generatedResourceDir "SafesterDialog_wix.bmp"

    if (Test-Path -LiteralPath $customBannerPath) {
        Assert-UiBitmapSize -Path $customBannerPath -ExpectedWidth 493 -ExpectedHeight 58 -BitmapLabel "WiX banner"
        Convert-To24BppBmp -SourcePath $customBannerPath -OutputPath $generatedBannerPath
    } else {
        New-InstallerBitmap -OutputPath $generatedBannerPath -IconPath $BrandIconPath -Width 493 -Height 58
    }

    if (Test-Path -LiteralPath $customDialogPath) {
        Assert-UiBitmapSize -Path $customDialogPath -ExpectedWidth 493 -ExpectedHeight 312 -BitmapLabel "WiX dialog"
        Convert-To24BppBmp -SourcePath $customDialogPath -OutputPath $generatedDialogPath
    } else {
        New-InstallerBitmap -OutputPath $generatedDialogPath -IconPath $BrandIconPath -Width 493 -Height 312 -Dialog
    }

    $generatedMainWxs = Join-Path $generatedResourceDir "main.wxs"
    if (-not (Test-Path -LiteralPath $generatedMainWxs)) {
        throw "Unable to find main.wxs in generated resource directory: $generatedMainWxs"
    }

    $mainWxsContent = Get-Content -LiteralPath $generatedMainWxs -Raw
    $mainWxsContent = $mainWxsContent.Replace('Value="SafesterBanner_wix.bmp"', ('Value="' + $generatedBannerPath + '"'))
    $mainWxsContent = $mainWxsContent.Replace('Value="SafesterDialog_wix.bmp"', ('Value="' + $generatedDialogPath + '"'))
    Set-Content -LiteralPath $generatedMainWxs -Value $mainWxsContent -Encoding UTF8
}

function New-IcoFromPng {
    param(
        [string]$PngPath,
        [string]$OutputPath
    )

    Add-Type -AssemblyName System.Drawing

    $sizes = @(16, 24, 32, 48, 256)
    $iconEntries = @()
    $sourceImage = [System.Drawing.Image]::FromFile($PngPath)

    try {
        foreach ($size in $sizes) {
            $bitmap = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
            $graphics = [System.Drawing.Graphics]::FromImage($bitmap)

            try {
                $graphics.Clear([System.Drawing.Color]::Transparent)
                $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
                $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
                $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
                $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
                $graphics.DrawImage($sourceImage, 0, 0, $size, $size)

                $pngStream = New-Object System.IO.MemoryStream
                try {
                    $bitmap.Save($pngStream, [System.Drawing.Imaging.ImageFormat]::Png)
                    $iconEntries += [pscustomobject]@{
                        Width  = $size
                        Height = $size
                        Bytes  = $pngStream.ToArray()
                    }
                } finally {
                    $pngStream.Dispose()
                }
            } finally {
                $graphics.Dispose()
                $bitmap.Dispose()
            }
        }
    } finally {
        $sourceImage.Dispose()
    }

    $outputDir = Split-Path -Parent $OutputPath
    if ($outputDir -and -not (Test-Path -LiteralPath $outputDir)) {
        New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    }

    $stream = [System.IO.File]::Open($OutputPath, [System.IO.FileMode]::Create, [System.IO.FileAccess]::Write, [System.IO.FileShare]::None)
    $writer = New-Object System.IO.BinaryWriter($stream)

    try {
        $writer.Write([UInt16]0)
        $writer.Write([UInt16]1)
        $writer.Write([UInt16]$iconEntries.Count)

        $offset = 6 + (16 * $iconEntries.Count)
        foreach ($entry in $iconEntries) {
            $writer.Write([byte]($entry.Width % 256))
            $writer.Write([byte]($entry.Height % 256))
            $writer.Write([byte]0)
            $writer.Write([byte]0)
            $writer.Write([UInt16]1)
            $writer.Write([UInt16]32)
            $writer.Write([UInt32]$entry.Bytes.Length)
            $writer.Write([UInt32]$offset)
            $offset += $entry.Bytes.Length
        }

        foreach ($entry in $iconEntries) {
            $writer.Write($entry.Bytes)
        }
    } finally {
        $writer.Dispose()
        $stream.Dispose()
    }
}

function Resolve-LauncherIconSourceFile {
    $candidatePath = $LauncherIconPath
    if (-not $candidatePath) {
        if (Test-Path -LiteralPath $defaultLauncherIconSource80) {
            $candidatePath = $defaultLauncherIconSource80
        } else {
            $candidatePath = $defaultLauncherIconSource60
        }
    }

    if (-not (Test-Path -LiteralPath $candidatePath)) {
        return $null
    }

    return (Resolve-Path -LiteralPath $candidatePath).Path
}

function Resolve-LauncherIconFile {
    param([string]$IconSourcePath)

    if (-not $IconSourcePath) {
        return $null
    }

    $extension = [System.IO.Path]::GetExtension($IconSourcePath).ToLowerInvariant()
    if ($extension -eq ".ico") {
        return $IconSourcePath
    }

    if ($extension -ne ".png") {
        throw "LauncherIconPath must point to a .png or .ico file: $IconSourcePath"
    }

    $generatedIconDir = Join-Path $TargetDir "generated-resources"
    $generatedIconPath = Join-Path $generatedIconDir "Safester.ico"
    New-IcoFromPng -PngPath $IconSourcePath -OutputPath $generatedIconPath
    return $generatedIconPath
}

function Resolve-PomVersion {
    if (-not (Test-Path -LiteralPath $pomPath)) {
        return $null
    }

    [xml]$pom = Get-Content -LiteralPath $pomPath -Raw
    return $pom.project.version
}

function Update-AppImageClasspath {
    param(
        [string]$AppImagePath,
        [object[]]$DependencyJarFiles
    )

    $configPath = Join-Path $AppImagePath "app\Safester.cfg"
    if (-not (Test-Path -LiteralPath $configPath)) {
        Write-Warning "Unable to update jpackage classpath because config file was not found: $configPath"
        return
    }

    $classpathLines = [System.Collections.Generic.List[string]]::new()
    $classpathLines.Add('app.classpath=$APPDIR\Safester.jar')
    foreach ($dependencyJarFile in $DependencyJarFiles) {
        $classpathLines.Add('app.classpath=$APPDIR\' + $dependencyJarFile.Name)
    }

    $originalLines = [string[]](Get-Content -LiteralPath $configPath)
    $lines = [System.Collections.Generic.List[string]]::new()
    $classpathInserted = $false

    foreach ($line in $originalLines) {
        if ($line -match '^app\.classpath=') {
            if (-not $classpathInserted) {
                $lines.AddRange([string[]]$classpathLines)
                $classpathInserted = $true
            }
            continue
        }

        $lines.Add($line)
    }

    if (-not $classpathInserted) {
        $inserted = $false
        for ($i = 0; $i -lt $lines.Count; $i++) {
            if ($lines[$i] -eq "[Application]") {
                for ($j = $classpathLines.Count - 1; $j -ge 0; $j--) {
                    $lines.Insert($i + 1, $classpathLines[$j])
                }
                $inserted = $true
                break
            }
        }

        if (-not $inserted) {
            $lines.Add("[Application]")
            $lines.AddRange([string[]]$classpathLines)
        }
    }

    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllLines($configPath, [string[]]$lines, $utf8NoBom)
}

function Copy-PackageToTargetRoot {
    param([System.IO.FileInfo]$PackageFile)

    $publishedPackagePath = Join-Path $TargetDir $PackageFile.Name
    if ($PackageFile.FullName -eq $publishedPackagePath) {
        return $publishedPackagePath
    }

    $lastError = $null
    for ($attempt = 1; $attempt -le 6; $attempt++) {
        try {
            Copy-Item -LiteralPath $PackageFile.FullName -Destination $publishedPackagePath -Force -ErrorAction Stop
            return $publishedPackagePath
        } catch {
            $lastError = $_
            Start-Sleep -Seconds 2
        }
    }

    $baseName = [System.IO.Path]::GetFileNameWithoutExtension($PackageFile.Name)
    $extension = [System.IO.Path]::GetExtension($PackageFile.Name)
    $timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
    $fallbackPackagePath = Join-Path $TargetDir ("{0}-{1}{2}" -f $baseName, $timestamp, $extension)

    Write-Warning ("Unable to overwrite {0}. It may still be open or locked by Windows. Last error: {1}" -f $publishedPackagePath, $lastError.Exception.Message)
    Copy-Item -LiteralPath $PackageFile.FullName -Destination $fallbackPackagePath -Force -ErrorAction Stop
    return $fallbackPackagePath
}

if (-not $TargetDir) {
    throw "TargetDir cannot be empty."
}

if (-not $AppVersion) {
    $AppVersion = Resolve-PomVersion
}

if (-not $AppVersion) {
    $AppVersion = "6.10"
}

$launcherIconSourceFile = Resolve-LauncherIconSourceFile
$launcherIconFile = Resolve-LauncherIconFile -IconSourcePath $launcherIconSourceFile
$jpackageExe = Resolve-JpackagePath
$jdkBinDir = Split-Path -Parent $jpackageExe
$resolvedJdkHome = Split-Path -Parent $jdkBinDir
$jarExe = Resolve-ExecutableFromJdk -CandidateJdkHome $resolvedJdkHome -ExecutableName "jar.exe"
$mavenExe = Require-CommandPath -Name "mvn.cmd"

if (-not $jarExe) {
    throw "Unable to find jar.exe in $resolvedJdkHome."
}

$buildEnvironment = @{
    "JAVA_HOME" = $resolvedJdkHome
    "Path" = $jdkBinDir + ";" + $env:Path
}

if (-not (Test-Path -LiteralPath $TargetDir)) {
    New-Item -ItemType Directory -Path $TargetDir -Force | Out-Null
}

Initialize-WindowsResourceDirectory -BrandIconPath $launcherIconSourceFile

if ($PackageType -ne "app-image") {
    Require-CommandPath -Name "candle.exe" | Out-Null
    Require-CommandPath -Name "light.exe" | Out-Null
}

$staleWorkspaceInstallerDir = Join-Path $workspaceTargetDir "installer"
if ($workspaceTargetDir -ne $TargetDir -and (Test-Path -LiteralPath $staleWorkspaceInstallerDir)) {
    try {
        Remove-Item -LiteralPath $staleWorkspaceInstallerDir -Recurse -Force -ErrorAction Stop
    } catch {
        Write-Warning "Unable to delete stale workspace installer directory: $staleWorkspaceInstallerDir"
    }
}

Reset-Directory -Path $dependencyDir

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

Invoke-External -FilePath $mavenExe -Arguments $mavenArguments -EnvironmentOverride $buildEnvironment

$mainJar = Get-ChildItem -LiteralPath $workspaceTargetDir -Filter "Safester-*.jar" -File |
    Where-Object { $_.Name -notlike "original-*" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $mainJar) {
    throw "Unable to find the Safester application jar under $workspaceTargetDir."
}

Reset-Directory -Path $inputDir
if (-not (Test-Path -LiteralPath $installerDir)) {
    New-Item -ItemType Directory -Path $installerDir | Out-Null
}

Copy-Item -LiteralPath $mainJar.FullName -Destination (Join-Path $inputDir "Safester.jar") -Force

$dependencyJars = Get-ChildItem -LiteralPath $dependencyDir -Filter "*.jar" -File -ErrorAction SilentlyContinue |
    Sort-Object Name

foreach ($dependencyJar in $dependencyJars) {
    Copy-Item -LiteralPath $dependencyJar.FullName -Destination (Join-Path $inputDir $dependencyJar.Name) -Force
}

foreach ($nativeRootFile in $nativeRootFiles) {
    $nativeSource = Join-Path $repoRoot $nativeRootFile
    if (-not (Test-Path -LiteralPath $nativeSource)) {
        throw "Required native file was not found: $nativeSource"
    }
    Copy-Item -LiteralPath $nativeSource -Destination (Join-Path $inputDir $nativeRootFile) -Force
}

$appImageDir = Join-Path $installerDir "Safester"
if (Test-Path -LiteralPath $appImageDir) {
    Remove-Item -LiteralPath $appImageDir -Recurse -Force
}

$appImageArguments = @(
    "--type", "app-image",
    "--dest", $installerDir,
    "--name", "Safester",
    "--input", $inputDir,
    "--main-jar", "Safester.jar",
    "--main-class", "net.safester.application.Safester",
    "--app-version", $AppVersion,
    "--vendor", "KawanSoft SAS",
    "--description", "Easy OpenPGP encryption for all"
)

if ($launcherIconFile) {
    $appImageArguments += @("--icon", $launcherIconFile)
}

Invoke-External -FilePath $jpackageExe -Arguments $appImageArguments -EnvironmentOverride $buildEnvironment

if (-not (Test-Path -LiteralPath $appImageDir)) {
    throw "jpackage did not produce the expected app-image directory: $appImageDir"
}

Update-AppImageClasspath -AppImagePath $appImageDir -DependencyJarFiles $dependencyJars

foreach ($nativeRootFile in $nativeRootFiles) {
    $nativeSource = Join-Path $repoRoot $nativeRootFile
    Copy-Item -LiteralPath $nativeSource -Destination (Join-Path $appImageDir $nativeRootFile) -Force
}

if ($PackageType -eq "app-image") {
    Write-Host ""
    Write-Host ("App-image ready: {0}" -f $appImageDir)
    exit 0
}

$packageArguments = @(
    "--type", $PackageType,
    "--dest", $installerDir,
    "--name", "Safester",
    "--app-image", $appImageDir,
    "--app-version", $AppVersion,
    "--vendor", "KawanSoft SAS",
    "--description", "Easy OpenPGP encryption for all",
    "--about-url", "https://www.safester.net/",
    "--install-dir", "Safester",
    "--win-menu",
    "--win-menu-group", "Safester",
    "--win-shortcut",
    "--win-dir-chooser",
    "--win-per-user-install",
    "--win-upgrade-uuid", $upgradeUuid,
    "--resource-dir", $generatedResourceDir
)

if (Test-Path -LiteralPath $licenseFile) {
    $packageArguments += @("--license-file", $licenseFile)
}

if ($launcherIconFile) {
    $packageArguments += @("--icon", $launcherIconFile)
}

$stalePackages = Get-ChildItem -LiteralPath $installerDir -File -ErrorAction SilentlyContinue |
    Where-Object { $_.Extension -ieq ("." + $PackageType) -and $_.Name -like "Safester*" }

foreach ($stalePackage in $stalePackages) {
    try {
        Remove-Item -LiteralPath $stalePackage.FullName -Force -ErrorAction Stop
    } catch {
        Write-Warning ("Unable to delete stale package before rebuild: {0}" -f $stalePackage.FullName)
    }
}

Invoke-External -FilePath $jpackageExe -Arguments $packageArguments -EnvironmentOverride $buildEnvironment

$packageExtension = "." + $PackageType
$generatedPackage = Get-ChildItem -LiteralPath $installerDir -File |
    Where-Object { $_.Extension -ieq $packageExtension -and $_.Name -like "Safester*" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $generatedPackage) {
    throw "Unable to find the generated $PackageType package in $installerDir."
}

$publishedPackagePath = Copy-PackageToTargetRoot -PackageFile $generatedPackage

Write-Host ""
Write-Host ("Package ready: {0}" -f $publishedPackagePath)

# Windows installer

This folder contains the Windows packaging flow for Safester.

## Current approach

- Maven builds the Safester application jar.
- Maven copies runtime dependencies into the jpackage input directory.
- jpackage creates an app-image.
- `moyocore_x64.dll` is copied to the root of the app-image before packaging.
- jpackage creates the final `msi` or `exe`.

The default output root is:

```text
C:\MacOsX\SafesterBuild
```

The unsigned EXE is generated under the jpackage output folder and copied to the target root:

```text
C:\MacOsX\SafesterBuild\installer
C:\MacOsX\SafesterBuild
```

## Prerequisites

- OpenJDK 17 or newer with `jpackage` available. The wrapper defaults to `C:\Program Files\Apache NetBeans\jdk`.
- Maven.
- WiX Toolset in `PATH` for `msi` or `exe` output.
- `I:\Safester\moyocore_x64.dll`.

## Unsigned EXE

From the repository root:

```powershell
powershell -ExecutionPolicy Bypass -File .\packaging\windows\build-unsigned-exe.ps1
```

Or by double-click:

```text
packaging\windows\build-unsigned-exe.cmd
```

Equivalent direct command:

```powershell
powershell -ExecutionPolicy Bypass -File .\packaging\windows\build-installer.ps1 `
  -TargetDir 'C:\MacOsX\SafesterBuild' `
  -JdkHome 'C:\Program Files\Apache NetBeans\jdk' `
  -PackageType exe
```

## Notes

- The Windows launcher icon is generated automatically from `java.src\net\safester\application\images\files\safester-icon-80.png`.
- If the 80 px icon is missing, the script falls back to `safester-icon-60.png`.
- You can pass a custom `.ico` or `.png` with `-LauncherIconPath`.
- Keep `--win-upgrade-uuid` stable across releases so upgrades work correctly.

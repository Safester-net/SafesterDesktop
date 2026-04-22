# macOS installer

This folder contains the macOS packaging flow for Safester.

## Current approach

- Maven builds the Safester application jar.
- Maven copies runtime dependencies into the jpackage input directory.
- `jpackage` from OpenJDK 16.0.2 creates a `Safester.app` app-image.
- `jpackage` creates the final unsigned `dmg`.

The default output root is:

```text
~/SafesterBuild
```

The unsigned DMG is generated under the jpackage output folder and copied to the target root:

```text
~/SafesterBuild/installer
~/SafesterBuild/Safester-6.10.dmg
```

## Prerequisites for the Mac target

- macOS 10.13 High Sierra on Intel x64.
- OpenJDK 16.0.2 x64 with `jpackage`.

Install OpenJDK 16.0.2:

```bash
cd ~/Downloads
curl -L -o openjdk-16.0.2_osx-x64_bin.tar.gz \
  https://download.java.net/java/GA/jdk16.0.2/d4a915d82b4c4fbb9bde534da945d746/7/GPL/openjdk-16.0.2_osx-x64_bin.tar.gz

sudo mkdir -p /Library/Java/JavaVirtualMachines
sudo tar -xzf openjdk-16.0.2_osx-x64_bin.tar.gz -C /Library/Java/JavaVirtualMachines
sudo xattr -dr com.apple.quarantine /Library/Java/JavaVirtualMachines/jdk-16.0.2.jdk
```

Verify:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 16)
export PATH="$JAVA_HOME/bin:$PATH"

java -version
jpackage --version
```

The packaging script expects:

```text
openjdk version "16.0.2"
jpackage 16.0.2
```

## Recommended flow: no Maven on the Mac

Prepare the jpackage input on Windows:

```powershell
powershell -ExecutionPolicy Bypass -File I:\Safester\packaging\macos\prepare-jpackage-input.ps1
```

Copy this Windows folder to the Mac:

```text
C:\MacOsX\SafesterMacPayload
```

Put it on the Mac as:

```text
~/SafesterMacPayload
```

Then run on the Mac from a copy of the Safester repository:

```bash
bash packaging/macos/build-unsigned-dmg-from-prepared-input.sh \
  --app-version 6.10 \
  --launcher-icon-path "$HOME/SafesterMacPayload/resources/safester-icon-80.png"
```

Output:

```text
~/SafesterBuild/Safester-6.10.dmg
```

## Optional flow: full build on a Mac with Maven

From the repository root:

```bash
bash packaging/macos/build-unsigned-dmg.sh
```

Equivalent direct command:

```bash
bash packaging/macos/build-installer.sh \
  --target-dir "$HOME/SafesterBuild" \
  --jdk-home "$(/usr/libexec/java_home -v 16)" \
  --package-type dmg \
  --app-version 6.10
```

## Notes

- The macOS launcher icon is generated automatically from `java.src/net/safester/application/images/files/safester-icon-80.png`.
- If the 80 px icon is missing, the script falls back to `safester-icon-60.png`.
- You can pass a custom `.icns` or `.png` with `--launcher-icon-path`.
- The package is unsigned and not notarized. On macOS, use right click, then Open, if Gatekeeper blocks first launch.
- The build must run on macOS. `jpackage` does not cross-compile macOS packages from Windows.
- Maven is not required on the Mac when using `build-unsigned-dmg-from-prepared-input.sh`.

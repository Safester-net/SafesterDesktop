#!/usr/bin/env bash
set -euo pipefail

PACKAGE_TYPE="dmg"
TARGET_DIR="${HOME}/SafesterBuild"
JDK_HOME="${SAFESTER_JDK_HOME:-}"
APP_VERSION=""
MAVEN_REPO_LOCAL=""
LAUNCHER_ICON_PATH=""
PREPARED_INPUT_DIR=""

usage() {
  cat <<'EOF'
Usage: build-installer.sh [options]

Options:
  --package-type TYPE       jpackage output type: app-image, dmg, or pkg. Default: dmg.
  --target-dir DIR          Build/output root. Default: $HOME/SafesterBuild.
  --jdk-home DIR            OpenJDK 16.0.2 home. Default: /usr/libexec/java_home -v 16.
  --app-version VERSION     Package version. Default: version from pom.xml.
  --maven-repo-local DIR    Optional Maven local repository path.
  --launcher-icon-path PATH Optional .icns or .png icon. Default: Safester 80 px icon.
  --prepared-input-dir DIR  Existing jpackage input with Safester.jar and dependencies.
  -h, --help                Show this help.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --package-type)
      PACKAGE_TYPE="$2"
      shift 2
      ;;
    --target-dir)
      TARGET_DIR="$2"
      shift 2
      ;;
    --jdk-home)
      JDK_HOME="$2"
      shift 2
      ;;
    --app-version)
      APP_VERSION="$2"
      shift 2
      ;;
    --maven-repo-local)
      MAVEN_REPO_LOCAL="$2"
      shift 2
      ;;
    --launcher-icon-path)
      LAUNCHER_ICON_PATH="$2"
      shift 2
      ;;
    --prepared-input-dir)
      PREPARED_INPUT_DIR="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

if [[ "$PACKAGE_TYPE" != "app-image" && "$PACKAGE_TYPE" != "dmg" && "$PACKAGE_TYPE" != "pkg" ]]; then
  echo "Unsupported package type: $PACKAGE_TYPE" >&2
  exit 2
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
POM_PATH="${REPO_ROOT}/pom.xml"
WORKSPACE_TARGET_DIR="${REPO_ROOT}/target"
DEPENDENCY_DIR="${TARGET_DIR}/dependencies"
INPUT_DIR="${TARGET_DIR}/jpackage-input"
INSTALLER_DIR="${TARGET_DIR}/installer"
GENERATED_RESOURCE_DIR="${TARGET_DIR}/generated-resources"
DEFAULT_ICON_80="${REPO_ROOT}/java.src/net/safester/application/images/files/safester-icon-80.png"
DEFAULT_ICON_60="${REPO_ROOT}/java.src/net/safester/application/images/files/safester-icon-60.png"

if [[ -z "$JDK_HOME" ]]; then
  JDK_HOME="$(/usr/libexec/java_home -v 16)"
fi

JAVA_EXE="${JDK_HOME}/bin/java"
JPACKAGE_EXE="${JDK_HOME}/bin/jpackage"

if [[ ! -x "$JAVA_EXE" ]]; then
  echo "Unable to find java under JDK home: $JAVA_EXE" >&2
  exit 1
fi

if [[ ! -x "$JPACKAGE_EXE" ]]; then
  echo "Unable to find jpackage under JDK home: $JPACKAGE_EXE" >&2
  exit 1
fi

JAVA_VERSION_OUTPUT="$("$JAVA_EXE" -version 2>&1 || true)"
JPACKAGE_VERSION="$("$JPACKAGE_EXE" --version 2>&1 | tr -d '\r')"

if [[ "$JPACKAGE_VERSION" != "16.0.2" ]]; then
  echo "Expected jpackage 16.0.2, got: $JPACKAGE_VERSION" >&2
  echo "JDK home: $JDK_HOME" >&2
  exit 1
fi

if ! grep -q 'openjdk version "16.0.2"' <<<"$JAVA_VERSION_OUTPUT"; then
  echo "Expected OpenJDK 16.0.2. java -version output was:" >&2
  echo "$JAVA_VERSION_OUTPUT" >&2
  exit 1
fi

if [[ -z "$APP_VERSION" ]]; then
  if [[ ! -f "$POM_PATH" ]]; then
    echo "Unable to find pom.xml for version detection: $POM_PATH" >&2
    echo "Pass --app-version when using this script outside the Safester repository." >&2
    exit 1
  fi

  APP_VERSION="$(awk '
    /<version>/ {
      line = $0
      sub(/^.*<version>/, "", line)
      sub(/<\/version>.*$/, "", line)
      print line
      exit
    }
  ' "$POM_PATH")"

  if [[ -z "$APP_VERSION" ]]; then
    echo "Unable to resolve project version from pom.xml" >&2
    exit 1
  fi
fi

if [[ -z "$LAUNCHER_ICON_PATH" ]]; then
  if [[ -f "$DEFAULT_ICON_80" ]]; then
    LAUNCHER_ICON_PATH="$DEFAULT_ICON_80"
  else
    LAUNCHER_ICON_PATH="$DEFAULT_ICON_60"
  fi
fi

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Unable to locate required command: $1" >&2
    exit 1
  fi
}

reset_dir() {
  local path="$1"
  case "$path" in
    "$TARGET_DIR"/*) ;;
    *)
      echo "Refusing to reset directory outside target dir: $path" >&2
      exit 1
      ;;
  esac

  rm -rf "$path"
  mkdir -p "$path"
}

absolute_path() {
  local path="$1"
  mkdir -p "$(dirname "$path")"
  (cd "$(dirname "$path")" && printf '%s/%s\n' "$(pwd)" "$(basename "$path")")
}

resolve_launcher_icon() {
  local source_path="$1"
  local extension="${source_path##*.}"
  local icon_output="${GENERATED_RESOURCE_DIR}/Safester.icns"

  if [[ ! -f "$source_path" ]]; then
    echo ""
    return
  fi

  if [[ "$extension" == "icns" ]]; then
    echo "$source_path"
    return
  fi

  if [[ "$extension" != "png" ]]; then
    echo "Launcher icon must be a .png or .icns file: $source_path" >&2
    exit 1
  fi

  require_command sips
  require_command iconutil

  mkdir -p "$GENERATED_RESOURCE_DIR"
  local iconset="${GENERATED_RESOURCE_DIR}/Safester.iconset"
  rm -rf "$iconset"
  mkdir -p "$iconset"

  sips -z 16 16 "$source_path" --out "$iconset/icon_16x16.png" >/dev/null
  sips -z 32 32 "$source_path" --out "$iconset/icon_16x16@2x.png" >/dev/null
  sips -z 32 32 "$source_path" --out "$iconset/icon_32x32.png" >/dev/null
  sips -z 64 64 "$source_path" --out "$iconset/icon_32x32@2x.png" >/dev/null
  sips -z 128 128 "$source_path" --out "$iconset/icon_128x128.png" >/dev/null
  sips -z 256 256 "$source_path" --out "$iconset/icon_128x128@2x.png" >/dev/null
  sips -z 256 256 "$source_path" --out "$iconset/icon_256x256.png" >/dev/null
  sips -z 512 512 "$source_path" --out "$iconset/icon_256x256@2x.png" >/dev/null
  sips -z 512 512 "$source_path" --out "$iconset/icon_512x512.png" >/dev/null
  sips -z 1024 1024 "$source_path" --out "$iconset/icon_512x512@2x.png" >/dev/null

  iconutil -c icns "$iconset" -o "$icon_output"
  echo "$icon_output"
}

export JAVA_HOME="$JDK_HOME"
export PATH="${JDK_HOME}/bin:${PATH}"

mkdir -p "$TARGET_DIR"
mkdir -p "$INSTALLER_DIR"
mkdir -p "$GENERATED_RESOURCE_DIR"

if [[ -n "$PREPARED_INPUT_DIR" ]]; then
  INPUT_DIR="$(cd "$PREPARED_INPUT_DIR" && pwd)"
  if [[ ! -f "${INPUT_DIR}/Safester.jar" ]]; then
    echo "Prepared input directory must contain Safester.jar: $INPUT_DIR" >&2
    exit 1
  fi
else
  if [[ ! -f "$POM_PATH" ]]; then
    echo "Unable to find pom.xml for Maven build: $POM_PATH" >&2
    exit 1
  fi

  require_command mvn
  reset_dir "$DEPENDENCY_DIR"
  reset_dir "$INPUT_DIR"

  MAVEN_ARGS=(
    -q
    -f "$POM_PATH"
    -Dmaven.clean.failOnError=false
    -DskipTests
    clean
    package
    dependency:copy-dependencies
    -DincludeScope=runtime
    "-DoutputDirectory=${DEPENDENCY_DIR}"
  )

  if [[ -n "$MAVEN_REPO_LOCAL" ]]; then
    MAVEN_ARGS=(
      -q
      -f "$POM_PATH"
      -Dmaven.clean.failOnError=false
      "-Dmaven.repo.local=${MAVEN_REPO_LOCAL}"
      -DskipTests
      clean
      package
      dependency:copy-dependencies
      -DincludeScope=runtime
      "-DoutputDirectory=${DEPENDENCY_DIR}"
    )
  fi

  echo ""
  printf '> mvn'
  printf ' %q' "${MAVEN_ARGS[@]}"
  echo ""
  mvn "${MAVEN_ARGS[@]}"

  MAIN_JAR="$(find "$WORKSPACE_TARGET_DIR" -maxdepth 1 -type f -name 'Safester-*.jar' ! -name 'original-*' -print | sort | tail -n 1)"
  if [[ -z "$MAIN_JAR" ]]; then
    echo "Unable to find Safester application jar under $WORKSPACE_TARGET_DIR" >&2
    exit 1
  fi

  cp "$MAIN_JAR" "${INPUT_DIR}/Safester.jar"
  find "$DEPENDENCY_DIR" -maxdepth 1 -type f -name '*.jar' -print0 | while IFS= read -r -d '' dependency_jar; do
    cp "$dependency_jar" "$INPUT_DIR/"
  done
fi

LAUNCHER_ICON_FILE="$(resolve_launcher_icon "$LAUNCHER_ICON_PATH")"

APP_IMAGE_DIR="${INSTALLER_DIR}/Safester.app"
rm -rf "$APP_IMAGE_DIR"

APP_IMAGE_ARGS=(
  --type app-image
  --dest "$INSTALLER_DIR"
  --name Safester
  --input "$INPUT_DIR"
  --main-jar Safester.jar
  --main-class net.safester.application.Safester
  --app-version "$APP_VERSION"
  --vendor "KawanSoft SAS"
  --description "Easy OpenPGP encryption for all"
  --java-options "-Dfile.encoding=UTF-8"
)

if [[ -n "$LAUNCHER_ICON_FILE" ]]; then
  APP_IMAGE_ARGS+=(--icon "$LAUNCHER_ICON_FILE")
fi

echo ""
printf '> %q' "$JPACKAGE_EXE"
printf ' %q' "${APP_IMAGE_ARGS[@]}"
echo ""
"$JPACKAGE_EXE" "${APP_IMAGE_ARGS[@]}"

if [[ ! -d "$APP_IMAGE_DIR" ]]; then
  echo "jpackage did not produce the expected app-image directory: $APP_IMAGE_DIR" >&2
  exit 1
fi

if [[ "$PACKAGE_TYPE" == "app-image" ]]; then
  echo ""
  echo "App-image ready: $APP_IMAGE_DIR"
  exit 0
fi

PACKAGE_ARGS=(
  --type "$PACKAGE_TYPE"
  --dest "$INSTALLER_DIR"
  --name Safester
  --app-image "$APP_IMAGE_DIR"
  --app-version "$APP_VERSION"
  --vendor "KawanSoft SAS"
  --description "Easy OpenPGP encryption for all"
)

if [[ -n "$LAUNCHER_ICON_FILE" ]]; then
  PACKAGE_ARGS+=(--icon "$LAUNCHER_ICON_FILE")
fi

find "$INSTALLER_DIR" -maxdepth 1 -type f \( -name "Safester*.dmg" -o -name "Safester*.pkg" \) -delete

echo ""
printf '> %q' "$JPACKAGE_EXE"
printf ' %q' "${PACKAGE_ARGS[@]}"
echo ""
"$JPACKAGE_EXE" "${PACKAGE_ARGS[@]}"

PACKAGE_EXTENSION=".${PACKAGE_TYPE}"
GENERATED_PACKAGE="$(find "$INSTALLER_DIR" -maxdepth 1 -type f -name "Safester*${PACKAGE_EXTENSION}" -print | sort | tail -n 1)"
if [[ -z "$GENERATED_PACKAGE" ]]; then
  echo "Unable to find generated ${PACKAGE_TYPE} package in $INSTALLER_DIR" >&2
  exit 1
fi

PUBLISHED_PACKAGE="${TARGET_DIR}/Safester-${APP_VERSION}${PACKAGE_EXTENSION}"
cp -f "$GENERATED_PACKAGE" "$PUBLISHED_PACKAGE"

echo ""
echo "Package ready: $PUBLISHED_PACKAGE"

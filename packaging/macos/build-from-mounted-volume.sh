#!/usr/bin/env bash
set -euo pipefail

PACKAGE_TYPE="dmg"
TARGET_DIR="${HOME}/SafesterBuild"
JDK_HOME="${SAFESTER_JDK_HOME:-}"
APP_VERSION=""
LAUNCHER_ICON_PATH=""
VOLUME_NAME="MacOsX"
VOLUME_ROOT=""
PAYLOAD_DIR=""
OPEN_AFTER_BUILD="false"

usage() {
  cat <<'EOF'
Usage: build-from-mounted-volume.sh [options]

Build Safester on macOS directly from a mounted shared volume, without Maven.

Options:
  --package-type TYPE       jpackage output type: app-image, dmg, or pkg. Default: dmg.
  --target-dir DIR          Local build/output root. Default: $HOME/SafesterBuild.
  --jdk-home DIR            OpenJDK 16.0.2 home. Default: /usr/libexec/java_home -v 16.
  --app-version VERSION     Package version. Default: read from README-payload.txt.
  --launcher-icon-path PATH Optional .icns or .png icon. Default: payload icon.
  --volume-name NAME        Mounted volume name under /Volumes. Default: MacOsX.
  --volume-root DIR         Mounted volume root. Default: /Volumes/<volume-name>.
  --payload-dir DIR         Prepared payload directory. Default: <volume-root>/SafesterMacPayload.
  --open                    Open the generated app or package after build.
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
    --launcher-icon-path)
      LAUNCHER_ICON_PATH="$2"
      shift 2
      ;;
    --volume-name)
      VOLUME_NAME="$2"
      shift 2
      ;;
    --volume-root)
      VOLUME_ROOT="$2"
      shift 2
      ;;
    --payload-dir)
      PAYLOAD_DIR="$2"
      shift 2
      ;;
    --open)
      OPEN_AFTER_BUILD="true"
      shift
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
BUILD_SCRIPT="${SCRIPT_DIR}/build-installer.sh"

if [[ ! -f "$BUILD_SCRIPT" ]]; then
  echo "Unable to find build-installer.sh next to this script: $BUILD_SCRIPT" >&2
  exit 1
fi

if [[ -z "$VOLUME_ROOT" ]]; then
  VOLUME_ROOT="/Volumes/${VOLUME_NAME}"
fi

if [[ -z "$PAYLOAD_DIR" ]]; then
  PAYLOAD_DIR="${VOLUME_ROOT}/SafesterMacPayload"
fi

INPUT_DIR="${PAYLOAD_DIR}/jpackage-input"
METADATA_FILE="${PAYLOAD_DIR}/README-payload.txt"

if [[ ! -d "$VOLUME_ROOT" ]]; then
  echo "Mounted volume not found: $VOLUME_ROOT" >&2
  exit 1
fi

if [[ ! -f "${INPUT_DIR}/Safester.jar" ]]; then
  echo "Prepared payload is missing Safester.jar: ${INPUT_DIR}/Safester.jar" >&2
  exit 1
fi

if [[ -z "$APP_VERSION" && -f "$METADATA_FILE" ]]; then
  APP_VERSION="$(awk -F= '/^AppVersion=/{print $2; exit}' "$METADATA_FILE" | tr -d '\r')"
fi

if [[ -z "$APP_VERSION" ]]; then
  echo "Unable to resolve AppVersion from $METADATA_FILE. Pass --app-version." >&2
  exit 1
fi

if [[ -z "$LAUNCHER_ICON_PATH" ]]; then
  if [[ -f "${PAYLOAD_DIR}/resources/safester-icon-80.png" ]]; then
    LAUNCHER_ICON_PATH="${PAYLOAD_DIR}/resources/safester-icon-80.png"
  elif [[ -f "${PAYLOAD_DIR}/resources/safester-icon-60.png" ]]; then
    LAUNCHER_ICON_PATH="${PAYLOAD_DIR}/resources/safester-icon-60.png"
  else
    echo "Unable to find a launcher icon in ${PAYLOAD_DIR}/resources" >&2
    exit 1
  fi
fi

COMMAND=(
  bash "$BUILD_SCRIPT"
  --package-type "$PACKAGE_TYPE"
  --target-dir "$TARGET_DIR"
  --prepared-input-dir "$INPUT_DIR"
  --app-version "$APP_VERSION"
  --launcher-icon-path "$LAUNCHER_ICON_PATH"
)

if [[ -n "$JDK_HOME" ]]; then
  COMMAND+=(--jdk-home "$JDK_HOME")
fi

echo ""
printf '> %q' "${COMMAND[@]}"
echo ""
"${COMMAND[@]}"

if [[ "$OPEN_AFTER_BUILD" != "true" ]]; then
  exit 0
fi

case "$PACKAGE_TYPE" in
  app-image)
    open "${TARGET_DIR}/installer/Safester.app"
    ;;
  dmg)
    open "${TARGET_DIR}/Safester-${APP_VERSION}.dmg"
    ;;
  pkg)
    open "${TARGET_DIR}/Safester-${APP_VERSION}.pkg"
    ;;
esac

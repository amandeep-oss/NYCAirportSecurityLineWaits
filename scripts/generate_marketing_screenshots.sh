#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "${script_dir}/.." && pwd)"

cd "$repo_root"

usage() {
  cat <<'EOF_USAGE'
Usage: generate_marketing_screenshots [--webp]

Runs screenshot tests, clears previous marketing outputs, and generates marketing
assets with the marketing_tools pipeline.

Options:
  --webp    Convert generated PNGs in images/frames/output to WebP
  -h, --help  Show this help text

Environment:
  MARKETING_TOOLS_DIR  Path to the marketing_tools repo (required)
EOF_USAGE
}

enable_webp=false
while [[ $# -gt 0 ]]; do
  case "$1" in
    --webp)
      enable_webp=true
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
  shift
done

if [[ -z "${MARKETING_TOOLS_DIR:-}" ]]; then
  echo "MARKETING_TOOLS_DIR is not set. Please export MARKETING_TOOLS_DIR to the marketing_tools repo." >&2
  exit 1
fi

if [[ -x /opt/local/lib/ImageMagick7/bin/magick ]]; then
  export PATH="/opt/local/lib/ImageMagick7/bin:${PATH}"
elif [[ -x /opt/local/bin/magick ]]; then
  export PATH="/opt/local/bin:${PATH}"
fi

time (
  rm -rf app/src/screenshotTestFullDebug/reference/ca/amandeep/playernumber/ui/main
  ./gradlew updateFullDebugScreenshotTest
  rm -rf images/frames/output/*.png || true
  rm -rf images/frames/output/*.webp || true
  uv run --project "${MARKETING_TOOLS_DIR}" app-store-screenshots --config images/frames/marketing_jobs.yaml
  if [[ "$enable_webp" == true ]]; then
    "${MARKETING_TOOLS_DIR}/scripts/convert_png_to_webp.sh" images/frames/output/
  fi
)

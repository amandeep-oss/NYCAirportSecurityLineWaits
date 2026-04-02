#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "${script_dir}/.." && pwd)"
tools_repo_dir="${MARKETING_TOOLS_REPO_DIR:-/Users/amandeep/AndroidStudioProjects/AppStoreMarketingScreenshotsTools}"
shared_runner="${tools_repo_dir}/scripts/run_marketing_generation.sh"

if [[ ! -x "${shared_runner}" ]]; then
  echo "Shared marketing runner not found or not executable: ${shared_runner}" >&2
  exit 1
fi

export MARKETING_REPO_ROOT="${repo_root}"
export MARKETING_SCREENSHOT_RESULTS_DIR="app/build/outputs/screenshotTest-results/preview/debug/full/rendered/ca/amandeep/nycairportsecuritylinewaits/ui/main/MarketingScreenshotsKt"
export MARKETING_REFERENCE_CLEANUP_DIR="app/src/screenshotTestFullDebug/reference/ca/amandeep/nycairportsecuritylinewaits/ui/main"

exec "${shared_runner}" "$@"

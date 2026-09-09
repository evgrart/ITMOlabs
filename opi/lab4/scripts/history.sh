#!/usr/bin/env bash
set -u

project_root="${1:-.}"
report_file="${2:-history-report.txt}"
build_file="${3:-build.xml}"

mkdir -p "$(dirname "${report_file}")"

{
  echo "History build report"
  echo "Project root: ${project_root}"
  echo "Build file: ${build_file}"
  echo
} >"${report_file}"

if ! command -v svn >/dev/null 2>&1; then
  echo "svn is not installed; history rollback is unavailable." >>"${report_file}"
  exit 0
fi

if ! command -v ant >/dev/null 2>&1; then
  echo "ant is not installed; compile checks by revision are unavailable." >>"${report_file}"
  exit 0
fi

if [ ! -d "${project_root}/.svn" ]; then
  echo "No .svn metadata found; history target supports SVN working copies only." >>"${report_file}"
  exit 0
fi

current_revision="$(svn info --show-item revision "${project_root}" 2>/dev/null || true)"
if [ -z "${current_revision}" ]; then
  echo "Unable to determine current SVN revision." >>"${report_file}"
  exit 1
fi

echo "Current revision: ${current_revision}" >>"${report_file}"
echo "All files in repository:" >>"${report_file}"
svn list -R "${project_root}" >>"${report_file}" 2>/dev/null || true
echo >>"${report_file}"

working_revision=""
for ((rev = current_revision; rev >= 1; rev--)); do
  echo "Trying revision ${rev}..." >>"${report_file}"
  if ! svn update -r "${rev}" "${project_root}" >/dev/null 2>&1; then
    echo "Failed to update to revision ${rev}." >>"${report_file}"
    continue
  fi

  if ant -f "${build_file}" -q compile >/dev/null 2>&1; then
    working_revision="${rev}"
    break
  fi
done

if [ -z "${working_revision}" ]; then
  echo "No compilable revision found from 1 to ${current_revision}." >>"${report_file}"
  svn update -r "${current_revision}" "${project_root}" >/dev/null 2>&1 || true
  exit 0
fi

next_revision=$((working_revision + 1))

echo "Last compilable revision: ${working_revision}" >>"${report_file}"
echo "Next revision after last compilable: ${next_revision}" >>"${report_file}"
echo >>"${report_file}"
echo "Changed revisions:" >>"${report_file}"
svn log -q -r "${working_revision}:${current_revision}" "${project_root}" >>"${report_file}" 2>/dev/null || true
echo >>"${report_file}"
echo "Changed files between working and next revision:" >>"${report_file}"
if [ "${next_revision}" -le "${current_revision}" ]; then
  svn diff -r "${working_revision}:${next_revision}" --summarize "${project_root}" >>"${report_file}" 2>/dev/null || true
else
  echo "No next revision available after the last compilable revision." >>"${report_file}"
fi

svn update -r "${current_revision}" "${project_root}" >/dev/null 2>&1 || true

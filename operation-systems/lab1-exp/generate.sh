#!/usr/bin/env bash
set -euo pipefail
LAB_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$LAB_ROOT"
DEST="${1:-.}"
mkdir -p "$DEST"
if [[ -e "$DEST/graph-rand.bin" || -e "$DEST/graph-seq.bin" ]]; then
    printf 'Графы в целевой папке уже существуют. Выберите другую папку.\n' >&2
    exit 1
fi
python3 graphgen.py -s 10M --seed 427 --topology chain \
    -b 0.5 --min-step-pages 2 --verify -o "$DEST/graph-rand.bin" \
    | tee "$DEST/generation-rand.txt"
python3 graphgen.py -s 10M --seed 427 --topology sequential \
    --verify -o "$DEST/graph-seq.bin" | tee "$DEST/generation-seq.txt"
sha256sum "$DEST/graph-rand.bin" "$DEST/graph-seq.bin" > "$DEST/initial.sha256"

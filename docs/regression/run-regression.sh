#!/usr/bin/env bash
# Non-regression runner for Requirement 7 (GRASP refactor).
#
# Usage, from the project root in Git Bash:
#   mvn clean compile
#   bash docs/regression/run-regression.sh <label>
#
# Each script in docs/regression/scripts is typed into the console menu of a
# fresh copy of docs/regression/fixture-data. The console output and the data
# files after the run are stored in docs/regression/results/<label>/.
# Values that change on every run (today's date, warranty end dates, return and
# warranty ids, the object reference printed in the return receipt) are replaced
# by fixed tags.
#
# Compare two runs with:
#   git diff --no-index docs/regression/results/baseline docs/regression/results/<label>

set -u
LABEL=${1:?"usage: bash docs/regression/run-regression.sh <label>"}
ROOT=$(cd "$(dirname "$0")" && pwd)
REPO=$(cd "$ROOT/../.." && pwd)
CLASSES="$REPO/target/classes"
JAVA_BIN=${JAVA:-java}
TODAY=$(date +%F)
PLUS6=$(date -d "$TODAY +6 months" +%F)
PLUS12=$(date -d "$TODAY +12 months" +%F)
MONTH=$(date +%-m)
YEAR=$(date +%Y)
OUT="$ROOT/results/$LABEL"

if [ ! -d "$CLASSES/com/gamezone" ]; then
  echo "Compiled classes not found. Run: mvn clean compile"
  exit 1
fi

rm -rf "$OUT"
mkdir -p "$OUT"

for script in "$ROOT"/scripts/*.txt; do
  name=$(basename "$script" .txt)
  work=$(mktemp -d)
  cp -r "$ROOT/fixture-data" "$work/data"
  sed -e "s/{TODAY}/$TODAY/g" -e "s/{MONTH}/$MONTH/g" -e "s/{YEAR}/$YEAR/g" \
      "$script" | tr -d '\r' > "$work/input.txt"

    (cd "$work" && "$JAVA_BIN" -Duser.language=es -Duser.country=CO -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 \
        -cp "$CLASSES" com.gamezone.Main < input.txt > output.txt 2>&1)

  {
    cat "$work/output.txt"
    for f in "$work"/data/*; do
      echo "=== data/$(basename "$f")"
      if [ "$(basename "$f")" = "products.dat" ]; then
        sha256sum < "$f" | cut -d' ' -f1
      else
        cat "$f"
      fi
    done
  } | tr -d '\r' | sed -E \
      -e "s/$TODAY/<TODAY>/g" \
      -e "s/$PLUS6/<TODAY+6M>/g" \
      -e "s/$PLUS12/<TODAY+12M>/g" \
      -e 's/RET-[0-9]+/RET-<ID>/g' \
      -e 's/WAR-(BAS|EXT)-[0-9a-f]{8}/WAR-\1-<ID>/g' \
      -e 's/Sale@[0-9a-f]+/Sale@<HASH>/g' \
      -e '/^Picked up JAVA_TOOL_OPTIONS/d' > "$OUT/$name.txt"

  rm -rf "$work"
  echo "  $name"
done

echo "Results stored in docs/regression/results/$LABEL"

#!/usr/bin/env bash
# Checks the .java files that are actually staged against Spotless and, if any of
# them weren't correctly formatted, fixes them in the working tree and ABORTS --
# matching the convention pre-commit.com's own formatter hooks (Black, Prettier,
# etc.) use: a hook that modifies files fails the run, so you always review and
# explicitly `git add` what you're about to commit rather than silently committing
# a fixed version of something you never looked at.
#
# Pass a git ref as $1 to instead format everything changed relative to that ref
# (e.g. `githooks/spotless-staged.sh origin/main` to format your whole branch).
# That mode never touches the index and always exits 0 -- it's for reviewing a
# formatting pass, not gating a commit.
#
# Used by githooks/pre-commit and, optionally, the pre-commit.com hook in
# .pre-commit-config.yaml -- both call this script so there's one source of truth.
set -euo pipefail

repo_root="$(git rev-parse --show-toplevel)"
cd "$repo_root"

base_ref="${1:-}"
if [ -n "$base_ref" ]; then
    mapfile -t files < <(git diff --name-only --diff-filter=ACMR "$base_ref" -- '*.java')
else
    mapfile -t files < <(git diff --cached --name-only --diff-filter=ACMR -- '*.java')
fi

if [ "${#files[@]}" -eq 0 ]; then
    exit 0
fi

pattern=""
for f in "${files[@]}"; do
    escaped="${f//./\\.}"
    if [ -z "$pattern" ]; then
        pattern="$escaped"
    else
        pattern="$pattern|$escaped"
    fi
done

# Spotless formats whatever is on disk. If a file has both staged and unstaged
# changes (e.g. from `git add -p`), we don't want to run the formatter against
# content the developer hasn't even staged yet. Set aside any unstaged portion of
# these files first, as a plain patch file -- not `git stash`, since the stash
# stack is shared with other tools/sessions -- and reapply it afterward either way.
patch_file=""
has_unstaged=0
if [ -z "$base_ref" ] && ! git diff --quiet -- "${files[@]}"; then
    has_unstaged=1
    patch_file="$(mktemp "${TMPDIR:-/tmp}/spotless-staged-unstaged.XXXXXX.patch")"
    git diff -- "${files[@]}" >"$patch_file"
    git checkout -- "${files[@]}"
fi

restore_unstaged() {
    if [ "$has_unstaged" -eq 1 ]; then
        if ! git apply --whitespace=nowarn "$patch_file" 2>/dev/null; then
            echo "spotless-staged.sh: could not reapply your unstaged changes on top of the" >&2
            echo "formatted files (they conflict). Your unstaged edits are saved at:" >&2
            echo "  $patch_file" >&2
            echo "Review and apply manually with: git apply $patch_file" >&2
            return 1
        fi
        rm -f "$patch_file"
    fi
    return 0
}

./gradlew spotlessApply -PspotlessFiles="$pattern" --quiet

if [ -n "$base_ref" ]; then
    # Ad-hoc run against a base ref: leaves the working tree formatted for the
    # caller to review; never touches the index.
    exit 0
fi

# Did formatting actually change any of the staged files? If so, fail the commit
# instead of staging the fix ourselves -- the developer reviews the diff and
# re-stages explicitly.
if ! git diff --quiet -- "${files[@]}"; then
    echo "spotless-staged.sh: the following files were not correctly formatted." >&2
    echo "They have been fixed in your working tree -- review the changes, then" >&2
    echo "\`git add\` them and commit again:" >&2
    for f in "${files[@]}"; do
        if ! git diff --quiet -- "$f"; then
            echo "  $f" >&2
        fi
    done
    restore_unstaged || true
    exit 1
fi

restore_unstaged

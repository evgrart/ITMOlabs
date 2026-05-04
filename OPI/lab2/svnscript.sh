#!/bin/bash

if [ -d "./commits" ]; then
    dir=$(realpath "./commits")
else
    exit 1
fi

rm -rf svn_server repo_wc
svnadmin create "$PWD/svn_server"
REPO_URL="file://$PWD/svn_server"

svn mkdir -m "Setup structure" "$REPO_URL/trunk" "$REPO_URL/branches"

svn co "$REPO_URL/trunk" repo_wc
cd repo_wc

apply_svn_revision() {
    local rev=$1
    local user=$2
    local path="$dir/commit$rev.zip"
    
    if [ ! -f "$path" ]; then
        exit 1
    fi
    
    find . -maxdepth 1 -mindepth 1 ! -name '.svn' -exec rm -rf {} + # делит все что не svn
    
    unzip -qo "$path" -d .
    
    svn add --force .
    svn status | grep '^\!' | sed 's/! *//' | xargs -I% svn rm "%" 2>/dev/null || true
    
    svn commit -m "${rev}" --username="${user}"
}

apply_svn_revision "0" "user1"
apply_svn_revision "1" "user1"
apply_svn_revision "2" "user1"

svn copy "$REPO_URL/trunk" "$REPO_URL/branches/bottom12" -m "Create branch bottom12" --username="user1"
svn switch "$REPO_URL/branches/bottom12"

apply_svn_revision "3" "user1"
apply_svn_revision "4" "user1"

svn copy "$REPO_URL/branches/bottom12" "$REPO_URL/branches/bottom21" -m "Create branch bottom21" --username="user2"
svn switch "$REPO_URL/branches/bottom21"

apply_svn_revision "5" "user2"

svn copy "$REPO_URL/branches/bottom21" "$REPO_URL/branches/bottom22" -m "Create branch bottom22" --username="user2"
svn switch "$REPO_URL/branches/bottom22"

apply_svn_revision "6" "user2"

svn switch "$REPO_URL/branches/bottom21"
apply_svn_revision "7" "user2"

svn copy "$REPO_URL/branches/bottom21" "$REPO_URL/branches/bottom23" -m "Create branch bottom23" --username="user2"
svn switch "$REPO_URL/branches/bottom23"

apply_svn_revision "8" "user2"

svn switch "$REPO_URL/trunk"
apply_svn_revision "9" "user1"

svn switch "$REPO_URL/branches/bottom12"
apply_svn_revision "10" "user1"
apply_svn_revision "11" "user1"

svn switch "$REPO_URL/trunk"
apply_svn_revision "12" "user1"
apply_svn_revision "13" "user1"

svn switch "$REPO_URL/branches/bottom23"
apply_svn_revision "14" "user2"
apply_svn_revision "15" "user2"

svn switch "$REPO_URL/branches/bottom12"
apply_svn_revision "16" "user1"

svn switch "$REPO_URL/branches/bottom23"
apply_svn_revision "17" "user2"

svn switch "$REPO_URL/trunk"
apply_svn_revision "18" "user1"
apply_svn_revision "19" "user1"

svn switch "$REPO_URL/branches/bottom21"
apply_svn_revision "20" "user2"

svn switch "$REPO_URL/trunk"
apply_svn_revision "21" "user1"

svn switch "$REPO_URL/branches/bottom22"
apply_svn_revision "22" "user2"

svn switch "$REPO_URL/branches/bottom23"
apply_svn_revision "23" "user2"

svn switch "$REPO_URL/branches/bottom12"
svn merge "^/branches/bottom23" --non-interactive --accept postpone
svn resolve --accept working -R . 2>/dev/null || true
svn commit -m "24" --username="user1"

svn switch "$REPO_URL/trunk"
apply_svn_revision "25" "user1"

svn switch "$REPO_URL/branches/bottom22"
apply_svn_revision "26" "user2"

svn switch "$REPO_URL/branches/bottom21"
svn merge "^/branches/bottom22" --non-interactive --accept postpone
svn resolve --accept working -R . 2>/dev/null || true
svn commit -m "27" --username="user2"

svn switch "$REPO_URL/branches/bottom12"
apply_svn_revision "28" "user1"

svn switch "$REPO_URL/trunk"
svn merge "^/branches/bottom12" --non-interactive --accept postpone
svn resolve --accept working -R . 2>/dev/null || true
svn commit -m "29" --username="user1"

svn switch "$REPO_URL/branches/bottom21"
apply_svn_revision "30" "user2"
apply_svn_revision "31" "user2"

svn switch "$REPO_URL/trunk"
svn merge "^/branches/bottom21" --non-interactive --accept postpone
svn resolve --accept working -R . 2>/dev/null || true
svn commit -m "32" --username="user1"

apply_svn_revision "33" "user1"
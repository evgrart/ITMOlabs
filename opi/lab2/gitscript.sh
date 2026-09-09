#!/bin/bash 
set -e 
 
if [ -d "./commits" ]; then 
    dir=$(realpath "./commits") 
else 
    echo "папка 'commits' не найдена" 
    exit 1 
fi 
 
apply_revision() { 
    local rev=$1 
    local path="$dir/commit$rev.zip" 
    if [ ! -f "$path" ]; then 
        echo "файл $path не найден" 
        exit 1 
    fi 
    rm -rf ./* 
    unzip -qo "$path" -d . 
    git add -A 
} 

set_user1() {
    git config user.name "User1"
    git config user.email "user1@itmo.ru"
}

set_user2() {
    git config user.name "User2"
    git config user.email "user2@itmo.ru"
}

rm -rf repo
mkdir repo
cd repo
git init
git config merge.tool vimdiff
git config mergetool.prompt false
git config mergetool.keepBackup false

set_user1
git branch -m master
apply_revision "0"
git commit --allow-empty -m "0"

apply_revision "1"
git commit --allow-empty -m "1"

apply_revision "2"
git commit --allow-empty -m "2"
git checkout -b bottom12

apply_revision "3"
git commit --allow-empty -m "3"

apply_revision "4"
git commit --allow-empty -m "4"
git checkout -b bottom21

set_user2
apply_revision "5"
git commit --allow-empty -m "5"
git checkout -b bottom22

apply_revision "6"
git commit --allow-empty -m "6"

git checkout bottom21
apply_revision "7"
git commit --allow-empty -m "7"
git checkout -b bottom23

apply_revision "8"
git commit --allow-empty -m "8"

set_user1
git checkout master
apply_revision "9"
git commit --allow-empty -m "9"

git checkout bottom12
apply_revision "10"
git commit --allow-empty -m "10"

apply_revision "11"
git commit --allow-empty -m "11"

git checkout master
apply_revision "12"
git commit --allow-empty -m "12"

apply_revision "13"
git commit --allow-empty -m "13"

set_user2
git checkout bottom23
apply_revision "14"
git commit --allow-empty -m "14"

apply_revision "15"
git commit --allow-empty -m "15"

set_user1
git checkout bottom12
apply_revision "16"
git commit --allow-empty -m "16"

set_user2
git checkout bottom23
apply_revision "17"
git commit --allow-empty -m "17"

set_user1
git checkout master
apply_revision "18"
git commit --allow-empty -m "18"

apply_revision "19"
git commit --allow-empty -m "19"

set_user2
git checkout bottom21
apply_revision "20"
git commit --allow-empty -m "20"

set_user1
git checkout master
apply_revision "21"
git commit --allow-empty -m "21"

set_user2
git checkout bottom22
apply_revision "22"
git commit --allow-empty -m "22"

git checkout bottom23
apply_revision "23"
git commit --allow-empty -m "23"

set_user1
git checkout bottom12
git merge --no-commit bottom23 || git mergetool
git commit --allow-empty -m "24"

git checkout master
apply_revision "25"
git commit --allow-empty -m "25"

set_user2
git checkout bottom22
apply_revision "26"
git commit --allow-empty -m "26"

git checkout bottom21
git merge --no-commit bottom22 || git mergetool
git commit --allow-empty -m "27"

set_user1
git checkout bottom12
apply_revision "28"
git commit --allow-empty -m "28"

git checkout master
git merge --no-commit bottom12 || git mergetool
git commit --allow-empty -m "29"

set_user2
git checkout bottom21
apply_revision "30"
git commit --allow-empty -m "30"

apply_revision "31"
git commit --allow-empty -m "31"

set_user1
git checkout master
git merge --no-commit bottom21 || git mergetool
git commit --allow-empty -m "32"

apply_revision "33"
git commit --allow-empty -m "33"

git log --graph

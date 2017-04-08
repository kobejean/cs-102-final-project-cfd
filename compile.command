#!/bin/sh
here="`dirname \"$0\"`"
echo "cd-ing to $here"
cd "$here" || exit 1
javac -g -encoding ISO-8859-1 @compile_list.txt -d bin

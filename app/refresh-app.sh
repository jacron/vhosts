#!/bin/bash
cd "$(dirname "$0")"
jardir=/Users/orion/Dev/java/javafx/vhosts/out/artifacts/vhosts_jar/vhosts.jar
cp "$jardir" vhosts.app/Contents/MacOS/
# read -p 'Hit [Enter] to continue...'
#!/usr/bin/env bash

# Simple utility to replace the string "faces" with "jsf" for all .adoc files in the specified folder
# Uses the git "mv" command to retain history

find "$1" -name "*.adoc" -type f -exec sh -c '
    for file do
      dir=$(dirname "$file")
      new_dir=$(echo "$dir" | rev | sed "s/fsj/\secaf/" | rev)
      echo "DIR: $file => $new_name"

      filename=$(basename "$file")
      new_name=$(echo "$filename" | rev | sed "s/fsj/\secaf/" | rev)
      echo "FILE: $file => $new_name"
      git mv "$file" "$dir/$new_name"
    done' sh {} +

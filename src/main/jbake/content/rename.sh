#!/bin/bash

while IFS= read -r folder_name
do
    file_update=$(find . -type f \( -name "*.adoc" -and -not -name "$folder_name*" \) | xargs grep "link:$folder_name" | grep -v "link\:$folder_name/" |  cut -d':' -f1)

    find . -type f \( -name "*.adoc" -and -not -name "$folder_name*" \) | xargs grep "link:$folder_name" | grep -v "link\:$folder_name/" |  cut -d':' -f1 | uniq | xargs sed -i "s@link:$folder_name@link:$folder_name/$folder_name@g"
done < "folders.txt"

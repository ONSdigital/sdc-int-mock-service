#!/bin/bash

# This script updates all of the captured data held in /src/main/resources/data
#
# To run it the mock-ai needs to have access to the security token used by AI.
# mock-ai therefore needs to have a value set for 'address-index.token'.
# Note that mock-ai is assumed to be running locally.

set -e

TMP_FILE="/tmp/found"

# Move to the captured data directory
cd "$(dirname "$0")"
cd ../src/main/resources/data

# Find captured files that need to be refetched
find . -name "*json" | grep -v notFound > $TMP_FILE

# Convert the list of captured files into curl commands
sed -i ".bak" 's|-|%20|g' $TMP_FILE
sed -i ".bak" 's|/partial/|/partial?input=|g' $TMP_FILE
sed -i ".bak" 's|/addresses/eq/|/addresses/eq?input=|g' $TMP_FILE
sed -i ".bak" 's|^.|curl -s localhost:8163/capture|g' $TMP_FILE
sed -i ".bak" 's|.json$||g' $TMP_FILE

# Invoke capture endpoint to refresh each existing data file
cat $TMP_FILE | while read line
do
  echo "Refreshing: $line"

  # Execute the curl command
  $line > /tmp/refreshCapture.result.json
  if [ $? -ne 0 ]
  then
    echo "*FAIL*"
  fi
done

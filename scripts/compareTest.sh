#!/bin/bash

#
# This script acts as a unit test for the mock-service.
#
# It invokes the real and mock Address Index for a number of queries.
# The script then does a diff between the real vs. mocked results.
#
# NOTE: This script requires an AI token to run. This must be set in
# the 'AI_TOKEN' environment variable.
# By default the script points at a WL based AI service.
#
# The mock-service must be running locally.
#

MOCK_AI="http://localhost:8162"
WL_AI="https://whitelodge-eq-ai-api.census-gcp.onsdigital.uk"

# The AI_TOKEN needs to be set.
AI_TOKEN=""

# Run the comparison of the real vs. mock-service responses
function compare {
  ENDPOINT=$1

  [ -z "$AI_TOKEN" ] && echo "Error: AI_TOKEN must be set" && exit 1;

  # Get the response status codes from genuine and mock AI
  curl -s -H "Authorization: $AI_TOKEN" -I $WL_AI/$ENDPOINT | grep HTTP | grep -Eo "[0-9]{3}" > /tmp/wl-ai.status.txt
  curl -s -I $MOCK_AI/$ENDPOINT | grep HTTP | grep -Eo "[0-9]{3}" > /tmp/mock-service.status.txt

  # Compare real & mock AI status codes
  diff "/tmp/wl-ai.status.txt" "/tmp/mock-service.status.txt" > /dev/null
  if [ $? -ne 0 ]
  then
    echo "*FAIL STATUS*: $ENDPOINT"
    echo -n "  Genuine: "
    cat /tmp/wl-ai.status.txt
    echo -n "  Mock   : "
    cat /tmp/mock-service.status.txt
  fi

  # Get responses from genuine and mock AI
  curl -s -H "Authorization: Bearer $AI_TOKEN" $WL_AI/$ENDPOINT | jq . > /tmp/wl-ai.json
  curl -s $MOCK_AI/$ENDPOINT | jq . > /tmp/mock-service.json

  # Compare real & mock AI results
  diff "/tmp/wl-ai.json" "/tmp/mock-service.json" > /dev/null
  if [ $? -eq 0 ]
  then
    echo "Pass: $ENDPOINT"
  else
    echo "*FAIL*: $ENDPOINT"
  fi
}


# RH POSTCODE
compare "addresses/rh/postcode/CF32TW"
compare "addresses/rh/postcode/CF32TW?offset=1"
compare "addresses/rh/postcode/CF32TW?limit=3"
compare "addresses/rh/postcode/CF32TW?offset=101&limit=8"

# PARTIAL
compare "addresses/partial?input=Treganna"
compare "addresses/partial?input=Treganna&limit=18"

# POSTCODE
compare "addresses/postcode/EX24LU"
compare "addresses/postcode/EX24LU?offset=6"
compare "addresses/postcode/EX24LU?limit=1"
compare "addresses/postcode/EX24LU?offset=2&limit=90"

# UPRN
compare "addresses/rh/uprn/10013745617"
compare "addresses/rh/uprn/100040239948"

# TYPE AHEAD"
compare "addresses/eq?input=Holbeche"
compare "addresses/eq?input=Holbe"
compare "addresses/eq?input=EX2"
compare "addresses/eq?input=8%20Fair%20CF51AD"

# Check responses for data with no results in real or mock AI
compare "addresses/rh/postcode/SO996AB"
compare "addresses/partial?input=rtoeutheuohh"
compare "addresses/postcode/SO996AB"
compare "addresses/rh/uprn/11"
compare "addresses/eq?input=tcexdeupydhp"

# EOF

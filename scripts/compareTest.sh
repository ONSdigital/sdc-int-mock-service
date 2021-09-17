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
EXTERNAL_AI="https://initial-test-bulk-1.aims.gcp.onsdigital.uk"

# The AI_TOKEN needs to be set.
[[ -z "$AI_TOKEN" ]] && AI_TOKEN=""

# Run the comparison of the real vs. mock-service responses
function compare {
  ENDPOINT=$1

  [ -z "$AI_TOKEN" ] && echo "Error: AI_TOKEN must be set" && exit 1;

  # Get the response status codes from genuine and mock AI
  curl -s -H "Authorization: Bearer $AI_TOKEN" -I $EXTERNAL_AI/$ENDPOINT | grep HTTP | grep -Eo "[0-9]{3}" > /tmp/external-ai.status.txt
  curl -s -I $MOCK_AI/$ENDPOINT | grep HTTP | grep -Eo "[0-9]{3}" > /tmp/mock-service.status.txt

  # Compare real & mock AI status codes
  diff "/tmp/external-ai.status.txt" "/tmp/mock-service.status.txt" > /dev/null
  if [ $? -ne 0 ]
  then
    echo "*FAIL STATUS*: $ENDPOINT"
    echo -n "  Genuine: "
    cat /tmp/external-ai.status.txt
    echo -n "  Mock   : "
    cat /tmp/mock-service.status.txt
  fi

  # Get responses from genuine and mock AI
  curl -s -H "Authorization: Bearer $AI_TOKEN" $EXTERNAL_AI/$ENDPOINT | jq . > /tmp/external-ai.json
  curl -s $MOCK_AI/$ENDPOINT | jq . > /tmp/mock-service.json

  # Compare real & mock AI results
  jq . /tmp/external-ai.json > /tmp/external-ai-pretty.json
  jq . /tmp/mock-service.json > /tmp/mock-service-pretty.json

  diff "/tmp/external-ai-pretty.json" "/tmp/mock-service-pretty.json" > /dev/null
  if [ $? -eq 0 ]
  then
    echo "Pass: $ENDPOINT"
  else
    echo "*FAIL*: $ENDPOINT"
  fi
}


# RH POSTCODE
compare "addresses/rh/postcode/cf32tw"
compare "addresses/rh/postcode/cf32tw?offset=1"
compare "addresses/rh/postcode/cf32tw?limit=3"
compare "addresses/rh/postcode/cf32tw?offset=101&limit=8"

# PARTIAL
compare "addresses/partial?input=treganna"
compare "addresses/partial?input=treganna&limit=18"

# POSTCODE
compare "addresses/postcode/ex24lu"
compare "addresses/postcode/ex24lu?offset=6"
compare "addresses/postcode/ex24lu?limit=1"
compare "addresses/postcode/ex24lu?offset=2&limit=90"

# UPRN
compare "addresses/rh/uprn/10013745617"
compare "addresses/rh/uprn/100040239948"

# TYPE AHEAD"
compare "addresses/eq?input=holbeche"
compare "addresses/eq?input=holbe"
compare "addresses/eq?input=ex2"
compare "addresses/eq?input=8%20fair%20cf51ad"

# Check responses for data with no results in real or mock AI
compare "addresses/rh/postcode/SO996AB"
compare "addresses/partial?input=rtoeutheuohh"
compare "addresses/postcode/SO996AB"
compare "addresses/rh/uprn/11"
compare "addresses/eq?input=tcexdeupydhp"

# EOF

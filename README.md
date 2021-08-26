
# Mock Service
This repository is a test service that can be used to mock the AI service and will also mock a call to the 
Conctact Centre (CC).

This mock contains 4 different postcodes from England and Wales (2 each). These postcodes will contain a list of addresses.
These postcodes are:
    
    CF3 2TW
    CF5 1AD
    EX2 4LU
    EX4 1EH

## Running

When running successfully version information can be obtained from the info endpoint
    
* localhost:8162/info

The below endpoints will help as a guide to using the endpoints:

* localhost:8162/addresses/help
* localhost:8162/cases/help

## Manual testing

### Addresses endpoints 

These endpoints return AI captured responses which are held within the data resources directory. 
If no data is held for a particular search query then a default 'notFound' response is returned. This mirrors the
behaviour of AI if it is also asked for data which it doesn't hold.

    curl -s localhost:8162/addresses/help

    curl -s localhost:8162/addresses/rh/postcode/{postcode}

    curl -s localhost:8162/addresses/rh/uprn/{uprn}
    
    curl -s localhost:8162/addresses/partial?input={partial}
    
    curl -s localhost:8162/addresses/postcode/{postcode}

    curl -s localhost:8162/addresses/eq?input={search}

### Capture endpoints

These are the same as the addresses endpoints except that they start with '/capture'. This causes the mock-ai
to make a call to AI and store the result in a data file. Subsequent calls to the addresses endpoints will 
return the captured data.

The capture endpoints connect to a real AI service using a valid AI security token. If this is not 
in 'address-index.token' then the endpoints will fail with an error.

    curl -s -H "Authorization: Bearer $AI_TOKEN" 'https://whitelodge-eq-ai-api.census-gcp.onsdigital.uk/capture/addresses/rh/postcode/{postcode}

    curl -s -H "Authorization: Bearer $AI_TOKEN" 'https://whitelodge-eq-ai-api.census-gcp.onsdigital.uk/capture/addresses/rh/uprn/{uprn}
    
    curl -s -H "Authorization: Bearer $AI_TOKEN" 'https://whitelodge-eq-ai-api.census-gcp.onsdigital.uk/capture/addresses/partial?input={partial}
    
    curl -s -H "Authorization: Bearer $AI_TOKEN" 'https://whitelodge-eq-ai-api.census-gcp.onsdigital.uk/capture/addresses/postcode/{postcode}
     
    curl -s -H "Authorization: Bearer $AI_TOKEN" 'https://whitelodge-eq-ai-api.census-gcp.onsdigital.uk/capture/addresses/eq?input={search}

Note: These endpoints will only run when the refreshCaptured.sh script is run. To grab them manually, run the
below curl commands, replacing the "$AI_TOKEN" with the actual AI token.

### Cases endpoints

These endpoints will return mock cases that is held within the cases and questionnaires yaml files that are held within
the resources directory.

    curl -s localhost:8162/cases/help

    curl -s localhost:8162/cases/examples

    curl -s localhost:8162/cases/{caseId}

    curl -s localhost:8162/cases/{caseId}/qid

    curl -s localhost:8162/cases/uprn/{uprn}

    curl -s localhost:8162/cases/ref/{caseRef}

Note: the "/cases/{caseId}/qid" will generate a new QID and UAC for a given case ID.

### Scripts

To verify that the responses from the mock-ai match a live ai you can run scripts/compareTest.sh. Please see the
script header for more details.

## Development notes

### Captured data

To update the captured data, so that it contains the latest responses from a real AI, you can run scripts/refreshCaptured.sh.
This will update all of the data files held in src/main/resources/data. 
Please see the script header for more details.

## Copyright
Copyright (C) 2021 Crown Copyright (Office for National Statistics)

# Mock AI

The mock AIMS part of mock-service has just enough realistic data to be able to allow our
services to function with selected data.

AIMS data tends to alter slightly with each refresh of the real service, so additional
scripts and endpoints are available in order to refresh and validate that data.

## Help endpoints

Here are examples of endpoints calls using `curl`

```
  $ curl -s localhost:8162/mockhelp/addresses
  $ curl -s localhost:8162/mockhelp/addresses/data
  $ curl -s localhost:8162/mockhelp/capture/addresses
```

This help gives a description of the various calls that can be made along with parameter options.

Note the the `/capture/addresses` endpoint is purely for development purposes to assist
with sychronising the data with the real AIMS.


## Manual testing

### Addresses endpoints

These endpoints return AI captured responses which are held within the data resources directory.
If no data is held for a particular search query then a default **notfound** response is returned.
This mirrors the behaviour of AI if it is also asked for data which it doesn't hold.

```
    curl -s localhost:8162/mockhelp/addresses
    curl -s localhost:8162/addresses/rh/postcode/{postcode}
    curl -s localhost:8162/addresses/rh/uprn/{uprn}
    curl -s localhost:8162/addresses/partial?input={partial}
    curl -s localhost:8162/addresses/postcode/{postcode}
    curl -s localhost:8162/addresses/eq?input={search}
```

### Capture endpoints

These are the same as the addresses endpoints except that they start with '/capture'.
This causes the mock-service to make a call to AI and store the result in a data file.
Subsequent calls to the addresses endpoints will return the captured data.

The capture endpoints connect to a real AI service using a valid AI security token.
If this is not in `address-index.token` then the endpoints will fail with an error.

```
    export URL="https://initial-test-bulk-1.aims.gcp.onsdigital.uk"
    curl -k -s -H "Authorization: Bearer $AI_TOKEN" '$URL/capture/addresses/rh/postcode/{postcode}
    curl -k -s -H "Authorization: Bearer $AI_TOKEN" '$URL/capture/addresses/rh/uprn/{uprn}
    curl -k -s -H "Authorization: Bearer $AI_TOKEN" '$URL/capture/addresses/partial?input={partial}
    curl -k -s -H "Authorization: Bearer $AI_TOKEN" '$URL/capture/addresses/postcode/{postcode}
    curl -k -s -H "Authorization: Bearer $AI_TOKEN" '$URL/capture/addresses/eq?input={search}
```

Note: Normally, these endpoints will only run when the `refreshCaptured.sh` script is run,
but can be run individually as above when needed.

### Scripts

To verify that the responses from the mock-service match a live AIM you can run
`scripts/compareTest.sh`. Please see the script header for more details.

### Captured data

To update the captured data, so that it contains the latest responses from a real AI,
you can run `scripts/refreshCaptured.sh`.
This will update all of the data files held in **src/main/resources/data**.
Please see the script header for more details.


## Copyright
Copyright (C) 2021 Crown Copyright (Office for National Statistics)

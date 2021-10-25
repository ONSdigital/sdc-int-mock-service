# Mock Service

A service for use in development and as part of the acceptance environment to mock out the following services:
- Address Indexing (AI or AIMS)
- RM Case Service

Since the case data and address data needs to be aligned, then it was decided to merge the mock services into this single service, for ease of maintenance, and reducing the possibility of data drift.

## Data goals

The "pre-canned" data served up by this service has the following goals:

- Respond as close as possible to the real service
- Have a small set of known manageable data that links across calls, e.g. if we have a Case for a specific UPRN, then the addressing endpoints will also return data.
- provide utilities/scripts to assist in data refresh where possible.

There are four postcodes chosen around which the rest of the data is hinged:
These postcodes are:

    CF3 2TW
    CF5 1AD
    EX2 4LU
    EX4 1EH

The data is keyed in a case-insensitive manner, to mirror the case-insensitive nature
of AIMS.

## Running

When running successfully version information can be obtained from the info endpoint

* localhost:8162/info

The below endpoint will help as a guide to using the service, and shows links to other help
items:

* localhost:8162/mockhelp

To get this help in the dev environment with curl do the following:
```
	curl -s -k "https://dev-rh.int.gcp.onsdigital.uk/mockhelp"
```

## Further details

Further details are provided here:

- [guide to using the mock AI](doc/mock-ai.md)
- [guide to using the mock Case Service](doc/mock-case-service.md)


## Copyright
Copyright (C) 2021 Crown Copyright (Office for National Statistics)

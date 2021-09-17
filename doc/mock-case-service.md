# Mock Case Service

The mock Case Service part of mock-service has just enough realistic data to be able to allow our
services to function with selected data.

## Help endpoints

Here are examples of endpoints calls using `curl`

```
  $ curl -s localhost:8162/mockhelp/cases
  $ curl -s localhost:8162/mockhelp/cases/data
```

This help gives a description of the various calls that can be made along with parameter options.

## Cases endpoints

These endpoints will return mock case data held within the resources directory.

```
    curl -s localhost:8162/cases/{caseId}
    curl -s localhost:8162/cases/{caseId}/qid
    curl -s localhost:8162/cases/uprn/{uprn}
    curl -s localhost:8162/cases/ref/{caseRef}
```

## Copyright
Copyright (C) 2021 Crown Copyright (Office for National Statistics)

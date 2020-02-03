# aws-lambda-kvp-minimal-java

A minimal example to deploy a HTTP GET-based KVP API written in Java on AWS Lambda.
The Java code make one external request and can also return an SVG image (because those are two breakthrough that I needed).

## Build and deploy

Build deployment file manually and configure using AWS Cloud Console:

Go to `/LambdaAPI`.

```bash
mvn clean package shade:shade
```

- [Create function](https://eu-central-1.console.aws.amazon.com/lambda/home?region=eu-central-1) _from scratch_
  - Function name: `minimal-kvp`
  - Runtime: `Java 8`
  - Use _"Create a new role with basic Lambda permissions"_
  - Upload jar file: `target/aws-lambda-kvp-0.1.0-SNAPSHOT.jar`
  - Configure handler: `de.ifgi.nuest.awslambda.kvp.APIHandler::handleRequest`
- Create API in the [API Gateway Console](https://console.aws.amazon.com/apigateway)
  - REST API, "New API"
    - Name: `Minimal REST API`
    - Resources
      - Proxy resource: YES
      - Name: `API`
      - Path: `{proxy+}`
      - Enable API Gateway CORS: YES
    - _/{proxy+} - ANY - Setup_
      - Integration type: `Lambda Function Proxy`
      - Lambda Function: `minimal-kvp`
      - "give API Gateway permission"
    - _Method Test_ from API Gateway configuration now possible
      - Path e.g., `/api/v1`
      - Query strings e.g., `param1=hello&param2=world`
      - Headers e.g., `x-user-request: Test`
      - Log excerpt:
        ```
        Execution log for request 89d139db-4eec-4bc3-94bb-50678599bcd9
        Mon Feb 03 09:54:45 UTC 2020 : Starting execution for request: 89d139db-4eec-4bc3-94bb-50678599bcd9
        Mon Feb 03 09:54:45 UTC 2020 : HTTP Method: GET, Resource Path: /api/v1
        Mon Feb 03 09:54:45 UTC 2020 : Method request path: {proxy=api/v1}
        Mon Feb 03 09:54:45 UTC 2020 : Method request query string: {param1=hello, param2=world}
        Mon Feb 03 09:54:45 UTC 2020 : Method request headers: {x-user-request= Test}
        [...]
        Mon Feb 03 09:54:46 UTC 2020 : Received response. Status: 200, Integration latency: 353 ms
        Mon Feb 03 09:54:46 UTC 2020 : Endpoint response headers: {Date=Mon, 03 Feb 2020 09:54:46 GMT, Content-Type=application/json, Content-Length=96, Connection=keep-alive, x-amzn-RequestId=aa3064d0-a11a-4eda-a5d5-bb1d9d91cf68, x-amzn-Remapped-Content-Length=0, X-Amz-Executed-Version=$LATEST, X-Amzn-Trace-Id=root=1-5e37ede5-40a3c3575576772bbf19725c;sampled=0}
        Mon Feb 03 09:54:46 UTC 2020 : Endpoint response body before transformations: {"headers":{"x-handled-by":"GET by Params"},"body":"Hello, thanks for asking!","statusCode":200}
        Mon Feb 03 09:54:46 UTC 2020 : Method response body after transformations: Hello, thanks for asking!
        Mon Feb 03 09:54:46 UTC 2020 : Method response headers: {x-handled-by=GET by Params, X-Amzn-Trace-Id=Root=1-5e37ede5-40a3c3575576772bbf19725c;Sampled=0}
        Mon Feb 03 09:54:46 UTC 2020 : Successfully completed execution
        Mon Feb 03 09:54:46 UTC 2020 : Method completed with status: 200
        ```
- Deploy API "/{proxy+}" via Actions > Deploy in Amazon API Gateway
  - Stage name: `public`
  - Note the Invoke URL, e.g. https://n10fvc22kj.execute-api.eu-central-1.amazonaws.com/public
  - Try out the URL
    - https://n10fvc22kj.execute-api.eu-central-1.amazonaws.com/public/api/v1/hello=world
    - `curl -X PUT 'https://n10fvc22kj.execute-api.eu-central-1.amazonaws.com/public/api/v1/hello=world?param=my-value' -H 'content-type: plain/text'`
    - `curl -X PUT 'https://n10fvc22kj.execute-api.eu-central-1.amazonaws.com/public/api/v1/svg?hello=world&param=my-value'`
    - https://n10fvc22kj.execute-api.eu-central-1.amazonaws.com/public/api/v1/svg?hello=world&param=my-valuu

## [WIP] Deploy with [SAM](https://aws.amazon.com/serverless/sam/)

```bash
# configure AWS credentials:
# aws configure

sam build

# uses .toml file created via sam deploy --guided
sam deploy
```

**Local testing**

```bash
sam local start-lambda
```



## Resources

- https://www.baeldung.com/aws-lambda-api-gateway
  - https://github.com/eugenp/tutorials/tree/master/aws-lambda

## License

This repository is Copyright 2019 Daniel Nüst and published under GPL v3 license.

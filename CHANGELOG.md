# Changelog

## [v1.0.0-SNAPSHOT](https://github.com/NASA-PDS/registry-api/tree/v1.0.0-SNAPSHOT) (2022-04-19)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v0.5.1...v1.0.0-SNAPSHOT)

**Requirements:**

- As a user, I want to see the version of the API specification in the URL of the service [\#8](https://github.com/NASA-PDS/registry-api/issues/8)

**Defects:**

- Invalid or corrupted registry-api-service.jar file in registry-api-service docker image [\#114](https://github.com/NASA-PDS/registry-api/issues/114) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]

**Other closed issues:**

- Update download repository in Registry API Service dockerfiles [\#115](https://github.com/NASA-PDS/registry-api/issues/115)
- Upgrade to OpenSearch java client [\#3](https://github.com/NASA-PDS/registry-api/issues/3)

## [v0.5.1](https://github.com/NASA-PDS/registry-api/tree/v0.5.1) (2022-03-25)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v0.5.0...v0.5.1)

## [v0.5.0](https://github.com/NASA-PDS/registry-api/tree/v0.5.0) (2022-03-25)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/66b22b0ede8c41921a37521433fa15a57f33513d...v0.5.0)

**Requirements:**

- As a user, I want to see the version of the API specification in the URL of the service [\#59](https://github.com/NASA-PDS/registry-api/issues/59)
- As a user, I want the end-point /api to redirect to the API documentation [\#63](https://github.com/NASA-PDS/registry-api/issues/63)
- As a user, I want the /products end point to work for any class of products [\#64](https://github.com/NASA-PDS/registry-api/issues/64)
- As a user,  I want specific end points for products which are not collections or bundles [\#65](https://github.com/NASA-PDS/registry-api/issues/65)
- As a user, I want to know why my query syntax is invalid [\#66](https://github.com/NASA-PDS/registry-api/issues/66)
- As an operator, I want to have a wrapper script for starting up the API service [\#67](https://github.com/NASA-PDS/registry-api/issues/67)
- As an API user, I want to handle long-running queries that take \>10 seconds. [\#68](https://github.com/NASA-PDS/registry-api/issues/68)
- As an API user, I want an average query response time of 1 second for q=\* queries [\#69](https://github.com/NASA-PDS/registry-api/issues/69)

**Improvements:**

- add ops:Tracking\_Meta/ops:node\_name and ops:Tracking\_Meta/ops:harvest\_date\_time  to meta section of API response [\#102](https://github.com/NASA-PDS/registry-api/issues/102)
- Rename `engineering` package naming to `registry` [\#10](https://github.com/NASA-PDS/registry-api/issues/10)
- implement freetext search [\#70](https://github.com/NASA-PDS/registry-api/issues/70)
- implement the start/limit efficiently [\#71](https://github.com/NASA-PDS/registry-api/issues/71)
- Add list of available fields in response format [\#55](https://github.com/NASA-PDS/registry-api/issues/55)
- As a user of the API, I want to have an homogeneous way of getting error or status for long requests or requests longer than supported by a synchronous web API. [\#56](https://github.com/NASA-PDS/registry-api/issues/56)

**Defects:**

- Content type pds4+xml does not namespace tags in \<pds\_api:meta\> with pds\_api [\#93](https://github.com/NASA-PDS/registry-api/issues/93)
- Fix unstable integration build failure [\#89](https://github.com/NASA-PDS/registry-api/issues/89) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- Service using JSON blob in pds4+xml response when it should use ops blob [\#81](https://github.com/NASA-PDS/registry-api/issues/81) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- blob/json\_blob should not be included in default response [\#73](https://github.com/NASA-PDS/registry-api/issues/73) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Improvements to branch pds-api-125 [\#18](https://github.com/NASA-PDS/registry-api/issues/18) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- CICD did not publish the jar on artifactory [\#2](https://github.com/NASA-PDS/registry-api/issues/2) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- AWS cost analysis tag  is not 'Alpha' but instead 'Alfa' [\#5](https://github.com/NASA-PDS/registry-api/issues/5) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- parsing right hand side of operator does not behave as desired [\#57](https://github.com/NASA-PDS/registry-api/issues/57) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- parsing of string does not succeed [\#58](https://github.com/NASA-PDS/registry-api/issues/58)

**Other closed issues:**

- Refactor meta section of pds4+xml response to use ops namespace [\#85](https://github.com/NASA-PDS/registry-api/issues/85)
- Refactor meta section of pds4+json response to use ops namespace [\#84](https://github.com/NASA-PDS/registry-api/issues/84)
- Update API to only return results where archive status in \(archived, certified\) [\#78](https://github.com/NASA-PDS/registry-api/issues/78)
- Fix code scanning alert - Failure to use HTTPS or SFTP URL in Maven artifact upload/download [\#74](https://github.com/NASA-PDS/registry-api/issues/74)
- As a developer, I want to update in a single place the list of supported MIME types [\#9](https://github.com/NASA-PDS/registry-api/issues/9)
- add creation of routing rule to terraform script [\#7](https://github.com/NASA-PDS/registry-api/issues/7)
- Revert  ES High Level Java API version 7.13.3 [\#6](https://github.com/NASA-PDS/registry-api/issues/6)
- As a developer, I want to update in a single place the list of supported MIME types [\#60](https://github.com/NASA-PDS/registry-api/issues/60)



\* *This Changelog was automatically generated by [github_changelog_generator](https://github.com/github-changelog-generator/github-changelog-generator)*

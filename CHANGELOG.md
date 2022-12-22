# Changelog

## [«unknown»](https://github.com/NASA-PDS/registry-api/tree/«unknown») (2022-12-22)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.2.0-SNAPSHOT...«unknown»)

**Requirements:**

- As a user, I want the API to support redundant `/` in the url [\#212](https://github.com/NASA-PDS/registry-api/issues/212)

**Defects:**

- observational end-point returns collections [\#202](https://github.com/NASA-PDS/registry-api/issues/202) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]

## [v1.2.0-SNAPSHOT](https://github.com/NASA-PDS/registry-api/tree/v1.2.0-SNAPSHOT) (2022-12-22)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.1.11...v1.2.0-SNAPSHOT)

**Requirements:**

- As a registry-tool/registry-user I want to ensure leading multiple forward-slashes in request paths are stripped out [\#208](https://github.com/NASA-PDS/registry-api/issues/208)

**Other closed issues:**

- Investigate NPE from SBNPSI registry [\#213](https://github.com/NASA-PDS/registry-api/issues/213) [[s.critical](https://github.com/NASA-PDS/registry-api/labels/s.critical)]

## [v1.1.11](https://github.com/NASA-PDS/registry-api/tree/v1.1.11) (2022-12-14)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.1.10...v1.1.11)

**Defects:**

- /all suffix returns a message which I don't understand [\#190](https://github.com/NASA-PDS/registry-api/issues/190) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Stable Roundup can no longer trigger Imaging workflow [\#188](https://github.com/NASA-PDS/registry-api/issues/188)

**Other closed issues:**

- Adapt code to new harvest behavior where all source properties in OpenSearch are arrays [\#203](https://github.com/NASA-PDS/registry-api/issues/203)
- Add CORS header to registry API deployment [\#189](https://github.com/NASA-PDS/registry-api/issues/189)

## [v1.1.10](https://github.com/NASA-PDS/registry-api/tree/v1.1.10) (2022-09-29)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.1.9...v1.1.10)

## [v1.1.9](https://github.com/NASA-PDS/registry-api/tree/v1.1.9) (2022-09-28)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.1.2...v1.1.9)

## [v1.1.2](https://github.com/NASA-PDS/registry-api/tree/v1.1.2) (2022-09-27)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.1.1...v1.1.2)

## [v1.1.1](https://github.com/NASA-PDS/registry-api/tree/v1.1.1) (2022-09-26)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.1.0...v1.1.1)

**Defects:**

- Sub-project should inherit parent project version in order to be able to automate versioning using mvn commands [\#182](https://github.com/NASA-PDS/registry-api/issues/182) [[s.low](https://github.com/NASA-PDS/registry-api/labels/s.low)]

## [v1.1.0](https://github.com/NASA-PDS/registry-api/tree/v1.1.0) (2022-09-16)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.0.2...v1.1.0)

**Requirements:**

- As a user,  I want to query for products from any PDS4 product type [\#12](https://github.com/NASA-PDS/registry-api/issues/12)

**Defects:**

- limit=0 is not providing list of properties \(fka summary-only\) [\#179](https://github.com/NASA-PDS/registry-api/issues/179) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- The /products, /bundles & /collections endpoints are missing from the API [\#178](https://github.com/NASA-PDS/registry-api/issues/178)
- Swagger-UI does not display properly out-of-the-box  [\#174](https://github.com/NASA-PDS/registry-api/issues/174)
- `fields` query parameter does not work consistently across all response formats [\#172](https://github.com/NASA-PDS/registry-api/issues/172) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- summary does not contain the property values [\#171](https://github.com/NASA-PDS/registry-api/issues/171) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- summary-only does not work as expected [\#167](https://github.com/NASA-PDS/registry-api/issues/167) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- summary-only does not work on products of a collection [\#152](https://github.com/NASA-PDS/registry-api/issues/152) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- bundle of a product does not return result [\#150](https://github.com/NASA-PDS/registry-api/issues/150) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- swagger-ui does not show the expected version [\#149](https://github.com/NASA-PDS/registry-api/issues/149) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- wildcard search in query parameter returning odd results [\#134](https://github.com/NASA-PDS/registry-api/issues/134) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- /bundles/{identifier}/all does not return any results [\#118](https://github.com/NASA-PDS/registry-api/issues/118) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]

**Other closed issues:**

- Provide POC of multi-tenant registry [\#169](https://github.com/NASA-PDS/registry-api/issues/169)
- Fix `like` functionality and document in the user's guide [\#159](https://github.com/NASA-PDS/registry-api/issues/159)

## [v1.0.2](https://github.com/NASA-PDS/registry-api/tree/v1.0.2) (2022-07-27)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.0.1...v1.0.2)

**Requirements:**

- As a user, I want to have an administrator contact when I am getting an error 500 from the server [\#109](https://github.com/NASA-PDS/registry-api/issues/109)

**Improvements:**

- Add back the Dockerfile used for the AWS/Fargate deployment [\#140](https://github.com/NASA-PDS/registry-api/issues/140)
- Refactor API endpoints for simpler architecture/design/implementation to maintain/extend [\#131](https://github.com/NASA-PDS/registry-api/issues/131)
- Support override of application.properties for AWS Docker image [\#117](https://github.com/NASA-PDS/registry-api/issues/117)

**Defects:**

- Cloudfront function api\_uri\_rewrite  does not check for empty command when parsing api URI [\#164](https://github.com/NASA-PDS/registry-api/issues/164)
- fields parameter does not return values if more than one value is requested for CSV format [\#162](https://github.com/NASA-PDS/registry-api/issues/162) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- / is not returning swagger API doc [\#141](https://github.com/NASA-PDS/registry-api/issues/141) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]

**Other closed issues:**

- apply bug fix on stable release [\#160](https://github.com/NASA-PDS/registry-api/issues/160)
- add ops:Tracking\_Meta/ops:archive\_status to meta section of API response [\#101](https://github.com/NASA-PDS/registry-api/issues/101)

## [v1.0.1](https://github.com/NASA-PDS/registry-api/tree/v1.0.1) (2022-06-09)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.0.0...v1.0.1)

**Requirements:**

- As an API caller\(user\) I want to specify fields for endpoints given a lidvid [\#80](https://github.com/NASA-PDS/registry-api/issues/80)

**Improvements:**

- \[SECURITY\] Upgrade jackson dependencies to remove vulnerability [\#122](https://github.com/NASA-PDS/registry-api/issues/122) [[s.low](https://github.com/NASA-PDS/registry-api/labels/s.low)]
- Remove the home controller from the swagger-ui [\#108](https://github.com/NASA-PDS/registry-api/issues/108)
- lidvid resolution need to use \_search instead of \_doc elasticsearch requests  [\#105](https://github.com/NASA-PDS/registry-api/issues/105)

**Defects:**

- \[SECURITY\] Log4j vulnerability in lexer [\#137](https://github.com/NASA-PDS/registry-api/issues/137) [[s.critical](https://github.com/NASA-PDS/registry-api/labels/s.critical)]
- Registry API Service docker container fails to start with error: Unable to access jarfile /usr/local/registry-api-service/registry-api-service.jar [\#128](https://github.com/NASA-PDS/registry-api/issues/128) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Update all endpoints to only allow access to public data. [\#113](https://github.com/NASA-PDS/registry-api/issues/113) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- documentation does not match behavior [\#106](https://github.com/NASA-PDS/registry-api/issues/106) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- LID -\> LIDVID conversion not consistent in registry-api [\#79](https://github.com/NASA-PDS/registry-api/issues/79) [[s.low](https://github.com/NASA-PDS/registry-api/labels/s.low)]

**Other closed issues:**

- Bump search version to 1.0 [\#143](https://github.com/NASA-PDS/registry-api/issues/143)

## [v1.0.0](https://github.com/NASA-PDS/registry-api/tree/v1.0.0) (2022-04-19)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v0.5.1...v1.0.0)

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
- As a user, I want to have a complete default fields \(for now at least\) [\#155](https://github.com/NASA-PDS/registry-api/issues/155)
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

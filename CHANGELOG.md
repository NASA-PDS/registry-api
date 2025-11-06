# Changelog

## [«unknown»](https://github.com/NASA-PDS/registry-api/tree/«unknown») (2025-11-06)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.6.2...«unknown»)

**Defects:**

- API search results using "search-after" returns empty \[data\] block even though I can find the product by lidvid [\#677](https://github.com/NASA-PDS/registry-api/issues/677) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]

**Other closed issues:**

- Registry API Test Suite is failing [\#680](https://github.com/NASA-PDS/registry-api/issues/680) [[s.critical](https://github.com/NASA-PDS/registry-api/labels/s.critical)]
- Manage errors as recommended in the spring mvc framework [\#286](https://github.com/NASA-PDS/registry-api/issues/286)

## [v1.6.2](https://github.com/NASA-PDS/registry-api/tree/v1.6.2) (2025-07-09)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/1.6.1...v1.6.2)

## [1.6.1](https://github.com/NASA-PDS/registry-api/tree/1.6.1) (2025-07-09)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.6.1...1.6.1)

## [v1.6.1](https://github.com/NASA-PDS/registry-api/tree/v1.6.1) (2025-07-08)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.6.0...v1.6.1)

**Requirements:**

- As a user, I want the total number of products returned to match both the total number of products expected to be loaded and the total number of products that were loaded [\#630](https://github.com/NASA-PDS/registry-api/issues/630)

## [v1.6.0](https://github.com/NASA-PDS/registry-api/tree/v1.6.0) (2025-06-26)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.5.0...v1.6.0)

**Requirements:**

- As application support, I want to see in the server side logs the full query being sent to OpenSearch [\#575](https://github.com/NASA-PDS/registry-api/issues/575)
- As a user, I want to receive a XML response that contains the PDS4 label metadata in XML format \(application/vnd.nasa.pds.pds4+xml\)  [\#440](https://github.com/NASA-PDS/registry-api/issues/440)
- As a client developer, I want to facet on 1 or more fields in the registry [\#283](https://github.com/NASA-PDS/registry-api/issues/283)
- As a user, I want to receive a JSON response that contains the PDS4 label metadata in JSON format \(application/vnd.nasa.pds.pds4+json\) [\#450](https://github.com/NASA-PDS/registry-api/issues/450)

**Defects:**

- The API fails when no Accept header is provided [\#638](https://github.com/NASA-PDS/registry-api/issues/638) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Fix broken branch testing [\#634](https://github.com/NASA-PDS/registry-api/issues/634) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Registry API returning \>1 version of data [\#616](https://github.com/NASA-PDS/registry-api/issues/616) [[s.critical](https://github.com/NASA-PDS/registry-api/labels/s.critical)]
- Responses do not return correct metadata when `fields` parameter is specified [\#548](https://github.com/NASA-PDS/registry-api/issues/548) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- Fix code scanning alerts per logging [\#522](https://github.com/NASA-PDS/registry-api/issues/522) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]

**Other closed issues:**

- Fix AWS deployment after dependabot upgrades [\#654](https://github.com/NASA-PDS/registry-api/issues/654)
- Analyze app scan [\#607](https://github.com/NASA-PDS/registry-api/issues/607)
- Latest `develop` version builds but does not run in local development environment. [\#605](https://github.com/NASA-PDS/registry-api/issues/605)
- Delete deprecated LIDs from Registry [\#560](https://github.com/NASA-PDS/registry-api/issues/560)
- Benchmark optimal number of products per page for API requests [\#552](https://github.com/NASA-PDS/registry-api/issues/552)
- Test task ignore me develop [\#545](https://github.com/NASA-PDS/registry-api/issues/545)
- Test task ignore me [\#543](https://github.com/NASA-PDS/registry-api/issues/543)
- Update to utilize new multi-tenancy approach [\#304](https://github.com/NASA-PDS/registry-api/issues/304)

## [v1.5.0](https://github.com/NASA-PDS/registry-api/tree/v1.5.0) (2024-09-03)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.4.1...v1.5.0)

**Requirements:**

- As a user, I want to get a description of the API when I request it from its base URL in a web browser [\#516](https://github.com/NASA-PDS/registry-api/issues/516)
- As a user, I want to get all the products for a specific PDS4 product class [\#511](https://github.com/NASA-PDS/registry-api/issues/511)
- As a user, I want to know what are all the possible properties I can search against \(`/properties`\) [\#506](https://github.com/NASA-PDS/registry-api/issues/506)
- As a user, I want to receive metadata only in the API responses \(no binary blobs\) [\#497](https://github.com/NASA-PDS/registry-api/issues/497)
- As a user, I want to filter the products by any available PDS4 property by combining comparison operators using logical operators [\#495](https://github.com/NASA-PDS/registry-api/issues/495)
- As a user, I want to filter the products by any available PDS4 property using comparison operators [\#494](https://github.com/NASA-PDS/registry-api/issues/494)
- As a user, I want to apply an additional query filter \(`q=`\) to the `/classes/{class}` result set [\#493](https://github.com/NASA-PDS/registry-api/issues/493)
- As a user, I want to apply an additional query filter \(`q=`\) to the `/products/{identifier}/member-of/member-of` result set [\#492](https://github.com/NASA-PDS/registry-api/issues/492)
- As a user, I want to apply an additional query filter \(`q=`\) to the `/products/{identifier}/member-of` result set [\#491](https://github.com/NASA-PDS/registry-api/issues/491)
- As a user, I want to apply an additional query filter \(`q=`\) to members of the members of an aggregate product \(`/products/{identifier}/members/members`\) [\#490](https://github.com/NASA-PDS/registry-api/issues/490)
- As a user, by default, I want to search for the latest versions of all products on the `/classes/{class}` endpoint unless explicitly requested [\#488](https://github.com/NASA-PDS/registry-api/issues/488)
- As a user, by default, I want to search only for the latest versions of all products on the `/products/{identifier}/member-of/member-of` endpoint [\#487](https://github.com/NASA-PDS/registry-api/issues/487)
- As a user, by default, I want to search for only the latest versions of all products on the `/products/{identifier}/member-of` endpoint [\#486](https://github.com/NASA-PDS/registry-api/issues/486)
- As a user, by default, I want to search for only the latest versions of all products on the `/products/{identifier}/members/members` endpoint [\#485](https://github.com/NASA-PDS/registry-api/issues/485)
- As a user, by default, I want to search for only the latest versions of all products on the `/products/{identifier}/members` endpoint [\#484](https://github.com/NASA-PDS/registry-api/issues/484)
- As a user, I want to filter the products by any available PDS4 property using a combination of comparison, logical, and precedence grouping operators [\#469](https://github.com/NASA-PDS/registry-api/issues/469)
- As a user, I want to get all product versions associated to one lid [\#436](https://github.com/NASA-PDS/registry-api/issues/436)
- As a user, by default, I want to resolve the latest version of a product when given a product logical\_identifier \(LID\) \(`/products/{logical_identifier}` endpoint\)  [\#435](https://github.com/NASA-PDS/registry-api/issues/435)
- As a user, I want to get a product description given a lidvid [\#434](https://github.com/NASA-PDS/registry-api/issues/434)
- As a user, I want to apply an additional query filter \(`q=`\) to members of an aggregate product \(`/products/{identifier}/members`\) [\#298](https://github.com/NASA-PDS/registry-api/issues/298)
- As a user, I want to be able to paginate over any number of results returned from a query. [\#176](https://github.com/NASA-PDS/registry-api/issues/176)
- As a user, by default, I want to search only for the latest versions of all products on the `/products` endpoint [\#426](https://github.com/NASA-PDS/registry-api/issues/426)

**Defects:**

- Investigate sporadic 500 and 504 errors with registry API [\#431](https://github.com/NASA-PDS/registry-api/issues/431) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- Insufficient scrubbing of user input values prior to logging [\#388](https://github.com/NASA-PDS/registry-api/issues/388) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- Cleanup logging of keys [\#387](https://github.com/NASA-PDS/registry-api/issues/387) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]

**Other closed issues:**

- Refactor common RegistrySearchRequestBuilder dot-chains into applyMultipleProductsDefaults\(\) [\#515](https://github.com/NASA-PDS/registry-api/issues/515)
- Manage renewal of AWS credentials [\#514](https://github.com/NASA-PDS/registry-api/issues/514)
- Investigate why the API is slow on MCP [\#510](https://github.com/NASA-PDS/registry-api/issues/510) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Complete deployment procedure for the SAs [\#509](https://github.com/NASA-PDS/registry-api/issues/509)
- Partial implementation of the opensearch serverless queries [\#470](https://github.com/NASA-PDS/registry-api/issues/470)
- ECS Roles for Registry-API [\#429](https://github.com/NASA-PDS/registry-api/issues/429)
- Demo partial implementation on AWS [\#423](https://github.com/NASA-PDS/registry-api/issues/423)
- Demo registry-api with new opensearchclient [\#422](https://github.com/NASA-PDS/registry-api/issues/422)
- Add pds-deep-registry-archive execution to branch testing [\#412](https://github.com/NASA-PDS/registry-api/issues/412)
- Add github action on dev branch push, running registry integration test with docker compose [\#301](https://github.com/NASA-PDS/registry-api/issues/301)

## [v1.4.1](https://github.com/NASA-PDS/registry-api/tree/v1.4.1) (2024-02-29)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.4.0...v1.4.1)

**Defects:**

- members query return 500 when members do not exist in the registry or alternate\_id does not exists [\#411](https://github.com/NASA-PDS/registry-api/issues/411) [[s.critical](https://github.com/NASA-PDS/registry-api/labels/s.critical)]
- Broken tests per pagination upgrade \#397 [\#404](https://github.com/NASA-PDS/registry-api/issues/404) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- /properties always returns json format [\#339](https://github.com/NASA-PDS/registry-api/issues/339) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]

## [v1.4.0](https://github.com/NASA-PDS/registry-api/tree/v1.4.0) (2024-01-23)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.3.1...v1.4.0)

**Defects:**

- Pagination performance does not meet requirements [\#352](https://github.com/NASA-PDS/registry-api/issues/352) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]

**Other closed issues:**

- Have the inner list shown as string with | delimiter for singular results [\#381](https://github.com/NASA-PDS/registry-api/issues/381)
- Investigate and fix intermittent Registry-API Errors [\#378](https://github.com/NASA-PDS/registry-api/issues/378)
- registry-api needs to use the new ancestry fields [\#353](https://github.com/NASA-PDS/registry-api/issues/353) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]

## [v1.3.1](https://github.com/NASA-PDS/registry-api/tree/v1.3.1) (2023-10-10)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/test_new_release...v1.3.1)

**Requirements:**

- As a user, I want my API request to execute successfully even when the registry contains corrupted documents [\#361](https://github.com/NASA-PDS/registry-api/issues/361)
- As a PDS operator, I want to know the health of the registry API service [\#336](https://github.com/NASA-PDS/registry-api/issues/336)

**Defects:**

- /products API endpoint is broken [\#379](https://github.com/NASA-PDS/registry-api/issues/379)
- text/csv format is impacted by the repairkit script \(apparently\) [\#375](https://github.com/NASA-PDS/registry-api/issues/375) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- `Accept:*` response not defaulting to valid application/json [\#356](https://github.com/NASA-PDS/registry-api/issues/356) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- api does not return information that OpenSearch says is public [\#355](https://github.com/NASA-PDS/registry-api/issues/355) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Request for json+pds4 response fails in production [\#349](https://github.com/NASA-PDS/registry-api/issues/349) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- API falsely reports 10000 hits for hits\>10000 [\#343](https://github.com/NASA-PDS/registry-api/issues/343) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- members of a bundle does not work on new test dataset [\#341](https://github.com/NASA-PDS/registry-api/issues/341) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- the request url in the error message does not make sense [\#262](https://github.com/NASA-PDS/registry-api/issues/262) [[s.low](https://github.com/NASA-PDS/registry-api/labels/s.low)]

**Other closed issues:**

- Create Cloudwatch alarm when Registry API throws an error [\#366](https://github.com/NASA-PDS/registry-api/issues/366)
- Investigate usage of registry api [\#364](https://github.com/NASA-PDS/registry-api/issues/364)
- Update the aws\_lb\_target\_group to use the new healthcheck endpoint [\#347](https://github.com/NASA-PDS/registry-api/issues/347)
- Design Logging and Monitoring Approach for Nucleus [\#342](https://github.com/NASA-PDS/registry-api/issues/342)
- Investigate how Google DataSet Search can help with the Search [\#338](https://github.com/NASA-PDS/registry-api/issues/338)
- Move terraform and docker folder to the root of the repository [\#320](https://github.com/NASA-PDS/registry-api/issues/320)
- Add parent collection identifier to product metadata [\#319](https://github.com/NASA-PDS/registry-api/issues/319)
- Add parent bundle identifier to collection metadata [\#318](https://github.com/NASA-PDS/registry-api/issues/318)
- Add registry/docker compose integration tests to github action on dev branches [\#269](https://github.com/NASA-PDS/registry-api/issues/269)

## [test_new_release](https://github.com/NASA-PDS/registry-api/tree/test_new_release) (2023-06-06)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.2.0...test_new_release)

**Defects:**

- PdsVid erroneously fails major-version of zero during validation [\#334](https://github.com/NASA-PDS/registry-api/issues/334)
- Product summary object has an incomplete "properties" set [\#277](https://github.com/NASA-PDS/registry-api/issues/277) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]

**Other closed issues:**

- Make the registry-api 1.2.0 deployment work on gamma [\#330](https://github.com/NASA-PDS/registry-api/issues/330)
- Remove the 'products' in the /classes [\#326](https://github.com/NASA-PDS/registry-api/issues/326)
- Remove ./support tree from repository [\#321](https://github.com/NASA-PDS/registry-api/issues/321)
- As a EN Operator, I want to the registry API to be fault tolerant, analysis [\#297](https://github.com/NASA-PDS/registry-api/issues/297)
- Update staging and production Registry APIs to increase window limit [\#291](https://github.com/NASA-PDS/registry-api/issues/291)

## [v1.2.0](https://github.com/NASA-PDS/registry-api/tree/v1.2.0) (2023-04-11)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.1.12...v1.2.0)

**Requirements:**

- As a user, I want to see available query params in the swagger documentation [\#249](https://github.com/NASA-PDS/registry-api/issues/249)
- Suggest: Return Content-Type application/json by default [\#248](https://github.com/NASA-PDS/registry-api/issues/248)
- As a user, I want to search by any metadata attribute [\#282](https://github.com/NASA-PDS/registry-api/issues/282)
- As a user, I want to know the members of a bundle product [\#223](https://github.com/NASA-PDS/registry-api/issues/223) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- As a user, I want to get application/json response format by default if I request an API url in my browser [\#439](https://github.com/NASA-PDS/registry-api/issues/439)
- As a user, I want to query only the latest versions of products unless explicitly requested [\#441](https://github.com/NASA-PDS/registry-api/issues/441)
- As a user, I want to be able to access the Search API Swagger interface from pds.nasa.gov [\#442](https://github.com/NASA-PDS/registry-api/issues/442)
- the default proposed mime type in swagger-ui.html should be 'application/json' [\#88](https://github.com/NASA-PDS/registry-api/issues/88) [[s.low](https://github.com/NASA-PDS/registry-api/labels/s.low)]

**Defects:**

- Registry API won't deploy with Java 17 [\#314](https://github.com/NASA-PDS/registry-api/issues/314)
- API not returning value for NAIF bundles [\#305](https://github.com/NASA-PDS/registry-api/issues/305) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- API crashes with JVM memory error on data sets with very large labels \(\>1MB\) [\#296](https://github.com/NASA-PDS/registry-api/issues/296) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- Requests with Accept:application/vnd.nasa.gds.pds4+json fail for products with no ops:Label\_File\_Info/ops:file\_name [\#293](https://github.com/NASA-PDS/registry-api/issues/293) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Project does not successfully build/run without global existence of some dependencies [\#279](https://github.com/NASA-PDS/registry-api/issues/279)
- product's members does not work on a collection [\#268](https://github.com/NASA-PDS/registry-api/issues/268) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- The members of a bundle can not be requested [\#261](https://github.com/NASA-PDS/registry-api/issues/261) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- api does not return 400 error when q parameter value cannot be parsed [\#260](https://github.com/NASA-PDS/registry-api/issues/260) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- not found lidvid does not return 404 error [\#258](https://github.com/NASA-PDS/registry-api/issues/258) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- requests with bad `q=` syntax should return 400 error [\#241](https://github.com/NASA-PDS/registry-api/issues/241) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- Pagination not working as expected with /collections/{identifier}/products [\#240](https://github.com/NASA-PDS/registry-api/issues/240) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- registry-api does not respect VID when a LIDVID is used as an id, instead returns latest version [\#234](https://github.com/NASA-PDS/registry-api/issues/234) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- /classes/collections/\<lidvid\>/members \(and deprecated equivalent\) hangs [\#231](https://github.com/NASA-PDS/registry-api/issues/231) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Keyword search does not work on registry-api deployed on pds.nasa.gov [\#227](https://github.com/NASA-PDS/registry-api/issues/227) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- registry-api does not return latest version of product metadata when multiple versions are harvested [\#224](https://github.com/NASA-PDS/registry-api/issues/224) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- swaggger ui error in production [\#211](https://github.com/NASA-PDS/registry-api/issues/211) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- lidvid not found raises 500 error [\#207](https://github.com/NASA-PDS/registry-api/issues/207) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- When q="" the returned status is 500 instead of 400 [\#206](https://github.com/NASA-PDS/registry-api/issues/206) [[s.low](https://github.com/NASA-PDS/registry-api/labels/s.low)]
- /classes endpoint does not work in a browser [\#200](https://github.com/NASA-PDS/registry-api/issues/200) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- documents / members returns an error [\#196](https://github.com/NASA-PDS/registry-api/issues/196) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- version number are treated as floats [\#191](https://github.com/NASA-PDS/registry-api/issues/191) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]
- \*Critical OSS Vulnerability:\* spring-web@5.3.20 [\#148](https://github.com/NASA-PDS/registry-api/issues/148) [[s.low](https://github.com/NASA-PDS/registry-api/labels/s.low)]
- JSON response is using HTTP in href references [\#145](https://github.com/NASA-PDS/registry-api/issues/145) [[s.low](https://github.com/NASA-PDS/registry-api/labels/s.low)]
- Fix vulnerabilities raised by sonalift [\#121](https://github.com/NASA-PDS/registry-api/issues/121) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]

**Other closed issues:**

- Remove provenance --reset option [\#310](https://github.com/NASA-PDS/registry-api/issues/310)
- Establish consistency between how Provenance and the API select \(filter\) documents by archive\_status [\#308](https://github.com/NASA-PDS/registry-api/issues/308)
- If \>1 products with the same LIDVID are registered by different nodes, we should respond with one of them [\#306](https://github.com/NASA-PDS/registry-api/issues/306)
- Update Java params and Docker image to expand JVM memory beyond 50% [\#300](https://github.com/NASA-PDS/registry-api/issues/300)
- Search criteria not producing expected matches [\#287](https://github.com/NASA-PDS/registry-api/issues/287)
- Format the source code in google style [\#272](https://github.com/NASA-PDS/registry-api/issues/272)
- Add --reset flag to scheduled provenance invocation [\#265](https://github.com/NASA-PDS/registry-api/issues/265)
- Incorporate misc changes loosely-related to \#252 [\#255](https://github.com/NASA-PDS/registry-api/issues/255)
- organize the swagger-ui section in a better way [\#245](https://github.com/NASA-PDS/registry-api/issues/245)
- Move scheduled execution of provenance.py to an AWS-based solution [\#232](https://github.com/NASA-PDS/registry-api/issues/232)
- Investigate if all fields are searchable [\#281](https://github.com/NASA-PDS/registry-api/issues/281)
- Establish parity between application.properties and application.properties.aws files [\#220](https://github.com/NASA-PDS/registry-api/issues/220)
- migrate  to jdk 17 [\#218](https://github.com/NASA-PDS/registry-api/issues/218)
- Make sure the documentation is clear on limit=0 instead of summary-only [\#198](https://github.com/NASA-PDS/registry-api/issues/198)

## [v1.1.12](https://github.com/NASA-PDS/registry-api/tree/v1.1.12) (2022-12-22)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.1.11...v1.1.12)

**Requirements:**

- As a user, I want the API to support redundant `/` in the url [\#212](https://github.com/NASA-PDS/registry-api/issues/212)
- As a registry-tool/registry-user I want to ensure leading multiple forward-slashes in request paths are stripped out [\#208](https://github.com/NASA-PDS/registry-api/issues/208)

**Defects:**

- observational end-point returns collections [\#202](https://github.com/NASA-PDS/registry-api/issues/202) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]

**Other closed issues:**

- Error when label is missing `pds:Time_Coordinates/pds:start_date_time` attribute [\#213](https://github.com/NASA-PDS/registry-api/issues/213) [[s.critical](https://github.com/NASA-PDS/registry-api/labels/s.critical)]

## [v1.1.11](https://github.com/NASA-PDS/registry-api/tree/v1.1.11) (2022-12-14)

[Full Changelog](https://github.com/NASA-PDS/registry-api/compare/v1.1.10...v1.1.11)

**Requirements:**

- As a user, I want an end-point of each of the PDS4 IM classes of products [\#461](https://github.com/NASA-PDS/registry-api/issues/461)

**Defects:**

- /all suffix returns a message which I don't understand [\#190](https://github.com/NASA-PDS/registry-api/issues/190) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
- Stable Roundup can no longer trigger Imaging workflow [\#188](https://github.com/NASA-PDS/registry-api/issues/188) [[s.medium](https://github.com/NASA-PDS/registry-api/labels/s.medium)]

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
- The /products, /bundles & /collections endpoints are missing from the API [\#178](https://github.com/NASA-PDS/registry-api/issues/178) [[s.high](https://github.com/NASA-PDS/registry-api/labels/s.high)]
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

- As a user, I want to receive error messages when an invalid request is submitted to the API [\#443](https://github.com/NASA-PDS/registry-api/issues/443)
- As a user, I want the API response media types to be compliant with RFC 6838 [\#464](https://github.com/NASA-PDS/registry-api/issues/464)
- As a user, I want to see the version of the API specification in the URL of the service [\#59](https://github.com/NASA-PDS/registry-api/issues/59)
- As a user, I want to get a key-value-pair JSON response [\#444](https://github.com/NASA-PDS/registry-api/issues/444)
- As an API user, I want a CSV response format option [\#445](https://github.com/NASA-PDS/registry-api/issues/445)
- As a user, I want to clearly see which formats are accepted by the API when a 406 error is raised [\#446](https://github.com/NASA-PDS/registry-api/issues/446)
- As an API user, I want to explicitly request the latest version of a product [\#448](https://github.com/NASA-PDS/registry-api/issues/448)
- As a API manager, I want to restrict access to registered products that should not be publicly accessible [\#466](https://github.com/NASA-PDS/registry-api/issues/466)
- As an API user, I want to know how long a request took to complete [\#463](https://github.com/NASA-PDS/registry-api/issues/463)
- As an API user, I want to get the latest version of a product, by default [\#449](https://github.com/NASA-PDS/registry-api/issues/449)
- As a user, I want to query for all versions of a product [\#438](https://github.com/NASA-PDS/registry-api/issues/438)
- As a user, I want to have a complete default fields \(for now at least\) [\#155](https://github.com/NASA-PDS/registry-api/issues/155)
- As a user, I want the end-point /api to redirect to the API documentation [\#63](https://github.com/NASA-PDS/registry-api/issues/63)
- As a user, I want the /products end point to work for any class of products [\#64](https://github.com/NASA-PDS/registry-api/issues/64)
- As a user,  I want specific end points for products which are not collections or bundles [\#65](https://github.com/NASA-PDS/registry-api/issues/65)
- As a user, I want to know why my query syntax is invalid [\#66](https://github.com/NASA-PDS/registry-api/issues/66)
- As an operator, I want to have a wrapper script for starting up the API service [\#67](https://github.com/NASA-PDS/registry-api/issues/67)
- As an API user, I want to search using URL parameters [\#462](https://github.com/NASA-PDS/registry-api/issues/462)
- As a developer, I never want the label blob to be returned [\#467](https://github.com/NASA-PDS/registry-api/issues/467)
- As an API user, I want to handle long-running queries that take \>10 seconds. [\#68](https://github.com/NASA-PDS/registry-api/issues/68)
- As an API user, I want an average query response time of 1 second for q=\* queries [\#69](https://github.com/NASA-PDS/registry-api/issues/69)
- As an API user, I want to search by a temporal range as an ISO-8601 time interval. [\#465](https://github.com/NASA-PDS/registry-api/issues/465)
- As an API user, I want to get an XML response [\#456](https://github.com/NASA-PDS/registry-api/issues/456)
- As an API user, I want to get only the fields I explicitly requested \(`fields=`\) [\#459](https://github.com/NASA-PDS/registry-api/issues/459)
- As a user, when I request specific fields I want to get records which have at least one of these fields [\#455](https://github.com/NASA-PDS/registry-api/issues/455)
- As an API user, I want to know the Bundle for a given Collection. [\#452](https://github.com/NASA-PDS/registry-api/issues/452)
- As an API user, I want to know the Collection\(s\) for a given Product. [\#451](https://github.com/NASA-PDS/registry-api/issues/451)
- As an API user, I want to know the Bundle for a given Product. [\#454](https://github.com/NASA-PDS/registry-api/issues/454)
- As an API user, I want to know the Product\(s\) that belong to a given Bundle. [\#453](https://github.com/NASA-PDS/registry-api/issues/453)
- As an API user, I want to know the children and ancestors of bundle, collections, and products [\#458](https://github.com/NASA-PDS/registry-api/issues/458)
- As an API user, I want to perform a search using wildcards [\#457](https://github.com/NASA-PDS/registry-api/issues/457)

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

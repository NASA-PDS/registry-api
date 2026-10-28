# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Implementation of the PDS Registry search API (spec: https://nasa-pds.github.io/pds-api/specifications.html). It is a Spring Boot service that translates REST requests into OpenSearch queries against the PDS registry indices. Data must be harvested, set to `archived` with registry-mgr, and processed by registry-sweepers before the API returns it correctly.

## Build and run

Java 25 and Maven are required (README, Dockerfile). Maven modules build in order `model` → `lexer` → `service`; `service` depends on the other two, so build from the root at least once.

```
mvn clean install                       # build everything, runs unit tests
mvn -pl service -am test                # unit tests only (builds model + lexer first)
mvn -pl service -am test -Dtest=RegistrySearchRequestBuilderTest#someMethod -Dsurefire.failIfNoSpecifiedTests=false   # single test
cd service && mvn spring-boot:run       # run locally (default port 8080, Swagger UI at /)
cd service && mvn -Dspring-boot.run.profiles=dev spring-boot:run   # with application-dev.properties
mvn spring-boot:build-image             # local docker image
```

Running locally needs an OpenSearch instance with data, normally the docker-compose from the separate `registry` repo (kill its `docker-registry-api-1` container first). For a local OpenSearch with that setup set `openSearch.sslCertificateCNVerification=false` and keep `openSearch.disciplineNodes=geo` in `service/src/main/resources/application.properties`. Do not commit those changes.

There is no linter configured. The only pre-commit hook is `detect-secrets` (baseline in `.secrets.baseline`).

## Architecture

- `model/`: no hand-written code. `swagger.yml` (OpenAPI spec, mirrored from the `pds-api` repo) is fed to openapi-generator (spring generator) to produce the controller interfaces and response objects under `gov.nasa.pds.model`. Change the API contract by editing `swagger.yml`, never the generated sources in `target/`.
- `lexer/`: ANTLR4 grammar `src/main/antlr4/.../lexer/Search.g4` for the `q` query parameter. Generated `SearchLexer`/`SearchParser` are consumed by the service.
- `service/` (package `gov.nasa.pds.api.registry`): the Spring Boot app (`SpringBootMain`).

Request flow in `service/`:

1. `controllers/` implement the generated interfaces and turn user input into the internal types (`ProductsController`, `SecurityValidationFilter`, central exception handler).
2. The `q` string is parsed by the ANTLR lexer and walked by `model/Antlr4SearchListener` to build OpenSearch query clauses.
3. `search/RegistrySearchRequestBuilder` is the single place that builds an OpenSearch `SearchRequest`. It adds the mandatory baseline filter (archive status from `filter.archiveStatus`) and handles index selection, sorting, `search-after`, and the requested/excluded `_source` fields. Do not create `SearchRequest` objects elsewhere, otherwise those global constraints get skipped (see CONTRIBUTING.md).
4. `search/OpenSearchRegistryConnectionImplBuilder` / `OpenSearchConfig` hold the OpenSearch connection, built from the `openSearch.*` properties. Indices are `registry` and `registry-refs`, optionally prefixed per discipline node (`openSearch.disciplineNodes`, comma separated, with cross-cluster search via `CCSEnabled`).
5. `model/` (hodgepodge of identifiers, transformers, product factories) maps hits to PDS4 products. `view/` serializes the result according to the `Accept` header (JSON, XML, CSV, HTML, PDS4 JSON/XML).

The `*Context` interfaces (`ConnectionContext`, `RequestBuildContext`, `RequestConstructionContext`, `UserContext`, `LidvidsContext`) carry per-request state; the rest of the code is stateless. Package responsibilities are described in CONTRIBUTING.md (MVC-like split between `controllers`, `model`, `search`, `view`).

Config: `application.properties` is the default; `.aws`, `.docker`, `.local`, `.all` variants and `application-dev/prod.properties` exist for other environments. Docker image settings are passed at runtime through `SERVER_PORT` and `SPRING_BOOT_APP_ARGS` (Spring args override the properties).

## Testing and contribution rules

- Unit tests are JUnit in `service/src/test/java`. Real behavior is validated by Postman integration tests that live in the separate `registry` repo (https://nasa-pds.github.io/registry/developer/integration-testing.html). Each feature, bug fix, or behavior change requires a new Postman test submitted there as a PR.
- Branch naming: `issue_<n>` for this repo's tickets, `<repo-name>_<n>` for tickets from other repos. Main development branch is `develop`.
- Avoid `null` in new code (CONTRIBUTING.md).

## Docker / AWS

- `docker/Dockerfile` builds the image from a prebuilt `registry-api-service.jar` (`--build-arg api_jar=...`, run from `service/`). CI publishes images to Docker Hub from `stable-cicd.yaml` and `unstable-cicd.yaml`.
- `terraform/` deploys the service on AWS ECS (needs an existing VPC, subnets, load balancer, security group, ECR image and OpenSearch). Copy `terraform.tfvars.example` to `terraform.tfvars` first; `terraform.tfvars` and `backend-config.tfvars` are environment specific.
- AWS Security Hub findings for the image on ECR are exported and analyzed with the `aws-security-hub-findings` skill (see the section below).

## AWS Security Hub vulnerabilities (docker image on ECR)

When asked to work on the vulnerabilities AWS Security Hub / Inspector reports for the docker image published in ECR (a `findings.json` export, a CVE list, "vulnerabilities in the image"), use the `aws-security-hub-findings` skill at `.claude/skills/aws-security-hub-findings/SKILL.md`. It describes how to export the findings, triage them against how the service is deployed, and propose fixes.

Status: the skill describes how to export the findings report (`aws securityhub get-findings` with a `security_hub_filter.json` filter, run from `docker/`), summarize it, triage it against the deployment and write a prioritized report of actions. Applying the fixes will be added later. Discuss the report with the user before changing the Dockerfile, `pom.xml` or `terraform/`.

---
name: aws-security-hub-findings
description: Work on the vulnerabilities AWS Security Hub / Inspector report for the registry-api docker image published in ECR. Use when the user mentions Security Hub, Inspector, CVEs or vulnerabilities in the docker image, or a findings.json export. Starts by exporting the findings report.
---

# AWS Security Hub findings for the registry-api docker image

Work in progress: steps 1 to 4 (export, summary, triage, prioritized report) are written. Applying the fixes will be added later. Present the report and wait for the user's approval before editing the Dockerfile, `pom.xml` or `terraform/`.

## Step 1: Export the findings report

Run from the `docker/` folder, with the credentials of the AWS account that hosts the ECR image applied (the `aws` CLI must be able to call Security Hub in `us-west-2`).

1. Create `docker/security_hub_filter.json` with the resource id of the image to scan:

   ```json
   {
     "ResourceId": [
       {
         "Value": "<URI>/<digest>",
         "Comparison": "EQUALS"
       }
     ]
   }
   ```

   `<URI>/<digest>` is the ECR resource id as Security Hub shows it, for example `arn:aws:ecr:us-west-2:<account-id>:repository/pds-registry-api-service/sha256:<digest>`. The digest identifies one pushed image, not a tag, so update it each time a new image is scanned.

2. Export the findings:

   ```
   cd docker
   aws securityhub get-findings \
     --region us-west-2 \
     --filters file://$PWD/security_hub_filter.json \
     --output json \
     > findings.json
   ```

   The result is `docker/findings.json`. It is large (about 140 lines per finding, so a few hundred findings is tens of thousands of lines): do not read it in full, summarize it with a script (step 2).

## Step 2: Summarize the findings by package

From `docker/`, count the findings by severity and group them by package. Each line of the output is one package and version, with the number of CVEs, their severities, how many have a public exploit, the CVSS attack vector (`AV:N` network, `AV:L` local, `AV:A` adjacent) and the fixed version.

```
python3 -I -c "
import json,collections
d=json.load(open('findings.json'))['Findings']
print(len(d),'findings;',dict(collections.Counter(f['Severity']['Label'] for f in d)))
g=collections.defaultdict(lambda:{'n':0,'sev':collections.Counter(),'exp':0,'av':collections.Counter(),'fix':set()})
for f in d:
    v=f['Vulnerabilities'][0]; p=v['VulnerablePackages'][0]
    k=(sorted({q['Name'] for q in v['VulnerablePackages']})[0],p['PackageManager'],p['Version'])
    e=g[k]; e['n']+=1; e['sev'][f['Severity']['Label'][:4]]+=1; e['exp']+=v.get('ExploitAvailable')=='YES'
    cv=[c for c in v.get('Cvss',[]) if c['Version']=='3.1']; e['av'][cv[0]['BaseVector'].split('/')[1] if cv else 'AV:?']+=1
    e['fix'].add(str(p.get('FixedInVersion')))
for k,e in sorted(g.items(),key=lambda kv:-kv[1]['n']):
    print(k,'n=',e['n'],dict(e['sev']),'exploit=',e['exp'],dict(e['av']),'fix=',sorted(e['fix'])[:2])
"
```

Fields of interest in each finding: `Severity.Label`, `Vulnerabilities[0].Id` (the CVE), `Vulnerabilities[0].VulnerablePackages[]` (`Name`, `Version`, `PackageManager` OS/JAVA/GENERIC, `FilePath`, `FixedInVersion`), `Vulnerabilities[0].Cvss[]`, `EpssScore`, `ExploitAvailable`, `FixAvailable` and `Description`. Use `Resources[0].Details.AwsEcrContainerImage` (`ImageTags`, `ImagePublishedAt`) to know which image was scanned. Read single findings from the file only to check a specific CVE.

## Step 3: Triage against how the service is deployed

Do not triage CVE by CVE. Classify each package group, then judge reachability. Deployment to keep in mind: the container runs on ECS/Fargate, only port 80 is open, to the Spring Boot application through an ALB. The ALB is reachable from the internal network and VPN, and from the public internet through a CloudFront distribution (HTTPS on 443). Confirm these facts with the user if they may have changed.

1. **Group by origin**:
   - base image, JDK (`oracle/jdk`, a Temurin runtime reported with the file `/opt/java/openjdk/release`);
   - base image, standalone Tomcat (`FilePath` under `/usr/local/tomcat`);
   - Ubuntu OS packages (`PackageManager` OS);
   - application dependencies (`PackageManager` JAVA, `FilePath` under `registry-api-service.jar/BOOT-INF/lib`).
2. **Check what really runs.** The Dockerfile `CMD` runs `java -jar registry-api-service.jar`, so the Spring Boot embedded Tomcat is used and the standalone Tomcat in the base image is not started. Re-read the Dockerfile to confirm this still holds. Findings on files that never run are removed by changing the base image, not by patching.
3. **Check the attack vector.** `AV:L` and `AV:A` findings need access to the container. They only matter after an attacker already has code execution, so they are low priority for exposure and are normally cleared by a base image rebuild.
4. **Check whether the vulnerable feature is used.** Examples seen so far, to verify in the repo each time:
   - Tomcat: HTTP/2 (`server.http2.enabled`), AJP, DIGEST authentication, the OpenSSL/FIPS listener, `web.xml` security constraints, `LoadBalancerDrainingValve`. These are off by default in Spring Boot, and TLS ends at the ALB/CloudFront.
   - Netty: find out which dependency pulls it in (`mvn dependency:tree -Dincludes=io.netty`). As a client library for AWS SDK calls it is not reachable from the internet; the CVEs are mostly server-side.
   - Jackson: needs attacker-controlled JSON bodies, `DataInput` parsing or polymorphic typing. Most endpoints are GET.
   - Thymeleaf: template injection needs unsanitized user input in the templates. Check whether Thymeleaf is used at all.
   - Spring: check whether the application serves static resources or evaluates SpEL on user input.
   - OS libraries (openssl, expat, perl, curl, glibc and others): Java uses its own TLS and XML code, so these matter only for tools run inside the container.
5. **Check fix availability.** `FixedInVersion` often names the nearest release, which can be on a newer major line (for example Spring 7.0.x or Spring Boot 4.0.x). Look for a backport on the version line in use before assuming a major upgrade. JDK findings can show `NotAvailable` until a new Temurin build is published, so a rebuild may clear them later.
6. **Treat Inspector severity with care.** Inspector often uses the NVD score, while the vendor (Ubuntu) may rate the same CVE lower. `ExploitAvailable` means a public exploit exists, not that it is reachable here.

## Step 4: Write the prioritized report

Give the user a report with one row per package group (not per CVE) covering all findings, so that the counts add up to the total. For each group give the number of findings, the single action that clears it, whether it is reachable in this deployment and why, and a priority.

Priorities:
- **P1**: reachable through the exposed HTTP surface, or a very cheap action that clears many findings (for example dropping an unused base image component).
- **P2**: probably not reachable, or limited impact, but cleared by an action that is needed anyway.
- **P3**: local-only, cleared by a rebuild, or noise.

Usual action types:
- rebuild the image on a current base image with `--pull` and `apt-get upgrade`, or choose a smaller base image to reduce OS findings structurally;
- stop shipping unused components (such as the standalone Tomcat);
- bump Spring Boot to the latest 3.5.x patch. The project imports the Spring Boot BOM, so overriding versions needs explicit `dependencyManagement` entries (ahead of the BOM import) instead of a property;
- upgrade or remove individual libraries (for example Thymeleaf);
- suppress accepted risks in Security Hub with a written reason instead of leaving them `NEW`.

Also include:
- the assumptions the judgments rely on (which feature flags and configuration were or were not verified);
- the image tag and publication date that were scanned compared with the current version in `pom.xml`, because an old image may report findings that are already fixed (re-scan a fresh build before changing versions);
- hardening that limits the damage of any remote code execution, independent of the CVEs: run as a non-root user on a port above 1024 (and update the target group), least-privilege task role and egress rules, and an ALB that only accepts traffic from CloudFront.

Caveats from earlier analyses:
- `curl` is installed by the Dockerfile but is probably also present in the Temurin base image. Check with `docker run --rm <image> dpkg -l curl` before promising that removing it clears the curl findings.
- `pom.xml` pins some versions (for example `spring-framework.version`, `jackson-version`) for reasons given in comments. Test after changing them.

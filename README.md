## Running the application locally with Docker images

The application consists of two Docker images:

* **Backend**: Spring Boot API, runs on port `8080`
* **Frontend**: Quasar/nginx frontend, runs on port `8081`

The backend can run either with the default in-memory H2 database or with PostgreSQL.

---

## Prerequisites

You need:

* Docker installed
* An AVWX API token
* Access to the container images, for example from GHCR

If the images are private, log in to GHCR first:

```bash
echo "YOUR_GITHUB_PAT" | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin
```

The GitHub token needs permission to read packages.

Set the image owner and tags:

```bash
export OWNER="your-github-username-or-org"
export BACKEND_TAG="latest"
export FRONTEND_TAG="latest"
export AVWX_API_KEY="Token your_avwx_token_here"
```

---

## Option 1: Run backend with H2

This is the quickest way to test the backend. Data is stored in memory and disappears when the container stops.

```bash
docker run --rm \
  --name weather-backend \
  -p 8080:8080 \
  -e AVWX_API_KEY="$AVWX_API_KEY" \
  ghcr.io/$OWNER/backend:$BACKEND_TAG
```

Test the backend:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/user/
curl http://localhost:8080/api/metar/LOWW
```

Expected result:

* `/actuator/health` should return status `UP`
* `/api/user/` should return the seeded users
* `/api/metar/LOWW` should return METAR data for Vienna airport

---

## Option 2: Run backend with PostgreSQL

This is closer to the production/Cloud SQL setup.

Start PostgreSQL:

```bash
docker run -d --rm \
  --name weather-postgres \
  -p 5432:5432 \
  -e POSTGRES_DB=weatherapp \
  -e POSTGRES_USER=weatherapp \
  -e POSTGRES_PASSWORD=weatherpass \
  postgres:16
```

Start the backend with the PostgreSQL profile:

```bash
docker run --rm \
  --name weather-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=postgres \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=weatherapp \
  -e DB_USERNAME=weatherapp \
  -e DB_PASSWORD=weatherpass \
  -e AVWX_API_KEY="$AVWX_API_KEY" \
  ghcr.io/$OWNER/backend:$BACKEND_TAG
```

Test the backend:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/user/
curl http://localhost:8080/api/metar/LOWW
```

In the health response, the database component should show PostgreSQL:

```json
{
  "components": {
    "db": {
      "details": {
        "database": "PostgreSQL"
      },
      "status": "UP"
    }
  },
  "status": "UP"
}
```

---

## Run the frontend

Start the frontend:

```bash
docker run --rm \
  --name weather-frontend \
  -p 8081:80 \
  ghcr.io/$OWNER/frontend:$FRONTEND_TAG
```

Open the app in the browser:

```text
http://localhost:8081
```

Make sure to use `http://`, not `https://`.

---

## Optional: Override frontend runtime config

If the frontend image needs to be told where the backend is running, create a local config file:

```bash
cat > config.local.js <<'EOF'
window.APP_CONFIG = {
  enabled: true,
  VITE_BACKEND_API_URL: 'http://localhost:8080/api'
};
EOF
```

Then run the frontend with the config file mounted into nginx:

```bash
docker run --rm \
  --name weather-frontend \
  -p 8081:80 \
  -v "$PWD/config.local.js:/usr/share/nginx/html/config.js:ro" \
  ghcr.io/$OWNER/frontend:$FRONTEND_TAG
```

For local Docker testing, use:

```js
VITE_BACKEND_API_URL: 'http://localhost:8080/api'
```

For Kubernetes, the frontend ConfigMap should use:

```js
VITE_BACKEND_API_URL: '/api'
```

---

## Useful backend endpoints

```text
GET http://localhost:8080/actuator/health
GET http://localhost:8080/api/user/
GET http://localhost:8080/api/metar/LOWW
GET http://localhost:8080/api/81150016-8501-4b97-9168-01113e21d8a5/favorite/
```

Example ICAO airport codes:

```text
LOWW = Vienna International Airport
LOWL = Linz Airport
LOWS = Salzburg Airport
LOWG = Graz Airport
EDDF = Frankfurt Airport
EGLL = London Heathrow
KJFK = New York JFK
```

---

## Notes

* H2 is useful for quick local testing.
* PostgreSQL is used for production-like testing and Cloud SQL compatibility.
* The AVWX API key must be passed to the backend as an environment variable.
* The frontend should not contain the AVWX API key.
* In Kubernetes, secrets will be provided through External Secrets and Kubernetes Secrets.
* In Kubernetes, the frontend should call the backend through `/api`, not `localhost`.


---

# Hochschule Burgenland - BSWE - WS2024 - 2nd Attempt - Weather App - Backend - Reference

[![](https://img.shields.io/github/license/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend?style=for-the-badge)](LICENSE.md)
[![](https://img.shields.io/github/actions/workflow/status/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend/verify.yml?style=for-the-badge)](https://github.com/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend/actions/workflows/verify.yml)
[![](https://api.scorecard.dev/projects/github.com/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend/badge?style=for-the-badge)](https://scorecard.dev/viewer/?uri=github.com/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend)
[![](https://img.shields.io/github/release-date/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend?style=for-the-badge)](https://github.com/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend/releases)

This is a reference implementation of the weather application's backend for the 2nd attempt of the "Software Management II" course at the Hochschule Burgenland in WS2024.
It solely acts as a reference, not as a complete implementation, and it is not expected by students to produce a similar implementation.

---

## API Specification

The OpenAPI specification can be found in [docs/openapi.yaml](./docs/openapi.yaml).

## Authentication / User Selection

The service does not provide authentication and users are selected by specifying their identifier (UUID) in the request URL.

---

## Configuration

See [src/main/resources/application.yaml](./src/main/resources/application.yaml) for all available and default configuration options.
To run the service successfully, you need to provide the following environment variables:

- `AVWX_API_KEY`: The API key for the [Aviation Weather Rest API](https://avwx.rest/) in the format `Token avwx-api-key`. The value will be used in the `Authorization` header when calling the API.

---

## Development

The service is implemented in Java using the Spring Boot framework and Gradle as the build tool.

### Code Quality

The code quality is ensured by the following tools:

- [Checkstyle](https://checkstyle.org/) for code style checking
- [PMD](https://pmd.github.io/) for static code analysis
- [JaCoCo](https://www.eclemma.org/jacoco/) for code coverage
- [PiTest](https://pitest.org/) for mutation testing
- [YAMLLint](https://yamllint.readthedocs.io/) for YAML linting
- [Spectral](https://stoplight.io/open-source/spectral/) for OpenAPI linting
- [Helm Lint](https://helm.sh/docs/helm/helm_lint/) for Helm chart linting
- [Conform](https://github.com/siderolabs/conform) for commit message linting
- [CycloneDX](https://cyclonedx.org/) for software bill of materials generation
- [Grype](https://github.com/anchore/grype) for software bill of materials scanning
- [Renovate](https://www.whitesourcesoftware.com/free-developer-tools/renovate/) for dependency updates

### Testing

To run the tests, run the following command:

```shell
# unit tests and jacoco reporting
./gradlew test jacocoTestReport jacocoTestCoverageVerification

# pitest
./gradlew pitest
```

### Linting

To run the linting checks, run the following command:

```shell
# checkstyle
./gradlew checkstyleMain checkstyleTest

# pmd
./gradlew pmdMain pmdTest

# yamllint
yamllint .

# spectral
spectral lint 'docs/**/*.yaml' --fail-severity info
spectral lint 'docs/**/*.yml' --fail-severity info

# helm lint
helm lint charts/*
```

### Software Bill of Materials

To generate the software bill of materials, run the following command:

```shell
./gradlew cycloneDxBom
grype sbom:build/reports/sbom.json
```

### Running

To run the service, run the following command:

```shell
./gradlew bootRun
```

### Building

To build the service, run the following command:

```shell
./gradlew bootJar
```

### Docker

To build the Docker image, run the following command:

```shell
./gradlew bootJar
docker build -t hochschule-burgenland-bswe-ws2024-2at-backend .
docker run -p 8080:8080 hochschule-burgenland-bswe-ws2024-2at-backend
```

### Commit Message

Commit messages must adhere to the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification.

To lint the commit messages, run the following command:

```shell
conform enforce
```

You can also use [Commitizen](https://commitizen.github.io/cz-cli/) to create commit messages:

```shell
cz commit
```

## GitHub Actions

The GitHub Actions workflows ensure that all code quality checks pass and that the code is deployable.

The release workflow creates a new release with publishing a container image to the [GitHub Container Registry](https://ghcr.io/).
It also published the SBOMs of the release assets, the OpenAPI specification, and their build provenance.

---

## Kubernetes Deployment

The backend service is provided as an OCI image in [ghcr.io/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend](https://ghcr.io/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend).

The following example shows an example deployment using the provided [Helm chart](./charts/weather-app-backend/).

```shell
# set the AVWX API key
export AVWX_API_KEY="<your-avwx-api-key>"

# create the helm values file
cat <<EOF > weather-app-backend-values.yaml
---
apiKeys:
  avwx: "Token ${AVWX_API_KEY}"
EOF

# install weather-app-backend with helm
helm install weather-app-backend ./charts/weather-app-backend -f weather-app-backend-values.yaml
```

### Private Container Registry

If you forked this repository and are using a private container registry, you need to provide the following Kubernetes secret:

```shell
kubectl create secret docker-registry ghcr-credentials \
  --docker-server=ghcr.io \
  --docker-username=<your-username> \
  --docker-password=<your-token>
```

Then, you can reference the secret in the Helm values file:

```yaml
imagePullSecrets:
  - name: ghcr-credentials
```

> Make sure to replace `<your-username>` and `<your-token>` with your GitHub username and a personal access token with the `read:packages` scope.

In [deploy/minikube.sh](./deploy/minikube.sh), you can find an example script to deploy (and access) the service to Minikube.

---

## Notes

- Unit test generation supported by GitHub Copilot (Anthropic Claude 3.5 Sonnet).
- Some workflow steps are allowed to fail due to the repository being private and not having access to certain security features.
- Features have been added, modified, or removed to showcase specific aspects of the implementation and software management.

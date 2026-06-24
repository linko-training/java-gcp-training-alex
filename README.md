# CloudRun API

Aplicación Spring Boot para subir archivos y desplegar en Google Cloud Run.

## Endpoints
- `POST /files/upload` → Subir archivo

## Deploy
1. Compilar con Maven
2. Construir imagen Docker
3. Subir a Artifact Registry
4. Desplegar en Cloud Run

# Read Me First
The following was discovered as part of building this project:

* No Docker Compose services found. As of now, the application won't start! Please add at least one service to the `compose.yaml` file.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.8-SNAPSHOT/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.8-SNAPSHOT/maven-plugin/build-image.html)
* [Google Cloud Storage](https://googlecloudplatform.github.io/spring-cloud-gcp/reference/html/index.html#cloud-storage)
* [Task](https://docs.spring.io/spring-cloud-task/reference/)
* [SpringDoc OpenAPI](https://springdoc.org/)
* [Docker Compose Support](https://docs.spring.io/spring-boot/4.0.8-SNAPSHOT/reference/features/dev-services.html#features.dev-services.docker-compose)

### Guides
The following guides illustrate how to use some features concretely:

* [Google Cloud Storage](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-storage-resource-sample)
* [SpringDoc OpenAPI](https://github.com/springdoc/springdoc-openapi-demos/)

### Docker Compose support
This project contains a Docker Compose file named `compose.yaml`.

However, no services were found. As of now, the application won't start!

Please make sure to add at least one service in the `compose.yaml` file.

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.


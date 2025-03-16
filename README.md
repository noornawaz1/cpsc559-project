# cpsc559-project

## Running the Server and Client

First ensure that Docker is installed on your OS. This can be done by downloading the official [Docker Desktop](https://www.docker.com/products/docker-desktop/) if you do not have it yet.

build the Docker image:

```bash
docker build -t spring-server:latest ./server
```

Build and run the replicated servers and client:

```bash
docker compose up --build
```

Visiting the [H2 database](http://localhost:8080/api/h2-console/) and [Swagger](http://localhost:8080/api/swagger-ui/index.html) show that the service is running.

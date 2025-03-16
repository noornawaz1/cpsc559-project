# cpsc559-project

## Running the Server and Client with Kubernetes (recommended)

First ensure that Docker is installed on your OS. This can be done by downloading the official [Docker Desktop](https://www.docker.com/products/docker-desktop/) if you do not have it yet.

In Docker Desktop, go to Settings -> Kubernetes and make sure `Enable Kubernetes` is enabled.

Open a terminal and start minikube

```bash
minikube start
```

After it is done initializing, ensure that Docker builds images directly into Minikube's environment

```bash
eval $(minikube docker-env)
```

Build the Docker images for both the server and client:

```bash
docker build --no-cache -t spring-server:latest ./server
docker build --no-cache -t react-client:latest ./client
```

Load the images into Minikube:

```bash
minikube image load spring-server:latest
minikube image load react-client:latest
```

Apply the custom Kubernetes Namespace, then the rest of the services and deployments. Note this will try to build Docker .yaml files too, but they will fail, this is OKAY.

```bash
kubectl apply -f namespace.yaml
kubectl apply -f . -n task-manager
```

Check to ensure all pods, services, and deployments are running:

```bash
kubectl get all -n task-manager
```

Just for now, we implement Port-Forwarding for the backend and frontend. Each command requires its own terminal, so either open another tab or window for your termial.

```bash
kubectl port-forward service/server-service 30303:30303 -n task-manager
kubectl port-forward service/client-service 30000:5173 -n task-manager
```

React client is now accessed at: http://localhost:30000

H2 Database is now accessed at: http://localhost:30303/api/h2-console/

Swagger is now accessed at: http://localhost:30303/api/swagger-ui/index.html

## Running the Server and Client with Docker (not being serviced)

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

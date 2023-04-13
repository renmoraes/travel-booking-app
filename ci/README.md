# Custom Jenkins Agent image with Docker

This README explains how to build and push a custom Jenkins agent Docker image, which includes Maven and Docker. You can use this custom agent in your Jenkins pipelines to build, test, and deploy your applications.

## Overview

This custom Jenkins agent is based on the maven:3.9.0-eclipse-temurin-11-focal image and includes Docker, allowing you to build and push Docker images within your Jenkins pipeline.

## Build the Custom Jenkins Agent

Replace your-registry and your-username with the appropriate values for your container registry.

```bash
docker build -t custom-jenkins-agent .
docker tag custom-jenkins-agent:latest your-registry/your-username/custom-jenkins-agent:latest
docker push your-registry/your-username/custom-jenkins-agent:latest
```

Now, you can use this custom Jenkins agent in your Jenkins pipelines by referring to the image path in the agent section of your Jenkinsfile:

```groovy
pipeline {
    agent {
        docker {
            image 'your-registry/your-username/custom-jenkins-agent:latest'
        }
    }
    stages {
        // Your build stages
    }
}
```
# Jenkins DevOps Project

This repository contains the Jenkins pipeline for automating the continuous integration (CI) and continuous deployment (CD) of a project using a variety of DevOps tools and practices. The pipeline automates tasks such as repository cloning, Docker container management, code compilation, unit testing, code quality analysis with SonarQube, and deployment via Docker Compose. It also sends email notifications on pipeline success or failure.

All the scripts and configurations in this repository are fully commented, with detailed explanations provided within the files. This ensures that each part of the pipeline is easy to understand and modify as needed for different use cases. Whether you're new to Jenkins or experienced with DevOps, you’ll find clear comments throughout to guide you through the setup and execution process.

## Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Pipeline Stages](#pipeline-stages)
    - [1. Clone Git Repository](#1-clone-git-repository)
    - [2. Clean Up Old Docker Containers](#2-clean-up-old-docker-containers)
    - [3. Verify and Start Essential Services](#3-verify-and-start-essential-services)
    - [4. Compile Codebase](#4-compile-codebase)
    - [5. Execute Unit Tests](#5-execute-unit-tests)
    - [6. Generate JaCoCo Code Coverage Report](#6-generate-jacoco-code-coverage-report)
    - [7. Perform SonarQube Code Analysis](#7-perform-sonarqube-code-analysis)
    - [8. Publish Artifacts to Nexus Repository](#8-publish-artifacts-to-nexus-repository)
    - [9. Build Docker Image for Application](#9-build-docker-image-for-application)
    - [10. Push Docker Image to Docker Hub](#10-push-docker-image-to-docker-hub)
    - [11. Deploy Application Using Docker Compose](#11-deploy-application-using-docker-compose)
    - [12. Send Completion Notification Email](#12-send-completion-notification-email)
- [How to Run the Pipeline](#how-to-run-the-pipeline)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Overview

The `JenkinsDevopsProject` repository is designed to streamline the deployment and integration of software projects. This repository provides a Jenkins pipeline that integrates with various tools such as Maven, Docker, SonarQube, Nexus, and Docker Compose. The pipeline automates the following processes:

- **Source Code Management (SCM)**: Clone code from GitHub repository
- **Build and Test**: Compile the codebase, run unit tests, and generate code coverage reports
- **Code Quality**: Perform SonarQube code analysis
- **Artifact Management**: Publish artifacts to Nexus repository
- **Containerization**: Build and push Docker images to Docker Hub
- **Deployment**: Deploy the application using Docker Compose
- **Notifications**: Email notifications for successful or failed builds

## Prerequisites

Before running this Jenkins pipeline, ensure the following prerequisites are met:

1. **Jenkins**: A running Jenkins server with the necessary plugins installed, including:
    - Git Plugin
    - Docker Pipeline Plugin
    - Email Extension Plugin
    - Maven Integration Plugin
    - SonarQube Scanner Plugin
    - Nexus Artifact Uploader Plugin
    - Docker CLI
2. **Docker**: Docker and Docker Compose installed on the server running the Jenkins agent.
3. **Nexus**: A running Nexus repository for storing artifacts.
4. **SonarQube**: A running SonarQube instance for code quality analysis.
5. **Docker Hub**: A Docker Hub account for pushing Docker images.
6. **Jenkins Credentials**: Ensure that all the necessary credentials are set up in Jenkins Credentials Manager. The following credentials should be configured:
    - **Git Credentials**: Set up your Git repository credentials (username and token or SSH key).
    - **SonarQube Token**: Set up a SonarQube token (`SONAR_TOKEN`) for authenticating with the SonarQube server.
    - **Nexus Credentials**: Set up your Nexus repository credentials (`NEXUS_USER` and `NEXUS_PASS`) for artifact deployment.
    - **Docker Hub Credentials**: Set up your Docker Hub credentials for pushing the Docker images.

You can add these credentials via **Jenkins > Manage Jenkins > Manage Credentials**, ensuring each credential is accessible to the Jenkins job executing this pipeline.

## Pipeline Stages

### 1. Clone Git Repository

This stage clones the project from a Git repository to the Jenkins workspace using the provided Git credentials. Replace the placeholder values with your specific repository details.

```groovy
git branch: 'your-branch', credentialsId: 'git-id', url: 'your-git-repository-url'
```

### 2. Clean Up Old Docker Containers

Removes any existing Docker containers from previous builds to ensure a fresh start. Replace the placeholder container names with your specific containers.

```groovy
sh 'docker rm -f nameofyourcontainer || true'
sh 'docker rm -f nameofyoursqlcontainer || true'
```

### 3. Verify and Start Essential Services

Starts the required services (SonarQube, Nexus, Prometheus, and Grafana) if not already running. The pipeline checks the health of each service using HTTP requests.
```groovy
def services = ['nexus', 'prometheus', 'grafana']
```

### 4. Compile Codebase

This stage compiles the source code using Maven.

```groovy
sh 'mvn clean compile'
```


### 5. Execute Unit Tests

Runs unit tests to ensure that the code behaves as expected.

```groovy
sh 'mvn test'
```

### 6. Generate JaCoCo Code Coverage Report

Generates a JaCoCo code coverage report to measure test coverage.

```groovy
sh 'mvn jacoco:report'
```

### 7. Perform SonarQube Code Analysis

Analyzes the code using SonarQube to detect issues and measure code quality.

```groovy
sh 'mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=$SONAR_TOKEN'
```

### 8. Publish Artifacts to Nexus Repository

Publishes the built artifacts to a Nexus repository using Maven.

```groovy
sh 'mvn deploy -Dnexus.login=$NEXUS_USER -Dnexus.password=$NEXUS_PASS'
```

### 9. Build Docker Image for Application

Builds the Docker image for the application.

```groovy
def imageName = "your-docker-image-name"
sh "docker build -t ${imageName} ."
```

### 10. Push Docker Image to Docker Hub

Pushes the Docker image to Docker Hub.

```groovy
sh "docker push ${imageName}"
```

### 11. Deploy Application Using Docker Compose

Deploys the application using Docker Compose.

```groovy
sh 'docker-compose up -d'
```


### 12. Send Completion Notification Email

Sends an email notification upon successful or failed completion of the pipeline.

```groovy
emailext(subject: "Pipeline Build #${BUILD_NUMBER} Status", body: "Build details here", mimeType: 'text/html', to: 'your-email@example.com')
```

## How to Run the Pipeline
1. **Clone the Repository**: Clone the repository to your Jenkins server or configure the repository URL in the Jenkins job.
2. **Configure Jenkins**: Ensure that Jenkins is set up with the required credentials and plugins.
3. **Run the Pipeline**: Trigger the pipeline manually or through a GitHub push trigger.


## Troubleshooting
- **Error Starting Docker Containers**: Ensure that Docker is installed and the services are correctly defined in the pipeline.
- **SonarQube Analysis Issues**: Verify that SonarQube is running and accessible from Jenkins.
- **Email Notifications Not Working**: Check the configuration of the Email Extension Plugin and the SMTP server settings.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

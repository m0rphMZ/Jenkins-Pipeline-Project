pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        PROJECT_NAME = 'JenkinsDevopsProject'
    }

    stages {
        stage('Clone Git Repository') {
            steps {
                git branch: '', //##Change to your desired GIT##
                    credentialsId: 'git-id',
                    url: '' //##Change to your desired GITHUB URL##
            }
        }

        stage('Clean Up Old Docker Containers') {
            steps {
                script {
                    echo 'Removing old Docker containers for the project...'
                    sh 'docker rm -f nameofyourcontainer || true' //##To change
                    sh 'docker rm -f nameofyoursqlcontainer || true' //##To change
                }
            }
        }

        stage('Verify and Start Essential Services') {
            steps {
                script {
                    echo 'Starting or verifying essential services (SonarQube, Nexus, Prometheus, Grafana)...'
                    def services = ['nexus', 'upbeat_wing', 'prometheus', 'grafana']
                    services.each { service ->
                        def containerId = sh(script: "docker ps -a -q -f name=${service}", returnStdout: true).trim()
                        if (containerId) {
                            sh "docker start ${containerId} || true"
                        } else {
                            error "Service ${service} is not found. Ensure it is started manually."
                        }
                    }

                    // Health checks
                    def maxRetries = 10
                    def retryInterval = 20
                    def servicesStatus = [
                        'SonarQube': 'http://localhost:9000/',
                        'Nexus': 'http://localhost:8081/service/rest/v1/status',
                        'Prometheus': 'http://localhost:9090/-/ready',
                        'Grafana': 'http://localhost:3000/api/health'
                    ]

                    servicesStatus.each { name, url ->
                        def statusCode = '000'
                        for (int i = 0; i < maxRetries; i++) {
                            try {
                                statusCode = sh(script: "curl -s -o /dev/null -w '%{http_code}' ${url}", returnStdout: true).trim()
                            } catch (Exception e) { echo "Error checking ${name} health: ${e.message}" }

                            if (statusCode == '200') {
                                echo "${name} is up and running."
                                break
                            }
                            sleep(time: retryInterval, unit: 'SECONDS')
                        }
                        if (statusCode != '200') {
                            error "${name} service did not start successfully within the timeout."
                        }
                    }
                }
            }
        }

        stage('Compile Codebase') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Execute Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Generate JaCoCo Code Coverage Report') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('Perform SonarQube Code Analysis') {
            steps {
                withCredentials([string(credentialsId: 'jenkins-sonar', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=$SONAR_TOKEN -Dsonar.jacoco.reportPaths=target/site/jacoco/jacoco.xml'
                }
            }
        }

        stage('Publish Artifacts to Nexus Repository') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nex-cred', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh 'mvn deploy -Dnexus.login=$NEXUS_USER -Dnexus.password=$NEXUS_PASS'
                }
            }
        }

        stage('Build Docker Image for Application') {
            steps {
                script {
                    def imageName = "" //##To change to your Docker image name
                    sh "docker build -t ${imageName} ."
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-cred', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
                    script {
                        def imageName = "" //##To change to your Docker image name
                        sh "echo $DOCKERHUB_PASS | docker login -u $DOCKERHUB_USER --password-stdin"
                        sh "docker push ${imageName}"
                    }
                }
            }
        }

        stage('Deploy Application Using Docker Compose') {
            steps {
                script {
                    echo 'Deploying application with Docker Compose...'
                    sh 'docker-compose up -d'
                    echo 'Deployment logs:'
                    sh 'docker-compose logs'
                }
            }
        }

        stage('Send Completion Notification Email') {
            steps {
                script {
                    def subject = "Pipeline - Build #${BUILD_NUMBER} Completed Successfully"
                    def body = readFile('emailTemplates/success_email_template.html')
                        .replaceAll('\\$\\{BUILD_NUMBER\\}', "${BUILD_NUMBER}")
                        .replaceAll('\\$\\{BUILD_URL\\}', "${BUILD_URL}")
                        .replaceAll('\\$\\{PROJECT_NAME\\}', "${PROJECT_NAME}")

                    emailext(
                        subject: subject,
                        body: body,
                        mimeType: 'text/html',
                        to: ''  //##To change to your email
                    )
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed.'
        }
        success {
            echo "Pipeline completed successfully."
        }
        failure {
            script {
                def subject = "Pipeline - Build #${BUILD_NUMBER} FAILED"
                def body = readFile('emailTemplates/failure_email_template.html')
                    .replaceAll('\\$\\{BUILD_NUMBER\\}', "${BUILD_NUMBER}")
                    .replaceAll('\\$\\{BUILD_URL\\}', "${BUILD_URL}")
                    .replaceAll('\\$\\{PROJECT_NAME\\}', "${PROJECT_NAME}")

                emailext(
                    subject: subject,
                    body: body,
                    mimeType: 'text/html',
                    to: ''  //##To change to your email
                )
            }
        }
    }
}

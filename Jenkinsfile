pipeline {
    agent { docker { 
        image 'remoraes/com.tus.custom-jenkins-agent' 
        args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
        } 
    }
    stages {
        stage('Build & Test') {
            steps {
                sh 'mvn clean compile checkstyle:checkstyle'
                realtimeJUnit('**/target/surefire-reports/TEST-*.xml') {
                    sh 'mvn package -pl carRental'
                }
            }
        }

        stage('Sonarqube Code Quality') {
            environment {
                SONAR_URL = "http://151.236.216.18:9000/"
            }
            steps {
                withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_AUTH_TOKEN')]) {
                sh 'mvn sonar:sonar -Dsonar.login=$SONAR_AUTH_TOKEN -Dsonar.host.url=${SONAR_URL} -pl carRental'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def branchName = env.BRANCH_NAME.replace('/', '_')
                    def commitHash = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    def dockerImageName = "remoraes/com.tus.microservices.car-rental:${branchName}_${env.BUILD_ID}_${commitHash.take(4)}"
                    docker.withRegistry('', 'jenkins_dockerhub') {
                        def customImage = docker.build( dockerImageName, "-f carRental/Dockerfile ./carRental")
                        customImage.push()
                    }
                }
            }
        }

    }
    post {
        always {
            // Publish checkstyle and warning NG reports
            recordIssues(tools: [ java(), checkStyle(pattern: '**/target/checkstyle-result.xml', reportEnconding: 'UTF-8')])
        }
        success {
            jacoco(
                execPattern: '**/build/jacoco/*.exec',
                classPattern: '**/build/classes/java/main',
                sourcePattern: '**/src/main'
            )
        }
    }
}
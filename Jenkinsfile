pipeline {
    agent none 
    stages {
        stage('Build & Test') {
            agent { docker { image 'maven:3.9.0-eclipse-temurin-11-focal'} }
            steps {
                sh 'mvn clean compile checkstyle:checkstyle'
                realtimeJUnit('**/target/surefire-reports/TEST-*.xml') {
                    sh 'mvn verify -pl carRental'
                }
            }
        }

        // stage('Code Quality') {
        //     agent { docker { image 'maven:3.9.0-eclipse-temurin-11-focal'} }
        //     steps {
        //         script {
        //             withSonarQubeEnv('sonarqube') {
        //                 sh 'mvn sonar:sonar -Dsonar.projectKey=myProjectKey -Dsonar.organization=myOrg -Dsonar.host.url=https://sonar.example.com'
        //             }
        //         }
        //     }
        // }


        stage('Create JAR') {
            agent { docker { image 'maven:3.9.0-eclipse-temurin-11-focal'} }
            steps {
                sh 'mvn install -DskipTests -pl carRental'
            }
        }

        stage('Build Docker Image') {
            agent any
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
            // Archive the Jacoco coverage reports
            jacoco(path: '**/**.exec')
            // Publish checkstyle and warning NG reports
            recordIssues(tools: [ java(), checkStyle(pattern: 'target/checkstyle-result.xml', reportEnconding: 'UTF-8')])
        }
    }
}
pipeline {
    agent none 
    stages {
        stage('Compile & Test') {
            agent { docker { image 'maven:3.9.0-eclipse-temurin-11-focal'} }
            steps {
                sh 'mvn clean compile -pl carRental'
                sh 'mvn test -pl carRental'
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

        stage('Publish Test Results') {
            agent any
            steps {
                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                rtTestPublisher (
                testResults: '**/target/surefire-reports/TEST-*.xml', 
                failBuildIfNoTests: true, 
                resolveInputVariables: true
                )
            }
        }

        stage('Build CI') {
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
}
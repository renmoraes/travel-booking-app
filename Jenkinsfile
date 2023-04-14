pipeline {
    agent none 
    stages {
        stage('Build') {
            agent { docker { image 'maven:3.9.0-eclipse-temurin-11-focal'} }
            steps {
                sh 'mvn clean compile checkstyle:checkstyle'
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
            agent { docker { image 'maven:3.9.0-eclipse-temurin-11-focal'} }
            steps {
                realtimeJUnit('**/target/surefire-reports/TEST-*.xml') {
                    sh 'mvn -Dmaven.test.failure.ignore=true clean verify -pl carRental'
                }

            }
        }

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
            jacoco(execPattern: '**/**.exec')
            // Publish checkstyle and warning NG reports
            recordIssues(tools: [checkStyle(pattern: 'target/checkstyle-result.xml'), warningsParser(parserConfigurations: [[parserName: 'Java Warnings', pattern: 'target/warnings.xml']])])
        }
    }
}
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

        stage('Build CI') {
            agent { docker { image 'maven:3.9.0-eclipse-temurin-11-focal'} }
            steps {
                sh 'mvn install -DskipTests -pl carRental'
            }
        }

        stage('Build Docker Image') {
            agent { docker { image 'docker:dind'} }
            steps {
                script {
                     def customImage = docker.build("my-image:${env.BUILD_ID}",
                                   "-f carRental/Dockerfile ./carRental")
                }
            }
        }
    }
}
pipeline {
    agent { docker { image 'maven:3.9.0-eclipse-temurin-11' } }
    stages {
        stage('Compile & Test') {
            steps {
                sh 'mvn clean compile -pl carRental'
                sh 'mvn test -pl carRental'
            }
        }

        stage('Build CI') {
            steps {
                sh 'mvn install -DskipTests -pl carRental'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                     def customImage = docker.build("my-image:${env.BUILD_ID}",
                                   "-f carRental/Dockerfile ./carRental")
                }
            }
        }
    }
}
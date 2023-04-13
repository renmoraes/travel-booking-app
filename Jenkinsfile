pipeline {
    agent { docker { image 'maven:3.9.0-eclipse-temurin-11' } }
    stages {
        stage('Compile & Test') {
            steps {
                sh 'mvn clean compile -pl carRental'
                sh 'mvn test -pl carRental'
            }
        }
    }
}
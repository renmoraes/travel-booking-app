pipeline {
    agent { docker { 
        image 'remoraes/com.tus.custom-jenkins-agent' 
        args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
        } 
    }
    stages {
        stage('Build and compile') {
            steps {
                sh 'mvn clean compile checkstyle:checkstyle'
            }
        }

        stage('Run test & Sonarqube Code Quality') {
            steps {
                realtimeJUnit(testResults: '**/target/failsafe-reports/TEST-*.xml', testData: '**/target/surefire-reports/TEST-*.xml' ) {
                     withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_AUTH_TOKEN')]) {
                        sh 'cd carRental && mvn verify'
                     }
                }
            }
        }

        stage('Create JAR file') {
            steps {
                sh 'cd carRental && mvn package -DskipTests'
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
                execPattern: '**/target/jacoco/*.exec',
                classPattern: '**/target/classes/java/main',
                sourcePattern: '**/src/main'
            )
        }
    }
}
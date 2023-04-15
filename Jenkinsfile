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
                realtimeJUnit('**/target/*/TEST-*.xml' ) {
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
        stage('Update k8s deployment manifest'){
            steps {
                script {
                    def branchName = env.BRANCH_NAME.replace('/', '_')
                    def commitHash = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    def imageTag = "${branchName}_${env.BUILD_ID}_${commitHash.take(4)}"
                    sh 'cd k8s && sed "s/{image_tag}/'${imageTag}'/g" deployment.tpl > deployment.yaml'

                           // Set up Git credentials
                    withCredentials([usernamePassword(credentialsId: 'jenkins-github-user', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                        sh '''
                            git config --global user.email "jenkins@example.com"
                            git config --global user.name "Jenkins automation"
                        '''

                        // Commit and push the new deployment.yaml file
                        sh """
                        git add k8s/deployment.yaml
                        git commit -m "Update deployment.yaml with new image tag: ${imageTag}"
                        git push ${env.GIT_URL} ${env.GIT_BRANCH}
                        """
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
                execPattern: '**/**/target/jacoco/*.exec',
                classPattern: '**/**/target/classes/java/main',
                sourcePattern: '**/**/src/main'
            )
        }
    }
}
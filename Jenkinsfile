pipeline {
    agent { docker { 
        image 'remoraes/com.tus.custom-jenkins-agent' 
        args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
        } 
    }
    stages {
        stage('Build and Compile') {
            steps {
                sh 'mvn clean compile checkstyle:checkstyle'
            }
        }

        stage('Run tests & Sonarqube Code Quality') {
            steps {
                realtimeJUnit('**/target/*/TEST-*.xml' ) {
                     withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_AUTH_TOKEN')]) {
                        sh 'cd car-rental && mvn verify'
                     }
                }
            }
        }

        stage('Create JAR file') {
            steps {
                sh 'cd car-rental && mvn package -DskipTests'
            }
        }

        stage('Build & Push docker image') {
            steps {
                script {
                    def branchName = env.BRANCH_NAME.replace('/', '_')
                    def commitHash = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    def dockerImageName = "remoraes/com.tus.microservices.car-rental:${branchName}_${env.BUILD_ID}_${commitHash.take(4)}"
                    docker.withRegistry('', 'jenkins_dockerhub') {
                        def customImage = docker.build( dockerImageName, "-f car-rental/Dockerfile ./car-rental")
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
                    sh 'cd k8s && sed "s/{image_tag}/' + imageTag + '/g" deployment.tpl > deployment.yaml'

                    // Set up Git credentials
                    sh '''
                        git config --global user.email "jenkins@example.com"
                        git config --global user.name "Jenkins automation"
                    '''
                    
                    sh '''
                        mkdir -p ~/.ssh
                        chmod 700 ~/.ssh
                    '''
                    
                    sh 'ssh-keyscan github.com >> ~/.ssh/known_hosts'
    
                    // Use ssh-agent with the configured SSH key
                    sshagent(credentials: ['jenkins-github-user']) {
                        // Commit and push the new deployment.yaml file
                        withEnv(["GIT_URL=${env.GIT_URL}", "GIT_BRANCH=${env.BRANCH_NAME}", "IMAGE_TAG=${imageTag}"]) {
                           
                            sh '''
                                git add k8s/deployment.yaml
                                git pull origin "$GIT_BRANCH"
                                git commit -m "Update deployment.yaml with new image tag: $IMAGE_TAG"
                                git push origin HEAD:"$GIT_BRANCH"
                            ''' 
                        }
                    }
                }
            }
        }

    }
    post {
        always {
            // Publish checkstyle and warning NG reports
            recordIssues(tools: [ java(), checkStyle(pattern: '**/target/checkstyle-result.xml', reportEnconding: 'UTF-8')])
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
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
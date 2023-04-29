pipeline {
    agent any
    stage
       stage('Clean') {
            steps {
               // Stop and remove old  docker container
                sh 'docker stop $(docker ps -a -q)'
                sh 'docker rm $(docker ps -a -q)'
            }
        }
 
    stages {
        stage('Checkout') {
            steps {  
                git branch: 'main', credentialsId: 'Sedkibani', url: 'git@github.com:https-github-com-Sedkibani/nxtya.git'
            }
        }
        
        stage('Build') {
            steps {
                sh  'docker build -t nxtya:1.0 -f docker/Dockerfile .'
            }
        }
        
        stage('Docker Login') {
            steps {
                 withDockerRegistry([credentialsId: 'my-docker-hub-credentials', url: 'https://index.docker.io/v1/'])
                     
                         {  sh 'docker pull nxtya:latest' }
                  }
                              }          

        
        stage('Push to Docker Hub') {
            steps {
                sh 'docker push banisedki/nxtya:latest'
            }
        }

        
        /*stage('Code Quality') {
            steps {
                sh 'docker run --rm nxtya:1.0 vendor/bin/phpstan analyze'
                // Additional commands for other code quality tools like SonarQube
            }
        }*/

        stage('Deploy') {
            steps {
                // Use Ansible playbook to deploy to DigitalOcean server
                ansiblePlaybook(
                    playbook: '/var/lib/jenkins/workspace/nxtya/ansible.yml',
                    inventory: '/var/lib/jenkins/workspace/nxtya/inventory.ini',
                    extras: "-e 'docker_image=nxtya:1.0'"
                )
            }
        }
    }

    // monitoring
    post {
        always {
            //  emails notification
            emailext (
                to: 'sedki99bani@gmail.com',
                subject: 'Pipeline Status - ${currentBuild.result}',
                body: """<p>Pipeline Status: ${currentBuild.result}</p>
                         <p>Build URL: ${env.BUILD_URL}</p>""",
                recipientProviders: [[$class: 'RequesterRecipientProvider']],
                replyTo: 'jenkins@domain.com',
                mimeType: 'text/html'
            )
        }
    }
}

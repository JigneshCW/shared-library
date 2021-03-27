import org.example.Notifications
def call() {
 pipeline {
    agent any

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        jdk "JDK"
        maven "MyMaven"
    }

    stages {
        stage('Build') {
            steps {
                // Get some code from a GitHub repository
                git 'https://github.com/jigneshmirani/Service-User.git'

                // Run Maven on a Unix agent.
                sh "mvn -Dmaven.test.failure.ignore=true clean package"

                // To run Maven on a Windows agent, use
                // bat "mvn -Dmaven.test.failure.ignore=true clean package"
            }

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts artifacts:'target/*.jar',fingerprint: true
                }
                always
                {
                    //echo mailBody()
                    mail bcc: '', body: '' + Notifications.mailBody() , cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "Build Status ${currentBuild.currentResult}: Project name -> ${env.JOB_NAME}", to: "jignesh.mirani@onlinepsbloans.com"
                }
            }
            
		
        }
        stage('Deployment On QA')
        {
            steps{
                sh "ssh ${env.B4L_QA_Server} 'fuser -k 9092/tcp'"
                sh "scp '/var/lib/jenkins/jobs/${env.JOB_NAME}/builds/${env.BUILD_NUMBER}/archive/target/user-service-0.0.1-SNAPSHOT.jar' ${env.B4L_QA_Server}:/apps/services/msme/service-TestService"
                sh "ssh ${env.B4L_QA_Server} 'java -jar /apps/services/msme/service-TestService/user-service-0.0.1-SNAPSHOT.jar' &"
            }
        }
        stage('Deployment On UAT')
        {
            
            input {
                message "Should we continue?"
                ok "Yes, we should."
                submitter "jignesh"
                parameters {
                    string(name: 'Say Anything', defaultValue: 'Anything', description: 'You can type anything to proceed')
                }
           
            }
            steps{
                sh "ssh jignesh.mirani@${env.B4L_UAT_Server} 'fuser -k 9092/tcp'"
                sh "scp '/var/lib/jenkins/jobs/${env.JOB_NAME}/builds/${env.BUILD_NUMBER}/archive/target/user-service-0.0.1-SNAPSHOT.jar' jignesh.mirani@${env.B4L_UAT_Server}:/apps/services/msme/service-TestService"
                sh "ssh jignesh.mirani@${env.B4L_UAT_Server} 'java -jar /apps/services/msme/service-TestService/user-service-0.0.1-SNAPSHOT.jar' &"
            }
        }
    }
}





}

import org.example.Notify

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
                sh "mvn clean package"

                // To run Maven on a Windows agent, use
                // bat "mvn -Dmaven.test.failure.ignore=true clean package"
            }

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
               
                always
                {
			//echo Notify.formatThis('test')
			echo "Job Name 1 : ${env.JOB_NAME}"
			
			//echo  Notify.mailBody("${env.JOB_NAME}")
			echo "${currentBuild.result}"
			echo Notify.mailBody("${currentBuild.result}")
                    //echo mailBody()
                   // mail bcc: '', body: '' + notify1.mailBody() , cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "Build Status ${currentBuild.currentResult}: Project name -> ${env.JOB_NAME}", to: "jignesh.mirani@onlinepsbloans.com"
                }
            }
            
		
        }
        
    }
}





}

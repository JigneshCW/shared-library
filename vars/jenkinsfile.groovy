def call() {
 pipeline {
    agent any

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        jdk "JDK"
        maven "Maven"
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
                    mail bcc: '', body: '' + mailBody() , cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "Build Status ${currentBuild.currentResult}: Project name -> ${env.JOB_NAME}", to: "jignesh.mirani@onlinepsbloans.com"
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

@NonCPS
def getChangeString() {
 MAX_MSG_LEN = 100
 def changeString = "<b>Changes :</b> <br/>"
 def haveChages = "No"

 
 def changeLogSets = currentBuild.changeSets
 for (int i = 0; i < changeLogSets.size(); i++) {
 def entries = changeLogSets[i].items
 for (int j = 0; j < entries.length; j++) {
 def entry = entries[j]
 truncated_msg = entry.msg.take(MAX_MSG_LEN)
 changeString += " - ${truncated_msg} [${entry.author}]\n"
 haveChages = "Yes"
 }
 }

 if (haveChages=="No") {
 changeString += " - No new changes"
 }
 return changeString
}

@NonCPS
def mailBody(){

    def mailBody = "<html><table border='1' width='100%' style='border: inset;'>"
    mailBody += "<tr><td colspan='2' style='background-color: cornflowerblue;color: white;'><b>Build Status :</b></td></tr>"
    mailBody += "<tr><td width='25%'><b>Project : </b></td><td width='75%'> ${env.JOB_NAME} </td></tr>"
    mailBody += "<tr><td width='25%'><b>Build Number : </b></td><td width='75%'> ${env.BUILD_NUMBER} </td></tr>"
    mailBody += "<tr><td width='25%'><b>URL build : </b></td><td width='75%'> ${env.BUILD_URL} </td></tr>"
    mailBody += "<tr><td colspan='2' style='background-color: cornflowerblue;color: white;'><b>Change Logs :</b></td></tr>"

    MAX_MSG_LEN = 100
    //def changeString = "<b>Changes :</b> <br/>"
    def haveChages = "No"

        def changeLogSets = currentBuild.changeSets
        for (int i = 0; i < changeLogSets.size(); i++) {
            def entries = changeLogSets[i].items
            for (int j = 0; j < entries.length; j++) {
                def entry = entries[j]
                mailBody += "<tr><td colspan='2'> ${entry.commitId} by <b>${entry.author}</b> on ${new Date(entry.timestamp)}</td></tr>"
                mailBody += "<tr><td><b>Commit Message : </b></td><td> ${entry.msg} </td></tr>"
                mailBody += "<tr><td><b> ACTION</b></td><td><b> File Name </b> </td></tr>"
                def files = new ArrayList(entry.affectedFiles)
                for (int k = 0; k < files.size(); k++) {
                    def file = files[k]
                    mailBody += "<tr><td><b>${file.editType.name} </td><td>${file.path} </td></tr>"
                }
                 haveChages = "Yes"
            }
        }

    if (haveChages=="No") {
        mailBody += "<tr><td colspan='2'> - No new changes </td></tr>"
    }
    mailBody += "</table></html>"
    return mailBody
}






}

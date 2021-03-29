def call(String recipients) {
 def status, logRegex

    switch (currentBuild.currentResult) {
        case 'SUCCESS':
            status = 'successed'
            logRegex = 'SUCCESS'
            break

        case 'UNSTABLE':
            status = 'unstable'
            logRegex = 'FAILURE'
            break

        case 'FAILURE':
            status = 'failed'
            logRegex = 'FAILURE'
            break

        case 'ABORTED':
            status = 'canceled'
            logRegex = 'ABORTED'
            break

    }
  def mailBody = "<html><table border='1' width='100%'>"
    mailBody += "<tr><td colspan='2'><b>Build Status :</b></td></tr>"
    mailBody += "<tr><td width='25%'><b>Project : </b></td><td width='75%'> ${env.JOB_NAME} </td></tr>"
    mailBody += "<tr><td width='25%'><b>Build Number : </b></td><td width='75%'> ${env.BUILD_NUMBER} </td></tr>"
    mailBody += "<tr><td width='25%'><b>URL build : </b></td><td width='75%'>Display Name : ${currentBuild.displayName} </td></tr>"
    mailBody += "<tr><td colspan='2'><b>Change Logs :</b></td></tr>"

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
    println mailBody
}

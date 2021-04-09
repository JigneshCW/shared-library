def call(msg) {
 
  def mailBody = "<html><table border='1' width='100%'>"
    mailBody += "<tr><td colspan='2'><b>Build Status :</b></td></tr>"
    mailBody += "<tr><td width='25%'><b>Project : </b></td><td width='75%'> ${env.JOB_NAME} </td></tr>"
    mailBody += "<tr><td width='25%'><b>Build Number : </b></td><td width='75%'> ${env.BUILD_NUMBER} </td></tr>"
    mailBody += "<tr><td width='25%'><b>URL build : </b></td><td width='75%'> ${env.BUILD_URL} </td></tr>"
    mailBody += "<tr><td colspan='25%'><b>Failure Reason :</b></td><td width='75%'> " + msg + "</td></tr>"
    mailBody += "</table></html>"
    return mailBody
}

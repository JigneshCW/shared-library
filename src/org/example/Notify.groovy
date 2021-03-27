package org.example

class Notify{
  def static String mailBody(){
        echo "${env.JOB_NAME}"
    
  }


}

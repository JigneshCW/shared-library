package org.example

class Notify{
  def static String mailBody(currntBuild){
        
    //println currntBuild
    // return CurrentBuild[0].toString()
   return "Size : " + currntBuild.size().toString()
  }


}

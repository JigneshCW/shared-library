def call(Map config=[:], Closure body) {
    pipeline {
        //checkout scm
        stages {
        stage('Install') {
            echo 'Tools has been installed'
        }
        stage('Test') {
            echo 'Testing Section '
        }
        stage('Deploy') {
           
              if (config.deploy) {
                echo 'Deployment on server'
            }
           
        }
        }
        body()
    }
}

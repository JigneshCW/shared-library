def call(Closure body) {
    pipeline {
        //checkout scm
        stage('Install') {
            echo 'Tools has been installed'
        }
        stage('Test') {
            echo 'Testing Section '
        }
        stage('Deploy') {
           
                echo 'Deployed on server'
           
        }
        
        body()
    }
}

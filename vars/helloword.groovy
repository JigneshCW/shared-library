def call() {
    pipeline {
        //checkout scm
        stage('Install') {
            echo 'Tools has been installed'
        }
        stage('Test') {
            echo 'Testing Section '
        }
        stage('Deploy') {
            if (deploy == true) {
                echo 'Deployed on server'
            }
        }
    }
}

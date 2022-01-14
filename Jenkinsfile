
pipeline {
    agent any
    
    tools {
        maven "maven"
    }
    
    stages {
        stage('git Pull') {
            steps {

                git branch: 'main', changelog: false, poll: false, credentialsId:'musicat-audio-github-access-token', url: 'https://github.com/jollypyun/musicat_audio.git'

            }
        }
        
        stage('Build') {
            steps {
                sh "mvn -Dmaven.test.failure.ignore=true -N -f pom.xml clean package"
            }
        }
        
        stage('Deploy') {
            steps {
                deploy adapters: [tomcat9(credentialsId: 'tomcat-db-username-password', path: '', url: 'http://13.124.245.202:80/')], contextPath: '/', onFailure: false, war: '**/*.war'
			}
        }
        
        stage('Restart') {
            steps {
                sh '''curl -u tomcat:1111 http://13.124.245.202:20000/host-manager/text/stop
curl -u tomcat:1111 http://13.124.245.202:20000/host-manager/text/start'''
            }
        }
    }
}

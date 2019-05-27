pipeline {
  agent any
  stages {
    stage('Setup') {
      steps {
        sh 'chmod +x gradlew
        sh './gradlew setupCIWorkspace'
      }
    }
    stage('Build') {
      steps {
        sh './gradlew build'
      }
    }
    stage('Deploy') {
      steps {
        archiveArtifacts 'build/libs/**.jar'
      }
    }
  }
}

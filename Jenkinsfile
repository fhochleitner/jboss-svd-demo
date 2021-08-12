#!groovy
properties([
        gitLabConnection('Demo-Gitlab-Connection'),
        pipelineTriggers([
                [
                        $class                        : 'GitLabPushTrigger',
                        branchFilterType              : 'All',
                        triggerOnPush                 : true,
                        triggerOnMergeRequest         : false,
                        triggerOpenMergeRequestOnPush : "never",
                        triggerOnNoteRequest          : true,
                        noteRegex                     : "rebuild",
                        skipWorkInProgressMergeRequest: true,
                        secretToken                   : "",
                        ciSkip                        : false,
                ]
        ])
])


pipeline {
    agent {
        node 'master'
    }

    tools {
        jdk 'jdk11'
        maven 'Maven 3.6.3'
    }

    options {
        disableConcurrentBuilds()
        timestamps()
        gitLabConnection('Demo-Gitlab-Connection')
        gitlabBuilds(builds: ['Build', 'Test'])
    }

    stages {
        stage("initialization") {
            steps {
                script {
                    currentBuild.description = sh(
                            script: 'git show --name-only',
                            returnStdout: true
                    ).trim()
                }
            }
        }

        stage('Build') {
            steps {
                gitlabCommitStatus(name: 'Build') {
                    sh 'mvn clean install -DskipTests=true'
                }
            }
        }

        stage('Test') {
            steps {
                gitlabCommitStatus(name: 'Test') {
                    sh 'mvn test -DskipTests=false'
                }
            }
            post {
                always {
                    script {
                        junit "**/surefire-reports/*.xml"
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            when { branch 'main' }
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
    }
    post {
        unsuccessful {
            script {
                gitlabCommitStatus("ERROR")
                echo "sending mail because there are test failures"
//                emailext(
//                        body: "Please go to ${env.BUILD_URL}/console for more details.",
//                        to: emailextrecipients([developers(), requestor()]),
//                        subject: "Compile-Pipeline Status is ${currentBuild.result}. ${env.BUILD_URL}"
//                )
            }
        }
        always {
            script {
                cleanWs()
            }
        }
    }
}
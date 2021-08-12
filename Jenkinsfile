properties([
        gitLabConnection(''),
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
        any
    }

    triggers {

    }

    tools {
        jdk 'jdk11'
        maven 'Maven 3.6.3'
    }

    options {
        disableConcurrentBuilds()
        timestamps()
        gitLabConnection('')
        gitlabBuilds(builds: ['Build & Unit-Test'])
    }

    stages {
        stage("initialization"){
            steps {
                script {
                    currentBuild.description=sh(
                            script: 'git show --name-only',
                            returnStdout: true
                    ).trim()
                }
            }
        }

        stage('Build & Unit-Test'){
            steps {
                gitlabCommitStatus(name: 'Build & Unit-Test') {
                    sleep(20)
                    echo "todo"
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

        stage('Deploy to Nexus'){
            steps {
                echo "todo"
            }
        }
    }
     post {
        failure {
            script {
                emailext(
                        body: "Please go to ${env.BUILD_URL}/console for more details.",
                        to: emailextrecipients([developers(), requestor()]),
                        subject: "Compile-Pipeline Status is ${currentBuild.result}. ${env.BUILD_URL}"
                )
            }
        }
        unstable {
            script {
                emailext(
                        body: "Please go to ${env.BUILD_URL}/console for more details.",
                        to: emailextrecipients([developers(), requestor()]),
                        subject: "Compile-Pipeline Build Status is ${currentBuild.result}. ${env.BUILD_URL}"
                )
            }
        }
        always {
            script {
                cleanWs()
            }
        }
    }
}
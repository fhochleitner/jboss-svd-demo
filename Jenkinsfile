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
        gitlabBuilds(builds: ['Build', 'Test', 'Deploy'])
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
                    configFileProvider([configFile(fileId: 'Maven-Settings', variable: 'MAVEN_SETTINGS_XML')]) {
                        sh 'mvn -s $MAVEN_SETTINGS_XML clean -DskipTests=true'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                gitlabCommitStatus(name: 'Build') {
                    script {
                        try {
                            // man kann auch die Settings für die Multi-Branch Pipeline für die gesamte Pipeline hinterlegen, dann sollte man den configFileProvider nicht mehr benötigen.
                            configFileProvider([configFile(fileId: 'Maven-Settings', variable: 'MAVEN_SETTINGS_XML')]) {
                                sh 'mvn -s $MAVEN_SETTINGS_XML clean -DskipTests=false'
                            }
                        } catch (exc) {
                            currentBuild.result = 'UNSTABLE'
                        }
                    }
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
                script {
                    if (currentBuild.result == 'SUCCESS') {
                        configFileProvider([configFile(fileId: 'Maven-Settings', variable: 'MAVEN_SETTINGS_XML')]) {
                            sh 'mvn -s $MAVEN_SETTINGS_XML deploy'
                        }
                    }
                }
            }

        }
    }
    post {
        failure {
            script {
                echo "sending mail because there are test failures"
//                emailext(
//                        body: "Please go to ${env.BUILD_URL}/console for more details.",
//                        to: emailextrecipients([developers(), requestor()]),
//                        subject: "Compile-Pipeline Status is ${currentBuild.result}. ${env.BUILD_URL}"
//                )
            }
        }
        unstable {
            script {
                echo "sending mail because there are test failures"
//                emailext(
//                        body: "Please go to ${env.BUILD_URL}/console for more details.",
//                        to: emailextrecipients([developers(), requestor()]),
//                        subject: "Compile-Pipeline Build Status is ${currentBuild.result}. ${env.BUILD_URL}"
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

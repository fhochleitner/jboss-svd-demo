#!groovy
properties([
        gitLabConnection('Demo-Gitlab-Connection'),
        pipelineTriggers([
                [
                        $class                        : 'GitLabPushTrigger',
                        branchFilterType              : 'All',
                        triggerOnPush                 : true,
                        triggerOnMergeRequest         : true,
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
        gitlabBuilds(builds: ['Build', 'Test', 'Sonar'])
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
                    sh 'mvn -B clean install -DskipTests=true -Pjacoco'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    try {
                        gitlabCommitStatus(name: 'Test') {
                            sh 'mvn -B test -DskipTests=false -Pjacoco'
                        }
                    } catch (exc) {
                        currentBuild.result = 'UNSTABLE'
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
            options {
                gitlabBuilds(builds: ['Deploy'])
            }
            when {
                beforeOptions true
                allOf {
                    branch 'main'
                    expression {
                        currentBuild.result == null
                    }
                }
            }
            steps {
                gitlabCommitStatus(name: 'Deploy'){
                    sh 'mvn -B deploy -DskipTests'
                }
            }
        }
        stage('Sonar analyse') {
            steps {
                gitlabCommitStatus(name: 'Sonar') {
                    withSonarQubeEnv('demoSonarQubeServer') {
                        sh 'mvn -B jacoco:report -Pjacoco'
                        sh 'mvn -B sonar:sonar'
                    }
                    timeout(time: 15, unit: 'MINUTES') {
                        // If analysis takes longer than indicated time, then build will be aborted
                        script {
                            def qualitygate = waitForQualityGate()
                            if (qualitygate.status != "OK") {
                                error "Pipeline aborted due to quality gate coverage failure: ${qualitygate.status}"
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        unsuccessful {
            script {
                echo "sending mail because there are test failures"
//                emailext(
//                        body: "Please go to ${env.BUILD_URL}/console for more details.",
//                        to: emailextrecipients([developers()]),
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
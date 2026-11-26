#!groovy

String workerNode = "devel12"
String slackChannel = "meta-notifications"

pipeline {
    agent { label workerNode }

    tools {
        maven "Maven 3"
    }

    triggers {
        pollSCM("H/03 * * * *")
        upstream(upstreamProjects: "Docker-payara6-bump-trigger",
                threshold: hudson.model.Result.SUCCESS)
    }

    options {
        timestamps()
    }

    environment {
        DOCKER_IMAGE_NAME = "docker-metascrum.artifacts.dbccloud.dk/rawrepo-introspect-backend"
        DOCKER_IMAGE_VERSION = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
        GITLAB_PRIVATE_TOKEN = credentials("metascrum-gitlab-api-token")
    }

    stages {
        stage("Clean Workspace") {
            steps {
                deleteDir()
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh "mvn verify pmd:pmd pmd:cpd spotbugs:spotbugs"

                junit testResults: '**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml'

                script {
                    def java = scanForIssues tool: [$class: 'Java']
                    def javadoc = scanForIssues tool: [$class: 'JavaDoc']
                    publishIssues issues: [java, javadoc], unstableTotalAll: 0

                    def pmd = scanForIssues tool: [$class: 'Pmd']
                    publishIssues issues: [pmd], unstableTotalAll: 1

                    // spotbugs still has some outstanding issues with regard
                    // to analyzing Java 11 bytecode.
                    // def spotbugs = scanForIssues tool: [$class: 'SpotBugs']
                    // publishIssues issues:[spotbugs], unstableTotalAll:1
                }
            }
        }
        stage("Docker build") {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                script {
                    def image = docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION}", '--pull --no-cache .')

                    image.push()
                }
            }
        }

        stage("Update staging version number") {
           when {
               branch "master-dm2"
           }
           steps {
               script {
                   withCredentials([sshUserPrivateKey(credentialsId: "gitlab-isworker", keyFileVariable: "sshkeyfile")]) {
                       env.GIT_SSH_COMMAND = "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ${sshkeyfile}"
                       sh """
                           nix run --refresh git+https://gitlab.dbc.dk/public-de-team/gitops-secrets-set-variables.git \
                               metascrum-staging:RAWREPO_INTROSPECT_BACKEND_VERSION=${env.BRANCH_NAME}-${env.BUILD_NUMBER}
                       """
                   }
               }
           }
       }
    }
    post {
        always {
            sh """
                docker rmi "${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION}"
            """
        }
        failure {
            script {
                if (BRANCH_NAME == "master-dm2") {
                    slackSend(channel: "${slackChannel}",
                            color: 'warning',
                            message: "${JOB_NAME} #${BUILD_NUMBER} failed and needs attention: ${BUILD_URL}",
                            tokenCredentialId: 'slack-global-integration-token')
                }
            }
        }
        success {
            script {
                if (BRANCH_NAME == 'master-dm2') {
                    slackSend(channel: "${slackChannel}",
                            color: 'good',
                            message: "${JOB_NAME} #${BUILD_NUMBER} completed, and pushed ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION} to artifactory.",
                            tokenCredentialId: 'slack-global-integration-token')

                }
            }
        }
        fixed {
            script {
                if (BRANCH_NAME == 'master-dm2') {
                    slackSend(channel: "${slackChannel}",
                            color: 'good',
                            message: "${JOB_NAME} #${BUILD_NUMBER} back to normal: ${BUILD_URL}",
                            tokenCredentialId: 'slack-global-integration-token')
                }
            }
        }
    }
}

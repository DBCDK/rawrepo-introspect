#!groovy

def workerNode = "devel8"

pipeline {
    agent { label workerNode }

    tools {
        maven "Maven 3"
    }

    triggers {
        pollSCM("H/03 * * * *")
    }

    options {
        timestamps()
    }

    environment {
        DOCKER_IMAGE_NAME = "docker-io.dbc.dk/rawrepo-introspect"
        DOCKER_IMAGE_VERSION = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
        DOCKER_IMAGE_DIT_VERSION = "DIT-${env.BUILD_NUMBER}"
        GITLAB_PRIVATE_TOKEN = credentials("metascrum-gitlab-api-token")
    }

    stages {
        stage("Clean Workspace") {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage("Verify") {
            steps {
                sh "mvn verify pmd:pmd"
                junit "**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml"
            }
        }

        stage("Publish PMD Results") {
            steps {
                step([$class          : 'hudson.plugins.pmd.PmdPublisher',
                      pattern         : '**/target/pmd.xml',
                      unstableTotalAll: "0",
                      failedTotalAll  : "0"])
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
                    def image = docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION}")
                    image.push()

                    if (env.BRANCH_NAME == 'master') {
                        sh """
                            docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION} ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_DIT_VERSION}
                            docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_DIT_VERSION}
                        """
                    }
                }
            }
        }

        stage("Bump deploy version") {
            agent {
                docker {
                    label workerNode
                    image "docker.dbc.dk/build-env:master-44"
                    alwaysPull true
                }
            }

            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                script {

                    if (env.BRANCH_NAME == 'master') {
                        sh """
                            set-new-version rawrepo-introspect-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/rawrepo-introspect-deploy ${DOCKER_IMAGE_DIT_VERSION} -b metascrum-staging
                            set-new-version rawrepo-introspect-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/rawrepo-introspect-deploy ${DOCKER_IMAGE_DIT_VERSION} -b fbstest
                            set-new-version rawrepo-introspect-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/rawrepo-introspect-deploy ${DOCKER_IMAGE_DIT_VERSION} -b basismig
                        """
                    }
                }
            }
        }
    }

}
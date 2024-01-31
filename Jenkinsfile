#!groovy

def workerNode = "devel11"

pipeline {
    agent { label workerNode }

    tools {
        maven "Maven 3"
        jdk "jdk11"
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
        DOCKER_IMAGE_NAME = "docker-metascrum.artifacts.dbccloud.dk/rawrepo-introspect"
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
                    publishIssues issues: [java, javadoc], unstableTotalAll:0

                    def pmd = scanForIssues tool: [$class: 'Pmd']
                    publishIssues issues: [pmd], unstableTotalAll:1

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
                    def image = docker.build("docker-metascrum.artifacts.dbccloud.dk/rawrepo-introspect-backend:${DOCKER_IMAGE_VERSION}", '--pull --no-cache .')

                    image.push()
                }
            }
        }

        stage("Bump deploy version") {
            agent {
                docker {
                    label workerNode
                    image "docker.dbc.dk/build-env:latest"
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
                            set-new-version rawrepo-introspect-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/rawrepo-introspect-deploy ${DOCKER_IMAGE_DIT_VERSION} -b fbstest-dm3
                            set-new-version rawrepo-introspect-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/rawrepo-introspect-deploy ${DOCKER_IMAGE_DIT_VERSION} -b basismig

                            set-new-version services/rawrepo-introspect-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/dit-gitops-secrets ${DOCKER_IMAGE_DIT_VERSION} -b master
                        """
                    }
                }
            }
        }
    }

}

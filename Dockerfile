FROM docker-dbc.artifacts.dbccloud.dk/payara6-micro:latest

LABEL RAWREPO_RECORD_SERVICE_URL="The record service url to use. E.g. 'http://rawrepo-record-service.fbstest.svc.cloud.dbc.dk' (Mandatory)"

USER root

RUN apt-get update && apt-get install -y diffutils colordiff sed libxml2-utils jq && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY script/* /bin/
RUN chmod +x /bin/*

USER gfish

COPY target/*.war app.json deployments/

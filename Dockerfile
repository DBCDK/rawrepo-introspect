FROM docker-dbc.artifacts.dbccloud.dk/payara6-micro:latest

LABEL RAWREPO_RECORD_SERVICE_URL="The record service url to use. E.g. 'http://rawrepo-record-service.fbstest.svc.cloud.dbc.dk' (Mandatory)"
LABEL MOREINFO_DANBIB_URL="Database url to the moreinfo danbib database (mandatory)"
LABEL MOREINFO_UPDATE_URL="Database url to the moreinfo update database (mandatory)"
LABEL MOREINFO_BASIS_URL="Database url to the moreinfo basis database (mandatory)"

RUN apt-get update && apt-get install -y --no-install-recommends diffutils colordiff sed libxml2-utils jq && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY target/*.war app.json deployments/

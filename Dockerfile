FROM docker.dbc.dk/payara5-micro:latest

LABEL RAWREPO_RECORD_SERVICE_URL="The record service url to use. E.g. 'http://rawrepo-record-service.fbstest.svc.cloud.dbc.dk' (Mandatory)"
LABEL INSTANCE="Name of the instance of this introspect. Typically the name of the environment e.g. FBStest"
LABEL MOREINFO_DANBIB_URL="Database url to the moreinfo danbib database (mandatory)"
LABEL MOREINFO_UPDATE_URL="Database url to the moreinfo update database (mandatory)"
LABEL MOREINFO_BASIS_URL="Database url to the moreinfo basis database (mandatory)"
LABEL HOLDINGS_ITEMS_URL="The holdings items database url"
LABEL HOLDINGS_ITEMS_INTROSPECT_URL="The holdings items introspect url"

USER root

RUN apt-get update && apt-get install -qy diffutils colordiff sed libxml2-utils jq

COPY rawrepo-introspect-api/script/* /bin/
RUN chmod +x /bin/*

USER gfish

COPY rawrepo-introspect-api/target/rawrepo-introspect-api-*.war app.json deployments/
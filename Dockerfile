FROM docker.dbc.dk/payara5-micro:latest

LABEL RAWREPO_RECORD_SERVICE_URL="The record service url to use. E.g. 'http://rawrepo-record-service.fbstest.svc.cloud.dbc.dk' (Mandatory)"
LABEL INSTANCE="Name of the instance of this introspect. Typically the name of the environment e.g. FBStest"

COPY rawrepo-introspect-api/target/rawrepo-introspect-api-*.war app.json deployments/
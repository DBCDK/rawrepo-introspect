FROM docker.dbc.dk/payara5-micro:latest

LABEL RECORD_SERVICE_URL="The record service url to use. E.g. 'http://rawrepo-record-service.fbstest.svc.cloud.dbc.dk' (Mandatory)"

COPY rawrepo-introspect-api/target/rawrepo-introspect-api-*.war app.json deployments/
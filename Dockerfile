FROM docker.dbc.dk/payara5-micro:latest

LABEL RAWREPO_RECORD_SERVICE_URL="The record service url to use. E.g. 'http://rawrepo-record-service.fbstest.svc.cloud.dbc.dk' (Mandatory)"
LABEL INSTANCE="Name of the instance of this introspect. Typically the name of the environment e.g. FBStest"
LABEL MOREINFO_DANBIB_URL="Database url to the moreinfo danbib database (mandatory)"
LABEL MOREINFO_UPDATE_URL="Database url to the moreinfo update database (mandatory)"
LABEL MOREINFO_BASIS_URL="Database url to the moreinfo basis database (mandatory)"
LABEL HOLDING_ITEMS_URL="The holdings items database url"
LABEL HOLDINGS_ITEMS_INTROSPECT_URL="The holdings items introspect url"

COPY rawrepo-introspect-api/target/rawrepo-introspect-api-*.war app.json deployments/
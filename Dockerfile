FROM docker.dbc.dk/payara5-micro:latest

COPY rawrepo-introspect-api/target/rawrepo-introspect-api-*.war app.json deployments/
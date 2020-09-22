#!/usr/bin/env bash

# Set java11 in compat mode in production
if [ "$NAIS_CLUSTER_NAME" = "dev-fss" ] || [ "$NAIS_CLUSTER_NAME" = "dev-sbs" ]; then
  export JAVA_OPTS="${JAVA_OPTS} --illegal-access=warn"
else
  export JAVA_OPTS="${JAVA_OPTS} --illegal-access=warn"
fi

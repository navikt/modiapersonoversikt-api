#!/usr/bin/env bash

# Set java11 in compat mode in production

# Opens module-system for tomcat;
# https://github.com/apache/tomcat/blob/master/bin/catalina.sh#L314
export JAVA_OPTS="${JAVA_OPTS} --add-opens=java.base/java.lang=ALL-UNNAMED"
export JAVA_OPTS="${JAVA_OPTS} --add-opens=java.base/java.io=ALL-UNNAMED"
export JAVA_OPTS="${JAVA_OPTS} --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED"

if [ "$NAIS_CLUSTER_NAME" = "dev-fss" ] || [ "$NAIS_CLUSTER_NAME" = "dev-sbs" ]; then
  export JAVA_OPTS="${JAVA_OPTS} --illegal-access=debug"
else
  export JAVA_OPTS="${JAVA_OPTS} --illegal-access=warn"
fi

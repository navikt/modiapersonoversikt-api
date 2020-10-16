#!/usr/bin/env bash
# Set java11 in compat mode in production

# Needed for Tomcat 9, no plans to fix on their behalf;
# https://github.com/apache/tomcat/blob/master/bin/catalina.sh#L314
# http://mail-archives.apache.org/mod_mbox/tomcat-users/201809.mbox/%3C6416c4e0-4c85-d76b-a201-6643a0a6421c@apache.org%3E
export JAVA_OPTS="${JAVA_OPTS} --add-opens=java.base/java.lang=ALL-UNNAMED"
export JAVA_OPTS="${JAVA_OPTS} --add-opens=java.base/java.io=ALL-UNNAMED"
export JAVA_OPTS="${JAVA_OPTS} --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED"

if [ "$NAIS_CLUSTER_NAME" = "dev-fss" ] || [ "$NAIS_CLUSTER_NAME" = "dev-sbs" ]; then
  export JAVA_OPTS="${JAVA_OPTS} --illegal-access=deny"
else
  export JAVA_OPTS="${JAVA_OPTS} --illegal-access=warn"
fi

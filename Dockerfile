FROM navikt/java:8-appdynamics

ENV APPD_ENABLED=true
ENV JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -Xms1024M -Xmx4096M -XX:MaxMetaspaceSize=512m"
COPY java-debug.sh /init-scripts/08-java-debug.sh

COPY /web/target/modiabrukerdialog /app

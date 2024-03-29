ARG image_version=17-appdynamics

FROM navikt/java:${image_version}

ENV APPD_ENABLED=true
ENV JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -Xms1024M -Xmx4096M -XX:MaxMetaspaceSize=512m"
COPY java-debug.sh /init-scripts/08-java-debug.sh
COPY java-17-compat-mode.sh /init-scripts/09-java-17-compat-mode.sh

COPY /web/target/modiabrukerdialog.jar app.jar

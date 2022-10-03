FROM appdynamics/java-agent:22.9.1 AS appdynamics
RUN find /opt/appdynamics -type d -name argentoDynamicService -exec rm -rf {} +;


FROM navikt/java:17-appdynamics

USER root
RUN rm -rf /opt/appdynamics
COPY --chown=apprunner:root --from=appdynamics /opt/appdynamics /opt/appdynamics
USER apprunner

ENV APPD_ENABLED=true
ENV JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -Xms1024M -Xmx4096M -XX:MaxMetaspaceSize=512m"
COPY java-debug.sh /init-scripts/08-java-debug.sh
COPY java-17-compat-mode.sh /init-scripts/09-java-17-compat-mode.sh

COPY /web/target/modiabrukerdialog.jar app.jar

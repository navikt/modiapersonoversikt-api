FROM navikt/java:8-appdynamics

ENV APPD_ENABLED=true
ENV JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -Xms1024M -Xmx4096M -XX:MaxMetaspaceSize=512m"
ENV JAVA_OPTS="${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"

COPY /web/target/modiabrukerdialog /app

#FROM docker.adeo.no:5000/pus/maven as builder
#
#ADD / /source
#WORKDIR /source
#RUN mvn package -DskipTests
#
#FROM docker.adeo.no:5000/pus/nais-java-app
#
#ENV JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -Xms1024M -Xmx4096M -XX:MaxMetaspaceSize=512m"
#
#COPY --from=builder /source/web/target/modiabrukerdialog /app

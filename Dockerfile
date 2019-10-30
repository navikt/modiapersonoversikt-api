FROM docker.adeo.no:5000/pus/maven as builder

ADD / /source
WORKDIR /source
RUN mvn package -DskipTests

FROM docker.adeo.no:5000/pus/nais-java-app

ENV JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -Xms2048M -Xmx4096M -XX:MaxMetaspaceSize=512m"

COPY --from=builder /source/web/target/modiabrukerdialog /app

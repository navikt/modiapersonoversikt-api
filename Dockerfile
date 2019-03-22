FROM docker.adeo.no:5000/pus/node as nodeBuilder

ADD / /source
WORKDIR /source/reactkomponenter
RUN npm install
RUN npx gulp
RUN npm test

FROM docker.adeo.no:5000/pus/maven as mavenBuilder

ADD / /source
WORKDIR /source

COPY --from=nodeBuilder /source/reactkomponenter/target /source/reactkomponenter/target
RUN mvn package -DskipTests

FROM docker.adeo.no:5000/pus/nais-java-app

ENV JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -Xms2048M -Xmx4096M -XX:MaxMetaspaceSize=512m"

COPY --from=mavenBuilder /source/web/target/modiabrukerdialog /app

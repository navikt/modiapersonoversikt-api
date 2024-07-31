FROM gcr.io/distroless/java17-debian12

ENV JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC -Xms1024M -Xmx4096M -XX:MaxMetaspaceSize=512m"

USER nonroot

COPY /proxy/target/modiabrukerdialog.jar app.jar
CMD ["app.jar"]
FROM gcr.io/distroless/java21-debian12

ENV JDK_JAVA_OPTIONS="${JAVA_OPTS} ${JAVA_PROXY_OPTIONS} -XX:+UseG1GC -Xms1024M -Xmx4096M -XX:MaxMetaspaceSize=512m"

USER nonroot

COPY /proxy/target/modiabrukerdialog.jar app.jar
CMD ["app.jar"]

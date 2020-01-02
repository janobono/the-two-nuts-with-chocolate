FROM openjdk:8-jre-alpine

ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV AB_ENABLED=jmx_exporter

RUN addgroup --gid 1000 nut; \
    adduser --disabled-password --gecos "" --home "$(pwd)" --ingroup nut --no-create-home --uid 1000 nut

EXPOSE 8080

COPY target/lib/* /app/lib/
COPY target/*-runner.jar /app/app.jar

RUN chown -R nut:nut /app

USER nut

WORKDIR /app

ENTRYPOINT ["java","-cp","lib/*","-jar","app.jar"]

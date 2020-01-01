FROM openjdk:8-jre-alpine

RUN addgroup --gid 1000 nut; \
    adduser --disabled-password --gecos "" --home "$(pwd)" --ingroup nut --no-create-home --uid 1000 nut

EXPOSE 8080

ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

RUN chown -R nut:nut /app

USER nut

VOLUME /tmp

ENTRYPOINT ["java","-cp","app:app/lib/*","sk.janobono.springbootnut.SpringBootNutApplication"]

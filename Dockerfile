FROM openjdk:11-jre-slim

ARG JAR_FILE=mock-service*.jar
RUN apt-get update
RUN apt-get -yq clean
RUN groupadd -g 989 mock-service && \
    useradd -r -u 989 -g mock-service mock-service
USER mock-service
COPY target/$JAR_FILE /opt/mock-service.jar

ENTRYPOINT [ "java", "-jar", "/opt/mock-service.jar" ]

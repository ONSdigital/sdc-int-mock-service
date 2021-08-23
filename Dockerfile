FROM openjdk:11-jre-slim

ARG JAR_FILE=mock-ai*.jar
RUN apt-get update
RUN apt-get -yq clean
RUN groupadd -g 989 mock-ai && \
    useradd -r -u 989 -g mock-ai mock-ai
USER mock-ai
COPY target/$JAR_FILE /opt/mock-ai.jar

ENTRYPOINT [ "java", "-jar", "/opt/mock-ai.jar" ]

FROM adoptopenjdk/openjdk11-openj9

COPY . /wiremock

WORKDIR /wiremock/

RUN  ./gradlew fatJar

EXPOSE 8443

CMD java -jar build/libs/wiremock-0.0.1-SNAPSHOT-all.jar
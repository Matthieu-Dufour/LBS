FROM openjdk:8
VOLUME /tmp
EXPOSE 8082
ADD ./target/dev-webService-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

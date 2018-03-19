# Reference docker file, check gradlew buildDocker
# Container can be invoked with docker run -e "JAVA_OPTS=-Xmx52m"
FROM java:8
VOLUME /tmp
RUN mkdir -p /app
ADD build/libs/polydata-storage-mongodb-rest-api-2.3.0-SNAPSHOT.jar /app/app.jar
RUN bash -c 'touch /app/app.jar'
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar
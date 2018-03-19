FROM java:8
VOLUME /tmp
RUN mkdir -p /app
ADD build/libs/polydata-storage-mongodb-rest-api-2.3.0-SNAPSHOT.jar /app/app.jar
RUN bash -c 'touch /app/app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
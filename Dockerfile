FROM openjdk
ARG VERSION
RUN mkdir /app
COPY target/receiver-${VERSION}.jar /app
RUN mv /app/receiver-${VERSION}.jar /app/receiver-SNAPSHOT.jar
CMD java -jar /app/receiver-SNAPSHOT.jar
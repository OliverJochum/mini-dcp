# get base image (jdk you're using)
FROM docker.io/library/eclipse-temurin:21.0.3_9-jdk-alpine

# copy jar file into the image
COPY build/libs/flightsearch_app-0.0.1-SNAPSHOT.jar /flightsearch_app-0.0.1-SNAPSHOT.jar

# set the executable that starts when the container boots
ENTRYPOINT [ "java", "-jar", "/flightsearch_app-0.0.1-SNAPSHOT.jar" ]

# expose ports 5100 and 9100
# 5100 is for the application, 9100 is for the actuator
EXPOSE 5100 9100
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# Copy Gradle wrapper first, then give it execute permissions
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew

COPY . .

COPY .git .git

RUN ./gradlew build --no-daemon

# get base image (jdk you're using)
FROM docker.io/library/eclipse-temurin:21.0.3_9-jdk-alpine

# set the working directory in the image
WORKDIR /app

# copy jar file into the image
COPY --from=builder /workspace/build/libs/*.jar /flightsearch_app.jar

# set the executable that starts when the container boots
ENTRYPOINT [ "java", "-jar", "/flightsearch_app.jar" ]

# expose ports 5100 and 9100
# 5100 is for the application, 9100 is for the actuator
EXPOSE 5100 9100
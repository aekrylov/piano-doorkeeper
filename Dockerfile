FROM gradle:jdk11 as builder

WORKDIR /home/gradle/project
COPY *.kts ./
# A hack to embrace Docker build cache
RUN gradle build -x test || return 0

COPY . .
RUN gradle build -x test

FROM openjdk:11-jre
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/piano-doorkeeper-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java", "-jar", "piano-doorkeeper-0.0.1-SNAPSHOT.jar"]

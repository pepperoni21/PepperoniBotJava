# Build stage
FROM gradle:latest AS BUILD
WORKDIR /workdir/server/
COPY . .
RUN gradle shadowJar

# Package stage
FROM amazoncorretto:17
WORKDIR /workdir/server/
COPY --from=BUILD /workdir/server/build/libs/PepperoniBot-1.0-all.jar .
ENTRYPOINT ["java", "-jar", "PepperoniBot-1.0-all.jar"]
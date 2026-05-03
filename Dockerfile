FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="Matthew Ambrose <mattbobambrose@gmail.com>"

# Run as a non-root user to reduce container security risk.
ENV APPLICATION_USER=playground

# Add the user, create /app, and give the user ownership.
RUN adduser -D -g '' $APPLICATION_USER && mkdir /app && chown -R $APPLICATION_USER /app
# Mark this container to use the specified $APPLICATION_USER
USER $APPLICATION_USER

COPY ./build/libs/playwright-scenario-playground-all.jar /app/playground.jar

# Make /app the working directory
WORKDIR /app

EXPOSE 8080

# Launch java to execute the jar with defaults intended for containers.
ENTRYPOINT ["java", "-server", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/playground.jar"]
FROM gradle:8.14-jdk21

WORKDIR /src/dislin

COPY build.gradle.kts settings.gradle.kts gradle.properites ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon || true

COPY src ./src

RUN gradle build --no-daemon

CMD ["java", "-jar", "build/libs/dislin.jar"]
# =================================================================
# Stage 1: Build the application using Gradle
# =================================================================
FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /workspace

# Gradle Wrapper 파일들을 먼저 복사합니다.
COPY gradlew .
COPY gradle gradle

# 의존성 파일(build.gradle)을 먼저 복사하여 Docker 레이어 캐싱을 활용합니다.
COPY build.gradle .
COPY settings.gradle .

# 의존성만 먼저 다운로드합니다.
RUN ./gradlew dependencies --no-daemon

# 소스 코드를 복사합니다.
COPY src src

# 애플리케이션을 빌드합니다. (테스트는 CI 단계에서 하므로 보통 스킵)
RUN ./gradlew build --no-daemon -x test

# =================================================================
# Stage 2: Create the final, small runtime image
# =================================================================
FROM gcr.io/google-appengine/openjdk:17

WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일만 복사해옵니다.
COPY --from=builder /workspace/build/libs/*.jar app.jar

# 컨테이너 실행 시 실행될 명령어 (prod 프로파일 활성화)
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=deploy", "app.jar"]
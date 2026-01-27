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
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일만 복사해옵니다.
COPY --from=builder /workspace/build/libs/*.jar app.jar

# TrustStore 파일 복사
# src/main/resources/truststore.jks 파일은 빌더 스테이지에서 /app/src/main/resources/truststore.jks 에 있었습니다.
# 이제 이 파일을 최종 이미지의 /app/truststore.jks 로 복사합니다.
# 이렇게 하면 JAR 파일 외부에 별도로 존재하게 됩니다.
COPY --from=builder /app/src/main/resources/truststore.jks /app/truststore.jks

# 컨테이너 실행 시 실행될 명령어 (prod 프로파일 활성화)
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=deploy", "-Djavax.net.ssl.trustStore=/app/truststore.jks","-Djavax.net.ssl.trustStorePassword=${KEYSTORE_PW}" "app.jar"]
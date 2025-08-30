FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Gradle Wrapper와 의존성 파일들을 먼저 복사
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

COPY gateway gateway/

RUN chmod +x gradlew

# 의존성 다운로드
RUN ./gradlew :api-gateway:dependencies --no-daemon

# 애플리케이션 빌드
RUN ./gradlew :api-gateway:bootJar --no-daemon


FROM openjdk:17-jre-slim

RUN groupadd -r ruang && useradd -r -g ruang ruang

# 패키지 설치 - 필요한 것
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/gateway/build/libs/*.jar app.jar

RUN chown -R ruang:ruang /app

USER ruang

# 헬스체크 
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8090/actuator/health || exit 1

ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:+UseContainerSupport"

EXPOSE 8090

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
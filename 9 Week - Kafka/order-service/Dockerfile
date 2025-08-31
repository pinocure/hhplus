FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Gradle Wrapper와 의존성 파일들을 먼저 복사
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

COPY common common/

COPY order-service order-service/

RUN chmod +x gradlew

# 의존성 다운로드
RUN ./gradlew :order-service:dependencies --no-daemon

# 애플리케이션 빌드
RUN ./gradlew :order-service:bootJar --no-daemon


FROM eclipse-temurin:17-jre

RUN groupadd -r ruang && useradd -r -g ruang ruang

# 패키지 설치 - 필요한 것
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/order-service/build/libs/*.jar app.jar

RUN chown -R ruang:ruang /app

USER ruang

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
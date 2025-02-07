# 1단계: 빌드 이미지
FROM maven:3.9-eclipse-temurin-17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 종속성 캐싱 (pom.xml만 사용)
COPY pom.xml .
RUN mvn dependency:go-offline

# 전체 프로젝트 복사 및 빌드
COPY src/ src/

# application.yml이 있는 폴더 복사
COPY src/main/resources/ src/main/resources/

# Maven 빌드 (JAR 파일 생성)
RUN mvn package -DskipTests  # JAR 파일 생성

# 2단계: 실행용 이미지
FROM openjdk:17-jdk

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/target/*.jar app.jar

# 컨테이너 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

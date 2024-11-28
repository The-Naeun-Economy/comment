# 베이스 이미지 선택
FROM openjdk:21-jdk

# 애플리케이션 jar 파일 추가
ARG JAR_FILE=build/libs/community-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

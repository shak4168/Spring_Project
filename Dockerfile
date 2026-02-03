# 1. 베이스 이미지 설정 (가볍고 안정적인 Java 17 버전 사용)
FROM amazoncorretto:17

# 2. 작성자 정보 (선택 사항)
LABEL maintainer="admin@example.com"

# 3. JAR 파일 위치 변수 설정
# (Gradle 빌드 시 libs 폴더에 생기는 jar 파일을 타겟으로 함)
ARG JAR_FILE=build/libs/*.jar

# 4. 컨테이너 내부로 JAR 파일 복사
COPY ${JAR_FILE} app.jar

# 5. 실행 명령어 설정
ENTRYPOINT ["java", "-jar", "/app.jar"]
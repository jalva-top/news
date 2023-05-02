FROM amazoncorretto:17
ARG JAR_FILE=build/libs/news-*.jar
EXPOSE 8080
COPY ${JAR_FILE} news.jar
ENTRYPOINT ["java","-jar","/news.jar"]
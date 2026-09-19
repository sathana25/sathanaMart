FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN javac -cp "lib/mysql-connector-j-26.7.0.jar" SathanaMartBackend.java

CMD ["java", "-cp", ".:lib/mysql-connector-j-26.7.0.jar", "SathanaMartBackend"]
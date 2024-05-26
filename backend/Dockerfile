FROM openjdk:22

WORKDIR /app

COPY . /app

RUN ./mvnw clean package -DskipTests

RUN mv target/*.jar app.jar 

RUN chmod +x app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

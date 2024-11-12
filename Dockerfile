# Use the official Maven image to build the application
FROM maven:3.8.4-openjdk-17 AS build  #Maven with OpenJDK 17 for building Java applications
WORKDIR /app  #Set the working directory inside the container

# Copy the source code into the container
COPY . .  #Copies all files from the host to the container's /app directory

# Build the application and create a JAR file
RUN mvn clean package  # Cleans the project and packages it into a JAR file

# Use the official OpenJDK image to run the application
FROM openjdk:17-jdk  #OpenJDK 17 for running Java applications
WORKDIR /app  #Set the working directory inside the container

# Copy the JAR file from the build stage to the runtime image
COPY --from=build /app/target/5arctic6-g2-foyer-0.0.1.jar app.jar  #Copies the built JAR from the previous stage

# Expose the port the app will run on (adjust as necessary)
EXPOSE 8080  #Makes port 8080 available for mapping from the host

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]  #Command to run the application

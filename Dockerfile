FROM openjdk:21-jdk-slim

# Install necessary tools
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz | tar xzf - -C /opt \
    && ln -s /opt/apache-maven-3.9.6 /opt/maven \
    && ln -s /opt/maven/bin/mvn /usr/local/bin/mvn

# Build the application
RUN mvn clean package -DskipTests

# Expose ports
EXPOSE 8080 4040

# Run the application with JDK 21 compatibility flags for Spark
CMD ["java", \
     "--add-opens=java.base/java.lang=ALL-UNNAMED", \
     "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED", \
     "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED", \
     "--add-opens=java.base/java.io=ALL-UNNAMED", \
     "--add-opens=java.base/java.net=ALL-UNNAMED", \
     "--add-opens=java.base/java.nio=ALL-UNNAMED", \
     "--add-opens=java.base/java.util=ALL-UNNAMED", \
     "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED", \
     "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED", \
     "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", \
     "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED", \
     "--add-opens=java.base/sun.security.action=ALL-UNNAMED", \
     "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED", \
     "--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED", \
     "-jar", "target/spark-as-a-service-1.0.0.jar"]
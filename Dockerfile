# Étape 1 : Construction
FROM eclipse-temurin:17-jdk AS build

# Définir le répertoire de travail
WORKDIR /app

# Instaler Maven
RUN apt-get update && apt-get install -y maven

# Copier les fichiers du projet dans l'image
COPY . .

# Construire le projet et générer le fichier JAR
RUN mvn clean package -DskipTests

# Étape 2 : Exécution
FROM eclipse-temurin:17-jre-slim

# Créer un utilisateur et groupe non-root
RUN groupadd -r spring && useradd -r -g spring -u 1000 spring

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR depuis l'étape de build avec les bonnes permissions
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# Passer à l'utilisateur non-root
USER spring:spring

# Exposer le port de l'application
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
# social_media

# Forum Social - Application Spring Boot

Une application de réseau social développée avec Spring Boot permettant aux utilisateurs de créer des profils, d'ajouter des amis, d'échanger des messages et de partager des publications.

## Fonctionnalités

- ✅ Authentification sécurisée (Spring Security)  
- ✅ Gestion des profils utilisateurs  
- ✅ Système d'amis avec demandes d'ajout  
- ✅ Messagerie privée entre amis  
- ✅ Fil d'actualité avec les publications des amis  
- ✅ Interface moderne et responsive  

## Technologies utilisées

- **Backend**: Spring Boot 3.2, Spring Security, Spring Data JPA  
- **Frontend**: Thymeleaf, HTML5, CSS3  
- **Base de données**: H2 (développement), MySQL (docker, production)  
- **Build**: Maven  
- **Déploiement**: Environnement de production (EC2 + ALB + RDS + S3)  

## Installation locale

### Prérequis
- Java 17+  
- Maven 3.6+  
- Git  
- Docker & Docker Compose (optionnel si vous voulez lancer avec Docker)  

### Lancer le projet avec Maven (JAR)

1. Cloner le repository :
```bash
git clone https://github.com/Sougoumay/social_media
cd social_media
mvn clean package
java -jar target/social_media-0.0.1-SNAPSHOT.jar
http://localhost:8080
```
### Lancer le projet avec Docker
```bash
git clone https://github.com/Sougoumay/social_media
cd social_media
docker-compose up -d
http://localhost:8080
```
## Déploiement
- Lancement de la procédure du déploiement est défini dans le readme du dossier deployments

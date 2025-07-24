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
- **Base de données**: H2 (développement), MySQL (production)
- **Build**: Maven
- **Déploiement**: Docker, Minikube en local, EKS pour l'environnement de production

## Installation locale

### Prérequis
- Java 17+
- Maven 3.6+
- Git

### Étapes

1. Cloner le repository :
```bash
git clone https://github.com/Sougoumay/social_media
cd social_media
docker-compose up -d
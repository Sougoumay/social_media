| Couche         | Niveau principal | Quand l'utiliser                  |
| -------------- | ---------------- | --------------------------------- |
| **Controller** | INFO             | requête entrante, réponse envoyée |
|                | DEBUG            | détails utiles du traitement      |
| **Service**    | DEBUG            | logique métier, étapes internes   |
|                | INFO             | action métier importante          |
| **Repository** | DEBUG            | uniquement si query spécifique    |
| **Exceptions** | ERROR            | erreur                            |
|                | WARN             | anomalie non bloquante            |

# ✅ 1. Environnement DEV (local / développement)

- 🔍 Objectif : voir un maximum d'informations pour comprendre ce qui se passe.

| Zone             | Niveau                                                    |
|------------------| --------------------------------------------------------- |
| Application      | **DEBUG**                                                 |
| Spring Framework | **INFO**                                                  |
| Hibernate SQL    | **DEBUG** (+ `TRACE` pour les paramètres SQL ➜ optionnel) |
| Root Logger      | **INFO**                                                  |

# ✅ 2. Environnement TEST / CI (pipeline CI/CD)

- 🎯 Objectif : reproduire la prod mais avec plus d'explications quand un test échoue.

| Zone        | Niveau                |
| ----------- | --------------------- |
| Application | **INFO**              |
| Tests       | **DEBUG** (optionnel) |

# ✅ 3. Environnement STAGING / PREPROD

- 🎯 Objectif : environnement proche de la production.
- 👉 Niveau de log quasi identique à la prod.

| Zone                   | Niveau    |
| ---------------------- | --------- |
| Application            | **INFO**  |
| Root                   | **INFO**  |
| Errors                 | **ERROR** |
| Aucune info sensible ! |           |


# ✅ 4. Environnement PRODUCTION

## 🎯 Objectif :

- ne jamais polluer avec des logs verbeux 
- minimiser le coût / espace disque 
- ogs archivables (rotation + compression)

| Zone           | Niveau    |
| -------------- | --------- |
| Application    | **INFO**  |
| Sécurité, auth | **WARN**  |
| Datasources    | **WARN**  |
| Root           | **WARN**  |
| Erreurs        | **ERROR** |


# 5- Configuration des logs

## 1- Limite des logs en text : 
- Format : 12:45:10.123 INFO  c.s.user.UserService - Profile update successful for user 'ali'
- lisible pour un humain, mais
  - difficile à parser automatiquement
  - fragile (si le format change, les outils cassent)
  - compliqué pour faire des recherches avancées 
    - tous les logs d’un utilisateur
    - toutes les erreurs d’un service
    - corrélation entre microservices

## 2- Avantages des Logs JSON (logs structurés)
### Format
```json
{
  "timestamp": "2025-12-21T12:45:10.123",
  "level": "INFO",
  "service": "social_media",
  "logger": "c.s.user.UserService",
  "message": "Profile update successful",
  "username": "ali",
  "thread": "http-nio-8080-exec-1"
}
```
### Avantages
- Parfait pour le monitoring 
  - ELK (Elasticsearch + Logstash + Kibana)
  - Grafana + Loki 
  - CloudWatch ...
- Standard industriel 
  - En prod, quasi tous les systèmes sérieux utilisent des logs JSON


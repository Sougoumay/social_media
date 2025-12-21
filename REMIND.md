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



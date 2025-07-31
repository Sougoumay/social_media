/terraform
├── main.tf            # Définition principale des ressources (provider, cluster, infra)
├── variables.tf       # Déclaration des variables d'entrée (ex: configs, secrets en variable)
├── outputs.tf         # Déclaration des outputs (ex: IPs, ARNs, identifiants, endpoints)
├── providers.tf       # Configuration des providers (ex: AWS, Kubernetes)
├── terraform.tfvars   # Valeurs des variables (sensibles ou non)
├── modules/           # Modules réutilisables (ex: module MySQL, module app)
│   ├── mysql/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── app/
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
└── README.md          # Instructions, infos utiles

flowchart TD
%% Classes CSS pour styliser le diagramme
classDef user fill:#f9f9f9,stroke:#333,stroke-width:2px;
classDef aws fill:#FF9900,stroke:#232F3E,stroke-width:2px,color:white;
classDef vpc fill:#F3F8F2,stroke:#3F8624,stroke-width:2px,stroke-dasharray: 5 5;
classDef publicSubnet fill:#E6F0FA,stroke:#007BFF,stroke-width:2px;
classDef privateSubnet fill:#FDEEEF,stroke:#DC3545,stroke-width:2px;
classDef compute fill:#F58536,stroke:#232F3E,stroke-width:2px,color:white;
classDef network fill:#8C4FFF,stroke:#232F3E,stroke-width:2px,color:white;
classDef database fill:#3355DA,stroke:#232F3E,stroke-width:2px,color:white;
classDef storage fill:#3F8624,stroke:#232F3E,stroke-width:2px,color:white;
classDef security fill:#DD344C,stroke:#232F3E,stroke-width:2px,color:white;

%% Acteurs Externes
User((👤 Utilisateurs)):::user
Admin((💻 Admin DevOps)):::user
GitHub((🐙 GitHub Actions)):::user

subgraph AWS [☁️ AWS Cloud Region]

subgraph GlobalServices [Services Globaux / Hors VPC]
IAM[🔐 IAM OIDC Provider & Rôles]:::security
S3[🪣 S3: sm-jar-bucket]:::storage
SSM[⚙️ Systems Manager]:::security
end

subgraph VPC [🌐 VPC: main_social_media_vpc]
IGW[🚪 Internet Gateway]:::network
VPCE[🚪 VPC Endpoint: S3]:::network

subgraph PublicAZ1 [Public Subnet 1 - AZ1]
ALB[⚖️ Application Load Balancer]:::network
Bastion[🛡️ EC2 Bastion]:::compute
NAT[🔄 NAT Gateway]:::network
end

subgraph PublicAZ2 [Public Subnet 2 - AZ2]
ALB_Node[⚖️ ALB Node]:::network
end

subgraph PrivateAZ1 [Private Subnet 1 - AZ1]
AppEC2[💻 EC2 App Server<br>Nginx + Spring Boot]:::compute
RDS_Primary[🗄️ RDS MySQL]:::database
end

subgraph PrivateAZ2 [Private Subnet 2 - AZ2]
RDS_SubnetGroup[🗄️ Subnet RDS dispo pour failover]:::database
end
end
end

%% Flux Utilisateurs
User ==>|HTTP 80 / HTTPS 443| IGW
IGW --> ALB & ALB_Node
ALB & ALB_Node ==>|HTTP 80| AppEC2

%% Flux d'Administration
Admin -.->|"SSH 22 (Ton IP IPV4)"| IGW
IGW -.-> Bastion
Bastion -.->|SSH 22| AppEC2

%% Flux Applicatifs Internes
AppEC2 -->|TCP 3306| RDS_Primary
AppEC2 -.->|Requêtes Sortantes<br>Téléchargements| NAT
NAT -.-> IGW

%% Flux Cloud et CI/CD
AppEC2 -->|Accès Privé| VPCE
VPCE --> S3

GitHub -->|AssumeRole OIDC| IAM
IAM -->|s3:PutObject| S3
IAM -->|ssm:SendCommand| SSM
SSM -->|Déploiement auto| AppEC2

%% Appliquer les styles aux subgraphs
class AWS aws;
class VPC vpc;
class PublicAZ1,PublicAZ2 publicSubnet;
class PrivateAZ1,PrivateAZ2 privateSubnet;
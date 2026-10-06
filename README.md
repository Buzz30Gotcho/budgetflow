# 💸 BudgetFlow

> Application web de **gestion de budget personnel** : suivez vos revenus et dépenses, organisez-les par catégorie, fixez des budgets mensuels et visualisez où part votre argent grâce à des graphiques clairs.

<p>
  <img src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white" />
  <img src="https://img.shields.io/badge/Angular-22-DD0031?logo=angular&logoColor=white" />
  <img src="https://img.shields.io/badge/Tailwind_CSS-4-38B2AC?logo=tailwindcss&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-316192?logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-000000?logo=jsonwebtokens&logoColor=white" />
</p>

---

## 📖 Présentation

**BudgetFlow** est une application **full-stack** permettant à chaque utilisateur de
garder le contrôle de ses finances personnelles. Après s'être inscrit, l'utilisateur
enregistre ses **revenus** et **dépenses**, les classe par **catégorie**, définit des
**budgets mensuels** et suit en temps réel la répartition de son argent via un **tableau
de bord graphique**.

Le projet a été pensé comme une vraie petite application de production : authentification
sécurisée par **JWT**, API **REST**, base de données **PostgreSQL**, et une interface
moderne et responsive.

## ✨ Fonctionnalités

- 🔐 **Inscription / connexion** sécurisées (authentification JWT)
- 💵 **Transactions** : ajout, modification, suppression de revenus et dépenses
- 🏷️ **Catégories** personnalisables (nom + couleur) avec édition en ligne
- 🎯 **Budgets mensuels** par catégorie avec barre de progression et alertes (dans le budget / bientôt dépassé / dépassé)
- 📊 **Tableau de bord** : solde, répartition des dépenses (camembert), évolution sur 6 mois
- 📥 **Import CSV** : ajoutez plusieurs transactions d'un coup depuis un fichier
- 📈 **Totaux et compteurs** automatiques (revenus, dépenses, solde)

## 🛠️ Technologies utilisées

**Langages**

![Java](https://img.shields.io/badge/Java-ED8B00?logo=openjdk&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white)
![SQL](https://img.shields.io/badge/SQL-4479A1?logo=postgresql&logoColor=white)

| Couche | Technologies |
|--------|--------------|
| **Back-end** | Java 17, Spring Boot 4 (Web, Security, Data JPA), JWT (jjwt), Hibernate |
| **Front-end** | Angular 22 (standalone, signals), TypeScript, Tailwind CSS 4, Chart.js |
| **Base de données** | PostgreSQL 16 |
| **Outils** | Maven, Docker, Git |

## 🏗️ Architecture

```
┌──────────────┐      REST / JSON (JWT)      ┌──────────────┐      JPA       ┌──────────────┐
│   Angular    │  ───────────────────────▶   │ Spring Boot  │  ──────────▶   │  PostgreSQL  │
│  (frontend)  │  ◀───────────────────────   │   (API REST) │  ◀──────────   │   (données)  │
└──────────────┘                             └──────────────┘                └──────────────┘
   port 4200                                     port 8080                       port 5432
```

Le back-end suit une architecture en couches classique : **Controller** (routes REST) →
**Service** (logique métier) → **Repository** (accès base de données). Les tables sont
générées automatiquement par Hibernate à partir des entités Java.

## 🚀 Lancer le projet

### Option 1 — Avec Docker 🐳 (recommandé)

Une seule commande lance la **base de données**, le **back-end** et le **front-end** :

```bash
docker compose up --build
```

➡️ Application disponible sur **http://localhost:4200**
*(les données sont conservées grâce à un volume Docker)*

### Option 2 — En local (manuel)

**Prérequis** : Java 17+, Node.js 22+, Docker

```bash
# 1. Base de données
docker run -d --name budgetflow-db \
  -e POSTGRES_DB=budgetflow -e POSTGRES_USER=budgetflow -e POSTGRES_PASSWORD=budgetflow \
  -p 5433:5432 postgres:16

# 2. Back-end (API sur http://localhost:8080)
cd backend
DB_URL=jdbc:postgresql://localhost:5433/budgetflow \
DB_USERNAME=budgetflow DB_PASSWORD=budgetflow \
./mvnw spring-boot:run

# 3. Front-end (app sur http://localhost:4200)
cd frontend
npm install
npx ng serve
```

## 📡 Aperçu de l'API

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/auth/register` | Inscription |
| `POST` | `/api/auth/login` | Connexion (renvoie un token JWT) |
| `GET` / `POST` / `PUT` / `DELETE` | `/api/categories` | Gestion des catégories |
| `GET` / `POST` / `PUT` / `DELETE` | `/api/transactions` | Gestion des transactions |
| `POST` | `/api/transactions/import` | Import CSV de transactions |
| `GET` | `/api/dashboard` | Données agrégées du tableau de bord |
| `GET` / `POST` / `PUT` / `DELETE` | `/api/budgets` | Gestion des budgets mensuels |

> Toutes les routes (sauf `/api/auth/**`) nécessitent un token JWT dans le header
> `Authorization: Bearer <token>`.

## 📂 Structure du projet

```
budgetflow/
├── backend/            # API Spring Boot (Java)
│   └── src/main/java/com/budgetflow/
│       ├── auth/         # inscription / connexion
│       ├── security/     # JWT, filtre, config sécurité
│       ├── user/ category/ transaction/ budget/ dashboard/
│       └── common/       # gestion des erreurs
└── frontend/           # Application Angular
    └── src/app/
        ├── core/         # modèles, services API, interceptor & guard JWT
        ├── features/     # auth, dashboard, transactions, categories, budgets
        └── layout/       # navigation
```

## 🔮 Évolutions futures

- 🏦 **Import bancaire automatique** via une API d'agrégation (Open Banking / DSP2, type Powens ou Tink), pour éviter la saisie manuelle
- 🔁 **Transactions récurrentes** (loyer, salaire ajoutés automatiquement chaque mois)
- ✅ Tests automatisés et intégration continue (GitHub Actions)

## 👤 Auteur

**Frédéric Makha SAR** — Développeur Full-Stack

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/frederic-sar-a377061a3)
[![GitHub](https://img.shields.io/badge/GitHub-181717?logo=github&logoColor=white)](https://github.com/Buzz30Gotcho)

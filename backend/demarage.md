```markdown
# Guide de démarrage — Projet Gestion de Stock

Ce projet est composé de **3 dépôts GitHub séparés** :

| Dépôt | Description | Technologie |
|---|---|---|
| [backend](https://github.com/gestion-de-stock-v2/backend) | API REST + microservices | Spring Boot 3 |
| [frontend-angular](https://github.com/gestion-de-stock-v2/frontend-angular) | Interface admin | Angular 20 |
| [react-frontend](https://github.com/gestion-de-stock-v2/react-frontend) | Interface React | React 18 |

---

## 📋 Prérequis généraux

Installez ces outils avant de commencer :

| Outil | Version minimale | Vérification |
|---|---|---|
| Java JDK | 21 | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Node.js | 22.22+ | `node -v` |
| npm | 10+ | `npm -v` |
| Docker | 20+ | `docker --version` |
| Docker Compose | v2 | `docker-compose --version` |
| Git | 2.30+ | `git --version` |

> Pour changer de version de Node, utilisez **nvm** :
> ```bash
> nvm install 22
> nvm use 22
> ```

---

## 1. BACKEND — Spring Boot

**Dépôt** : https://github.com/gestion-de-stock-v2/backend

### 1.1 Cloner

```bash
git clone git@github.com:gestion-de-stock-v2/backend.git
cd backend
```

### 1.2 Structure

```
backend/
├── ms-stock-management/
│   ├── docker-compose.yml      ← Infrastructure (Postgres, Kafka, ...)
│   ├── standalone/             ← API REST unique (port 8080)
│   └── services/               ← Microservices
│       ├── config-server/      ← 8888
│       ├── discovery/          ← 8761 (Eureka)
│       ├── gateway/            ← 8222
│       ├── customer/           ← 8090 (MongoDB)
│       ├── product/            ← 8050 (PostgreSQL)
│       ├── order/              ← 8070
│       ├── payment/            ← 8060
│       └── notification/       ← 8040
├── start-tout.sh               ← Script de démarrage complet
└── stop-tout.sh                ← Script d'arrêt
```

### 1.3 Lancer tout le backend (recommandé)

```bash
cd ~/backend
./start-tout.sh
```

Ce script :
1. Démarre Docker (Postgres 5433, MongoDB 27017, Kafka 9092, Zipkin 9411, pgAdmin 5050)
2. Démarre le backend standalone sur **8080**
3. Démarre le Config Server sur **8888**
4. Démarre Discovery (Eureka) sur **8761**
5. Démarre les 6 microservices

À la fin, vous verrez :

```
📊 Vérification des ports :
   ✅ 8080 UP
   ✅ 8050 UP
   ✅ 8060 UP
   ✅ 8070 UP
   ✅ 8090 UP
   ✅ 8222 UP
   ✅ 8761 UP
   ✅ 8888 UP
```

### 1.4 Arrêter

```bash
./stop-tout.sh
```

### 1.5 Vérifier que tout tourne

```bash
# Backend standalone (API REST)
curl http://localhost:8080/api/categorias

# Gateway (microservices)
curl http://localhost:8222/actuator/health

# Eureka (services enregistrés)
curl http://localhost:8761/actuator/health

# Config Server
curl http://localhost:8888/actuator/health
```

### 1.6 Comptes de test

| Utilisateur | Mot de passe | Rôle |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `gerant` | `gerant123` | GERANT |
| `vendeur` | `vendeur123` | VENDEUR |
| `observateur` | `observateur123` | OBSERVATEUR |

### 1.7 Voir les logs

```bash
# Tous les logs en direct
tail -f logs/*.log

# Un seul service
tail -50 logs/product.log
```

### 1.8 Lancer un service seul (dev)

```bash
# Backend standalone
cd ms-stock-management/standalone
mvn spring-boot:run

# Un microservice
cd ms-stock-management/services/product
mvn spring-boot:run
```

---

## 2. FRONTEND ANGULAR

**Dépôt** : https://github.com/gestion-de-stock-v2/frontend-angular

### 2.1 Cloner

```bash
git clone git@github.com:gestion-de-stock-v2/frontend-angular.git
cd frontend-angular
```

### 2.2 Installer les dépendances

```bash
npm install
```

### 2.3 Lancer le serveur de dev

```bash
ng serve
```

Attendu :

```
Application bundle generation complete.
➜  Local:   http://localhost:4200/
```

Ouvre **http://localhost:4200** dans le navigateur.

### 2.4 Build de production

```bash
ng build --configuration production
```

Résultat dans `dist/`.

### 2.5 Configuration

Le fichier `proxy.conf.json` redirige `/api` vers le backend :

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

**⚠️ Le backend doit être lancé avant le frontend.**

---

## 3. FRONTEND REACT

**Dépôt** : https://github.com/gestion-de-stock-v2/react-frontend

### 3.1 Cloner

```bash
git clone git@github.com:gestion-de-stock-v2/react-frontend.git
cd react-frontend
```

### 3.2 Installer les dépendances

Avec **npm** :

```bash
npm install
```

Ou avec **yarn** :

```bash
yarn install
```

### 3.3 Lancer le serveur de dev

```bash
npm start
```

Ou :

```bash
npm run dev
```

Attendu :

```
VITE v5.x.x  ready in 500 ms
➜  Local:   http://localhost:3000/
```

Ouvre **http://localhost:3000** (ou `http://localhost:5173` si Vite).

### 3.4 Build de production

```bash
npm run build
```

Résultat dans `dist/` ou `build/`.

### 3.5 Configuration

Le fichier `.env` (à créer si absent) contient l'URL du backend :

```env
VITE_API_URL=http://localhost:8080
```

Ou pour Create React App :

```env
REACT_APP_API_URL=http://localhost:8080
```

---

## 4. Lancement complet (les 3 en parallèle)

### Terminal 1 — Backend

```bash
cd ~/backend
./start-tout.sh
```

### Terminal 2 — Angular

```bash
cd ~/frontend-angular
ng serve
```

### Terminal 3 — React

```bash
cd ~/react-frontend
npm start
```

### URLs

| Application | URL |
|---|---|
| **Angular** | http://localhost:4200 |
| **React** | http://localhost:3000 |
| **Backend standalone** | http://localhost:8080 |
| **Gateway microservices** | http://localhost:8222 |
| **Eureka** | http://localhost:8761 |
| **Config Server** | http://localhost:8888 |
| **pgAdmin** | http://localhost:5050 |
| **Zipkin** | http://localhost:9411 |
| **MailDev** | http://localhost:1080 |

---

## 5. Dépannage

### Port déjà utilisé

```bash
sudo lsof -i :8080
sudo kill -9 <PID>
```

### Backend ne démarre pas

```bash
# Vérifier Java
java -version    # doit être 21+

# Vérifier Docker
docker ps        # doit afficher les conteneurs

# Voir le log
tail -50 logs/backend-8080.log
```

### Erreur "Module not found" (Angular/React)

```bash
rm -rf node_modules package-lock.json
npm install
```

### Erreur CORS

Vérifie que le backend tourne bien sur `http://localhost:8080`.

### Eureka vide

Attends 30 secondes après le démarrage — les microservices s'enregistrent progressivement.

---

## 6. Arrêt

### Backend

```bash
cd ~/backend
./stop-tout.sh
```

### Angular / React

`Ctrl+C` dans leur terminal respectif.

---

## 7. Documentation API

Le fichier `endpoint.md` à la racine du backend liste **tous les endpoints** avec exemples.

---

## 8. Auteurs

| Nom | Rôle |
|---|---|
| **Franck** | Endpoints backend de base |
| **Guilbert Ange** | Complétion backend + Auth JWT + Frontend Angular |
| **Landry** | Frontend React |
| **Mr Clément** | Review + tests |

---

**Dernière mise à jour** : Septembre 2026
```

---

Pour l'enregistrer :

```bash
cd ~/"gestion de stock"
nano README.md
```

Colle le contenu, puis :
- **Ctrl+O** pour sauvegarder
- **Entrée** pour confirmer
- **Ctrl+X** pour quitter

Ou en une commande (colle tout le contenu entre `<< 'EOF'` et `EOF`) :

```bash
cat > ~/"gestion de stock/README.md" << 'EOF'
...contenu...
EOF
```

Puis pousse-le :

```bash
cd ~/"gestion de stock/backend"
git add ../README.md
git commit -m "Ajout README avec guide de démarrage"
git push origin main
```

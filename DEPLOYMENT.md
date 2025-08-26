# 🚀 Guide de Déploiement Cloud

## 📋 Pré-requis
- [ ] Compte GitHub avec le repository
- [ ] Compte Railway (backend)
- [ ] Compte Vercel (frontend)

## 🚂 Déploiement Backend sur Railway

### 1. Connexion et Configuration
1. Aller sur [railway.app](https://railway.app)
2. Se connecter avec GitHub
3. Cliquer sur "New Project" → "Deploy from GitHub repo"
4. Sélectionner votre repository `liens-court-java`

### 2. Configuration du Service Backend
1. Railway détectera automatiquement le projet Java
2. Définir le **Root Directory** : `backend`
3. Ajouter les variables d'environnement :

```bash
# Variables d'environnement Railway
PORT=8080
SPRING_PROFILES_ACTIVE=prod

# Base de données (Railway MySQL)
DATABASE_URL=mysql://user:pass@host:port/dbname
# ⚠️ Railway fournira automatiquement cette URL après ajout de MySQL

# CORS et URLs
FRONTEND_URL=https://your-app.vercel.app
BACKEND_URL=https://your-backend.railway.app

# Application
APP_BASE_URL=https://your-backend.railway.app
```

### 3. Ajouter MySQL Database
1. Dans votre projet Railway, cliquer sur "Add Service"
2. Choisir "MySQL"
3. Railway connectera automatiquement la base de données

### 4. Déployer
1. Railway déploiera automatiquement
2. Obtenir l'URL du backend : `https://your-backend.railway.app`

## ⚡ Déploiement Frontend sur Vercel

### 1. Connexion et Import
1. Aller sur [vercel.com](https://vercel.com)
2. Se connecter avec GitHub
3. Cliquer sur "New Project"
4. Importer votre repository `liens-court-java`

### 2. Configuration Build
```bash
# Build Settings
Framework Preset: Vite
Root Directory: frontend
Build Command: npm run build
Output Directory: dist
Install Command: npm install
```

### 3. Variables d'Environnement
```bash
# Variables d'environnement Vercel
VITE_API_BASE_URL=https://your-backend.railway.app/api
```

### 4. Déployer
1. Vercel déploiera automatiquement
2. Obtenir l'URL du frontend : `https://your-app.vercel.app`

## 🔄 Configuration CORS Final

### Mettre à jour Railway (Backend)
Une fois les deux URL obtenues, mettre à jour les variables Railway :

```bash
FRONTEND_URL=https://your-app.vercel.app
BACKEND_URL=https://your-backend.railway.app
```

## 🌐 Domaines Personnalisés (Optionnel)

### Railway
1. Dans Settings → Domains
2. Ajouter un domaine personnalisé : `api.votre-domaine.com`

### Vercel
1. Dans Settings → Domains
2. Ajouter un domaine personnalisé : `votre-domaine.com`

## 🔍 Vérification du Déploiement

### Backend (Railway)
- [ ] Health Check : `https://your-backend.railway.app/actuator/health`
- [ ] API Test : `https://your-backend.railway.app/api/dashboard/stats`
- [ ] CORS OK : Test depuis le frontend

### Frontend (Vercel)  
- [ ] Page d'accueil accessible
- [ ] Dashboard fonctionne
- [ ] Analytics charge les données
- [ ] Création de liens fonctionne

## 🔧 Commandes Utiles

### Railway CLI (Optionnel)
```bash
# Installer Railway CLI
npm install -g @railway/cli

# Login et déployer
railway login
railway link
railway up
```

### Vercel CLI (Optionnel)
```bash
# Installer Vercel CLI
npm install -g vercel

# Déployer
cd frontend
vercel --prod
```

## 🐛 Résolution de Problèmes

### Backend ne démarre pas
- Vérifier les logs Railway
- Vérifier `DATABASE_URL` 
- Vérifier `PORT` dynamique

### Frontend ne se connecte pas au Backend  
- Vérifier `VITE_API_BASE_URL`
- Vérifier CORS sur Railway
- Tester les endpoints avec curl

### Erreurs de Base de Données
- Vérifier la connexion MySQL sur Railway
- Vérifier les tables créées automatiquement
- Redémarrer le service si nécessaire

## 🎉 Félicitations !
Votre application est maintenant déployée sur Railway + Vercel !

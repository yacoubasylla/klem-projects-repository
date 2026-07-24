# 🏛️ Architecture Technique - Site KLEM

## 🛠️ Stack Technique
- **CMS :** WordPress (Version LTS isolée via Composer).
- **Structure :** Type *Bedrock* permettant de sortir `wp-content` de la racine du Core WordPress pour sécuriser l'application.
- **Base de Données :** MySQL 8.0.
- **Moteur de Thème :** Thème PHP Blank propriétaire, interfacé avec **Vite** et **Tailwind CSS**.
- **Environnement local :** Docker (Conteneurs isolés pour PHP-FPM, Nginx et MySQL).

## 🚀 Stratégie de Déploiement (Pipeline CI/CD)
1. **Validation Git :** Les secrets (mots de passe BD, clés d'API) sont exclus via le fichier `.env` non versionné.
2. **Pipeline GitHub Actions :** À chaque push sur `main`, un workflow valide la syntaxe PHP et compile les assets de production (CSS/JS minifiés).
3. **Livraison :** Les fichiers compilés et le code PHP propre sont poussés vers le serveur de production sécurisé, garantissant une interruption de service de 0 seconde.
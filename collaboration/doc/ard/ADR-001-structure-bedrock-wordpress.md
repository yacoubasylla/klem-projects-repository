# ADR-001 — Structure Bedrock WordPress

**Date :** 2026-06-24  
**Statut :** Accepté  
**Décideur :** Équipe KLEM / Claude Code

---

## Contexte

Le projet nécessite une installation WordPress robuste et sécurisée en environnement Docker. L'installation standard de WordPress mélange le core, les thèmes et les plugins dans un seul arbre, ce qui complique le versionning et la sécurité.

## Décision

Adopter la structure **Bedrock-style** :

```
web/              ← document root Apache (monté comme /var/www/html)
├── index.php     ← point d'entrée unique (redirige vers wp/)
├── wp/           ← WordPress core installé par Composer (johnpbloch/wordpress)
├── app/          ← remplace wp-content/ (thèmes, plugins, mu-plugins)
│   └── themes/klem-theme/
└── wp-config.php ← config DB + redéfinition des chemins Bedrock
```

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Installation WordPress standard | `wp-content/` dans la racine du core = difficile à versionner proprement |
| WordPress headless (REST/API) | Complexité inutile pour un site vitrine ; pas de frontend découplé prévu à court terme |

## Conséquences

- ✅ `web/wp/` est géré par Composer → mise à jour du core sans toucher au code métier
- ✅ `web/app/` est intégralement versionné dans Git (thème, mu-plugins)
- ✅ Le document root Apache expose `web/` et non `web/wp/` → le core n'est pas directement accessible
- ⚠️ Nécessite de redéfinir `WP_CONTENT_DIR` et `WP_CONTENT_URL` dans `wp-config.php`

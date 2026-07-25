# infrastructure/docker/

Réservé aux Dockerfiles et overlays Compose par service/application à mesure que le monorepo
grandit au-delà d'un unique `docker-compose.yml` racine.

L'actuel `docker-compose.yml` racine (stack de dev locale) reste exactement où il est pour
l'instant — `pnpm dev` et la documentation d'onboarding référencent ce chemin. À migrer ici
uniquement une fois qu'il y a suffisamment de services indépendants pour qu'un unique fichier
compose à plat devienne ingérable.

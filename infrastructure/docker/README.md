# infrastructure/docker/

Reserved for per-service/per-app Dockerfiles and Compose overlays as the monorepo grows past a
single root `docker-compose.yml`.

The current root-level `docker-compose.yml` (local dev stack) stays exactly where it is for now —
`pnpm dev` and onboarding docs reference that path. Migrate it here only once there are enough
independent services that a single flat compose file becomes unwieldy.

# Publish LIAS through GitHub and Vercel

The repository root is this directory (`JEE_Project`). The React application runs on Vercel; the Java 21 Spring Boot service runs on the existing backend host with PostgreSQL and a persistent upload volume.

## Backend host

Build the Dockerfile in `lias-backend`, or run `./mvnw package` with Java 21 and start `java -jar target/lias-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=production`.

Required environment variables:

| Variable | Value |
| --- | --- |
| DB_URL | PostgreSQL JDBC URL, including TLS options required by the provider |
| DB_USERNAME / DB_PASSWORD | Database credentials, configured only in the host secret settings |
| APP_JWT_SECRET | Random secret of at least 32 bytes; use 64+ random characters |
| CORS_ALLOWED_ORIGINS | Exact frontend origin, e.g. `https://your-project.vercel.app` |
| UPLOAD_DIR | Persistent writable directory for CVs, documents, photos, PVs and generated reports |
| PORT | Port assigned by the host (defaults to 8080) |
| LAB_CREATION_DATE | Actual laboratory creation date; left unset rather than inventing a date |
| MAIL_ENABLED | `true` only after configuring MAIL_HOST, MAIL_PORT and SMTP credentials |
| SEED_DEMO_DATA | `false` in production; `true` temporarily for initial bootstrap only |
| SEED_PASSWORD | Strong unique initial password when bootstrapping accounts |

The initial bootstrap creates `admin@lias.ma` and `director@lias.ma` (plus member/doctoral development accounts). Disable seeding after the initial startup. Replace bootstrap credentials and remove development accounts from active use before sharing the site. The director requires an active mandate, which is seeded during bootstrap. Never commit passwords or environment files.

Back up both PostgreSQL and the upload directory. Revision history is stored in PostgreSQL audit tables. Existing installations receive new columns and audit tables through Hibernate schema update; back up the database before upgrading. Uploads require persistent storage: ephemeral host disks lose files across deployments.

Health check: `GET /api/health`. Annual PDFs are generated January 1 at 02:00 UTC by the running backend; keep the service running for that schedule. Manual annual/monthly generation is available to direction and administration.

## GitHub and Vercel

1. Push this directory to the selected GitHub repository. `.gitignore` excludes dependencies, uploads, builds and local secrets.
2. Import the repository into Vercel. Keep repository root as the project root; `vercel.json` supplies installation, build, output and SPA routing settings.
3. Set `VITE_API_URL` to the backend's public HTTPS origin, without a trailing `/api` (example: `https://backend.example.com`). Set it before the frontend build.
4. Set `CORS_ALLOWED_ORIGINS` on the backend to the final Vercel production URL and redeploy/restart the backend.
5. Verify public teams/events/publications, sign in, submit an application, approve it as director, upload/download a file, and generate an annual report.

Vercel preview deployments use different origins; explicitly add trusted preview origins to backend CORS if previews need API access.

## Local verification

- Backend: Java 21, `cd lias-backend && ./mvnw test`. Workflow tests use an isolated H2 database, never the production database.
- Frontend: `cd lias-frontend && npm ci && npm run build`.
- GitHub Actions runs both checks on pushes and pull requests.

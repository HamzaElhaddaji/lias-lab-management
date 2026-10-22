# LIAS laboratory management

Java 21 / Spring Boot 4 / PostgreSQL backend, with a React and TypeScript frontend. The frontend reads and writes the backend database; there is no demo-data fallback.

## Run locally

Create PostgreSQL database `lias_db`, user `lias_user`, and grant the user access to the database and its public schema. Supply database values through `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` when they differ from the local defaults.

```sh
cd lias-backend
./mvnw spring-boot:run
```

Windows: use `mvnw.cmd`. Java 21 is required. The API listens on port 8080.

```sh
cd lias-frontend
npm ci
npm run dev
```

Open the URL printed by Vite (normally port 5173). In development, Vite proxies `/api` to the backend. To use an external API, set `VITE_API_URL` before starting/building the frontend.

Local development seeds accounts `admin@lias.ma`, `director@lias.ma`, `member@lias.ma`, and `doctorant@lias.ma`. Default local password: `FriendDemo2026!`. The director account is backed by an actual dated mandate. Production disables demo seeding and requires environment secrets; see [DEPLOYMENT.md](DEPLOYMENT.md).

Set `SEED_SAMPLE_DATA=true` with development seeding enabled to add a fictional demonstration dataset: 10 additional members, 16 publications, 8 events, downloadable sample documents and minutes, equipment and assignments, collaborations, discussions, applications and notifications. This is enabled automatically in the local preview profile and skipped on subsequent starts when already seeded. Sample emails use `example.test`; these records are fictional. Production leaves sample data disabled unless explicitly enabled.

## Workflows

- Public laboratory presentation, teams, events and publications without sign-in.
- Online membership applications with profile, motivation and PDF CV; only the director with an active mandate can approve/reject. Approval creates the account.
- Member profiles, photo upload, private birth date, affiliation periods and departure/retirement handling.
- Dated director, vice-director and team responsibilities. Access is recalculated from active mandates on each authenticated request.
- Doctoral accounts can add/consult publications and access their personal profile/notifications. Internal modules are blocked server-side.
- Associated members can consult events/publications; direction can explicitly grant publication contribution rights.
- Events, editions and organizer assignment; publications searchable by author/year/team.
- Document uploads/downloads with event association; meetings, agendas and PDF minutes.
- Equipment arrivals, stock, requests, direction decisions, assignments, returns and members who have never received equipment.
- Partner agreements, linked archive documents and associated collaboration activities.
- Laboratory, team and event discussion channels; team channels enforce membership.
- Search across accessible modules, monthly calendar navigation and personal notifications.
- Annual/monthly PDF activity reports; annual generation scheduled for January 1, 02:00 UTC while the backend is running.
- Revision history with actor, timestamp and previous values. Archiving retains database records and uploaded files.

## Verify

```sh
cd lias-backend
./mvnw verify
```

Nine tests run against isolated H2 databases, including HTTP workflows for access restrictions, admissions, revision history, files, equipment, discussions, collaboration, reports and access revocation. These tests do not touch the PostgreSQL database.

```sh
cd lias-frontend
npm run build
```

GitHub Actions runs both checks. `vercel.json` builds the frontend from the repository root and supports client-side routes. The backend includes a production Dockerfile. Deploying the Java service, configuring persistent file storage and connecting the real PostgreSQL database are separate from hosting the frontend on Vercel.

Set the actual laboratory creation date with `LAB_CREATION_DATE`. Email delivery requires SMTP configuration and `MAIL_ENABLED=true`; in-app notifications work without SMTP.

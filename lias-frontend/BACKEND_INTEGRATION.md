# Backend integration

The active application uses `src/services/http.ts` and React Query. All module records come from the backend. Failed requests show an error instead of substituting sample records.

Development uses Vite's `/api` proxy to `http://localhost:8080`. For Vercel, set `VITE_API_URL` to the backend HTTPS origin before building. Add the Vercel origin to the backend `CORS_ALLOWED_ORIGINS`.

- Public content: `/api/public/laboratory`, `/api/public/teams`, `/api/public/events`, `/api/public/publications`.
- Membership application: multipart `POST /api/public/applications` with email, password, firstName, lastName, establishment, originLaboratory, memberStatus, team, motivation and PDF `cv`.
- Authentication: `POST /api/auth/login`; `GET /api/members/me` supplies the actual profile and effective roles. Legacy password-only registration is disabled.
- Applications: `/api/admissions`; decisions at `PATCH /api/admissions/{id}/decision`; PDF CV at `GET /api/admissions/{id}/cv`. Only active direction may use these routes.
- Profiles: `/api/members`, `/api/members/{id}`, `/api/members/me`; photo at multipart `POST /api/members/me/photo`.
- Affiliations: `/api/affiliations/me`, `/api/affiliations/member/{memberId}`, `/api/affiliations/member/{memberId}/close`.
- Events, publications, documents, equipment, meetings, mandates and collaborations use their matching `/api/{module}` routes.
- Documents upload with multipart `POST /api/documents`, including `file`, optional description/type/eventId. Download at `/api/documents/{id}/download`.
- Meeting minutes: multipart `POST /api/meetings/{id}/pv` with `file`; download at `/api/meetings/{id}/pv/download`.
- Equipment requests: POST `/api/equipment/requests`; current member's list `/api/equipment/requests/my`; direction decisions `/api/equipment/requests/{id}/validate`.
- Equipment distribution: `/api/equipment/assignments`, `/api/equipment/assignments/{id}/return`, `/api/equipment/assignments/no-equipment`.
- Discussions: `/api/discussions`, with optional `team` and `eventId`. Team membership is enforced on reads and writes.
- Notifications: `/api/notifications`, `/api/notifications/{id}/read`.
- PDF reports: `/api/report/annual?year=2026`, `/api/report/monthly?year=2026&month=10`.
- Revision history: `/api/history/{kind}/{id}` for direction/administration.

Downloads include the current bearer token. Role-based navigation improves usability; the backend independently enforces access on every request. Frontend environment variables must never contain database credentials, JWT signing secrets or SMTP passwords.

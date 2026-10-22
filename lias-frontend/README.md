# LIAS Frontend

A modern React + TypeScript frontend for the Laboratory Information and Administration System.

## Features

- **Authentication**: Login/Register with JWT token management
- **Dashboard**: Overview of laboratory activities
- **Members Management**: View and manage laboratory members
- **Events**: Create and manage laboratory events
- **Publications**: Manage research publications
- **Documents**: Upload and organize documents
- **Equipment**: Track and manage laboratory equipment
- **Meetings**: Schedule and document meetings
- **Governance**: Manage mandates and governance structure

## Tech Stack

- **React 18** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool
- **Tailwind CSS** - Styling
- **Shadcn/UI** - UI components
- **React Router** - Client-side routing
- **TanStack Query** - Server state management
- **Zustand** - Client state management
- **Axios** - HTTP client

## Getting Started

### Prerequisites

- Node.js 16+ or npm 8+

### Installation

1. Install dependencies:

```bash
npm install
```

2. Create a `.env` file:

```
VITE_API_URL=http://localhost:8080
```

3. Start the development server:

```bash
npm run dev
```

The application will be available at `http://localhost:5173`

## Project Structure

```
src/
├── components/        # Reusable components
│   ├── ui/           # UI components (Button, Card, etc.)
│   └── Layout.tsx    # Main layout component
├── pages/            # Page components
│   ├── auth/         # Authentication pages
│   ├── dashboard/    # Dashboard page
│   ├── members/      # Members page
│   ├── events/       # Events page
│   ├── publications/ # Publications page
│   ├── documents/    # Documents page
│   ├── equipment/    # Equipment page
│   ├── meetings/     # Meetings page
│   └── governance/   # Governance page
├── services/         # API services
│   ├── apiClient.ts  # Axios configuration
│   ├── authService.ts
│   ├── memberService.ts
│   └── eventService.ts
├── store/            # Zustand stores
│   └── authStore.ts  # Authentication state
├── hooks/            # Custom hooks
│   └── useAuth.ts    # Authentication hook
├── types/            # TypeScript types
│   └── index.ts      # Type definitions
├── lib/              # Utilities
│   └── utils.ts      # Utility functions
├── App.tsx           # Main app component
├── main.tsx          # Entry point
└── index.css         # Global styles
```

## Configuration

### API URL

Update the API URL in your `.env` file or modify the proxy in `vite.config.ts`:

```ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
},
```

## Building for Production

```bash
npm run build
```

The production build will be created in the `dist/` directory.

## Development

### Run Development Server

```bash
npm run dev
```

### Lint Code

```bash
npm run lint
```

### Type Check

```bash
npx tsc --noEmit
```

## Backend Integration

The frontend is configured to communicate with the Spring Boot backend at `http://localhost:8080`. Make sure:

1. The backend is running on port 8080
2. CORS is properly configured in the backend
3. Authentication endpoints are available at:
   - `POST /api/auth/login`
   - `POST /api/auth/register`

## Adding New Pages

To add a new page:

1. Create a new directory in `src/pages/`
2. Create a component file (e.g., `YourPage.tsx`)
3. Add the route to `src/App.tsx`
4. Update the navigation in `src/components/Layout.tsx`

## Adding New API Services

Create a new service file in `src/services/` following the pattern:

```ts
import { apiClient } from './apiClient';
import { YourType } from '@/types';

export const yourService = {
  async getAll(): Promise<YourType[]> {
    const response = await apiClient.get('/api/your-endpoint');
    return response.data;
  },
  // ... more methods
};
```

## License

This project is part of the LIAS system.

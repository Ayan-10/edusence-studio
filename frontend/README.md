# Edusence Studio Frontend

React + Vite + Tailwind CSS frontend for the Edusence Studio Adaptive Training Planner.

## Setup

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

The frontend will be available at `http://localhost:5173`

## Features

### Teacher Dashboard
- View active assessments
- Take assessments and submit responses
- View assigned micro-modules
- Track progress and analytics

### DIET Professional Dashboard
- Analytics dashboard with charts
- Group management
- Module upload and AI-powered splitting
- Feedback cycle management
- Assign modules to groups or individual teachers

## Tech Stack

- React 18
- Vite
- Tailwind CSS
- React Router
- Axios
- Recharts (for analytics charts)
- Lucide React (icons)

## API Configuration

The frontend is configured to connect to the backend at `http://localhost:8080/api/v1`. Update this in `src/services/api.js` if your backend runs on a different port.

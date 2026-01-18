# Edusence Studio - Adaptive Training Planner

A comprehensive solution for DIETs (District Institute of Education and Training) to create personalized, data-driven teacher training programs.

## Problem Statement

DIETs and SCERTs lack the institutional agility to continuously update, contextualize, and personalize teacher training modules, resulting in static "one-size-fits-all" programs that fail to address the diverse, evolving needs of teachers in the field.

## Solution Overview

Edusence Studio provides:
- **Data-Driven Planning**: Real-time analytics from teacher assessments
- **Smart Customization**: AI-powered module splitting and language adaptation
- **Personalized Training**: Assign micro-modules to groups or individual teachers based on problem areas
- **Quick Updates**: Rapid feedback cycles and module updates

## Architecture

### Backend
- **Framework**: Spring Boot (Java 17)
- **Database**: PostgreSQL
- **Storage**: AWS S3 (for module PDFs)
- **API**: RESTful APIs with JWT authentication (placeholder)

### Frontend
- **Framework**: React 18 with Vite
- **Styling**: Tailwind CSS
- **Charts**: Recharts
- **Routing**: React Router

## Getting Started

### One-Command Docker Setup (Recommended for Hackathon)

Prerequisite: Docker + Docker Compose (install Docker Desktop)

```bash
docker compose up --build
```

That is the only command needed. No Java, Node, or other tools are required.

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080/api/v1`

#### S3 Uploads (Optional)
The backend requires AWS S3 config values at startup. In `docker-compose.yml`, update these:

- `AWS_S3_REGION`
- `AWS_S3_BUCKET`
- `AWS_S3_ACCESS_KEY`
- `AWS_S3_SECRET_KEY`

If you don't need PDF uploads, the app will still run, but S3 upload calls will fail until valid credentials are set.

### Local Development (Optional)

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Access the application at `http://localhost:5173`

Backend (local):
```bash
./gradlew bootRun
```

## Key Features

### For Teachers
- Take assessments and provide feedback
- Access assigned micro-modules
- View progress and analytics
- Filter modules by language

### For DIET Professionals
- Upload 50-page training modules
- AI-powered module splitting into micro-modules
- Create groups based on problem tags
- Assign modules to groups or individual teachers
- View comprehensive analytics dashboard
- Manage feedback cycles

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration

### Modules
- `GET /api/v1/modules` - List all main modules
- `POST /api/v1/modules/upload` - Upload main module
- `POST /api/v1/modules/{id}/split-ai` - Split module with AI
- `GET /api/v1/modules/{id}/micro-modules` - Get micro-modules

### Analytics
- `GET /api/v1/analytics/overview` - Overview analytics
- `GET /api/v1/analytics/teachers/{id}` - Teacher analytics
- `GET /api/v1/analytics/groups/{id}` - Group analytics
- `GET /api/v1/analytics/problems` - Problem tag analytics

### Groups
- `GET /api/v1/groups` - List all groups
- `POST /api/v1/groups` - Create group
- `POST /api/v1/groups/{id}/teachers/{teacherId}` - Add teacher to group

### Assessments
- `GET /api/v1/assessments` - List all assessments
- `GET /api/v1/assessments/{id}/questions` - Get assessment questions
- `POST /api/v1/assessment-responses` - Submit assessment response

## User Roles

- **TEACHER**: Can take assessments, view assigned modules, track progress
- **TRAINING_PROFESSIONAL**: Can manage modules, groups, analytics, and feedback cycles
- **ADMIN**: Full system access

## Notes

- Authentication uses placeholder JWT tokens (for demo purposes)
- AI module splitting is mocked (generates 7 micro-modules with common problem tags)
- Analytics aggregates data from assessment responses

## Future Enhancements

- Real AI integration for module splitting
- Full JWT authentication with refresh tokens
- Real-time notifications
- Advanced analytics with machine learning
- Multi-language support for UI
- Mobile app version

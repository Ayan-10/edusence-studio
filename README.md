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

### One-Command Docker Setup 

Prerequisite: Docker + Docker Compose (Install Docker Desktop)

#### Step 1: Create Environment File

**IMPORTANT:** Before running the project, you must create a `.env` file in the root directory with your sensitive credentials. This file is gitignored and will not be committed to the repository.

1. Copy the example file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and add your actual values for the following variables:

   **Database Configuration:**
   - `POSTGRES_DB` - PostgreSQL database name
   - `POSTGRES_USER` - PostgreSQL username
   - `POSTGRES_PASSWORD` - PostgreSQL password

   **AWS S3 Configuration (required for PDF uploads):**
   - `AWS_S3_REGION` - AWS S3 region (e.g., eu-north-1, us-east-1)
   - `AWS_S3_BUCKET` - AWS S3 bucket name
   - `AWS_S3_ACCESS_KEY` - AWS access key ID
   - `AWS_S3_SECRET_KEY` - AWS secret access key

   **Google Gemini API (optional, for AI module splitting):**
   - `GEMINI_API_KEY` - Get your free API key from: https://makersuite.google.com/app/apikey
   
   **Note:** Without the Gemini API key, the AI splitting will use a fallback mock implementation. The free tier allows 15 requests/minute and 1,500 requests/day - perfect for hackathon demos!

#### Step 2: Run the Project

```bash
docker compose up --build
```

That is the only command needed. No Java, Node, or other tools are required.

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080/api/v1`

**Security Note:** All sensitive information (database credentials, AWS keys, API keys) must be stored in the `.env` file, which is automatically gitignored. Never commit the `.env` file to version control.

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

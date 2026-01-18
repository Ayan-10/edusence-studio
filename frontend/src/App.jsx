import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import TeacherDashboard from './pages/teacher/TeacherDashboard';
import TeacherAssessments from './pages/teacher/TeacherAssessments';
import TeacherModules from './pages/teacher/TeacherModules';
import TeacherProgress from './pages/teacher/TeacherProgress';
import ProfessionalDashboard from './pages/professional/ProfessionalDashboard';
import ProfessionalAnalytics from './pages/professional/ProfessionalAnalytics';
import ProfessionalGroups from './pages/professional/ProfessionalGroups';
import ProfessionalModules from './pages/professional/ProfessionalModules';
import ProfessionalFeedbackCycles from './pages/professional/ProfessionalFeedbackCycles';
import ProfessionalCourses from './pages/professional/ProfessionalCourses';
import ProfessionalAssessments from './pages/professional/ProfessionalAssessments';

const AppRoutes = () => {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      
      <Route
        path="/teacher/*"
        element={
          <ProtectedRoute allowedRoles={['TEACHER']}>
            <Routes>
              <Route path="dashboard" element={<TeacherDashboard />} />
              <Route path="assessments" element={<TeacherAssessments />} />
              <Route path="modules" element={<TeacherModules />} />
              <Route path="progress" element={<TeacherProgress />} />
            </Routes>
          </ProtectedRoute>
        }
      />

      <Route
        path="/professional/*"
        element={
          <ProtectedRoute allowedRoles={['TRAINING_PROFESSIONAL', 'ADMIN']}>
            <Routes>
              <Route path="dashboard" element={<ProfessionalDashboard />} />
              <Route path="analytics" element={<ProfessionalAnalytics />} />
              <Route path="groups" element={<ProfessionalGroups />} />
              <Route path="modules" element={<ProfessionalModules />} />
              <Route path="courses" element={<ProfessionalCourses />} />
              <Route path="feedback-cycles" element={<ProfessionalFeedbackCycles />} />
              <Route path="assessments" element={<ProfessionalAssessments />} />
            </Routes>
          </ProtectedRoute>
        }
      />

      <Route
        path="/"
        element={
          user ? (
            user.role === 'TEACHER' ? (
              <Navigate to="/teacher/dashboard" replace />
            ) : (
              <Navigate to="/professional/dashboard" replace />
            )
          ) : (
            <Navigate to="/login" replace />
          )
        }
      />
    </Routes>
  );
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;

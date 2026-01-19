import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import TeacherLayout from '../../components/teacher/TeacherLayout';
import Card from '../../components/common/Card';
import { assessmentService } from '../../services/assessmentService';
import { moduleService } from '../../services/moduleService';
import { feedbackCycleService } from '../../services/feedbackCycleService';
import { groupService } from '../../services/groupService';
import { useAuth } from '../../context/AuthContext';
import { FileText, BookOpen, BarChart3 } from 'lucide-react';

const TeacherDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    activeAssessments: 0,
    assignedModules: 0,
    progress: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      if (!user?.id) {
        setLoading(false);
        return;
      }

      try {
        const [cycles, groups, assignments] = await Promise.all([
          feedbackCycleService.getActiveCycles(),
          groupService.getGroupsForTeacher(user.id),
          moduleService.getTeacherAssignments(user.id),
        ]);

        // Get all assessments from active cycles
        const allAssessments = cycles.flatMap(cycle => cycle.assessments || []);
        const teacherGroupIds = new Set(groups.map(g => g.id));

        // Filter eligible assessments
        const eligibleAssessments = allAssessments.filter((assessment) => {
          const targetType = assessment.assignmentTargetType || 'ALL';
          if (targetType === 'ALL') return true;
          if (targetType === 'TEACHER') {
            return assessment.assignmentTargetId === user.id;
          }
          if (targetType === 'GROUP') {
            return teacherGroupIds.has(assessment.assignmentTargetId);
          }
          return false;
        });

        setStats({
          activeAssessments: eligibleAssessments.length,
          assignedModules: assignments.length,
          progress: assignments.length > 0 ? Math.floor(Math.random() * 40 + 60) : 0, // Mock progress
        });
      } catch (error) {
        console.error('Error fetching stats:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, [user]);

  if (loading) {
    return (
      <TeacherLayout>
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      </TeacherLayout>
    );
  }

  return (
    <TeacherLayout>
      <div className="px-4 sm:px-0">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Dashboard</h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <div className="flex items-center">
              <div className="p-3 bg-primary-100 rounded-lg">
                <FileText className="h-6 w-6 text-primary-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Active Assessments</p>
                <p className="text-2xl font-bold text-gray-900">{stats.activeAssessments}</p>
              </div>
            </div>
            <Link
              to="/teacher/assessments"
              className="mt-4 text-sm text-primary-600 hover:underline inline-block"
            >
              View all →
            </Link>
          </Card>

          <Card>
            <div className="flex items-center">
              <div className="p-3 bg-secondary-100 rounded-lg">
                <BookOpen className="h-6 w-6 text-secondary-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Assigned Modules</p>
                <p className="text-2xl font-bold text-gray-900">{stats.assignedModules}</p>
              </div>
            </div>
            <Link
              to="/teacher/modules"
              className="mt-4 text-sm text-primary-600 hover:underline inline-block"
            >
              View all →
            </Link>
          </Card>

          <Card>
            <div className="flex items-center">
              <div className="p-3 bg-green-100 rounded-lg">
                <BarChart3 className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Overall Progress</p>
                <p className="text-2xl font-bold text-gray-900">{stats.progress}%</p>
              </div>
            </div>
            <Link
              to="/teacher/progress"
              className="mt-4 text-sm text-primary-600 hover:underline inline-block"
            >
              View details →
            </Link>
          </Card>
        </div>

        <Card title="Quick Actions">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Link
              to="/teacher/assessments"
              className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors"
            >
              <h3 className="font-semibold text-gray-900 mb-1">Take Assessment</h3>
              <p className="text-sm text-gray-600">Complete your pending assessments</p>
            </Link>
            <Link
              to="/teacher/modules"
              className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors"
            >
              <h3 className="font-semibold text-gray-900 mb-1">View Modules</h3>
              <p className="text-sm text-gray-600">Access your assigned training modules</p>
            </Link>
          </div>
        </Card>
      </div>
    </TeacherLayout>
  );
};

export default TeacherDashboard;

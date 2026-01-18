import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import { analyticsService } from '../../services/analyticsService';
import { groupService } from '../../services/groupService';
import { moduleService } from '../../services/moduleService';
import { Users, BookOpen, BarChart3, FileText } from 'lucide-react';

const ProfessionalDashboard = () => {
  const [stats, setStats] = useState({
    totalTeachers: 0,
    totalGroups: 0,
    totalModules: 0,
    totalResponses: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const [overview, groups, modules] = await Promise.all([
        analyticsService.getOverviewAnalytics(),
        groupService.getAllGroups().catch(() => []),
        moduleService.getAllModules().catch(() => []),
      ]);

      setStats({
        totalTeachers: overview.totalTeachers || 0,
        totalGroups: overview.totalGroups || groups.length || 0,
        totalModules: modules.length || 0,
        totalResponses: overview.totalResponses || 0,
      });
    } catch (error) {
      console.error('Error fetching stats:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <ProfessionalLayout>
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      </ProfessionalLayout>
    );
  }

  return (
    <ProfessionalLayout>
      <div className="px-4 sm:px-0">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Dashboard</h2>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card>
            <div className="flex items-center">
              <div className="p-3 bg-primary-100 rounded-lg">
                <Users className="h-6 w-6 text-primary-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Total Teachers</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalTeachers}</p>
              </div>
            </div>
          </Card>

          <Card>
            <div className="flex items-center">
              <div className="p-3 bg-secondary-100 rounded-lg">
                <Users className="h-6 w-6 text-secondary-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Active Groups</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalGroups}</p>
              </div>
            </div>
          </Card>

          <Card>
            <div className="flex items-center">
              <div className="p-3 bg-green-100 rounded-lg">
                <BookOpen className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Modules Uploaded</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalModules}</p>
              </div>
            </div>
          </Card>

          <Card>
            <div className="flex items-center">
              <div className="p-3 bg-purple-100 rounded-lg">
                <FileText className="h-6 w-6 text-purple-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Total Responses</p>
                <p className="text-2xl font-bold text-gray-900">{stats.totalResponses}</p>
              </div>
            </div>
          </Card>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card title="Quick Actions">
            <div className="space-y-3">
              <Link
                to="/professional/groups"
                className="block p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors"
              >
                <h3 className="font-semibold text-gray-900 mb-1">Create Group</h3>
                <p className="text-sm text-gray-600">Organize teachers by problem areas</p>
              </Link>
              <Link
                to="/professional/modules"
                className="block p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors"
              >
                <h3 className="font-semibold text-gray-900 mb-1">Upload Module</h3>
                <p className="text-sm text-gray-600">Add new training modules</p>
              </Link>
              <Link
                to="/professional/feedback-cycles"
                className="block p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors"
              >
                <h3 className="font-semibold text-gray-900 mb-1">Create Feedback Cycle</h3>
                <p className="text-sm text-gray-600">Set up new assessment cycles</p>
              </Link>
              <Link
                to="/professional/assessments"
                className="block p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors"
              >
                <h3 className="font-semibold text-gray-900 mb-1">Create Assessment</h3>
                <p className="text-sm text-gray-600">Build questions and assign to teachers</p>
              </Link>
              <Link
                to="/professional/courses"
                className="block p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors"
              >
                <h3 className="font-semibold text-gray-900 mb-1">Create Coursework</h3>
                <p className="text-sm text-gray-600">Bundle micro-modules and assign</p>
              </Link>
            </div>
          </Card>

          <Card title="Analytics Overview">
            <Link
              to="/professional/analytics"
              className="block p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-500 hover:bg-primary-50 transition-colors"
            >
              <div className="flex items-center">
                <BarChart3 className="h-8 w-8 text-primary-600 mr-3" />
                <div>
                  <h3 className="font-semibold text-gray-900 mb-1">View Analytics</h3>
                  <p className="text-sm text-gray-600">Explore detailed insights and reports</p>
                </div>
              </div>
            </Link>
          </Card>
        </div>
      </div>
    </ProfessionalLayout>
  );
};

export default ProfessionalDashboard;

import { useEffect, useState } from 'react';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import { analyticsService } from '../../services/analyticsService';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

const ProfessionalAnalytics = () => {
  const [overview, setOverview] = useState(null);
  const [problemTags, setProblemTags] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const [overviewData, problemTagsData] = await Promise.all([
        analyticsService.getOverviewAnalytics(),
        analyticsService.getProblemTagAnalytics(),
      ]);
      setOverview(overviewData);
      setProblemTags(problemTagsData);
    } catch (error) {
      console.error('Error fetching analytics:', error);
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

  const problemTagChartData = overview?.problemTagDistribution
    ? Object.entries(overview.problemTagDistribution).map(([tag, count]) => ({
        name: tag,
        value: count,
      }))
    : [];

  const clusterChartData = overview?.clusterDistribution
    ? Object.entries(overview.clusterDistribution).map(([cluster, count]) => ({
        name: cluster,
        teachers: count,
      }))
    : [];

  const COLORS = ['#0ea5e9', '#22c55e', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899'];

  return (
    <ProfessionalLayout>
      <div className="px-4 sm:px-0">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Analytics Dashboard</h2>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card>
            <p className="text-sm text-gray-600 mb-1">Total Teachers</p>
            <p className="text-3xl font-bold text-gray-900">{overview?.totalTeachers || 0}</p>
          </Card>
          <Card>
            <p className="text-sm text-gray-600 mb-1">Total Groups</p>
            <p className="text-3xl font-bold text-gray-900">{overview?.totalGroups || 0}</p>
          </Card>
          <Card>
            <p className="text-sm text-gray-600 mb-1">Total Responses</p>
            <p className="text-3xl font-bold text-gray-900">{overview?.totalResponses || 0}</p>
          </Card>
          <Card>
            <p className="text-sm text-gray-600 mb-1">Problem Tags</p>
            <p className="text-3xl font-bold text-gray-900">
              {overview?.problemTagDistribution
                ? Object.keys(overview.problemTagDistribution).length
                : 0}
            </p>
          </Card>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <Card title="Problem Tag Distribution">
            {problemTagChartData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={problemTagChartData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {problemTagChartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <p className="text-gray-600 text-center py-8">No data available</p>
            )}
          </Card>

          <Card title="Cluster Distribution">
            {clusterChartData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={clusterChartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="teachers" fill="#0ea5e9" />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <p className="text-gray-600 text-center py-8">No data available</p>
            )}
          </Card>
        </div>

        {overview?.topProblemTags && overview.topProblemTags.length > 0 && (
          <Card title="Top Problem Tags">
            <div className="space-y-3">
              {overview.topProblemTags.map((tag, index) => (
                <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-semibold text-gray-900">{tag.tag}</p>
                    <p className="text-sm text-gray-600">{tag.count} responses</p>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        )}
      </div>
    </ProfessionalLayout>
  );
};

export default ProfessionalAnalytics;

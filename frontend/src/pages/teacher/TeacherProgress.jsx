import { useEffect, useState } from 'react';
import TeacherLayout from '../../components/teacher/TeacherLayout';
import Card from '../../components/common/Card';
import { analyticsService } from '../../services/analyticsService';
import { useAuth } from '../../context/AuthContext';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const TeacherProgress = () => {
  const { user } = useAuth();
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user?.id) {
      fetchAnalytics();
    }
  }, [user]);

  const fetchAnalytics = async () => {
    try {
      const data = await analyticsService.getTeacherAnalytics(user.id);
      setAnalytics(data);
    } catch (error) {
      console.error('Error fetching analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <TeacherLayout>
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      </TeacherLayout>
    );
  }

  const problemTagData = analytics?.problemTagCounts
    ? Object.entries(analytics.problemTagCounts).map(([tag, count]) => ({
        tag,
        count,
      }))
    : [];

  return (
    <TeacherLayout>
      <div className="px-4 sm:px-0">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">My Progress</h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <p className="text-sm text-gray-600 mb-1">Total Responses</p>
            <p className="text-3xl font-bold text-gray-900">
              {analytics?.totalResponses || 0}
            </p>
          </Card>
          <Card>
            <p className="text-sm text-gray-600 mb-1">Average Score</p>
            <p className="text-3xl font-bold text-gray-900">
              {analytics?.averageNumericResponse
                ? analytics.averageNumericResponse.toFixed(1)
                : 'N/A'}
            </p>
          </Card>
          <Card>
            <p className="text-sm text-gray-600 mb-1">Cluster</p>
            <p className="text-3xl font-bold text-gray-900">
              {analytics?.clusterName || 'N/A'}
            </p>
          </Card>
        </div>

        {problemTagData.length > 0 && (
          <Card title="Problem Areas">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={problemTagData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="tag" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="count" fill="#0ea5e9" />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        )}

        {analytics?.responsesByProblemTag && (
          <Card title="Detailed Responses" className="mt-6">
            {Object.entries(analytics.responsesByProblemTag).map(([tag, responses]) => (
              <div key={tag} className="mb-6 pb-6 border-b last:border-b-0">
                <h3 className="font-semibold text-gray-900 mb-3">{tag}</h3>
                <div className="space-y-2">
                  {responses.slice(0, 3).map((response, idx) => (
                    <div key={idx} className="text-sm text-gray-600">
                      <p className="font-medium">{response.questionText}</p>
                      <p className="text-gray-500">
                        Response: {response.numericResponse || response.textResponse || 'N/A'}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </Card>
        )}
      </div>
    </TeacherLayout>
  );
};

export default TeacherProgress;

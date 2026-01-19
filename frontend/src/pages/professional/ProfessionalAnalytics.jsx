import { useEffect, useState } from 'react';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import { analyticsService } from '../../services/analyticsService';
import { groupService } from '../../services/groupService';
import { userService } from '../../services/userService';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
} from 'recharts';

const ProfessionalAnalytics = () => {
  const [overview, setOverview] = useState(null);
  const [topProblems, setTopProblems] = useState(null);
  const [groups, setGroups] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [clusters, setClusters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterType, setFilterType] = useState('all');
  const [filterValue, setFilterValue] = useState('');
  const [topN, setTopN] = useState(10);

  const languageOptions = ['EN', 'HI', 'BN', 'TA', 'TE', 'MR'];

  useEffect(() => {
    fetchInitialData();
  }, []);

  useEffect(() => {
    if (overview) {
      fetchTopProblems();
    }
  }, [filterType, filterValue, topN]);

  const fetchInitialData = async () => {
    try {
      const [overviewData, groupsData, usersData] = await Promise.all([
        analyticsService.getOverviewAnalytics(),
        groupService.getAllGroups().catch(() => []),
        userService.getAllUsers().catch(() => []),
      ]);
      setOverview(overviewData);
      setGroups(groupsData);
      setTeachers(usersData.filter((u) => u.role === 'TEACHER'));

      // Extract unique clusters from overview
      if (overviewData?.clusterDistribution) {
        setClusters(Object.keys(overviewData.clusterDistribution));
      }

      setLoading(false);
    } catch (error) {
      console.error('Error fetching analytics:', error);
      setLoading(false);
    }
  };

  const fetchTopProblems = async () => {
    try {
      const data = await analyticsService.getTopProblems(
        topN,
        filterType === 'all' ? null : filterType,
        filterValue || null
      );
      setTopProblems(data);
    } catch (error) {
      console.error('Error fetching top problems:', error);
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

  const topProblemsChartData = topProblems?.topProblems
    ? topProblems.topProblems.map((p) => ({
        problem: p.problemTag,
        count: p.count,
        avgScore: p.averageScore?.toFixed(1) || 0,
      }))
    : [];

  const COLORS = ['#0ea5e9', '#22c55e', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#14b8a6', '#f97316', '#a855f7', '#e11d48'];

  const getFilterLabel = () => {
    if (filterType === 'all') return 'Overall';
    if (filterType === 'cluster') return `Cluster: ${filterValue}`;
    if (filterType === 'group') {
      const group = groups.find((g) => g.id === filterValue);
      return `Group: ${group?.name || filterValue}`;
    }
    if (filterType === 'teacher') {
      const teacher = teachers.find((t) => t.id === filterValue);
      return `Teacher: ${teacher?.name || filterValue}`;
    }
    if (filterType === 'language') return `Language: ${filterValue}`;
    return 'Overall';
  };

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

        <Card title="Top Problems Analysis" className="mb-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter By
              </label>
              <select
                value={filterType}
                onChange={(e) => {
                  setFilterType(e.target.value);
                  setFilterValue('');
                }}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="all">Overall</option>
                <option value="cluster">By Cluster</option>
                <option value="group">By Group</option>
                <option value="teacher">By Teacher</option>
                <option value="language">By Language</option>
              </select>
            </div>

            {filterType !== 'all' && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  {filterType === 'cluster'
                    ? 'Cluster'
                    : filterType === 'group'
                    ? 'Group'
                    : filterType === 'teacher'
                    ? 'Teacher'
                    : 'Language'}
                </label>
                <select
                  value={filterValue}
                  onChange={(e) => setFilterValue(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  <option value="">Select...</option>
                  {filterType === 'cluster' &&
                    clusters.map((cluster) => (
                      <option key={cluster} value={cluster}>
                        {cluster}
                      </option>
                    ))}
                  {filterType === 'group' &&
                    groups.map((group) => (
                      <option key={group.id} value={group.id}>
                        {group.name}
                      </option>
                    ))}
                  {filterType === 'teacher' &&
                    teachers.map((teacher) => (
                      <option key={teacher.id} value={teacher.id}>
                        {teacher.name}
                      </option>
                    ))}
                  {filterType === 'language' &&
                    languageOptions.map((lang) => (
                      <option key={lang} value={lang}>
                        {lang}
                      </option>
                    ))}
                </select>
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Top N Problems
              </label>
              <select
                value={topN}
                onChange={(e) => setTopN(Number(e.target.value))}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value={5}>Top 5</option>
                <option value={10}>Top 10</option>
                <option value={15}>Top 15</option>
                <option value={20}>Top 20</option>
              </select>
            </div>

            <div className="flex items-end">
              <div className="w-full">
                <p className="text-sm text-gray-600 mb-1">Current View</p>
                <p className="text-sm font-semibold text-primary-600">{getFilterLabel()}</p>
              </div>
            </div>
          </div>

          {topProblemsChartData.length > 0 ? (
            <div className="mb-6">
              <ResponsiveContainer width="100%" height={400}>
                <BarChart data={topProblemsChartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis
                    dataKey="problem"
                    angle={-45}
                    textAnchor="end"
                    height={100}
                    interval={0}
                  />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="count" fill="#0ea5e9" name="Response Count" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          ) : (
            <p className="text-gray-600 text-center py-8">
              {filterType !== 'all' && !filterValue
                ? 'Please select a filter value'
                : 'No data available for this filter'}
            </p>
          )}

          {topProblems?.topProblems && topProblems.topProblems.length > 0 && (
            <div className="mt-6">
              <h3 className="text-lg font-semibold mb-4">Top {topN} Problems Details</h3>
              <div className="space-y-3">
                {topProblems.topProblems.map((problem, index) => (
                  <div
                    key={index}
                    className="flex items-center justify-between p-4 bg-gray-50 rounded-lg"
                  >
                    <div className="flex items-center space-x-4">
                      <div className="flex-shrink-0 w-8 h-8 bg-primary-600 text-white rounded-full flex items-center justify-center font-bold">
                        {index + 1}
                      </div>
                      <div>
                        <p className="font-semibold text-gray-900">{problem.problemTag}</p>
                        <p className="text-sm text-gray-600">
                          {problem.count} responses
                          {problem.averageScore > 0 && (
                            <span className="ml-2">• Avg Score: {problem.averageScore.toFixed(1)}</span>
                          )}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </Card>

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
                  <XAxis dataKey="name" angle={-45} textAnchor="end" height={100} />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="teachers" fill="#22c55e" />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <p className="text-gray-600 text-center py-8">No data available</p>
            )}
          </Card>
        </div>
      </div>
    </ProfessionalLayout>
  );
};

export default ProfessionalAnalytics;

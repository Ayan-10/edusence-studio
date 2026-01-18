import { useEffect, useState } from 'react';
import TeacherLayout from '../../components/teacher/TeacherLayout';
import Card from '../../components/common/Card';
import { moduleService } from '../../services/moduleService';
import { useAuth } from '../../context/AuthContext';
import { BookOpen, Download, Globe } from 'lucide-react';

const TeacherModules = () => {
  const { user } = useAuth();
  const [assignments, setAssignments] = useState([]);
  const [filter, setFilter] = useState('all');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user?.id) {
      fetchAssignments();
    }
  }, [user]);

  const fetchAssignments = async () => {
    try {
      const data = await moduleService.getTeacherAssignments(user.id);
      setAssignments(data);
    } catch (error) {
      console.error('Error fetching assignments:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredAssignments = assignments.filter((assignment) => {
    if (filter === 'all') return true;
    return assignment.microModule?.languageCode === filter;
  });

  const uniqueLanguages = [
    ...new Set(assignments.map((a) => a.microModule?.languageCode).filter(Boolean)),
  ];

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
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
          <h2 className="text-2xl font-bold text-gray-900">My Modules</h2>
          <div className="flex items-center space-x-2">
            <select
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            >
              <option value="all">All Languages</option>
              {uniqueLanguages.map((lang) => (
                <option key={lang} value={lang}>
                  {lang}
                </option>
              ))}
            </select>
          </div>
        </div>

        {filteredAssignments.length === 0 ? (
          <Card>
            <div className="text-center py-8">
              <BookOpen className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-600">No modules assigned yet.</p>
            </div>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredAssignments.map((assignment) => (
              <Card key={assignment.id}>
                <div className="flex items-start justify-between mb-3">
                  <h3 className="text-lg font-semibold text-gray-900">
                    {assignment.microModule?.title || 'Untitled Module'}
                  </h3>
                </div>

                {assignment.microModule?.languageCode && (
                  <div className="flex items-center text-sm text-gray-600 mb-3">
                    <Globe className="h-4 w-4 mr-1" />
                    Language: {assignment.microModule.languageCode}
                  </div>
                )}

                {assignment.microModule?.focusProblemTag && (
                  <div className="mb-3">
                    <span className="inline-block px-2 py-1 bg-primary-100 text-primary-700 text-xs rounded">
                      {assignment.microModule.focusProblemTag}
                    </span>
                  </div>
                )}

                {assignment.microModule?.fileUrl && (
                  <a
                    href={assignment.microModule.fileUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center text-primary-600 hover:text-primary-700 text-sm font-medium"
                  >
                    <Download className="h-4 w-4 mr-1" />
                    Download Module
                  </a>
                )}
              </Card>
            ))}
          </div>
        )}
      </div>
    </TeacherLayout>
  );
};

export default TeacherModules;

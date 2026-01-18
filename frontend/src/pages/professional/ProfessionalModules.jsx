import { useEffect, useState } from 'react';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { moduleService } from '../../services/moduleService';
import { groupService } from '../../services/groupService';
import { userService } from '../../services/userService';
import { useAuth } from '../../context/AuthContext';
import { Upload, Sparkles, Users, BookOpen } from 'lucide-react';

const ProfessionalModules = () => {
  const { user } = useAuth();
  const [modules, setModules] = useState([]);
  const [groups, setGroups] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [showUploadForm, setShowUploadForm] = useState(false);
  const [selectedModule, setSelectedModule] = useState(null);
  const [uploadData, setUploadData] = useState({
    title: '',
    description: '',
    file: null,
  });
  const [splitting, setSplitting] = useState(false);
  const [assigning, setAssigning] = useState(false);
  const [loading, setLoading] = useState(true);
  const [splitLanguage, setSplitLanguage] = useState('EN');

  const languageOptions = [
    { code: 'EN', label: 'English' },
    { code: 'HI', label: 'Hindi' },
    { code: 'BN', label: 'Bengali' },
    { code: 'TA', label: 'Tamil' },
    { code: 'TE', label: 'Telugu' },
    { code: 'MR', label: 'Marathi' },
  ];

  useEffect(() => {
    fetchModules();
    fetchGroups();
    fetchTeachers();
  }, []);

  const fetchModules = async () => {
    try {
      const data = await moduleService.getAllModules();
      setModules(data);
    } catch (error) {
      console.error('Error fetching modules:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchGroups = async () => {
    try {
      const data = await groupService.getAllGroups();
      setGroups(data);
    } catch (error) {
      console.error('Error fetching groups:', error);
    }
  };

  const fetchTeachers = async () => {
    try {
      const data = await userService.getAllUsers();
      setTeachers(data.filter((u) => u.role === 'TEACHER'));
    } catch (error) {
      console.error('Error fetching teachers:', error);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!uploadData.file || !user?.id) return;

    try {
      await moduleService.uploadModule(
        uploadData.title,
        uploadData.description,
        uploadData.file,
        user.id
      );
      setShowUploadForm(false);
      setUploadData({ title: '', description: '', file: null });
      fetchModules();
    } catch (error) {
      console.error('Error uploading module:', error);
      alert('Failed to upload module');
    }
  };

  const handleSplitWithAI = async (moduleId, languageCode = 'EN') => {
    setSplitting(true);
    try {
      await moduleService.splitModuleWithAI(moduleId, languageCode);
      alert('Module split successfully!');
      fetchModules();
    } catch (error) {
      console.error('Error splitting module:', error);
      alert('Failed to split module');
    } finally {
      setSplitting(false);
    }
  };

  const handleAssign = async (microModuleId, targetType, targetId) => {
    setAssigning(true);
    try {
      if (targetType === 'teacher') {
        await moduleService.assignToTeacher(microModuleId, targetId);
      } else {
        await moduleService.assignToGroup(microModuleId, targetId);
      }
      alert('Module assigned successfully!');
    } catch (error) {
      console.error('Error assigning module:', error);
      alert('Failed to assign module');
    } finally {
      setAssigning(false);
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
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Modules</h2>
          <Button onClick={() => setShowUploadForm(true)} className="sm:w-auto w-full">
            <Upload className="h-4 w-4 mr-2" />
            Upload Module
          </Button>
        </div>

        {showUploadForm && (
          <Card className="mb-6">
            <h3 className="text-lg font-semibold mb-4">Upload Main Module</h3>
            <form onSubmit={handleUpload}>
              <Input
                label="Title"
                value={uploadData.title}
                onChange={(e) => setUploadData({ ...uploadData, title: e.target.value })}
                required
              />
              <Input
                label="Description"
                value={uploadData.description}
                onChange={(e) => setUploadData({ ...uploadData, description: e.target.value })}
              />
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  PDF File <span className="text-red-500">*</span>
                </label>
                <input
                  type="file"
                  accept=".pdf"
                  onChange={(e) =>
                    setUploadData({ ...uploadData, file: e.target.files[0] })
                  }
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  required
                />
              </div>
              <div className="flex flex-col sm:flex-row gap-3">
                <Button type="submit">Upload</Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    setShowUploadForm(false);
                    setUploadData({ title: '', description: '', file: null });
                  }}
                >
                  Cancel
                </Button>
              </div>
            </form>
          </Card>
        )}

        <div className="space-y-4">
          {modules.map((module) => (
            <Card key={module.id}>
              <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3 mb-4">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">{module.title}</h3>
                  {module.description && (
                    <p className="text-gray-600 mt-1">{module.description}</p>
                  )}
                </div>
                <div className="flex flex-col sm:flex-row gap-2">
                  <Button
                    variant="outline"
                    onClick={() => handleSplitWithAI(module.id, splitLanguage)}
                    disabled={splitting}
                  >
                    <Sparkles className="h-4 w-4 mr-2" />
                    Split with AI
                  </Button>
                  <select
                    value={splitLanguage}
                    onChange={(e) => setSplitLanguage(e.target.value)}
                    className="text-sm px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-primary-500"
                  >
                    {languageOptions.map((option) => (
                      <option key={option.code} value={option.code}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                  <Button
                    variant="outline"
                    onClick={() =>
                      setSelectedModule(selectedModule === module.id ? null : module.id)
                    }
                  >
                    {selectedModule === module.id ? 'Hide' : 'View Micro-Modules'}
                  </Button>
                </div>
              </div>

              {selectedModule === module.id && (
                <div className="mt-4 pt-4 border-t">
                  <h4 className="font-semibold mb-3">Micro-Modules</h4>
                  {module.microModules && module.microModules.length > 0 ? (
                    <div className="space-y-3">
                      {module.microModules.map((micro) => (
                        <div
                          key={micro.id}
                          className="p-3 bg-gray-50 rounded-lg flex items-center justify-between"
                        >
                          <div>
                            <p className="font-medium">{micro.title}</p>
                            <div className="flex items-center space-x-2 mt-1">
                              <span className="text-xs text-gray-600">
                                Language: {micro.languageCode}
                              </span>
                              {micro.focusProblemTag && (
                                <span className="text-xs px-2 py-1 bg-primary-100 text-primary-700 rounded">
                                  {micro.focusProblemTag}
                                </span>
                              )}
                            </div>
                          </div>
                          <div className="flex space-x-2">
                            <select
                              onChange={(e) => {
                                if (e.target.value) {
                                  const [type, id] = e.target.value.split(':');
                                  handleAssign(micro.id, type, id);
                                  e.target.value = '';
                                }
                              }}
                              className="text-sm px-3 py-1 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-primary-500"
                              disabled={assigning}
                            >
                              <option value="">Assign to...</option>
                              <optgroup label="Groups">
                                {groups.map((g) => (
                                  <option key={g.id} value={`group:${g.id}`}>
                                    {g.name}
                                  </option>
                                ))}
                              </optgroup>
                              <optgroup label="Teachers">
                                {teachers.map((t) => (
                                  <option key={t.id} value={`teacher:${t.id}`}>
                                    {t.name}
                                  </option>
                                ))}
                              </optgroup>
                            </select>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <p className="text-gray-600 text-sm">
                      No micro-modules yet. Click "Split with AI" to generate them.
                    </p>
                  )}
                </div>
              )}
            </Card>
          ))}

          {modules.length === 0 && (
            <Card>
              <div className="text-center py-8">
                <BookOpen className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-600">No modules uploaded yet.</p>
              </div>
            </Card>
          )}
        </div>
      </div>
    </ProfessionalLayout>
  );
};

export default ProfessionalModules;

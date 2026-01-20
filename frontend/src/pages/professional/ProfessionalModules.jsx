import { useEffect, useState } from 'react';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import Toast from '../../components/common/Toast';
import { moduleService } from '../../services/moduleService';
import { groupService } from '../../services/groupService';
import { userService } from '../../services/userService';
import { problemTagService } from '../../services/problemTagService';
import { useAuth } from '../../context/AuthContext';
import { Upload, Sparkles, Users, BookOpen, Loader2, Trash2, Settings, Plus, X, Download } from 'lucide-react';

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
  const [uploading, setUploading] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [deletingMicro, setDeletingMicro] = useState(false);
  const [loading, setLoading] = useState(true);
  const [splitLanguage, setSplitLanguage] = useState('EN');
  const [toast, setToast] = useState(null);
  const [splittingModuleId, setSplittingModuleId] = useState(null);
  const [deletingModuleId, setDeletingModuleId] = useState(null);
  const [deletingMicroModuleId, setDeletingMicroModuleId] = useState(null);
  const [problemTags, setProblemTags] = useState([]);
  const [selectedProblemAreas, setSelectedProblemAreas] = useState([]);
  const [numberOfModules, setNumberOfModules] = useState('');
  const [showAdvancedOptions, setShowAdvancedOptions] = useState(false);
  const [showNewTagForm, setShowNewTagForm] = useState(false);
  const [newTagCode, setNewTagCode] = useState('');
  const [newTagDescription, setNewTagDescription] = useState('');
  const [creatingTag, setCreatingTag] = useState(false);

  const languageOptions = [
    { code: 'EN', label: 'English' },
    { code: 'HI', label: 'Hindi (हिंदी)' },
    { code: 'BN', label: 'Bengali (বাংলা)' },
    { code: 'TA', label: 'Tamil (தமிழ்)' },
    { code: 'TE', label: 'Telugu (తెలుగు)' },
    { code: 'MR', label: 'Marathi (मराठी)' },
    { code: 'GU', label: 'Gujarati (ગુજરાતી)' },
    { code: 'KN', label: 'Kannada (ಕನ್ನಡ)' },
    { code: 'ML', label: 'Malayalam (മലയാളം)' },
    { code: 'OR', label: 'Odia (ଓଡ଼ିଆ)' },
    { code: 'PA', label: 'Punjabi (ਪੰਜਾਬੀ)' },
    { code: 'AS', label: 'Assamese (অসমীয়া)' },
    { code: 'UR', label: 'Urdu (اردو)' },
    { code: 'SA', label: 'Sanskrit (संस्कृतम्)' },
    { code: 'KO', label: 'Konkani (कोंकणी)' },
    { code: 'MN', label: 'Manipuri (ꯃꯅꯤꯄꯨꯔꯤ)' },
    { code: 'NE', label: 'Nepali (नेपाली)' },
    { code: 'BR', label: 'Bodo (बड़ो)' },
    { code: 'DO', label: 'Dogri (डोगरी)' },
    { code: 'MA', label: 'Maithili (मैथिली)' },
    { code: 'ST', label: 'Santhali (ᱥᱟᱱᱛᱟᱲᱤ)' },
    { code: 'SD', label: 'Sindhi (سنڌي)' },
    { code: 'KS', label: 'Kashmiri (कॉशुर)' },
  ];

  useEffect(() => {
    fetchModules();
    fetchGroups();
    fetchTeachers();
    fetchProblemTags();
  }, []);

  const fetchModules = async () => {
    try {
      setLoading(true);
      const data = await moduleService.getAllModules();
      setModules(data);
    } catch (error) {
      console.error('Error fetching modules:', error);
      setToast({ message: 'Failed to load modules. Please refresh the page.', type: 'error' });
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

  const fetchProblemTags = async () => {
    try {
      const data = await problemTagService.getAllProblemTags();
      setProblemTags(data);
    } catch (error) {
      console.error('Error fetching problem tags:', error);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!uploadData.file || !user?.id) {
      setToast({ message: 'Please fill in all required fields.', type: 'warning' });
      return;
    }

    setUploading(true);
    try {
      await moduleService.uploadModule(
        uploadData.title,
        uploadData.description,
        uploadData.file,
        user.id
      );
      setToast({ message: 'Module uploaded successfully!', type: 'success' });
      setShowUploadForm(false);
      setUploadData({ title: '', description: '', file: null });
      await fetchModules();
    } catch (error) {
      console.error('Error uploading module:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to upload module. Please try again.';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setUploading(false);
    }
  };

  const handleSplitWithAI = async (moduleId, languageCode = 'EN') => {
    setSplitting(true);
    setSplittingModuleId(moduleId);
    try {
      const problemAreas = selectedProblemAreas.length > 0 ? selectedProblemAreas.map(tag => tag.code) : null;
      const moduleCount = numberOfModules ? parseInt(numberOfModules) : null;
      await moduleService.splitModuleWithAI(moduleId, languageCode, problemAreas, moduleCount);
      setToast({ message: 'Module split successfully! Micro-modules have been generated.', type: 'success' });
      await fetchModules();
      // Reset form
      setSelectedProblemAreas([]);
      setNumberOfModules('');
      setShowAdvancedOptions(false);
    } catch (error) {
      console.error('Error splitting module:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to split module. Please try again.';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setSplitting(false);
      setSplittingModuleId(null);
    }
  };

  const handleCreateProblemTag = async (e) => {
    e.preventDefault();
    if (!newTagCode.trim()) {
      setToast({ message: 'Please enter a problem tag code.', type: 'warning' });
      return;
    }

    setCreatingTag(true);
    try {
      await problemTagService.createProblemTag(newTagCode.trim(), newTagDescription.trim());
      setToast({ message: 'Problem tag created successfully!', type: 'success' });
      setNewTagCode('');
      setNewTagDescription('');
      setShowNewTagForm(false);
      await fetchProblemTags();
    } catch (error) {
      console.error('Error creating problem tag:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to create problem tag.';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setCreatingTag(false);
    }
  };

  const toggleProblemArea = (tag) => {
    setSelectedProblemAreas(prev => {
      const exists = prev.find(t => t.id === tag.id);
      if (exists) {
        return prev.filter(t => t.id !== tag.id);
      } else {
        return [...prev, tag];
      }
    });
  };

  const handleAssign = async (microModuleId, targetType, targetId) => {
    setAssigning(true);
    try {
      if (targetType === 'teacher') {
        await moduleService.assignToTeacher(microModuleId, targetId);
      } else {
        await moduleService.assignToGroup(microModuleId, targetId);
      }
      setToast({ message: 'Module assigned successfully!', type: 'success' });
    } catch (error) {
      console.error('Error assigning module:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to assign module. Please try again.';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setAssigning(false);
    }
  };

  const handleDelete = async (moduleId, moduleTitle) => {
    if (!window.confirm(`Are you sure you want to delete "${moduleTitle}"? This will also delete all associated micro-modules. This action cannot be undone.`)) {
      return;
    }

    setDeleting(true);
    setDeletingModuleId(moduleId);
    try {
      await moduleService.deleteModule(moduleId);
      setToast({ message: 'Module deleted successfully!', type: 'success' });
      await fetchModules();
      // Close the module view if it was open
      if (selectedModule === moduleId) {
        setSelectedModule(null);
      }
    } catch (error) {
      console.error('Error deleting module:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to delete module. Please try again.';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setDeleting(false);
      setDeletingModuleId(null);
    }
  };

  const handleDeleteMicroModule = async (microModuleId, microModuleTitle) => {
    if (!window.confirm(`Are you sure you want to delete "${microModuleTitle}"? This will also delete all assignments for this micro-module. This action cannot be undone.`)) {
      return;
    }

    setDeletingMicro(true);
    setDeletingMicroModuleId(microModuleId);
    try {
      await moduleService.deleteMicroModule(microModuleId);
      setToast({ message: 'Micro-module deleted successfully!', type: 'success' });
      await fetchModules();
    } catch (error) {
      console.error('Error deleting micro-module:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to delete micro-module. Please try again.';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setDeletingMicro(false);
      setDeletingMicroModuleId(null);
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
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
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
                disabled={uploading}
              />
              <Input
                label="Description"
                value={uploadData.description}
                onChange={(e) => setUploadData({ ...uploadData, description: e.target.value })}
                disabled={uploading}
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
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
                  required
                  disabled={uploading}
                />
              </div>
              <div className="flex flex-col sm:flex-row gap-3">
                <Button type="submit" disabled={uploading}>
                  {uploading ? (
                    <>
                      <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                      Uploading...
                    </>
                  ) : (
                    'Upload'
                  )}
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    setShowUploadForm(false);
                    setUploadData({ title: '', description: '', file: null });
                  }}
                  disabled={uploading}
                >
                  Cancel
                </Button>
              </div>
              {uploading && (
                <div className="mt-4">
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div className="bg-primary-600 h-2 rounded-full animate-pulse" style={{ width: '60%' }}></div>
                  </div>
                  <p className="text-sm text-gray-600 mt-2">Uploading PDF to Supabase Storage... This may take a moment for large files.</p>
                </div>
              )}
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
                <div className="flex flex-col gap-3">
                  <div className="flex flex-col sm:flex-row gap-2">
                    <Button
                      variant="outline"
                      onClick={() => handleSplitWithAI(module.id, splitLanguage)}
                      disabled={splitting || deleting}
                    >
                      {splitting && splittingModuleId === module.id ? (
                        <>
                          <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                          Splitting...
                        </>
                      ) : (
                        <>
                          <Sparkles className="h-4 w-4 mr-2" />
                          Split with AI
                        </>
                      )}
                    </Button>
                    <select
                      value={splitLanguage}
                      onChange={(e) => setSplitLanguage(e.target.value)}
                      disabled={splitting || deleting}
                      className="text-sm px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {languageOptions.map((option) => (
                        <option key={option.code} value={option.code}>
                          {option.label}
                        </option>
                      ))}
                    </select>
                    <Button
                      variant="outline"
                      onClick={() => setShowAdvancedOptions(!showAdvancedOptions)}
                      disabled={splitting || deleting}
                      className="flex items-center gap-2"
                    >
                      <Settings className="h-4 w-4" />
                      {showAdvancedOptions ? 'Hide Options' : 'Advanced Options'}
                    </Button>
                  </div>

                  {showAdvancedOptions && (
                    <div className="p-4 bg-gray-50 rounded-lg border border-gray-200 space-y-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Problem Areas (Optional - Select to focus on specific areas)
                        </label>
                        <div className="flex flex-wrap gap-2 mb-2">
                          {problemTags.map((tag) => {
                            const isSelected = selectedProblemAreas.find(t => t.id === tag.id);
                            return (
                              <button
                                key={tag.id}
                                type="button"
                                onClick={() => toggleProblemArea(tag)}
                                disabled={splitting || deleting}
                                className={`px-3 py-1 text-sm rounded-full border transition-colors ${
                                  isSelected
                                    ? 'bg-primary-500 text-white border-primary-500'
                                    : 'bg-white text-gray-700 border-gray-300 hover:border-primary-300'
                                } disabled:opacity-50 disabled:cursor-not-allowed`}
                              >
                                {tag.code}
                                {tag.description && ` - ${tag.description}`}
                              </button>
                            );
                          })}
                        </div>
                        {selectedProblemAreas.length > 0 && (
                          <div className="text-xs text-gray-600 mt-1">
                            Selected: {selectedProblemAreas.map(t => t.code).join(', ')}
                          </div>
                        )}
                        <button
                          type="button"
                          onClick={() => setShowNewTagForm(!showNewTagForm)}
                          disabled={splitting || deleting}
                          className="mt-2 text-sm text-primary-600 hover:text-primary-700 flex items-center gap-1 disabled:opacity-50"
                        >
                          <Plus className="h-3 w-3" />
                          {showNewTagForm ? 'Cancel' : 'Add New Problem Area'}
                        </button>
                        {showNewTagForm && (
                          <form onSubmit={handleCreateProblemTag} className="mt-2 p-3 bg-white rounded border border-gray-300">
                            <div className="flex flex-col sm:flex-row gap-2">
                              <input
                                type="text"
                                value={newTagCode}
                                onChange={(e) => setNewTagCode(e.target.value.toUpperCase())}
                                placeholder="Problem Area Code (e.g., ABSENTEEISM)"
                                disabled={creatingTag}
                                className="flex-1 px-3 py-2 text-sm border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50"
                                required
                              />
                              <input
                                type="text"
                                value={newTagDescription}
                                onChange={(e) => setNewTagDescription(e.target.value)}
                                placeholder="Description (optional)"
                                disabled={creatingTag}
                                className="flex-1 px-3 py-2 text-sm border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50"
                              />
                              <Button
                                type="submit"
                                disabled={creatingTag}
                                className="text-sm"
                              >
                                {creatingTag ? (
                                  <>
                                    <Loader2 className="h-3 w-3 mr-1 animate-spin" />
                                    Creating...
                                  </>
                                ) : (
                                  'Add'
                                )}
                              </Button>
                            </div>
                          </form>
                        )}
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Number of Micro-Modules (Optional - Default: 7)
                        </label>
                        <input
                          type="number"
                          value={numberOfModules}
                          onChange={(e) => setNumberOfModules(e.target.value)}
                          min="1"
                          max="20"
                          placeholder="e.g., 6"
                          disabled={splitting || deleting}
                          className="w-full sm:w-32 px-3 py-2 text-sm border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
                        />
                        <p className="text-xs text-gray-500 mt-1">
                          Leave empty to use default (6-8 modules)
                        </p>
                      </div>
                    </div>
                  )}
                  <div className="flex flex-col sm:flex-row gap-2">
                    <Button
                      variant="outline"
                      onClick={() =>
                        setSelectedModule(selectedModule === module.id ? null : module.id)
                      }
                      disabled={deleting}
                    >
                      {selectedModule === module.id ? 'Hide' : 'View Micro-Modules'}
                    </Button>
                    <Button
                      variant="outline"
                      onClick={() => handleDelete(module.id, module.title)}
                      disabled={deleting || splitting}
                      className="text-red-600 hover:text-red-700 hover:bg-red-50 border-red-300"
                    >
                      {deleting && deletingModuleId === module.id ? (
                        <>
                          <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                          Deleting...
                        </>
                      ) : (
                        <>
                          <Trash2 className="h-4 w-4 mr-2" />
                          Delete
                        </>
                      )}
                    </Button>
                  </div>
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
                          <div className="flex flex-col sm:flex-row gap-2 items-start sm:items-center">
                            {micro.fileUrl && (
                              <a
                                href={micro.fileUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="text-primary-600 hover:text-primary-700 hover:bg-primary-50 border border-primary-300 rounded px-3 py-1 text-sm font-medium transition-colors flex items-center gap-1"
                                title="Download PDF"
                              >
                                <Download className="h-3 w-3" />
                                Download
                              </a>
                            )}
                            <select
                              onChange={(e) => {
                                if (e.target.value) {
                                  const [type, id] = e.target.value.split(':');
                                  handleAssign(micro.id, type, id);
                                  e.target.value = '';
                                }
                              }}
                              className="text-sm px-3 py-1 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
                              disabled={assigning || deletingMicro}
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
                            <button
                              onClick={() => handleDeleteMicroModule(micro.id, micro.title)}
                              disabled={deletingMicro || assigning}
                              className="text-red-600 hover:text-red-700 hover:bg-red-50 border border-red-300 rounded px-3 py-1 text-sm font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1"
                            >
                              {deletingMicro && deletingMicroModuleId === micro.id ? (
                                <>
                                  <Loader2 className="h-3 w-3 animate-spin" />
                                  Deleting...
                                </>
                              ) : (
                                <>
                                  <Trash2 className="h-3 w-3" />
                                  Delete
                                </>
                              )}
                            </button>
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

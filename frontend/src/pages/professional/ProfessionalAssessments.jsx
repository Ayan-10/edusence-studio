import { useEffect, useState } from 'react';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { assessmentService } from '../../services/assessmentService';
import { feedbackCycleService } from '../../services/feedbackCycleService';
import { groupService } from '../../services/groupService';
import { userService } from '../../services/userService';
import { Plus, Trash2, Edit2, Eye, X, Loader2 } from 'lucide-react';
import Toast from '../../components/common/Toast';

const problemTagOptions = [
  'ABSENTEEISM',
  'LANGUAGE_BARRIER',
  'SCIENCE_TLM',
  'CLASSROOM_MANAGEMENT',
  'PARENT_ENGAGEMENT',
  'MIXED_LEVEL_CLASSROOM',
];

const ProfessionalAssessments = () => {
  const [cycles, setCycles] = useState([]);
  const [groups, setGroups] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [assessments, setAssessments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [editingAssessment, setEditingAssessment] = useState(null);
  const [viewingAssessment, setViewingAssessment] = useState(null);
  const [deletingAssessment, setDeletingAssessment] = useState(null);
  const [deletingCycle, setDeletingCycle] = useState(null);
  const [deletingQuestion, setDeletingQuestion] = useState(null);
  const [toast, setToast] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    feedbackCycleId: '',
    assignmentTargetType: 'ALL',
    assignmentTargetId: '',
  });
  const [questions, setQuestions] = useState([
    {
      questionText: '',
      questionType: 'TEXT',
      maxScore: 10,
      problemTag: '',
    },
  ]);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [cycleData, groupData, usersData, assessmentData] = await Promise.all([
        feedbackCycleService.getAllCycles(),
        groupService.getAllGroups(),
        userService.getAllUsers(),
        assessmentService.getAllAssessments(),
      ]);
      setCycles(cycleData);
      setGroups(groupData);
      setTeachers(usersData.filter((u) => u.role === 'TEACHER'));
      setAssessments(assessmentData);
    } catch (error) {
      console.error('Error fetching assessment data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleQuestionChange = (index, field, value) => {
    setQuestions((prev) =>
      prev.map((q, idx) => (idx === index ? { ...q, [field]: value } : q))
    );
  };

  const addQuestion = () => {
    setQuestions((prev) => [
      ...prev,
      { questionText: '', questionType: 'TEXT', maxScore: 10, problemTag: '' },
    ]);
  };

  const removeQuestion = (index) => {
    setQuestions((prev) => prev.filter((_, idx) => idx !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.feedbackCycleId) return;

    setSubmitting(true);
    try {
      const payload = {
        title: formData.title,
        feedbackCycleId: formData.feedbackCycleId,
        assignmentTargetType: formData.assignmentTargetType,
        assignmentTargetId:
          formData.assignmentTargetType === 'ALL' ? null : formData.assignmentTargetId,
        questions,
      };
      await assessmentService.createAssessment(payload);
      setFormData({
        title: '',
        feedbackCycleId: '',
        assignmentTargetType: 'ALL',
        assignmentTargetId: '',
      });
      setQuestions([
        { questionText: '', questionType: 'TEXT', maxScore: 10, problemTag: '' },
      ]);
      setToast({ message: 'Assessment created successfully!', type: 'success' });
      fetchData();
    } catch (error) {
      console.error('Error creating assessment:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to create assessment';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleEditAssessment = (assessment) => {
    setEditingAssessment(assessment);
    setFormData({
      title: assessment.title,
      feedbackCycleId: assessment.feedbackCycle?.id || '',
      assignmentTargetType: assessment.assignmentTargetType || 'ALL',
      assignmentTargetId: assessment.assignmentTargetId || '',
    });
    if (assessment.questions && assessment.questions.length > 0) {
      setQuestions(assessment.questions.map(q => ({
        questionText: q.questionText,
        questionType: q.questionType,
        maxScore: q.maxScore,
        problemTag: q.problemTag,
      })));
    }
  };

  const handleUpdateAssessment = async (e) => {
    e.preventDefault();
    if (!editingAssessment || !formData.feedbackCycleId) return;

    setSubmitting(true);
    try {
      const payload = {
        title: formData.title,
        feedbackCycleId: formData.feedbackCycleId,
        assignmentTargetType: formData.assignmentTargetType,
        assignmentTargetId:
          formData.assignmentTargetType === 'ALL' ? null : formData.assignmentTargetId,
        questions,
      };
      await assessmentService.updateAssessment(editingAssessment.id, payload);
      setToast({ message: 'Assessment updated successfully!', type: 'success' });
      setEditingAssessment(null);
      setFormData({
        title: '',
        feedbackCycleId: '',
        assignmentTargetType: 'ALL',
        assignmentTargetId: '',
      });
      setQuestions([
        { questionText: '', questionType: 'TEXT', maxScore: 10, problemTag: '' },
      ]);
      fetchData();
    } catch (error) {
      console.error('Error updating assessment:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to update assessment';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteAssessment = async (assessmentId, assessmentTitle) => {
    if (!window.confirm(`Are you sure you want to delete "${assessmentTitle}"? This will also delete all associated questions. This action cannot be undone.`)) {
      return;
    }

    setDeletingAssessment(assessmentId);
    try {
      await assessmentService.deleteAssessment(assessmentId);
      setToast({ message: 'Assessment deleted successfully!', type: 'success' });
      fetchData();
    } catch (error) {
      console.error('Error deleting assessment:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to delete assessment';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setDeletingAssessment(null);
    }
  };

  const handleViewAssessment = async (assessmentId) => {
    try {
      const assessment = await assessmentService.getAssessment(assessmentId);
      const questionsData = await assessmentService.getAssessmentQuestions(assessmentId);
      setViewingAssessment({ ...assessment, questions: questionsData });
    } catch (error) {
      console.error('Error fetching assessment:', error);
      setToast({ message: 'Failed to load assessment details', type: 'error' });
    }
  };

  const handleDeleteCycle = async (cycleId, cycleName) => {
    if (!window.confirm(`Are you sure you want to delete "${cycleName}"? This will also delete all associated assessments. This action cannot be undone.`)) {
      return;
    }

    setDeletingCycle(cycleId);
    try {
      await feedbackCycleService.deleteCycle(cycleId);
      setToast({ message: 'Feedback cycle deleted successfully!', type: 'success' });
      fetchData();
    } catch (error) {
      console.error('Error deleting cycle:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to delete feedback cycle';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setDeletingCycle(null);
    }
  };

  const handleDeleteQuestion = async (questionId) => {
    if (!window.confirm('Are you sure you want to delete this question? This action cannot be undone.')) {
      return;
    }

    setDeletingQuestion(questionId);
    try {
      await assessmentService.deleteQuestion(questionId);
      setToast({ message: 'Question deleted successfully!', type: 'success' });
      // Refresh the viewing assessment
      if (viewingAssessment) {
        const updatedAssessment = await assessmentService.getAssessment(viewingAssessment.id);
        const questionsData = await assessmentService.getAssessmentQuestions(viewingAssessment.id);
        setViewingAssessment({ ...updatedAssessment, questions: questionsData });
      }
      fetchData();
    } catch (error) {
      console.error('Error deleting question:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to delete question';
      setToast({ message: errorMessage, type: 'error' });
    } finally {
      setDeletingQuestion(null);
    }
  };

  const cancelEdit = () => {
    setEditingAssessment(null);
    setFormData({
      title: '',
      feedbackCycleId: '',
      assignmentTargetType: 'ALL',
      assignmentTargetId: '',
    });
    setQuestions([
      { questionText: '', questionType: 'TEXT', maxScore: 10, problemTag: '' },
    ]);
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
      <div className="px-4 sm:px-0 space-y-6">
        <Card title={editingAssessment ? 'Edit Assessment' : 'Create Assessment'}>
          <form onSubmit={editingAssessment ? handleUpdateAssessment : handleSubmit}>
            <Input
              label="Assessment Title"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              required
            />
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Feedback Cycle <span className="text-red-500">*</span>
              </label>
              <select
                value={formData.feedbackCycleId}
                onChange={(e) => setFormData({ ...formData, feedbackCycleId: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                required
              >
                <option value="">Select a cycle</option>
                {cycles.map((cycle) => (
                  <option key={cycle.id} value={cycle.id}>
                    {cycle.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Assign To
                </label>
                <select
                  value={formData.assignmentTargetType}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      assignmentTargetType: e.target.value,
                      assignmentTargetId: '',
                    })
                  }
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  <option value="ALL">All Teachers</option>
                  <option value="GROUP">Specific Group</option>
                  <option value="TEACHER">Specific Teacher</option>
                </select>
              </div>
              {formData.assignmentTargetType !== 'ALL' && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    {formData.assignmentTargetType === 'GROUP' ? 'Group' : 'Teacher'}
                  </label>
                  <select
                    value={formData.assignmentTargetId}
                    onChange={(e) =>
                      setFormData({ ...formData, assignmentTargetId: e.target.value })
                    }
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                    required
                  >
                    <option value="">Select</option>
                    {formData.assignmentTargetType === 'GROUP'
                      ? groups.map((group) => (
                          <option key={group.id} value={group.id}>
                            {group.name}
                          </option>
                        ))
                      : teachers.map((teacher) => (
                          <option key={teacher.id} value={teacher.id}>
                            {teacher.name} ({teacher.email})
                          </option>
                        ))}
                  </select>
                </div>
              )}
            </div>

            <div className="space-y-4">
              {questions.map((question, index) => (
                <div key={index} className="p-4 border border-gray-200 rounded-lg">
                  <div className="flex items-center justify-between mb-3">
                    <h4 className="font-semibold text-gray-900">Question {index + 1}</h4>
                    {questions.length > 1 && (
                      <button
                        type="button"
                        onClick={() => removeQuestion(index)}
                        className="text-red-600 hover:text-red-700"
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    )}
                  </div>
                  <Input
                    label="Question Text"
                    value={question.questionText}
                    onChange={(e) => handleQuestionChange(index, 'questionText', e.target.value)}
                    required
                  />
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Question Type
                      </label>
                      <select
                        value={question.questionType}
                        onChange={(e) =>
                          handleQuestionChange(index, 'questionType', e.target.value)
                        }
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      >
                        <option value="TEXT">Text</option>
                        <option value="SCALE">Scale/Numeric</option>
                      </select>
                    </div>
                    <div>
                      <Input
                        label="Max Score"
                        type="number"
                        value={question.maxScore ?? 10}
                        onChange={(e) =>
                          handleQuestionChange(index, 'maxScore', Number(e.target.value))
                        }
                        min={1}
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Problem Tag <span className="text-red-500">*</span>
                      </label>
                      <select
                        value={question.problemTag}
                        onChange={(e) =>
                          handleQuestionChange(index, 'problemTag', e.target.value)
                        }
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                        required
                      >
                        <option value="">Select tag</option>
                        {problemTagOptions.map((tag) => (
                          <option key={tag} value={tag}>
                            {tag}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                </div>
              ))}
              <Button type="button" variant="outline" onClick={addQuestion}>
                <Plus className="h-4 w-4 mr-2" />
                Add Question
              </Button>
            </div>

            <div className="mt-6 flex flex-col sm:flex-row gap-3">
              <Button type="submit" disabled={submitting}>
                {submitting ? (
                  <>
                    <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                    {editingAssessment ? 'Updating...' : 'Creating...'}
                  </>
                ) : (
                  editingAssessment ? 'Update Assessment' : 'Create Assessment'
                )}
              </Button>
              {editingAssessment && (
                <Button type="button" variant="outline" onClick={cancelEdit} disabled={submitting}>
                  Cancel
                </Button>
              )}
            </div>
          </form>
        </Card>

        <Card title="Existing Assessments">
          {assessments.length === 0 ? (
            <p className="text-gray-600">No assessments created yet.</p>
          ) : (
            <div className="space-y-3">
              {assessments.map((assessment) => (
                <div key={assessment.id} className="p-4 bg-gray-50 rounded-lg border border-gray-200">
                  <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
                    <div className="flex-1">
                      <p className="font-medium text-gray-900 mb-1">{assessment.title}</p>
                      <p className="text-sm text-gray-500 mb-2">
                        Cycle: {assessment.feedbackCycle?.name || 'N/A'} | 
                        Questions: {assessment.questions?.length || 0} | 
                        Target: {assessment.assignmentTargetType || 'ALL'}
                      </p>
                    </div>
                    <div className="flex flex-wrap gap-2">
                      <Button
                        variant="outline"
                        onClick={() => handleViewAssessment(assessment.id)}
                        className="text-sm"
                      >
                        <Eye className="h-4 w-4 mr-1" />
                        View
                      </Button>
                      <Button
                        variant="outline"
                        onClick={() => handleEditAssessment(assessment)}
                        disabled={deletingAssessment === assessment.id}
                        className="text-sm"
                      >
                        <Edit2 className="h-4 w-4 mr-1" />
                        Edit
                      </Button>
                      <Button
                        variant="outline"
                        onClick={() => handleDeleteAssessment(assessment.id, assessment.title)}
                        disabled={deletingAssessment === assessment.id}
                        className="text-sm text-red-600 hover:text-red-700 hover:bg-red-50 border-red-300"
                      >
                        {deletingAssessment === assessment.id ? (
                          <>
                            <Loader2 className="h-4 w-4 mr-1 animate-spin" />
                            Deleting...
                          </>
                        ) : (
                          <>
                            <Trash2 className="h-4 w-4 mr-1" />
                            Delete
                          </>
                        )}
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>

        {viewingAssessment && (
          <Card title={`Assessment: ${viewingAssessment.title}`}>
            <div className="mb-4">
              <Button
                variant="outline"
                onClick={() => setViewingAssessment(null)}
                className="mb-4"
              >
                <X className="h-4 w-4 mr-2" />
                Close
              </Button>
              <div className="bg-gray-50 p-4 rounded-lg mb-4">
                <p className="text-sm text-gray-600 mb-2">
                  <strong>Feedback Cycle:</strong> {viewingAssessment.feedbackCycle?.name || 'N/A'}
                </p>
                <p className="text-sm text-gray-600 mb-2">
                  <strong>Assignment Target:</strong> {viewingAssessment.assignmentTargetType || 'ALL'}
                </p>
                <p className="text-sm text-gray-600">
                  <strong>Total Questions:</strong> {viewingAssessment.questions?.length || 0}
                </p>
              </div>
            </div>
            {viewingAssessment.questions && viewingAssessment.questions.length > 0 ? (
              <div className="space-y-4">
                <h4 className="font-semibold text-gray-900 mb-3">Questions:</h4>
                {viewingAssessment.questions.map((question, index) => (
                  <div key={question.id} className="p-4 border border-gray-200 rounded-lg">
                    <div className="flex items-start justify-between mb-2">
                      <h5 className="font-medium text-gray-900">Question {index + 1}</h5>
                      <button
                        onClick={() => handleDeleteQuestion(question.id)}
                        disabled={deletingQuestion === question.id}
                        className="text-red-600 hover:text-red-700 hover:bg-red-50 p-1 rounded transition-colors disabled:opacity-50"
                        title="Delete question"
                      >
                        {deletingQuestion === question.id ? (
                          <Loader2 className="h-4 w-4 animate-spin" />
                        ) : (
                          <Trash2 className="h-4 w-4" />
                        )}
                      </button>
                    </div>
                    <p className="text-gray-700 mb-3">{question.questionText}</p>
                    <div className="flex flex-wrap gap-2 text-sm text-gray-500">
                      <span>Type: {question.questionType}</span>
                      {question.maxScore && <span>| Max Score: {question.maxScore}</span>}
                      {question.problemTag && <span>| Problem Tag: {question.problemTag}</span>}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-600 text-center py-4">No questions found for this assessment.</p>
            )}
          </Card>
        )}
      </div>
    </ProfessionalLayout>
  );
};

export default ProfessionalAssessments;

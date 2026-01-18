import { useEffect, useState } from 'react';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { assessmentService } from '../../services/assessmentService';
import { feedbackCycleService } from '../../services/feedbackCycleService';
import { groupService } from '../../services/groupService';
import { userService } from '../../services/userService';
import { Plus, Trash2 } from 'lucide-react';

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
      fetchData();
    } catch (error) {
      console.error('Error creating assessment:', error);
      alert('Failed to create assessment');
    } finally {
      setSubmitting(false);
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
      <div className="px-4 sm:px-0 space-y-6">
        <Card title="Create Assessment">
          <form onSubmit={handleSubmit}>
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
                        <option value="NUMERIC">Numeric</option>
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

            <div className="mt-6">
              <Button type="submit" disabled={submitting}>
                {submitting ? 'Creating...' : 'Create Assessment'}
              </Button>
            </div>
          </form>
        </Card>

        <Card title="Existing Assessments">
          {assessments.length === 0 ? (
            <p className="text-gray-600">No assessments created yet.</p>
          ) : (
            <div className="space-y-3">
              {assessments.map((assessment) => (
                <div key={assessment.id} className="p-3 bg-gray-50 rounded-lg">
                  <p className="font-medium text-gray-900">{assessment.title}</p>
                  <p className="text-sm text-gray-500">
                    Questions: {assessment.questions?.length || 0}
                  </p>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>
    </ProfessionalLayout>
  );
};

export default ProfessionalAssessments;

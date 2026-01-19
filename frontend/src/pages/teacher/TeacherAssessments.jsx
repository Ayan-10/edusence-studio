import { useEffect, useState } from 'react';
import TeacherLayout from '../../components/teacher/TeacherLayout';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { assessmentService } from '../../services/assessmentService';
import { feedbackCycleService } from '../../services/feedbackCycleService';
import { groupService } from '../../services/groupService';
import { useAuth } from '../../context/AuthContext';
import { Calendar, FileText } from 'lucide-react';

const TeacherAssessments = () => {
  const { user } = useAuth();
  const [cycles, setCycles] = useState([]);
  const [selectedAssessment, setSelectedAssessment] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [responses, setResponses] = useState({});
  const [teacherGroupIds, setTeacherGroupIds] = useState(new Set());
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchActiveCycles();
  }, []);

  useEffect(() => {
    fetchTeacherGroups();
  }, [user]);

  const fetchActiveCycles = async () => {
    try {
      const activeCycles = await feedbackCycleService.getActiveCycles();
      setCycles(activeCycles);
    } catch (error) {
      console.error('Error fetching cycles:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchTeacherGroups = async () => {
    if (!user?.id) return;
    try {
      const groups = await groupService.getGroupsForTeacher(user.id);
      setTeacherGroupIds(new Set(groups.map((g) => g.id)));
    } catch (error) {
      console.error('Error fetching teacher groups:', error);
    }
  };

  const handleTakeAssessment = async (assessmentId) => {
    try {
      const questionsData = await assessmentService.getAssessmentQuestions(assessmentId);
      if (!questionsData || questionsData.length === 0) {
        alert('No questions found for this assessment. Please contact the administrator.');
        return;
      }
      setQuestions(questionsData);
      setSelectedAssessment(assessmentId);
      setResponses({});
    } catch (error) {
      console.error('Error fetching questions:', error);
      alert('Failed to load assessment questions. Please try again.');
    }
  };

  const handleResponseChange = (questionId, value) => {
    setResponses({ ...responses, [questionId]: value });
  };

  const handleSubmit = async () => {
    if (!user?.id || !selectedAssessment) return;

    setSubmitting(true);
    try {
      for (const [questionId, value] of Object.entries(responses)) {
        const question = questions.find((q) => q.id === questionId);
        if (question) {
          const numericResponse = question.questionType === 'SCALE' ? parseInt(value) : null;
          const textResponse = question.questionType === 'TEXT' ? value : null;

          await assessmentService.submitResponse(
            user.id,
            questionId,
            numericResponse,
            textResponse
          );
        }
      }
      alert('Assessment submitted successfully!');
      setSelectedAssessment(null);
      setQuestions([]);
      setResponses({});
    } catch (error) {
      console.error('Error submitting assessment:', error);
      alert('Failed to submit assessment. Please try again.');
    } finally {
      setSubmitting(false);
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

  if (selectedAssessment) {
    return (
      <TeacherLayout>
        <div className="px-4 sm:px-0">
          <Button
            variant="outline"
            onClick={() => {
              setSelectedAssessment(null);
              setQuestions([]);
              setResponses({});
            }}
            className="mb-4"
          >
            ← Back to Assessments
          </Button>

          <Card title="Assessment Questions">
            {questions.length === 0 ? (
              <p className="text-gray-600 text-center py-4">No questions found for this assessment.</p>
            ) : (
              questions.map((question) => (
              <div key={question.id} className="mb-6 pb-6 border-b last:border-b-0">
                <h3 className="font-semibold text-gray-900 mb-2">{question.questionText}</h3>
                <p className="text-sm text-gray-500 mb-3">
                  Problem Tag: {question.problemTag} | Type: {question.questionType}
                </p>

                {question.questionType === 'SCALE' ? (
                  <Input
                    type="number"
                    value={responses[question.id] || ''}
                    onChange={(e) => handleResponseChange(question.id, e.target.value)}
                    placeholder={`Enter a number (0-${question.maxScore || 10})`}
                    max={question.maxScore || 10}
                    min={0}
                  />
                ) : (
                  <textarea
                    value={responses[question.id] || ''}
                    onChange={(e) => handleResponseChange(question.id, e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                    rows={4}
                    placeholder="Enter your response..."
                  />
                )}
              </div>
              ))
            )}

            {questions.length > 0 && (
              <Button onClick={handleSubmit} disabled={submitting} className="w-full mt-4">
                {submitting ? 'Submitting...' : 'Submit Assessment'}
              </Button>
            )}
          </Card>
        </div>
      </TeacherLayout>
    );
  }

  return (
    <TeacherLayout>
      <div className="px-4 sm:px-0">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Assessments</h2>

        {cycles.length === 0 ? (
          <Card>
            <p className="text-gray-600">No active feedback cycles available.</p>
          </Card>
        ) : (
          <div className="space-y-4">
            {cycles.map((cycle) => {
              const eligibleAssessments =
                cycle.assessments?.filter((assessment) => {
                  const targetType = assessment.assignmentTargetType || 'ALL';
                  if (targetType === 'ALL') return true;
                  if (targetType === 'TEACHER') {
                    return assessment.assignmentTargetId === user?.id;
                  }
                  if (targetType === 'GROUP') {
                    return teacherGroupIds.has(assessment.assignmentTargetId);
                  }
                  return false;
                }) || [];

              return (
                <Card key={cycle.id}>
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">{cycle.name}</h3>
                    {cycle.description && (
                      <p className="text-gray-600 mb-3">{cycle.description}</p>
                    )}
                    <div className="flex items-center text-sm text-gray-500 space-x-4">
                      <div className="flex items-center">
                        <Calendar className="h-4 w-4 mr-1" />
                        {new Date(cycle.startDate).toLocaleDateString()} -{' '}
                        {new Date(cycle.endDate).toLocaleDateString()}
                      </div>
                      <div className="flex items-center">
                        <FileText className="h-4 w-4 mr-1" />
                        {cycle.assessments?.length || 0} assessments
                      </div>
                    </div>
                  </div>
                </div>

                {eligibleAssessments.length > 0 ? (
                  <div className="mt-4 space-y-2">
                    {eligibleAssessments.map((assessment) => (
                      <Button
                        key={assessment.id}
                        onClick={() => handleTakeAssessment(assessment.id)}
                        className="w-full"
                      >
                        Take Assessment: {assessment.title}
                      </Button>
                    ))}
                  </div>
                ) : (
                  <p className="mt-4 text-sm text-gray-500">
                    No assessments assigned in this cycle.
                  </p>
                )}
              </Card>
              );
            })}
          </div>
        )}
      </div>
    </TeacherLayout>
  );
};

export default TeacherAssessments;

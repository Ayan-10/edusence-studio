import api from './api';

export const assessmentService = {
  getAllAssessments: async () => {
    const response = await api.get('/assessments');
    return response.data;
  },

  getAssessment: async (id) => {
    const response = await api.get(`/assessments/${id}`);
    return response.data;
  },

  getAssessmentQuestions: async (id) => {
    const response = await api.get(`/assessments/${id}/questions`);
    return response.data;
  },

  createAssessment: async (assessmentData) => {
    const response = await api.post('/assessments', assessmentData);
    return response.data;
  },

  submitResponse: async (teacherId, questionId, numericResponse, textResponse) => {
    await api.post('/assessment-responses', {
      teacherId,
      questionId,
      numericResponse,
      textResponse,
    });
  },

  updateAssessment: async (id, assessmentData) => {
    const response = await api.put(`/assessments/${id}`, assessmentData);
    return response.data;
  },

  deleteAssessment: async (id) => {
    await api.delete(`/assessments/${id}`);
  },

  deleteQuestion: async (questionId) => {
    await api.delete(`/assessments/questions/${questionId}`);
  },
};

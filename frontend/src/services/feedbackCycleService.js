import api from './api';

export const feedbackCycleService = {
  getAllCycles: async () => {
    const response = await api.get('/feedback-cycles');
    return response.data;
  },

  getCycle: async (id) => {
    const response = await api.get(`/feedback-cycles/${id}`);
    return response.data;
  },

  getActiveCycles: async () => {
    const response = await api.get('/feedback-cycles/active');
    return response.data;
  },

  createCycle: async (cycleData) => {
    const response = await api.post('/feedback-cycles', cycleData);
    return response.data;
  },

  activateCycle: async (id) => {
    const response = await api.post(`/feedback-cycles/${id}/activate`);
    return response.data;
  },

  deleteCycle: async (id) => {
    await api.delete(`/feedback-cycles/${id}`);
  },
};

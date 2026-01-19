import api from './api';

export const problemTagService = {
  getAllProblemTags: async () => {
    const response = await api.get('/problem-tags');
    return response.data;
  },

  getProblemTag: async (id) => {
    const response = await api.get(`/problem-tags/${id}`);
    return response.data;
  },

  createProblemTag: async (code, description) => {
    const response = await api.post('/problem-tags', null, {
      params: { code, description },
    });
    return response.data;
  },

  updateProblemTag: async (id, code, description) => {
    const response = await api.put(`/problem-tags/${id}`, null, {
      params: { code, description },
    });
    return response.data;
  },

  deleteProblemTag: async (id) => {
    await api.delete(`/problem-tags/${id}`);
  },
};

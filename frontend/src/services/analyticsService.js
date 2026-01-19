import api from './api';

export const analyticsService = {
  getTeacherAnalytics: async (teacherId) => {
    const response = await api.get(`/analytics/teachers/${teacherId}`);
    return response.data;
  },

  getGroupAnalytics: async (groupId) => {
    const response = await api.get(`/analytics/groups/${groupId}`);
    return response.data;
  },

  getProblemTagAnalytics: async () => {
    const response = await api.get('/analytics/problems');
    return response.data;
  },

  getOverviewAnalytics: async () => {
    const response = await api.get('/analytics/overview');
    return response.data;
  },

  getTopProblems: async (limit = 10, filterType = null, filterValue = null) => {
    const params = new URLSearchParams({ limit: limit.toString() });
    if (filterType) params.append('filterType', filterType);
    if (filterValue) params.append('filterValue', filterValue);
    const response = await api.get(`/analytics/top-problems?${params.toString()}`);
    return response.data;
  },
};

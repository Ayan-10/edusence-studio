import api from './api';

export const groupService = {
  getAllGroups: async () => {
    const response = await api.get('/groups');
    return response.data;
  },

  createGroup: async (groupData) => {
    const response = await api.post('/groups', groupData);
    return response.data;
  },

  getGroupsForTeacher: async (teacherId) => {
    const response = await api.get(`/groups/teacher/${teacherId}`);
    return response.data;
  },

  addTeacherToGroup: async (groupId, teacherId) => {
    await api.post(`/groups/${groupId}/teachers/${teacherId}`);
  },

  removeTeacherFromGroup: async (groupId, teacherId) => {
    await api.delete(`/groups/${groupId}/teachers/${teacherId}`);
  },
};

import api from './api';

export const moduleService = {
  getAllModules: async () => {
    const response = await api.get('/modules');
    return response.data;
  },

  getModule: async (id) => {
    const response = await api.get(`/modules/${id}`);
    return response.data;
  },

  getMicroModules: async (moduleId) => {
    const response = await api.get(`/modules/${moduleId}/micro-modules`);
    return response.data;
  },

  uploadModule: async (title, description, file, uploadedByUserId) => {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('file', file);
    formData.append('uploadedByUserId', uploadedByUserId);

    const response = await api.post('/modules/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  splitModuleWithAI: async (moduleId, languageCode = 'EN') => {
    const response = await api.post(`/modules/${moduleId}/split-ai?languageCode=${languageCode}`);
    return response.data;
  },

  getTeacherAssignments: async (teacherId) => {
    const response = await api.get(`/micro-modules/assignments/teacher/${teacherId}`);
    return response.data;
  },

  assignToTeacher: async (microModuleId, teacherId) => {
    await api.post('/micro-modules/assignments/teacher', {
      microModuleId,
      teacherId,
    });
  },

  assignToGroup: async (microModuleId, groupId) => {
    await api.post('/micro-modules/assignments/group', {
      microModuleId,
      groupId,
    });
  },
};

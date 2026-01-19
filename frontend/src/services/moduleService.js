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

  splitModuleWithAI: async (moduleId, languageCode = 'EN', problemAreas = null, numberOfModules = null) => {
    const params = new URLSearchParams({ languageCode });
    if (problemAreas && problemAreas.length > 0) {
      problemAreas.forEach(area => params.append('problemAreas', area));
    }
    if (numberOfModules) {
      params.append('numberOfModules', numberOfModules.toString());
    }
    const response = await api.post(`/modules/${moduleId}/split-ai?${params.toString()}`);
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

  deleteModule: async (moduleId) => {
    await api.delete(`/modules/${moduleId}`);
  },

  deleteMicroModule: async (microModuleId) => {
    await api.delete(`/modules/micro-modules/${microModuleId}`);
  },
};

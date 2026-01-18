import api from './api';

export const courseService = {
  getAllCourses: async () => {
    const response = await api.get('/courses');
    return response.data;
  },

  getCourse: async (id) => {
    const response = await api.get(`/courses/${id}`);
    return response.data;
  },

  createCourse: async (courseData) => {
    const response = await api.post('/courses', courseData);
    return response.data;
  },

  assignCourseToTeacher: async (courseId, teacherId) => {
    await api.post(`/courses/${courseId}/assign/teacher/${teacherId}`);
  },

  assignCourseToGroup: async (courseId, groupId) => {
    await api.post(`/courses/${courseId}/assign/group/${groupId}`);
  },
};

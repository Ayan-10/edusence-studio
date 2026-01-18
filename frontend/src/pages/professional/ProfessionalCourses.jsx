import { useEffect, useMemo, useState } from 'react';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { moduleService } from '../../services/moduleService';
import { courseService } from '../../services/courseService';
import { groupService } from '../../services/groupService';
import { userService } from '../../services/userService';
import { BookOpen } from 'lucide-react';

const ProfessionalCourses = () => {
  const [modules, setModules] = useState([]);
  const [courses, setCourses] = useState([]);
  const [groups, setGroups] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [assigning, setAssigning] = useState(false);
  const [selectedModuleId, setSelectedModuleId] = useState('all');
  const [formData, setFormData] = useState({
    title: '',
    description: '',
  });
  const [selectedMicroModuleIds, setSelectedMicroModuleIds] = useState(new Set());

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [moduleData, courseData, groupData, userData] = await Promise.all([
        moduleService.getAllModules(),
        courseService.getAllCourses(),
        groupService.getAllGroups(),
        userService.getAllUsers(),
      ]);
      setModules(moduleData);
      setCourses(courseData);
      setGroups(groupData);
      setTeachers(userData.filter((u) => u.role === 'TEACHER'));
    } catch (error) {
      console.error('Error fetching course data:', error);
    } finally {
      setLoading(false);
    }
  };

  const availableMicroModules = useMemo(() => {
    if (selectedModuleId === 'all') {
      return modules.flatMap((m) => m.microModules || []);
    }
    return modules.find((m) => m.id === selectedModuleId)?.microModules || [];
  }, [modules, selectedModuleId]);

  const toggleMicroModule = (id) => {
    setSelectedMicroModuleIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  };

  const handleCreateCourse = async (e) => {
    e.preventDefault();
    if (selectedMicroModuleIds.size === 0) return;
    try {
      await courseService.createCourse({
        title: formData.title,
        description: formData.description,
        microModuleIds: Array.from(selectedMicroModuleIds),
      });
      setFormData({ title: '', description: '' });
      setSelectedMicroModuleIds(new Set());
      setSelectedModuleId('all');
      fetchData();
    } catch (error) {
      console.error('Error creating course:', error);
      alert('Failed to create course');
    }
  };

  const handleAssignCourse = async (courseId, targetType, targetId) => {
    if (!targetId) return;
    setAssigning(true);
    try {
      if (targetType === 'teacher') {
        await courseService.assignCourseToTeacher(courseId, targetId);
      } else {
        await courseService.assignCourseToGroup(courseId, targetId);
      }
      alert('Course assigned successfully!');
    } catch (error) {
      console.error('Error assigning course:', error);
      alert('Failed to assign course');
    } finally {
      setAssigning(false);
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
        <Card title="Create Coursework">
          <form onSubmit={handleCreateCourse}>
            <Input
              label="Course Title"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              required
            />
            <Input
              label="Description"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            />
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Filter by Main Module
              </label>
              <select
                value={selectedModuleId}
                onChange={(e) => setSelectedModuleId(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="all">All Modules</option>
                {modules.map((module) => (
                  <option key={module.id} value={module.id}>
                    {module.title}
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-4">
              <p className="text-sm font-medium text-gray-700 mb-2">
                Select Micro-Modules <span className="text-red-500">*</span>
              </p>
              {availableMicroModules.length === 0 ? (
                <p className="text-sm text-gray-500">No micro-modules available.</p>
              ) : (
                <div className="max-h-64 overflow-y-auto space-y-2">
                  {availableMicroModules.map((micro) => (
                    <label
                      key={micro.id}
                      className="flex items-center space-x-3 p-2 border border-gray-200 rounded-lg"
                    >
                      <input
                        type="checkbox"
                        checked={selectedMicroModuleIds.has(micro.id)}
                        onChange={() => toggleMicroModule(micro.id)}
                        className="h-4 w-4 text-primary-600"
                      />
                      <span className="text-sm text-gray-800">
                        {micro.title} ({micro.languageCode || 'EN'})
                      </span>
                    </label>
                  ))}
                </div>
              )}
            </div>

            <Button type="submit" disabled={selectedMicroModuleIds.size === 0}>
              Create Course
            </Button>
          </form>
        </Card>

        <Card title="Existing Courses">
          {courses.length === 0 ? (
            <div className="text-center py-8">
              <BookOpen className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-600">No courses created yet.</p>
            </div>
          ) : (
            <div className="space-y-4">
              {courses.map((course) => (
                <div key={course.id} className="p-4 bg-gray-50 rounded-lg">
                  <div className="flex items-start justify-between mb-2">
                    <div>
                      <h3 className="font-semibold text-gray-900">{course.title}</h3>
                      {course.description && (
                        <p className="text-sm text-gray-600">{course.description}</p>
                      )}
                    </div>
                    <span className="text-xs text-gray-500">
                      {course.microModules?.length || 0} micro-modules
                    </span>
                  </div>
                  <div className="flex space-x-2">
                    <select
                      onChange={(e) => {
                        if (e.target.value) {
                          const [type, id] = e.target.value.split(':');
                          handleAssignCourse(course.id, type, id);
                          e.target.value = '';
                        }
                      }}
                      className="text-sm px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-primary-500"
                      disabled={assigning}
                    >
                      <option value="">Assign to...</option>
                      <optgroup label="Groups">
                        {groups.map((group) => (
                          <option key={group.id} value={`group:${group.id}`}>
                            {group.name}
                          </option>
                        ))}
                      </optgroup>
                      <optgroup label="Teachers">
                        {teachers.map((teacher) => (
                          <option key={teacher.id} value={`teacher:${teacher.id}`}>
                            {teacher.name}
                          </option>
                        ))}
                      </optgroup>
                    </select>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>
    </ProfessionalLayout>
  );
};

export default ProfessionalCourses;

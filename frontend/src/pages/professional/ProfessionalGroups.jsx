import { useEffect, useState } from 'react';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { groupService } from '../../services/groupService';
import { userService } from '../../services/userService';
import { useAuth } from '../../context/AuthContext';
import { Users, Plus, X } from 'lucide-react';

const ProfessionalGroups = () => {
  const { user } = useAuth();
  const [groups, setGroups] = useState([]);
  const [users, setUsers] = useState([]);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    focusProblemTag: '',
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchGroups();
    fetchUsers();
  }, []);

  const fetchGroups = async () => {
    try {
      const data = await groupService.getAllGroups();
      setGroups(data);
    } catch (error) {
      console.error('Error fetching groups:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchUsers = async () => {
    try {
      const data = await userService.getAllUsers();
      setUsers(data.filter((u) => u.role === 'TEACHER'));
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  const handleCreateGroup = async (e) => {
    e.preventDefault();
    try {
      await groupService.createGroup({
        ...formData,
        createdByUserId: user.id,
      });
      setShowCreateForm(false);
      setFormData({ name: '', description: '', focusProblemTag: '' });
      fetchGroups();
    } catch (error) {
      console.error('Error creating group:', error);
      alert('Failed to create group');
    }
  };

  const handleAddTeacher = async (groupId, teacherId) => {
    try {
      await groupService.addTeacherToGroup(groupId, teacherId);
      fetchGroups();
    } catch (error) {
      console.error('Error adding teacher:', error);
      alert('Failed to add teacher');
    }
  };

  const handleRemoveTeacher = async (groupId, teacherId) => {
    try {
      await groupService.removeTeacherFromGroup(groupId, teacherId);
      fetchGroups();
    } catch (error) {
      console.error('Error removing teacher:', error);
      alert('Failed to remove teacher');
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
      <div className="px-4 sm:px-0">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Groups</h2>
          <Button onClick={() => setShowCreateForm(true)} className="sm:w-auto w-full">
            <Plus className="h-4 w-4 mr-2" />
            Create Group
          </Button>
        </div>

        {showCreateForm && (
          <Card className="mb-6">
            <h3 className="text-lg font-semibold mb-4">Create New Group</h3>
            <form onSubmit={handleCreateGroup}>
              <Input
                label="Group Name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
              />
              <Input
                label="Description"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              />
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Focus Problem Tag <span className="text-red-500">*</span>
                </label>
                <select
                  value={formData.focusProblemTag}
                  onChange={(e) => setFormData({ ...formData, focusProblemTag: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  required
                >
                  <option value="">Select problem tag</option>
                  <option value="ABSENTEEISM">Absenteeism</option>
                  <option value="LANGUAGE_BARRIER">Language Barrier</option>
                  <option value="SCIENCE_TLM">Science TLM</option>
                  <option value="CLASSROOM_MANAGEMENT">Classroom Management</option>
                  <option value="PARENT_ENGAGEMENT">Parent Engagement</option>
                  <option value="MIXED_LEVEL_CLASSROOM">Mixed Level Classroom</option>
                </select>
              </div>
              <div className="flex flex-col sm:flex-row gap-3">
                <Button type="submit">Create</Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    setShowCreateForm(false);
                    setFormData({ name: '', description: '', focusProblemTag: '' });
                  }}
                >
                  Cancel
                </Button>
              </div>
            </form>
          </Card>
        )}

        <div className="space-y-4">
          {groups.map((group) => (
            <Card key={group.id}>
              <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3 mb-4">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">{group.name}</h3>
                  {group.description && (
                    <p className="text-gray-600 mt-1">{group.description}</p>
                  )}
                  <div className="mt-2">
                    <span className="inline-block px-2 py-1 bg-primary-100 text-primary-700 text-xs rounded">
                      {group.focusProblemTag}
                    </span>
                  </div>
                </div>
                <Button
                  variant="outline"
                  onClick={() =>
                    setSelectedGroup(selectedGroup === group.id ? null : group.id)
                  }
                >
                  {selectedGroup === group.id ? 'Hide' : 'Manage'}
                </Button>
              </div>

              {selectedGroup === group.id && (
                <div className="mt-4 pt-4 border-t">
                  <h4 className="font-semibold mb-3">Add Teacher to Group</h4>
                  <div className="mb-4">
                    <select
                      onChange={(e) => {
                        if (e.target.value) {
                          handleAddTeacher(group.id, e.target.value);
                          e.target.value = '';
                        }
                      }}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                    >
                      <option value="">Select a teacher...</option>
                      {users
                        .filter(
                          (u) =>
                            !group.teachers?.some((t) => t.teacher?.user?.id === u.id)
                        )
                        .map((u) => (
                          <option key={u.id} value={u.id}>
                            {u.name} ({u.email})
                          </option>
                        ))}
                    </select>
                  </div>

                  <h4 className="font-semibold mb-3">Group Members</h4>
                  {group.teachers && group.teachers.length > 0 ? (
                    <div className="space-y-2">
                      {group.teachers.map((member) => (
                        <div
                          key={member.id}
                          className="flex items-center justify-between p-2 bg-gray-50 rounded"
                        >
                          <span className="text-sm">
                            {member.teacher?.user?.name || 'Unknown'}
                          </span>
                          <button
                            onClick={() => handleRemoveTeacher(group.id, member.teacher?.id)}
                            className="text-red-600 hover:text-red-700"
                          >
                            <X className="h-4 w-4" />
                          </button>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <p className="text-gray-600 text-sm">No members yet</p>
                  )}
                </div>
              )}
            </Card>
          ))}

          {groups.length === 0 && (
            <Card>
              <div className="text-center py-8">
                <Users className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-600">No groups created yet.</p>
              </div>
            </Card>
          )}
        </div>
      </div>
    </ProfessionalLayout>
  );
};

export default ProfessionalGroups;

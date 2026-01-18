import { useEffect, useState } from 'react';
import ProfessionalLayout from '../../components/professional/ProfessionalLayout';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { feedbackCycleService } from '../../services/feedbackCycleService';
import { Calendar, Plus, CheckCircle, XCircle } from 'lucide-react';

const ProfessionalFeedbackCycles = () => {
  const [cycles, setCycles] = useState([]);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    startDate: '',
    endDate: '',
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCycles();
  }, []);

  const fetchCycles = async () => {
    try {
      const data = await feedbackCycleService.getAllCycles();
      setCycles(data);
    } catch (error) {
      console.error('Error fetching cycles:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateCycle = async (e) => {
    e.preventDefault();
    try {
      await feedbackCycleService.createCycle(formData);
      setShowCreateForm(false);
      setFormData({ name: '', description: '', startDate: '', endDate: '' });
      fetchCycles();
    } catch (error) {
      console.error('Error creating cycle:', error);
      alert('Failed to create feedback cycle');
    }
  };

  const handleActivate = async (id) => {
    try {
      await feedbackCycleService.activateCycle(id);
      fetchCycles();
    } catch (error) {
      console.error('Error activating cycle:', error);
      alert('Failed to activate cycle');
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
          <h2 className="text-2xl font-bold text-gray-900">Feedback Cycles</h2>
          <Button onClick={() => setShowCreateForm(true)} className="sm:w-auto w-full">
            <Plus className="h-4 w-4 mr-2" />
            Create Cycle
          </Button>
        </div>

        {showCreateForm && (
          <Card className="mb-6">
            <h3 className="text-lg font-semibold mb-4">Create Feedback Cycle</h3>
            <form onSubmit={handleCreateCycle}>
              <Input
                label="Cycle Name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
              />
              <Input
                label="Description"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              />
              <Input
                label="Start Date"
                type="date"
                value={formData.startDate}
                onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                required
              />
              <Input
                label="End Date"
                type="date"
                value={formData.endDate}
                onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                required
              />
              <div className="flex flex-col sm:flex-row gap-3">
                <Button type="submit">Create</Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    setShowCreateForm(false);
                    setFormData({ name: '', description: '', startDate: '', endDate: '' });
                  }}
                >
                  Cancel
                </Button>
              </div>
            </form>
          </Card>
        )}

        <div className="space-y-4">
          {cycles.map((cycle) => (
            <Card key={cycle.id}>
              <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
                <div className="flex-1">
                  <div className="flex items-center space-x-3 mb-2">
                    <h3 className="text-lg font-semibold text-gray-900">{cycle.name}</h3>
                    {cycle.status === 'ACTIVE' ? (
                      <span className="inline-flex items-center px-2 py-1 bg-green-100 text-green-700 text-xs rounded">
                        <CheckCircle className="h-3 w-3 mr-1" />
                        Active
                      </span>
                    ) : (
                      <span className="inline-flex items-center px-2 py-1 bg-gray-100 text-gray-700 text-xs rounded">
                        <XCircle className="h-3 w-3 mr-1" />
                        Draft
                      </span>
                    )}
                  </div>
                  {cycle.description && (
                    <p className="text-gray-600 mb-3">{cycle.description}</p>
                  )}
                  <div className="flex items-center text-sm text-gray-500">
                    <Calendar className="h-4 w-4 mr-1" />
                    {new Date(cycle.startDate).toLocaleDateString()} -{' '}
                    {new Date(cycle.endDate).toLocaleDateString()}
                  </div>
                </div>
                {cycle.status !== 'ACTIVE' && (
                  <Button onClick={() => handleActivate(cycle.id)} variant="secondary">
                    Activate
                  </Button>
                )}
              </div>
            </Card>
          ))}

          {cycles.length === 0 && (
            <Card>
              <div className="text-center py-8">
                <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-600">No feedback cycles created yet.</p>
              </div>
            </Card>
          )}
        </div>
      </div>
    </ProfessionalLayout>
  );
};

export default ProfessionalFeedbackCycles;

import { Plus, Save, X } from 'lucide-react';
import { useEffect, useState } from 'react';

const emptyForm = {
  title: '',
  description: '',
  dueDate: '',
  email: '',
  phoneNumber: '',
};

export default function TaskForm({ editingTask, onCancelEdit, onSubmit, saving }) {
  const [form, setForm] = useState(emptyForm);

  useEffect(() => {
    if (!editingTask) {
      setForm(emptyForm);
      return;
    }

    setForm({
      title: editingTask.title || '',
      description: editingTask.description || '',
      dueDate: toInputDateTime(editingTask.dueDate),
      email: editingTask.email || '',
      phoneNumber: editingTask.phoneNumber || '',
    });
  }, [editingTask]);

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    await onSubmit({
      ...form,
      dueDate: form.dueDate || null,
    });
    if (!editingTask) {
      setForm(emptyForm);
    }
  }

  return (
    <form className="task-form" onSubmit={handleSubmit}>
      <div className="form-row">
        <label>
          Title
          <input name="title" value={form.title} onChange={updateField} required />
        </label>
        <label>
          Due date
          <input type="datetime-local" name="dueDate" value={form.dueDate} onChange={updateField} />
        </label>
      </div>

      <label>
        Description
        <textarea name="description" value={form.description} onChange={updateField} rows="3" />
      </label>

      <div className="form-row">
        <label>
          Email
          <input type="email" name="email" value={form.email} onChange={updateField} />
        </label>
        <label>
          Phone
          <input name="phoneNumber" value={form.phoneNumber} onChange={updateField} placeholder="+91XXXXXXXXXX" />
        </label>
      </div>

      <div className="form-actions">
        <button type="submit" disabled={saving} title={editingTask ? 'Save task' : 'Create task'}>
          {editingTask ? <Save size={18} /> : <Plus size={18} />}
          {editingTask ? 'Save' : 'Add'}
        </button>
        {editingTask && (
          <button type="button" className="secondary" onClick={onCancelEdit} title="Cancel edit">
            <X size={18} />
            Cancel
          </button>
        )}
      </div>
    </form>
  );
}

function toInputDateTime(value) {
  if (!value) {
    return '';
  }
  return value.slice(0, 16);
}

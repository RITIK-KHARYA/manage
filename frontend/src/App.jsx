import { useEffect, useMemo, useState } from 'react';
import Loader from './components/Loader.jsx';
import Navbar from './components/Navbar.jsx';
import TaskForm from './components/TaskForm.jsx';
import TaskList from './components/TaskList.jsx';
import { completeTask, createTask, deleteTask, getTasks, updateTask } from './services/api.js';

export default function App() {
  const [tasks, setTasks] = useState([]);
  const [editingTask, setEditingTask] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadTasks();
  }, []);

  const stats = useMemo(() => {
    const completed = tasks.filter((task) => task.completed).length;
    return {
      total: tasks.length,
      completed,
      open: tasks.length - completed,
    };
  }, [tasks]);

  async function loadTasks() {
    try {
      setError('');
      setLoading(true);
      setTasks(await getTasks());
    } catch (exception) {
      setError(readError(exception));
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(payload) {
    try {
      setError('');
      setSaving(true);
      if (editingTask) {
        await updateTask(editingTask.id, { ...payload, completed: editingTask.completed });
        setEditingTask(null);
      } else {
        await createTask(payload);
      }
      await loadTasks();
    } catch (exception) {
      setError(readError(exception));
    } finally {
      setSaving(false);
    }
  }

  async function handleComplete(id) {
    try {
      setError('');
      await completeTask(id);
      await loadTasks();
    } catch (exception) {
      setError(readError(exception));
    }
  }

  async function handleDelete(id) {
    try {
      setError('');
      await deleteTask(id);
      if (editingTask?.id === id) {
        setEditingTask(null);
      }
      await loadTasks();
    } catch (exception) {
      setError(readError(exception));
    }
  }

  return (
    <>
      <Navbar />
      <main className="app-shell">
        <section className="workspace">
          <aside className="panel">
            <div className="panel-heading">
              <h1>{editingTask ? 'Edit task' : 'Create task'}</h1>
              <p>{stats.open} open, {stats.completed} complete</p>
            </div>
            <TaskForm
              editingTask={editingTask}
              onCancelEdit={() => setEditingTask(null)}
              onSubmit={handleSubmit}
              saving={saving}
            />
          </aside>

          <section className="content">
            <div className="content-header">
              <div>
                <h2>Tasks</h2>
                <p>{stats.total} total</p>
              </div>
              <button type="button" className="secondary" onClick={loadTasks}>
                Refresh
              </button>
            </div>

            {error && <div className="error-banner">{error}</div>}
            {loading ? (
              <Loader />
            ) : (
              <TaskList
                tasks={tasks}
                onComplete={handleComplete}
                onDelete={handleDelete}
                onEdit={setEditingTask}
              />
            )}
          </section>
        </section>
      </main>
    </>
  );
}

function readError(exception) {
  return exception.response?.data?.message || exception.message || 'Something went wrong';
}

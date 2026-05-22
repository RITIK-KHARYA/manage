import TaskItem from './TaskItem.jsx';

export default function TaskList({ tasks, onComplete, onDelete, onEdit }) {
  if (tasks.length === 0) {
    return <div className="empty-state">No tasks yet.</div>;
  }

  return (
    <section className="task-list">
      {tasks.map((task) => (
        <TaskItem
          key={task.id}
          task={task}
          onComplete={onComplete}
          onDelete={onDelete}
          onEdit={onEdit}
        />
      ))}
    </section>
  );
}

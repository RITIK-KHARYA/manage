import { Check, Clock, Edit3, Mail, Phone, Trash2 } from 'lucide-react';

export default function TaskItem({ task, onComplete, onDelete, onEdit }) {
  return (
    <article className={`task-item ${task.completed ? 'completed' : ''}`}>
      <div className="task-main">
        <div>
          <h3>{task.title}</h3>
          {task.description && <p>{task.description}</p>}
        </div>
        <span className={`status ${task.completed ? 'done' : 'open'}`}>
          {task.completed ? 'Done' : 'Open'}
        </span>
      </div>

      <div className="task-meta">
        {task.dueDate && (
          <span>
            <Clock size={15} />
            {formatDate(task.dueDate)}
          </span>
        )}
        {task.email && (
          <span>
            <Mail size={15} />
            {task.email}
          </span>
        )}
        {task.phoneNumber && (
          <span>
            <Phone size={15} />
            {task.phoneNumber}
          </span>
        )}
      </div>

      <div className="task-actions">
        {!task.completed && (
          <button type="button" className="icon-button success" onClick={() => onComplete(task.id)} title="Mark complete">
            <Check size={18} />
          </button>
        )}
        <button type="button" className="icon-button" onClick={() => onEdit(task)} title="Edit task">
          <Edit3 size={18} />
        </button>
        <button type="button" className="icon-button danger" onClick={() => onDelete(task.id)} title="Delete task">
          <Trash2 size={18} />
        </button>
      </div>
    </article>
  );
}

function formatDate(value) {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
}

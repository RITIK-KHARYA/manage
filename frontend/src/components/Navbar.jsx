import { CheckSquare } from 'lucide-react';

export default function Navbar() {
  return (
    <header className="navbar">
      <div className="brand">
        <CheckSquare size={24} />
        <span>Todo App</span>
      </div>
    </header>
  );
}

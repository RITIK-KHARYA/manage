import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
});

export async function getTasks() {
  const response = await api.get('/tasks');
  return response.data.data;
}

export async function createTask(payload) {
  const response = await api.post('/tasks', payload);
  return response.data.data;
}

export async function updateTask(id, payload) {
  const response = await api.put(`/tasks/${id}`, payload);
  return response.data.data;
}

export async function completeTask(id) {
  const response = await api.patch(`/tasks/${id}/complete`);
  return response.data.data;
}

export async function deleteTask(id) {
  await api.delete(`/tasks/${id}`);
}

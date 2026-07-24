import axios from 'axios';
import { safeStorage } from './safeStorage';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8081/api/v1',
  headers: { 'Content-Type': 'application/json' },
});

apiClient.interceptors.request.use((config) => {
  const token = safeStorage.getItem('cc_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      safeStorage.removeItem('cc_token');
      safeStorage.removeItem('cc_user');
      window.location.href = '/login';
    }
    const message = error.response?.data?.message || 'Erreur réseau';
    return Promise.reject(new Error(message));
  }
);

export default apiClient;

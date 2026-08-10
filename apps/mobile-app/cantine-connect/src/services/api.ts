import axios from 'axios'
import { router } from 'expo-router'
import { secureStorage } from './secureStorage'

// Même backend Spring Boot que client-frontend (web) — aucun nouvel endpoint,
// aucun nouveau service : cette app mobile est un second client du même
// server-backend Cantine Connect existant.
const API_URL = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8081/api/v1'
const TOKEN_KEY = 'cc_token'

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use(async (config) => {
  const token = await secureStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      await secureStorage.removeItem(TOKEN_KEY)
      router.replace('/(auth)/login')
    }
    const message = error.response?.data?.message || 'Erreur réseau'
    return Promise.reject(new Error(message))
  }
)

export { TOKEN_KEY }
export default api

import { createContext, useState, useEffect } from 'react'
import apiClient from '../services/apiClient'
import { safeStorage } from '../services/safeStorage'

export const AuthContext = createContext(null)

const TOKEN_KEY = 'cc_token'
const USER_KEY  = 'cc_user'

export function AuthProvider({ children }) {
  const [user, setUser]       = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = safeStorage.getItem(TOKEN_KEY)
    const saved = safeStorage.getItem(USER_KEY)
    if (token && saved) {
      apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`
      try {
        setUser(JSON.parse(saved))
      } catch {
        safeStorage.removeItem(TOKEN_KEY)
        safeStorage.removeItem(USER_KEY)
      }
    }
    setLoading(false)
  }, [])

  const login = (authResponse) => {
    const { token, ...userData } = authResponse
    safeStorage.setItem(TOKEN_KEY, token)
    safeStorage.setItem(USER_KEY, JSON.stringify(userData))
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`
    setUser(userData)
  }

  const logout = () => {
    safeStorage.removeItem(TOKEN_KEY)
    safeStorage.removeItem(USER_KEY)
    delete apiClient.defaults.headers.common['Authorization']
    setUser(null)
  }

  const updateUser = (partial) => {
    setUser((prev) => {
      const next = { ...prev, ...partial }
      safeStorage.setItem(USER_KEY, JSON.stringify(next))
      return next
    })
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, updateUser, loading, isAuthenticated: Boolean(user) }}>
      {children}
    </AuthContext.Provider>
  )
}

import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import { authService, ROLE_KEY, type AuthResponse } from '../services/authService'
import { secureStorage } from '../services/secureStorage'
import { TOKEN_KEY } from '../services/api'

interface AuthContextValue {
  isAuthenticated: boolean
  isLoading: boolean
  role: string | null
  login: (email: string, motDePasse: string) => Promise<AuthResponse>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setAuthenticated] = useState(false)
  const [isLoading, setLoading] = useState(true)
  const [role, setRole] = useState<string | null>(null)

  useEffect(() => {
    Promise.all([secureStorage.getItem(TOKEN_KEY), secureStorage.getItem(ROLE_KEY)]).then(
      ([token, persistedRole]) => {
        setAuthenticated(Boolean(token))
        setRole(persistedRole)
        setLoading(false)
      }
    )
  }, [])

  const login = async (email: string, motDePasse: string) => {
    const auth = await authService.login(email, motDePasse)
    setAuthenticated(true)
    setRole(auth.role)
    return auth
  }

  const logout = async () => {
    await authService.logout()
    setAuthenticated(false)
    setRole(null)
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, isLoading, role, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth doit être utilisé dans un <AuthProvider>')
  return ctx
}

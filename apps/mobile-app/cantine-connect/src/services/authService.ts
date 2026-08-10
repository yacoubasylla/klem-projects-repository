import api, { TOKEN_KEY } from './api'
import { secureStorage } from './secureStorage'

export const ROLE_KEY = 'cc_role'

export interface AuthResponse {
  token: string
  role: string
  doitChangerMotDePasse?: boolean
}

export const authService = {
  async login(email: string, motDePasse: string): Promise<AuthResponse> {
    const res = await api.post('/auth/login', { email, motDePasse })
    const auth = res.data.data as AuthResponse
    await secureStorage.setItem(TOKEN_KEY, auth.token)
    await secureStorage.setItem(ROLE_KEY, auth.role)
    return auth
  },
  async logout(): Promise<void> {
    await secureStorage.removeItem(TOKEN_KEY)
    await secureStorage.removeItem(ROLE_KEY)
  },
}

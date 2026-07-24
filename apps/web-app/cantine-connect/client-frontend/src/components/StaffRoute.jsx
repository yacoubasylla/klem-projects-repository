import { Navigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

export default function StaffRoute({ children }) {
  const { user } = useAuth()
  if (user?.role === 'PARENT') return <Navigate to="/dashboard" replace />
  return children
}

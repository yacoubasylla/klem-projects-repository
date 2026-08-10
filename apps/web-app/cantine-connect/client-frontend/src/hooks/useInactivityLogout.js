import { useEffect, useRef, useCallback } from 'react'
import { useNavigate } from 'react-router'
import { useAuth } from './useAuth'

const DEFAULT_TIMEOUT_MS = 15 * 60 * 1000 // 15 min d'inactivité
const ACTIVITY_EVENTS = ['mousemove', 'mousedown', 'keydown', 'touchstart', 'scroll', 'wheel']

/**
 * Déconnecte automatiquement l'utilisateur après une période d'inactivité
 * et le redirige vers la page de connexion. À monter une seule fois, dans
 * une zone déjà protégée par ProtectedRoute (l'utilisateur est authentifié).
 */
export function useInactivityLogout(timeoutMs = DEFAULT_TIMEOUT_MS) {
  const { logout } = useAuth()
  const navigate = useNavigate()
  const timerRef = useRef(null)

  const resetTimer = useCallback(() => {
    clearTimeout(timerRef.current)
    timerRef.current = setTimeout(() => {
      logout()
      navigate('/login', { replace: true, state: { reason: 'inactivite' } })
    }, timeoutMs)
  }, [logout, navigate, timeoutMs])

  useEffect(() => {
    resetTimer()
    ACTIVITY_EVENTS.forEach((evt) => window.addEventListener(evt, resetTimer))
    return () => {
      ACTIVITY_EVENTS.forEach((evt) => window.removeEventListener(evt, resetTimer))
      clearTimeout(timerRef.current)
    }
  }, [resetTimer])
}

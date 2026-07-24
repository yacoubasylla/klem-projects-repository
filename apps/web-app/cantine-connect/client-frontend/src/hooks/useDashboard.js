import { useState, useEffect, useCallback } from 'react'
import { dashboardService } from '../services/dashboardService'

export function useDashboard() {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const charger = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await dashboardService.getStats()
      setStats(data)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { charger() }, [charger])

  return { stats, loading, error, recharger: charger }
}

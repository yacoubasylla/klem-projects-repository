import { useState, useEffect } from 'react'
import { etablissementService } from '../services/etablissementService'

export function useClasses(etablissementId) {
  const [classes, setClasses] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!etablissementId) { setClasses([]); return }
    setLoading(true)
    etablissementService
      .getClasses(etablissementId)
      .then(setClasses)
      .catch(() => setClasses([]))
      .finally(() => setLoading(false))
  }, [etablissementId])

  return { classes, loading }
}

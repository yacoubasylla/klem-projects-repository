import { useState, useEffect, useCallback } from 'react'
import { etablissementService } from '../services/etablissementService'

export function useEtablissements(enabled = true) {
  const [etablissements, setEtablissements] = useState([])
  const [loading, setLoading] = useState(enabled)
  const [error, setError] = useState(null)

  const charger = useCallback(async () => {
    if (!enabled) return
    setLoading(true)
    setError(null)
    try {
      const data = await etablissementService.lister()
      setEtablissements(data)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [enabled])

  useEffect(() => { charger() }, [charger])

  const creer = async (dto) => {
    const created = await etablissementService.creer(dto)
    setEtablissements((prev) => [...prev, created])
    return created
  }

  const modifier = async (id, dto) => {
    const updated = await etablissementService.modifier(id, dto)
    setEtablissements((prev) => prev.map((e) => (e.id === id ? updated : e)))
    return updated
  }

  const supprimer = async (id) => {
    await etablissementService.supprimer(id)
    setEtablissements((prev) => prev.filter((e) => e.id !== id))
  }

  return { etablissements, loading, error, creer, modifier, supprimer, recharger: charger }
}

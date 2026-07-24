import { useState, useEffect, useCallback } from 'react'
import { configService } from '../services/configService'

export function useConfigurations() {
  const [configs,  setConfigs]  = useState([])
  const [loading,  setLoading]  = useState(false)
  const [error,    setError]    = useState(null)

  const charger = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setConfigs(await configService.lister())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { charger() }, [charger])

  const modifier = async (cle, valeur) => {
    const updated = await configService.modifier(cle, valeur)
    setConfigs(prev => prev.map(c => c.cle === cle ? updated : c))
    return updated
  }

  return { configs, loading, error, modifier, recharger: charger }
}

export function useConfigValeur(cle, defaultValue = 'false') {
  const [valeur,  setValeur]  = useState(defaultValue)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    configService.getParCle(cle)
      .then(cfg => setValeur(cfg?.valeur ?? defaultValue))
      .catch(() => setValeur(defaultValue))
      .finally(() => setLoading(false))
  }, [cle, defaultValue])

  return { valeur, loading }
}

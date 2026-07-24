import { useState, useCallback, useEffect } from 'react'
import { scanService } from '../services/scanService'
import { cacheOfflineService } from '../services/cacheOfflineService'

export function useScan() {
  const [result,          setResult]          = useState(null)
  const [scanning,        setScanning]        = useState(false)
  const [error,           setError]           = useState(null)
  const [isOnline,        setIsOnline]        = useState(navigator.onLine)
  const [cache,           setCache]           = useState(() => cacheOfflineService.charger())
  const [refreshingCache, setRefreshingCache] = useState(false)

  useEffect(() => {
    const up   = () => setIsOnline(true)
    const down = () => setIsOnline(false)
    window.addEventListener('online',  up)
    window.addEventListener('offline', down)
    return () => { window.removeEventListener('online', up); window.removeEventListener('offline', down) }
  }, [])

  const scanner = useCallback(async (token) => {
    const t = token.trim()
    if (!t) return
    setScanning(true)
    setError(null)
    setResult(null)
    try {
      if (isOnline) {
        const r = await scanService.scanner(t)
        setResult({ ...r, source: 'online' })
      } else {
        throw new Error('offline')
      }
    } catch {
      // Fallback cache offline si réseau indisponible
      const r = cacheOfflineService.scanner(t)
      if (r) {
        setResult({ ...r, source: 'offline' })
      } else {
        setError(isOnline
          ? 'Erreur réseau — cache hors-ligne non disponible.'
          : 'Hors ligne — veuillez télécharger le cache avant de déconnecter.')
      }
    } finally {
      setScanning(false)
    }
  }, [isOnline])

  const rafraichirCache = useCallback(async () => {
    setRefreshingCache(true)
    setError(null)
    try {
      const data = await scanService.getCache()
      cacheOfflineService.sauvegarder(data)
      setCache(cacheOfflineService.charger())
    } catch (e) {
      setError('Impossible de télécharger le cache : ' + e.message)
    } finally {
      setRefreshingCache(false)
    }
  }, [])

  const reinitialiser = useCallback(() => { setResult(null); setError(null) }, [])

  return {
    result, scanning, error,
    isOnline, cache, refreshingCache,
    scanner, rafraichirCache, reinitialiser,
  }
}

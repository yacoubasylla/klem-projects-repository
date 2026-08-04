import { useState, useEffect, useCallback } from 'react'
import { configService } from '../services/configService'
import apiClient from '../services/apiClient'

const BACKEND_ORIGIN = (apiClient.defaults.baseURL || '').replace(/\/api\/v1\/?$/, '')
const NOM_DEFAUT = 'Cantine Connect'

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

// Personnalisation par client (branding) : nom + logo affichés dans l'en-tête,
// avec repli sur les valeurs par défaut Cantine Connect si non paramétrés ou
// si l'appel échoue (ex. page publique chargée avant que l'API ne réponde).
export function useOrganisationBranding() {
  const [branding, setBranding] = useState({ nom: NOM_DEFAUT, logoUrl: '' })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let actif = true
    configService.lister()
      .then((configs) => {
        if (!actif) return
        const carte = Object.fromEntries(configs.map((c) => [c.cle, c.valeur]))
        const logo = carte.ORGANISATION_LOGO_URL
        setBranding({
          nom: carte.ORGANISATION_NOM?.trim() || NOM_DEFAUT,
          // Chemin relatif (upload via /configurations/logo) -> préfixer par l'origine
          // de l'API ; URL déjà absolue (logo hébergé ailleurs) -> utiliser telle quelle.
          logoUrl: logo ? (/^https?:\/\//.test(logo) ? logo : `${BACKEND_ORIGIN}${logo}`) : '',
        })
      })
      .catch(() => { if (actif) setBranding({ nom: NOM_DEFAUT, logoUrl: '' }) })
      .finally(() => { if (actif) setLoading(false) })
    return () => { actif = false }
  }, [])

  return { ...branding, loading }
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

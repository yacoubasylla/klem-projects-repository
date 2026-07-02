import { safeStorage } from './safeStorage'

const CACHE_KEY = 'cc_scan_cache'
const CACHE_TTL_MS = 24 * 60 * 60 * 1000

export const cacheOfflineService = {
  sauvegarder(data) {
    safeStorage.setItem(CACHE_KEY, JSON.stringify({ data, savedAt: Date.now() }))
  },

  charger() {
    try {
      const stored = safeStorage.getItem(CACHE_KEY)
      if (!stored) return null
      const { data, savedAt } = JSON.parse(stored)
      if (Date.now() - savedAt > CACHE_TTL_MS) {
        safeStorage.removeItem(CACHE_KEY)
        return null
      }
      return { data, savedAt }
    } catch {
      return null
    }
  },

  ageTexte(savedAt) {
    const diffMs = Date.now() - savedAt
    const diffMin = Math.floor(diffMs / 60000)
    if (diffMin < 1)  return 'à l\'instant'
    if (diffMin < 60) return `il y a ${diffMin} min`
    const diffH = Math.floor(diffMin / 60)
    return `il y a ${diffH}h`
  },

  scanner(token) {
    const cache = this.charger()
    if (!cache) return null
    const entree = cache.data.find((e) => e.qrCodeToken === token.trim())
    if (!entree) {
      return { acces: 'REFUSÉ', resultat: 'REFUSE', motifRefus: 'QR_CODE_INCONNU', nomComplet: 'Inconnu', classeNom: '—', etablissementNom: '—' }
    }
    const nomComplet = `${entree.prenom} ${entree.nom}`
    if (entree.statutAcces === 'SUSPENDU') {
      return { acces: 'REFUSÉ', resultat: 'REFUSE', motifRefus: 'STATUT_SUSPENDU', nomComplet, classeNom: entree.classeNom, etablissementNom: entree.etablissementNom }
    }
    if (entree.statutAcces === 'EN_ATTENTE_PAIEMENT') {
      return { acces: 'REFUSÉ', resultat: 'REFUSE', motifRefus: 'STATUT_EN_ATTENTE_PAIEMENT', nomComplet, classeNom: entree.classeNom, etablissementNom: entree.etablissementNom }
    }
    return {
      acces: 'ACCORDÉ', resultat: 'ACCORDE', motifRefus: null,
      nomComplet, classeNom: entree.classeNom, etablissementNom: entree.etablissementNom,
      passageId: null, heurePassage: new Date().toISOString(),
    }
  },

  supprimer() {
    safeStorage.removeItem(CACHE_KEY)
  },
}

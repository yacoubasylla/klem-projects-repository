import { useState, useCallback } from 'react'
import { paiementService } from '../services/paiementService'
import { scanService } from '../services/scanService'

// Garde-fou : au-delà de 50 pages (10 000 lignes à 200/page), on arrête la collecte
// plutôt que de bloquer le navigateur sur une période trop large.
const MAX_PAGES = 50
const PAGE_SIZE = 200

async function fetchAll(listerFn, params) {
  let page = 0
  let all = []
  let totalElements = 0
  while (page < MAX_PAGES) {
    const result = await listerFn({ ...params, page, size: PAGE_SIZE })
    const content = result?.content ?? []
    totalElements = result?.totalElements ?? content.length
    all = all.concat(content)
    if (all.length >= totalElements || content.length === 0) break
    page += 1
  }
  return { items: all, tronque: all.length < totalElements, totalElements }
}

export function useRapports() {
  const [paiements, setPaiements] = useState([])
  const [passages, setPassages]   = useState([])
  const [tronque, setTronque]     = useState(false)
  const [loading, setLoading]     = useState(false)
  const [error, setError]         = useState(null)

  const generer = useCallback(async (filtres) => {
    setLoading(true)
    setError(null)
    try {
      const paiementsParams = { dateDebut: filtres.dateDebut, dateFin: filtres.dateFin }
      const passagesParams  = { dateDebut: filtres.dateDebut, dateFin: filtres.dateFin }
      if (filtres.etablissementId) passagesParams.etablissementId = filtres.etablissementId

      const [resPaiements, resPassages] = await Promise.all([
        fetchAll(paiementService.lister, paiementsParams),
        fetchAll(scanService.getPassages, passagesParams),
      ])

      setPaiements(resPaiements.items)
      setPassages(resPassages.items)
      setTronque(resPaiements.tronque || resPassages.tronque)
    } catch (e) {
      setError(e.response?.data?.message ?? e.message ?? 'Erreur de génération du rapport')
    } finally {
      setLoading(false)
    }
  }, [])

  const resume = {
    montantEncaisse: paiements
      .filter((p) => p.statut === 'ACCEPTE')
      .reduce((sum, p) => sum + Number(p.montant ?? 0), 0),
    nbParStatut: paiements.reduce((acc, p) => {
      acc[p.statut] = (acc[p.statut] ?? 0) + 1
      return acc
    }, {}),
    nbPaiements: paiements.length,
    nbPassagesAccordes: passages.filter((p) => p.resultat === 'ACCORDE').length,
    nbPassagesRefuses: passages.filter((p) => p.resultat === 'REFUSE').length,
    nbPassages: passages.length,
  }
  resume.tauxAcces = resume.nbPassages > 0
    ? Math.round((resume.nbPassagesAccordes / resume.nbPassages) * 1000) / 10
    : null

  return { paiements, passages, resume, tronque, loading, error, generer }
}

import { useState, useCallback } from 'react'
import { paiementService } from '../services/paiementService'
import { scanService } from '../services/scanService'
import { eleveService } from '../services/eleveService'
import { demandeAccesService } from '../services/demandeAccesService'

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
  const [eleves, setEleves]       = useState([])
  const [demandes, setDemandes]   = useState([])
  const [tronque, setTronque]     = useState(false)
  const [loading, setLoading]     = useState(false)
  const [error, setError]         = useState(null)

  const generer = useCallback(async (filtres, { inclureDemandes = false } = {}) => {
    setLoading(true)
    setError(null)
    try {
      const paiementsParams = { dateDebut: filtres.dateDebut, dateFin: filtres.dateFin }
      const passagesParams  = { dateDebut: filtres.dateDebut, dateFin: filtres.dateFin }
      const elevesParams    = {}
      if (filtres.etablissementId) {
        passagesParams.etablissementId = filtres.etablissementId
        elevesParams.etablissementId = filtres.etablissementId
      }

      const promises = [
        fetchAll(paiementService.lister, paiementsParams),
        fetchAll(scanService.getPassages, passagesParams),
        fetchAll(eleveService.lister, elevesParams),
        inclureDemandes ? fetchAll(demandeAccesService.lister, {}) : Promise.resolve({ items: [], tronque: false }),
      ]

      const [resPaiements, resPassages, resEleves, resDemandes] = await Promise.all(promises)

      // PaiementResponseDTO n'expose pas etablissementId — on filtre côté client
      // en croisant avec les eleveId de l'établissement sélectionné (déjà filtrés
      // côté serveur via GET /eleves?etablissementId=...).
      let paiementsFiltres = resPaiements.items
      if (filtres.etablissementId) {
        const eleveIds = new Set(resEleves.items.map((e) => e.id))
        paiementsFiltres = paiementsFiltres.filter((p) => eleveIds.has(p.eleveId))
      }

      // Les demandes d'accès ne sont pas rattachées à un établissement (le
      // parent ne choisit pas d'école dans sa demande) — seul le filtre de
      // période s'applique, sur la date de soumission.
      const demandesFiltrees = resDemandes.items.filter((d) => {
        const date = d.dateSoumission?.slice(0, 10)
        return date && date >= filtres.dateDebut && date <= filtres.dateFin
      })

      setPaiements(paiementsFiltres)
      setPassages(resPassages.items)
      setEleves(resEleves.items)
      setDemandes(demandesFiltrees)
      setTronque(resPaiements.tronque || resPassages.tronque || resEleves.tronque)
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

  // Photographie actuelle des élèves (statut d'accès / période d'abonnement) —
  // indépendante de la plage de dates, qui ne s'applique qu'aux événements
  // (paiements, passages, demandes), pas à un état courant non historisé.
  const acces = {
    total: eleves.length,
    parStatut: eleves.reduce((acc, e) => {
      acc[e.statutAcces] = (acc[e.statutAcces] ?? 0) + 1
      return acc
    }, {}),
    parPeriode: eleves.reduce((acc, e) => {
      const cle = e.periodeAbonnement ?? 'NON_DEFINI'
      acc[cle] = (acc[cle] ?? 0) + 1
      return acc
    }, {}),
  }

  const demandesResume = {
    total: demandes.length,
    parStatut: demandes.reduce((acc, d) => {
      acc[d.statut] = (acc[d.statut] ?? 0) + 1
      return acc
    }, {}),
    delaiMoyenHeures: (() => {
      const traitees = demandes.filter((d) => d.dateTraitement)
      if (traitees.length === 0) return null
      const totalHeures = traitees.reduce((sum, d) => {
        const debut = new Date(d.dateSoumission).getTime()
        const fin = new Date(d.dateTraitement).getTime()
        return sum + (fin - debut) / 3_600_000
      }, 0)
      return Math.round((totalHeures / traitees.length) * 10) / 10
    })(),
  }

  return {
    paiements, passages, eleves, demandes,
    resume, acces, demandesResume,
    tronque, loading, error, generer,
  }
}

import { useState, useEffect, useCallback } from 'react'
import { scanService } from '../services/scanService'

const today = () => new Date().toISOString().split('T')[0]

export function usePassages() {
  const [filtres, setFiltres] = useState({
    dateDebut:       today(),
    dateFin:         today(),
    etablissementId: '',
    resultat:        '',
    search:          '',
  })

  const [passages,     setPassages]     = useState([])
  const [total,        setTotal]        = useState(0)
  const [page,         setPage]         = useState(0)
  const [rowsPerPage,  setRowsPerPage]  = useState(25)
  const [loading,      setLoading]      = useState(false)
  const [error,        setError]        = useState(null)

  const charger = useCallback(async (f = filtres, p = page, rpp = rowsPerPage) => {
    setLoading(true)
    setError(null)
    try {
      const params = {
        dateDebut: f.dateDebut,
        dateFin:   f.dateFin,
        page:      p,
        size:      rpp,
        sort:      'heurePassage,desc',
      }
      if (f.etablissementId) params.etablissementId = f.etablissementId
      if (f.resultat)        params.resultat        = f.resultat
      if (f.search?.trim())  params.search          = f.search.trim()

      const data = await scanService.getPassages(params)
      setPassages(data?.content ?? [])
      setTotal(data?.totalElements ?? 0)
    } catch (e) {
      setError(e.response?.data?.message ?? e.message ?? 'Erreur de chargement')
    } finally {
      setLoading(false)
    }
  }, [filtres, page, rowsPerPage])

  useEffect(() => { charger() }, [charger])

  const setFiltre = (name, value) => {
    const next = { ...filtres, [name]: value }
    setFiltres(next)
    setPage(0)
  }

  const handlePageChange = (_, newPage) => setPage(newPage)

  const handleRowsPerPageChange = (e) => {
    setRowsPerPage(parseInt(e.target.value, 10))
    setPage(0)
  }

  const modifier = async (id, dto) => {
    const updated = await scanService.modifierPassage(id, dto)
    setPassages((prev) => prev.map((p) => ((p.passageId ?? p.id) === id ? updated : p)))
    return updated
  }

  const supprimer = async (id) => {
    await scanService.supprimerPassage(id)
    setPassages((prev) => prev.filter((p) => (p.passageId ?? p.id) !== id))
    setTotal((prev) => prev - 1)
  }

  return {
    passages, total, page, rowsPerPage, loading, error,
    filtres, setFiltre,
    handlePageChange, handleRowsPerPageChange,
    recharger: charger,
    modifier, supprimer,
  }
}

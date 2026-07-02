import { useState, useEffect, useCallback } from 'react'
import { eleveService } from '../services/eleveService'

export function useEleves(filtres = {}) {
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(10)
  const [data, setData] = useState({ content: [], totalElements: 0 })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const filtresKey = JSON.stringify(filtres)

  const charger = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const result = await eleveService.lister({
        ...JSON.parse(filtresKey),
        page,
        size: rowsPerPage,
        sort: 'nom,asc',
      })
      setData(result)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, rowsPerPage, filtresKey])

  useEffect(() => { charger() }, [charger])

  const creer = async (dto) => {
    const created = await eleveService.creer(dto)
    charger()
    return created
  }

  const modifier = async (id, dto) => {
    const updated = await eleveService.modifier(id, dto)
    charger()
    return updated
  }

  const changerStatut = async (id, statut) => {
    const updated = await eleveService.changerStatut(id, statut)
    charger()
    return updated
  }

  const supprimer = async (id) => {
    await eleveService.supprimer(id)
    charger()
  }

  return {
    eleves: data.content,
    total: data.totalElements,
    page, setPage,
    rowsPerPage, setRowsPerPage,
    loading, error,
    creer, modifier, changerStatut, supprimer,
    recharger: charger,
  }
}

import { useState, useEffect, useCallback } from 'react'
import { paiementService } from '../services/paiementService'

export function usePaiements(filtres = {}) {
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(20)
  const [data, setData] = useState({ content: [], totalElements: 0 })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const filtresKey = JSON.stringify(filtres)

  const charger = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const result = await paiementService.lister({
        ...JSON.parse(filtresKey),
        page,
        size: rowsPerPage,
        sort: 'dateCreation,desc',
      })
      setData(result ?? { content: [], totalElements: 0 })
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, rowsPerPage, filtresKey])

  useEffect(() => { charger() }, [charger])

  const initier = async (dto) => {
    const result = await paiementService.initier(dto)
    charger()
    return result
  }

  const modifier = async (id, dto) => {
    const updated = await paiementService.modifier(id, dto)
    setData((prev) => ({
      ...prev,
      content: prev.content.map((p) => (p.id === id ? updated : p)),
    }))
    return updated
  }

  const supprimer = async (id) => {
    await paiementService.supprimer(id)
    setData((prev) => ({
      ...prev,
      content: prev.content.filter((p) => p.id !== id),
      totalElements: prev.totalElements - 1,
    }))
  }

  return {
    paiements: data.content,
    total: data.totalElements,
    page, setPage,
    rowsPerPage, setRowsPerPage,
    loading, error,
    initier, modifier, supprimer,
    recharger: charger,
  }
}

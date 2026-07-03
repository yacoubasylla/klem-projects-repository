import { useState, useEffect, useCallback } from 'react'
import { parentService } from '../services/parentService'

export function useParents(filtres = {}) {
  const [page, setPage]           = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(10)
  const [data, setData]           = useState({ content: [], totalElements: 0 })
  const [loading, setLoading]     = useState(false)
  const [error, setError]         = useState(null)

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const filtresKey = JSON.stringify(filtres)

  const charger = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const result = await parentService.lister({ ...JSON.parse(filtresKey), page, size: rowsPerPage })
      setData(result)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, rowsPerPage, filtresKey])

  useEffect(() => { charger() }, [charger])

  const creer = async (payload) => {
    const created = await parentService.creer(payload)
    await charger()
    return created
  }

  const modifierEnfants = async (id, eleveIds) => {
    const updated = await parentService.modifierEnfants(id, eleveIds)
    await charger()
    return updated
  }

  const supprimer = async (id) => {
    await parentService.supprimer(id)
    await charger()
  }

  return {
    parents: data.content ?? [],
    total: data.totalElements ?? 0,
    page, setPage,
    rowsPerPage, setRowsPerPage,
    loading, error,
    creer, modifierEnfants, supprimer,
  }
}

import { useState, useEffect, useCallback } from 'react'
import { utilisateurService } from '../services/utilisateurService'

export function useUtilisateurs(filtres = {}) {
  const [page, setPage]               = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(20)
  const [data, setData]               = useState({ content: [], totalElements: 0 })
  const [loading, setLoading]         = useState(false)
  const [error, setError]             = useState(null)

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const filtresKey = JSON.stringify(filtres)

  const charger = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const result = await utilisateurService.lister({
        ...JSON.parse(filtresKey),
        page, size: rowsPerPage, sort: 'nom,asc',
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
    const created = await utilisateurService.creer(dto)
    charger()
    return created
  }

  const modifier = async (id, dto) => {
    const updated = await utilisateurService.modifier(id, dto)
    charger()
    return updated
  }

  const changerRole = async (id, role) => {
    await utilisateurService.changerRole(id, role)
    charger()
  }

  const desactiver = async (id) => {
    await utilisateurService.desactiver(id)
    charger()
  }

  const reactiver = async (id) => {
    await utilisateurService.reactiver(id)
    charger()
  }

  const supprimer = async (id) => {
    await utilisateurService.supprimer(id)
    charger()
  }

  return {
    utilisateurs: data.content,
    total: data.totalElements,
    page, setPage,
    rowsPerPage, setRowsPerPage,
    loading, error,
    creer, modifier, changerRole, desactiver, reactiver, supprimer,
    recharger: charger,
  }
}

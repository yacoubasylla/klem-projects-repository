import api from './api'

export interface PaiementDTO {
  id: number
  eleveId: number
  eleveNomComplet: string
  referenceInterne: string
  referencePlateforme: string | null
  operateur: string
  montant: number
  devise: string
  telephonePayeur: string
  statut: string
  dateCreation: string
  dateMiseAJour: string
}

interface PageResponse<T> {
  content: T[]
  totalElements: number
}

export const paiementService = {
  // GET /api/v1/paiements — un compte PARENT ne voit que les paiements de ses
  // propres enfants (filtrage fait côté backend, même endpoint que le web).
  async lister(params: Record<string, unknown> = {}): Promise<PageResponse<PaiementDTO>> {
    const res = await api.get('/paiements', { params })
    return res.data.data
  },
}

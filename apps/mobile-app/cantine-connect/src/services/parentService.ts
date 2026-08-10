import api from './api'

export interface EnfantDTO {
  id: number
  matricule: string
  nom: string
  prenom: string
  statutAcces: string
  classeLibelle: string | null
  qrCodeToken: string | null
  solde: number
}

export interface ParentMoiDTO {
  id: number
  nom: string
  prenom: string
  email: string
  telephone: string
  enfants: EnfantDTO[]
}

export const parentService = {
  async getMoi(): Promise<ParentMoiDTO> {
    const res = await api.get('/parents/moi')
    return res.data.data
  },
}

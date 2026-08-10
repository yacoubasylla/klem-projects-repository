import api from './api'

export interface ScanResultDTO {
  acces: string // "ACCORDÉ" ou "REFUSÉ"
  resultat: 'ACCORDE' | 'REFUSE'
  motifRefus: string | null
  eleveId: number
  nomComplet: string
  matricule: string
  classeNom: string
  etablissementNom: string
  passageId: number
  heurePassage: string
}

export const scanService = {
  // Même endpoint que le poste de contrôle réfectoire web (ScanPage.jsx) :
  // POST /api/v1/scan/{qrCodeToken} — aucune logique dupliquée côté mobile.
  async scanner(qrCodeToken: string): Promise<ScanResultDTO> {
    const res = await api.post(`/scan/${qrCodeToken}`)
    return res.data.data
  },
}

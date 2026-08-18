import apiClient from './apiClient'

export const parentService = {
  lister: (params) =>
    apiClient.get('/parents', { params }).then((r) => r.data.data),

  getMoi: () =>
    apiClient.get('/parents/moi').then((r) => r.data.data),

  getById: (id) =>
    apiClient.get(`/parents/${id}`).then((r) => r.data.data),

  creer: (data) =>
    apiClient.post('/parents', data).then((r) => r.data.data),

  modifierEnfants: (id, eleveIds) =>
    apiClient.put(`/parents/${id}/enfants`, eleveIds).then((r) => r.data.data),

  ajouterEnfant: (data) =>
    apiClient.post('/parents/moi/enfants', data).then((r) => r.data.data),

  modifierEnfant: (eleveId, data) =>
    apiClient.put(`/parents/moi/enfants/${eleveId}`, data).then((r) => r.data.data),

  desactiverEnfant: (eleveId) =>
    apiClient.delete(`/parents/moi/enfants/${eleveId}`).then((r) => r.data),

  supprimer: (id) =>
    apiClient.delete(`/parents/${id}`).then((r) => r.data),
}

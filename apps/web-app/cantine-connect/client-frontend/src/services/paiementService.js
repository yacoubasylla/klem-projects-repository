import apiClient from './apiClient'

export const paiementService = {
  lister: (params) =>
    apiClient.get('/paiements', { params }).then((r) => r.data.data),

  getById: (id) =>
    apiClient.get(`/paiements/${id}`).then((r) => r.data.data),

  initier: (data) =>
    apiClient.post('/paiements/initier', data).then((r) => r.data.data),

  modifier: (id, data) =>
    apiClient.put(`/paiements/${id}`, data).then((r) => r.data.data),

  supprimer: (id) =>
    apiClient.delete(`/paiements/${id}`).then((r) => r.data),
}

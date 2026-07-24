import apiClient from './apiClient'

export const scanService = {
  scanner: (qrCodeToken) =>
    apiClient.post(`/scan/${qrCodeToken}`).then((r) => r.data.data),

  getCache: () =>
    apiClient.get('/scan/cache').then((r) => r.data.data),

  getPassages: (params) =>
    apiClient.get('/passages', { params }).then((r) => r.data.data),

  modifierPassage: (id, data) =>
    apiClient.put(`/passages/${id}`, data).then((r) => r.data.data),

  supprimerPassage: (id) =>
    apiClient.delete(`/passages/${id}`).then((r) => r.data),
}

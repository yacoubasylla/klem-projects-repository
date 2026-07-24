import apiClient from './apiClient'

export const utilisateurService = {
  lister: (params) =>
    apiClient.get('/utilisateurs', { params }).then((r) => r.data.data),

  creer: (data) =>
    apiClient.post('/utilisateurs', data).then((r) => r.data.data),

  changerRole: (id, role) =>
    apiClient.patch(`/utilisateurs/${id}/role`, { role }).then((r) => r.data.data),

  modifier: (id, data) =>
    apiClient.put(`/utilisateurs/${id}`, data).then((r) => r.data.data),

  supprimer: (id) =>
    apiClient.delete(`/utilisateurs/${id}/permanent`),

  desactiver: (id) =>
    apiClient.delete(`/utilisateurs/${id}`),

  reactiver: (id) =>
    apiClient.patch(`/utilisateurs/${id}/reactiver`).then((r) => r.data.data),
}

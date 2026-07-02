import apiClient from './apiClient';

export const eleveService = {
  lister: (params) =>
    apiClient.get('/eleves', { params }).then((r) => r.data.data),

  getById: (id) =>
    apiClient.get(`/eleves/${id}`).then((r) => r.data.data),

  creer: (data) =>
    apiClient.post('/eleves', data).then((r) => r.data.data),

  modifier: (id, data) =>
    apiClient.put(`/eleves/${id}`, data).then((r) => r.data.data),

  changerStatut: (id, statut) =>
    apiClient
      .patch(`/eleves/${id}/statut`, null, { params: { statut } })
      .then((r) => r.data.data),

  supprimer: (id) =>
    apiClient.delete(`/eleves/${id}`).then((r) => r.data),

  supprimerDefinitivement: (id) =>
    apiClient.delete(`/eleves/${id}/permanent`).then((r) => r.data),
};

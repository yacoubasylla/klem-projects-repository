import apiClient from './apiClient'

export const demandeAccesService = {
  soumettre: (payload) =>
    apiClient.post('/demandes-acces', payload).then((r) => r.data.data),

  lister: (params) =>
    apiClient.get('/demandes-acces', { params }).then((r) => r.data.data),

  valider: (id) =>
    apiClient.patch(`/demandes-acces/${id}/valider`).then((r) => r.data.data),

  rejeter: (id, motif) =>
    apiClient.patch(`/demandes-acces/${id}/rejeter`, { motif }).then((r) => r.data.data),
}

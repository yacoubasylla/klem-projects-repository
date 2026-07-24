import apiClient from './apiClient';

export const etablissementService = {
  lister: () =>
    apiClient.get('/etablissements').then((r) => r.data.data),

  creer: (data) =>
    apiClient.post('/etablissements', data).then((r) => r.data.data),

  getClasses: (id, anneeScolaire) =>
    apiClient
      .get(`/etablissements/${id}/classes`, { params: { anneeScolaire } })
      .then((r) => r.data.data),

  getNiveaux: (id) =>
    apiClient.get(`/etablissements/${id}/niveaux`).then((r) => r.data.data),

  creerNiveau: (etablissementId, data) =>
    apiClient.post(`/etablissements/${etablissementId}/niveaux`, data).then((r) => r.data.data),

  creerClasse: (niveauId, data) =>
    apiClient.post(`/etablissements/niveaux/${niveauId}/classes`, data).then((r) => r.data.data),

  supprimerNiveau: (niveauId) =>
    apiClient.delete(`/etablissements/niveaux/${niveauId}`).then((r) => r.data),

  supprimerClasse: (classeId) =>
    apiClient.delete(`/etablissements/classes/${classeId}`).then((r) => r.data),

  modifier: (id, data) =>
    apiClient.put(`/etablissements/${id}`, data).then((r) => r.data.data),

  supprimer: (id) =>
    apiClient.delete(`/etablissements/${id}`).then((r) => r.data),

  modifierClasse: (classeId, data) =>
    apiClient.put(`/etablissements/classes/${classeId}`, data).then((r) => r.data.data),

  modifierNiveau: (niveauId, data) =>
    apiClient.put(`/etablissements/niveaux/${niveauId}`, data).then((r) => r.data.data),
};

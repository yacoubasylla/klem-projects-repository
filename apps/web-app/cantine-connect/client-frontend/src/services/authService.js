import apiClient from './apiClient';

export const authService = {
  login: (email, motDePasse) =>
    apiClient.post('/auth/login', { email, motDePasse }).then((r) => r.data.data),

  me: () =>
    apiClient.get('/auth/me').then((r) => r.data.data),

  changerMotDePasse: (motDePasseActuel, nouveauMotDePasse) =>
    apiClient
      .post('/auth/changer-mot-de-passe', { motDePasseActuel, nouveauMotDePasse })
      .then((r) => r.data),
};

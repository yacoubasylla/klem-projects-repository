import apiClient from './apiClient'

export const configService = {
  lister:    ()          => apiClient.get('/configurations').then(r => r.data.data),
  getParCle: (cle)       => apiClient.get(`/configurations/${cle}`).then(r => r.data.data),
  modifier:  (cle, valeur) => apiClient.put(`/configurations/${cle}`, { valeur }).then(r => r.data.data),
}

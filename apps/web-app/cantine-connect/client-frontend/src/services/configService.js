import apiClient from './apiClient'

export const configService = {
  lister:    ()          => apiClient.get('/configurations').then(r => r.data.data),
  getParCle: (cle)       => apiClient.get(`/configurations/${cle}`).then(r => r.data.data),
  modifier:  (cle, valeur) => apiClient.put(`/configurations/${cle}`, { valeur }).then(r => r.data.data),
  uploaderLogo: (fichier) => {
    const formData = new FormData()
    formData.append('fichier', fichier)
    return apiClient
      .post('/configurations/logo', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(r => r.data.data)
  },
  uploaderFondEcran: (fichier) => {
    const formData = new FormData()
    formData.append('fichier', fichier)
    return apiClient
      .post('/configurations/fond-ecran-login', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(r => r.data.data)
  },
}

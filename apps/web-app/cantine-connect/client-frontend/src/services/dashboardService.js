import apiClient from './apiClient'

export const dashboardService = {
  getStats: () =>
    apiClient.get('/dashboard/stats').then((r) => r.data.data),
}

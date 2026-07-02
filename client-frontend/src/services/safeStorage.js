// localStorage peut lever une exception (Firefox : Protection renforcée contre le
// pistage en mode strict, cookies/données de site bloqués, navigation privée...).
// Ces accès ne doivent jamais faire planter l'application.
export const safeStorage = {
  getItem(key) {
    try {
      return localStorage.getItem(key)
    } catch {
      return null
    }
  },

  setItem(key, value) {
    try {
      localStorage.setItem(key, value)
      return true
    } catch {
      return false
    }
  },

  removeItem(key) {
    try {
      localStorage.removeItem(key)
    } catch {
      // ignoré : rien à nettoyer si le stockage est inaccessible
    }
  },
}

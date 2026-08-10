import { Platform } from 'react-native'
import * as SecureStore from 'expo-secure-store'
import AsyncStorage from '@react-native-async-storage/async-storage'

/**
 * expo-secure-store n'a pas d'implémentation web — bascule sur AsyncStorage
 * (localStorage sous le capot en PWA) pour que le même code tourne partout,
 * même logique que `safeStorage.js` côté client-frontend web.
 */
export const secureStorage = {
  async getItem(key: string): Promise<string | null> {
    return Platform.OS === 'web' ? AsyncStorage.getItem(key) : SecureStore.getItemAsync(key)
  },
  async setItem(key: string, value: string): Promise<void> {
    return Platform.OS === 'web' ? AsyncStorage.setItem(key, value) : SecureStore.setItemAsync(key, value)
  },
  async removeItem(key: string): Promise<void> {
    return Platform.OS === 'web' ? AsyncStorage.removeItem(key) : SecureStore.deleteItemAsync(key)
  },
}

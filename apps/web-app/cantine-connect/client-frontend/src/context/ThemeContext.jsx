import { useState } from 'react'
import { safeStorage } from '../services/safeStorage'
import { ThemeModeContext } from './ThemeModeContextObject'

const STORAGE_KEY = 'klem-theme'
const DEFAULT_THEME = 'premium'
const VALID_THEMES = ['premium', 'corporate', 'modern']

export function ThemeModeProvider({ children }) {
  const [themeName, setThemeName] = useState(() => {
    const saved = safeStorage.getItem(STORAGE_KEY)
    return VALID_THEMES.includes(saved) ? saved : DEFAULT_THEME
  })

  const changeTheme = (name) => {
    if (!VALID_THEMES.includes(name)) return
    setThemeName(name)
    safeStorage.setItem(STORAGE_KEY, name)
  }

  return (
    <ThemeModeContext.Provider value={{ themeName, changeTheme }}>
      {children}
    </ThemeModeContext.Provider>
  )
}

import { useContext } from 'react'
import { ThemeModeContext } from '../context/ThemeModeContextObject'

export const useThemeMode = () => useContext(ThemeModeContext)

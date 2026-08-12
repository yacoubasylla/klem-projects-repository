import { ThemeProvider, CssBaseline } from '@mui/material'
import { THEMES } from './theme/themes'
import { useThemeMode } from './hooks/useThemeMode'
import { AuthProvider } from './context/AuthContext'
import App from './App.jsx'

export function ThemedApp() {
  const { themeName } = useThemeMode()
  const theme = THEMES[themeName] ?? THEMES.premium
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <App />
      </AuthProvider>
    </ThemeProvider>
  )
}

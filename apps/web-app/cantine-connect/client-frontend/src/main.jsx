import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { ThemeProvider, CssBaseline } from '@mui/material'
import { BrowserRouter } from 'react-router'
import '@fontsource/plus-jakarta-sans/600.css'
import '@fontsource/plus-jakarta-sans/700.css'
import '@fontsource/plus-jakarta-sans/800.css'
import '@fontsource/inter/400.css'
import '@fontsource/inter/500.css'
import '@fontsource/inter/600.css'
import './index.css'
import { THEMES } from './theme/themes'
import { ThemeModeProvider, useThemeMode } from './context/ThemeContext'
import { AuthProvider } from './context/AuthContext'
import App from './App.jsx'

function ThemedApp() {
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

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <ThemeModeProvider>
        <ThemedApp />
      </ThemeModeProvider>
    </BrowserRouter>
  </StrictMode>,
)

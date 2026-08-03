import { createTheme } from '@mui/material/styles'

// ─────────────────────────────────────────────────────────────────────────────
// 🇨🇮  Cantine Connect — thème unique, chaleureux et premium
// Palette inspirée de l'école ivoirienne : orange doux, vert profond, crème,
// touche d'or discrète pour les accents premium. Un seul thème cohérent pour
// toute l'application (remplace l'ancien sélecteur à 3 thèmes).
// ─────────────────────────────────────────────────────────────────────────────

const theme = createTheme({
  palette: {
    primary: {
      main: '#E8720C',
      light: '#FFA94D',
      dark: '#B85400',
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: '#0E7C4A',
      light: '#3FA873',
      dark: '#075A34',
      contrastText: '#FFFFFF',
    },
    gold: {
      main: '#D4A017',
      light: '#E6C158',
      dark: '#A67D0F',
    },
    background: {
      default: '#FFF9F2',
      paper: '#FFFFFF',
    },
    text: {
      primary: '#2B2118',
      secondary: '#6B5E52',
      disabled: '#B8AA9C',
    },
    success: { main: '#0E7C4A' },
    warning: { main: '#D48806' },
    error: { main: '#C62828' },
    divider: 'rgba(43,33,24,0.10)',
  },

  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: { fontFamily: '"Plus Jakarta Sans", "Inter", sans-serif', fontWeight: 800, letterSpacing: '-0.02em' },
    h2: { fontFamily: '"Plus Jakarta Sans", "Inter", sans-serif', fontWeight: 800, letterSpacing: '-0.02em' },
    h3: { fontFamily: '"Plus Jakarta Sans", "Inter", sans-serif', fontWeight: 700, letterSpacing: '-0.01em' },
    h4: { fontFamily: '"Plus Jakarta Sans", "Inter", sans-serif', fontWeight: 700, letterSpacing: '-0.01em' },
    h5: { fontFamily: '"Plus Jakarta Sans", "Inter", sans-serif', fontWeight: 700 },
    h6: { fontFamily: '"Plus Jakarta Sans", "Inter", sans-serif', fontWeight: 700 },
    subtitle1: { fontWeight: 600 },
    button: { fontWeight: 700 },
  },

  shape: { borderRadius: 14 },

  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 700,
          borderRadius: 12,
          transition: 'all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1)',
        },
        containedPrimary: {
          background: 'linear-gradient(135deg, #FFA94D 0%, #E8720C 100%)',
          boxShadow: '0 6px 20px rgba(232,114,12,0.32)',
          '&:hover': {
            background: 'linear-gradient(135deg, #E8720C 0%, #B85400 100%)',
            boxShadow: '0 10px 26px rgba(232,114,12,0.42)',
            transform: 'translateY(-2px)',
          },
        },
        containedSecondary: {
          background: 'linear-gradient(135deg, #3FA873 0%, #0E7C4A 100%)',
          boxShadow: '0 6px 20px rgba(14,124,74,0.30)',
          '&:hover': {
            background: 'linear-gradient(135deg, #0E7C4A 0%, #075A34 100%)',
            boxShadow: '0 10px 26px rgba(14,124,74,0.40)',
            transform: 'translateY(-2px)',
          },
        },
        outlinedPrimary: {
          borderWidth: 2,
          '&:hover': { borderWidth: 2, backgroundColor: 'rgba(232,114,12,0.06)' },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: '#FFFFFF',
          boxShadow: '0 4px 24px rgba(43,33,24,0.08)',
          border: '1px solid rgba(232,114,12,0.08)',
          borderRadius: 20,
        },
      },
    },
    MuiPaper: {
      styleOverrides: { rounded: { borderRadius: 16 } },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          fontWeight: 700,
          backgroundColor: '#FFF3E4',
          color: '#B85400',
          borderBottom: '2px solid #E8720C',
        },
      },
    },
    MuiTableRow: {
      styleOverrides: {
        root: { '&:hover': { backgroundColor: 'rgba(232,114,12,0.04)' } },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          background: 'linear-gradient(135deg, #E8720C 0%, #B85400 100%)',
          boxShadow: '0 6px 24px rgba(184,84,0,0.28)',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          borderRight: 'none',
          boxShadow: '4px 0 30px rgba(43,33,24,0.06)',
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          borderRadius: 10,
          margin: '2px 8px',
          width: 'calc(100% - 16px)',
          '&.Mui-selected': {
            backgroundColor: '#FFF3E4',
            borderLeft: '3px solid #E8720C',
            '& .MuiListItemIcon-root': { color: '#E8720C' },
            '& .MuiListItemText-primary': { fontWeight: 700, color: '#B85400' },
          },
          '&.Mui-selected:hover': { backgroundColor: '#FFE8CC' },
        },
      },
    },
    MuiChip: {
      styleOverrides: { root: { fontWeight: 600, borderRadius: 8 } },
    },
    MuiTabs: {
      styleOverrides: {
        indicator: {
          background: 'linear-gradient(90deg, #E8720C, #0E7C4A)',
          height: 3,
          borderRadius: 2,
        },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: { textTransform: 'none', fontWeight: 600 },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 12,
            '&.Mui-focused fieldset': {
              borderColor: '#E8720C',
              boxShadow: '0 0 0 3px rgba(232,114,12,0.14)',
            },
          },
        },
      },
    },
  },
})

export default theme

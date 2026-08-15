import { createTheme } from '@mui/material/styles'

// Police des titres du site vitrine KLEM (site-klem, classe utilitaire `font-heading`).
const headingFontFamily = '"Questrial", "Inter", "Roboto", "Helvetica", "Arial", sans-serif'

const baseTypography = {
  fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
  h1: { fontFamily: headingFontFamily },
  h2: { fontFamily: headingFontFamily },
  h3: { fontFamily: headingFontFamily },
  h4: { fontFamily: headingFontFamily },
  h5: { fontFamily: headingFontFamily, fontWeight: 700 },
  h6: { fontFamily: headingFontFamily, fontWeight: 700 },
  subtitle1: { fontWeight: 600 },
  button: { fontWeight: 600 },
}

// Accent or partagé par les 3 thèmes — utilisé par quelques composants (ex.
// badge "Restauration scolaire" de la page d'accueil) quel que soit le thème actif.
const goldPalette = {
  main: '#D4A017',
  light: '#E6C158',
  dark: '#A67D0F',
}

// ─────────────────────────────────────────────────────────────────────────────
// 🇨🇮  PREMIUM — thème par défaut, chaleureux et premium (refonte 2026)
// Palette inspirée de l'école ivoirienne : orange doux, vert profond, crème,
// touche d'or discrète pour les accents premium.
// ─────────────────────────────────────────────────────────────────────────────
const premium = createTheme({
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
    gold: goldPalette,
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
    h1: { fontFamily: headingFontFamily, fontWeight: 800, letterSpacing: '-0.02em' },
    h2: { fontFamily: headingFontFamily, fontWeight: 800, letterSpacing: '-0.02em' },
    h3: { fontFamily: headingFontFamily, fontWeight: 700, letterSpacing: '-0.01em' },
    h4: { fontFamily: headingFontFamily, fontWeight: 700, letterSpacing: '-0.01em' },
    h5: { fontFamily: headingFontFamily, fontWeight: 700 },
    h6: { fontFamily: headingFontFamily, fontWeight: 700 },
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

// ─────────────────────────────────────────────────────────────────────────────
// 🏢  CORPORATIF — Dark mode vert forêt, sobre et moins terne que l'ardoise grise
// ─────────────────────────────────────────────────────────────────────────────
const corporate = createTheme({
  palette: {
    mode: 'dark',
    primary:    { main: '#34D399', light: '#6EE7B7', dark: '#059669' },
    secondary:  { main: '#FB923C', light: '#FCA5A5', dark: '#EA580C' },
    gold: goldPalette,
    background: { default: '#16261C', paper: '#1E3328' },
    divider:    'rgba(134,178,155,0.18)',
    text: {
      primary:   '#EAF3EE',
      secondary: '#A9C4B6',
      disabled:  '#5E7A6C',
    },
  },
  typography: baseTypography,
  shape: { borderRadius: 8 },
  components: {
    MuiButton: {
      styleOverrides: {
        root: { textTransform: 'none', fontWeight: 600 },
        containedPrimary: {
          backgroundColor: '#059669',
          boxShadow: '0 2px 8px rgba(5,150,105,0.40)',
          '&:hover': { backgroundColor: '#047857', boxShadow: '0 4px 14px rgba(5,150,105,0.50)' },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: '#1E3328',
          boxShadow: '0 4px 24px rgba(0,0,0,0.25)',
          border: '1px solid rgba(134,178,155,0.15)',
          borderRadius: 10,
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          fontWeight: 700,
          backgroundColor: '#101C15',
          color: '#34D399',
          borderBottom: '2px solid #059669',
        },
        body: {
          borderBottom: '1px solid rgba(134,178,155,0.12)',
        },
      },
    },
    MuiTableRow: {
      styleOverrides: {
        root: {
          '&:hover': { backgroundColor: 'rgba(52,211,153,0.07)' },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: '#101C15',
          borderBottom: '1px solid rgba(134,178,155,0.15)',
          boxShadow: 'none',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: '#16261C',
          borderRight: '1px solid rgba(134,178,155,0.15)',
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          '&.Mui-selected': {
            backgroundColor: 'rgba(52,211,153,0.14)',
            borderLeft: '3px solid #34D399',
            '& .MuiListItemIcon-root': { color: '#34D399' },
            '& .MuiListItemText-primary': { fontWeight: 700, color: '#34D399' },
          },
          '&.Mui-selected:hover': { backgroundColor: 'rgba(52,211,153,0.20)' },
        },
      },
    },
    MuiChip: {
      styleOverrides: { root: { fontWeight: 600 } },
    },
    MuiTabs: {
      styleOverrides: {
        indicator: { backgroundColor: '#34D399', height: 3 },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          color: '#8FAA9C',
          '&.Mui-selected': { color: '#34D399' },
        },
      },
    },
    MuiDivider: {
      styleOverrides: { root: { borderColor: 'rgba(134,178,155,0.18)' } },
    },
    MuiPaper: {
      styleOverrides: { root: { backgroundImage: 'none' } },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          '& fieldset': { borderColor: 'rgba(134,178,155,0.30)' },
          '&:hover fieldset': { borderColor: 'rgba(134,178,155,0.55)' },
        },
      },
    },
  },
})

// ─────────────────────────────────────────────────────────────────────────────
// ✨  MODERNE — Blanc pur, gradients bling, effets premium KLEM
// ─────────────────────────────────────────────────────────────────────────────
const modern = createTheme({
  palette: {
    primary:    { main: '#1565C0', light: '#42A5F5', dark: '#0D47A1' },
    secondary:  { main: '#FF6D00', light: '#FF9E40', dark: '#E65100' },
    gold: goldPalette,
    background: { default: '#F5F7FF', paper: '#FFFFFF' },
  },
  typography: {
    ...baseTypography,
    h5: { fontFamily: headingFontFamily, fontWeight: 800, letterSpacing: '-0.03em' },
    h6: { fontFamily: headingFontFamily, fontWeight: 700, letterSpacing: '-0.02em' },
  },
  shape: { borderRadius: 16 },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 700,
          borderRadius: 10,
          transition: 'all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1)',
        },
        containedPrimary: {
          background: 'linear-gradient(135deg, #42A5F5 0%, #1565C0 100%)',
          boxShadow: '0 6px 20px rgba(21,101,192,0.40)',
          '&:hover': {
            background: 'linear-gradient(135deg, #1E88E5 0%, #0D47A1 100%)',
            boxShadow: '0 10px 28px rgba(21,101,192,0.55)',
            transform: 'translateY(-2px)',
          },
        },
        containedSecondary: {
          background: 'linear-gradient(135deg, #FF9E40 0%, #FF6D00 100%)',
          boxShadow: '0 6px 20px rgba(255,109,0,0.40)',
          '&:hover': {
            background: 'linear-gradient(135deg, #FF6D00 0%, #E65100 100%)',
            boxShadow: '0 10px 28px rgba(255,109,0,0.55)',
            transform: 'translateY(-2px)',
          },
        },
        outlinedPrimary: {
          borderWidth: 2,
          '&:hover': { borderWidth: 2, backgroundColor: 'rgba(21,101,192,0.06)' },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: '#FFFFFF',
          boxShadow: '0 4px 24px rgba(21,101,192,0.10)',
          border: 'none',
          borderRadius: 20,
          transition: 'all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)',
          '&:hover': {
            boxShadow: '0 12px 40px rgba(21,101,192,0.18)',
            transform: 'translateY(-4px)',
          },
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          fontWeight: 700,
          background: 'linear-gradient(135deg, #1565C0 0%, #42A5F5 100%)',
          color: '#FFFFFF',
        },
      },
    },
    MuiTableSortLabel: {
      styleOverrides: {
        root: {
          color: 'rgba(255,255,255,0.85)',
          '&:hover': { color: '#FFFFFF' },
          '&.Mui-active': { color: '#FFFFFF' },
        },
        icon: { color: 'rgba(255,255,255,0.70) !important' },
      },
    },
    MuiTableRow: {
      styleOverrides: {
        root: {
          transition: 'background-color 0.15s ease',
          '&:hover': { backgroundColor: 'rgba(21,101,192,0.04)' },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          background: 'linear-gradient(135deg, #1565C0 0%, #0D47A1 60%, #FF6D00 200%)',
          boxShadow: '0 6px 30px rgba(13,71,161,0.35)',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          borderRight: 'none',
          boxShadow: '4px 0 30px rgba(21,101,192,0.08)',
          background: 'linear-gradient(180deg, #FFFFFF 0%, #F5F7FF 100%)',
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          borderRadius: 10,
          margin: '2px 8px',
          width: 'calc(100% - 16px)',
          transition: 'all 0.15s ease',
          '&.Mui-selected': {
            background: 'linear-gradient(135deg, rgba(21,101,192,0.12) 0%, rgba(66,165,245,0.08) 100%)',
            boxShadow: '0 2px 8px rgba(21,101,192,0.15)',
            '& .MuiListItemIcon-root': { color: '#1565C0' },
            '& .MuiListItemText-primary': { fontWeight: 700, color: '#1565C0' },
          },
          '&.Mui-selected:hover': {
            background: 'linear-gradient(135deg, rgba(21,101,192,0.18) 0%, rgba(66,165,245,0.12) 100%)',
          },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          fontWeight: 600,
          borderRadius: 8,
        },
        colorPrimary: {
          background: 'linear-gradient(135deg, #42A5F5 0%, #1565C0 100%)',
          color: '#FFFFFF',
        },
        colorSecondary: {
          background: 'linear-gradient(135deg, #FF9E40 0%, #FF6D00 100%)',
          color: '#FFFFFF',
        },
      },
    },
    MuiPaper: {
      styleOverrides: { rounded: { borderRadius: 16 } },
    },
    MuiTabs: {
      styleOverrides: {
        indicator: {
          background: 'linear-gradient(90deg, #1565C0, #FF6D00)',
          height: 3,
          borderRadius: 2,
        },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          '&.Mui-selected': { color: '#1565C0' },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 10,
            '&.Mui-focused fieldset': {
              borderColor: '#1565C0',
              boxShadow: '0 0 0 3px rgba(21,101,192,0.15)',
            },
          },
        },
      },
    },
  },
})

export const THEMES = { premium, corporate, modern }

export const THEME_META = {
  premium: {
    label: 'Premium',
    description: 'Thème par défaut — orange, vert, crème',
    colors: ['#E8720C', '#0E7C4A', '#FFF9F2'],
    emoji: '🇨🇮',
  },
  corporate: {
    label: 'Corporatif',
    description: 'Dark mode — vert forêt',
    colors: ['#16261C', '#1E3328', '#34D399'],
    emoji: '🏢',
  },
  modern: {
    label: 'Moderne',
    description: 'Blanc pur — gradients bling',
    colors: ['#1565C0', '#FF6D00', '#F5F7FF'],
    emoji: '✨',
  },
}

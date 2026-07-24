import { createTheme } from '@mui/material/styles'

const baseTypography = {
  fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
  h5: { fontWeight: 700 },
  h6: { fontWeight: 700 },
  subtitle1: { fontWeight: 600 },
  button: { fontWeight: 600 },
}

// ─────────────────────────────────────────────────────────────────────────────
// 🏢  CORPORATIF — Dark mode ardoise, gris clair et sobre
// ─────────────────────────────────────────────────────────────────────────────
const corporate = createTheme({
  palette: {
    mode: 'dark',
    primary:    { main: '#60A5FA', light: '#93C5FD', dark: '#2563EB' },
    secondary:  { main: '#FB923C', light: '#FCA5A5', dark: '#EA580C' },
    background: { default: '#252D3B', paper: '#303C4E' },
    divider:    'rgba(148,163,184,0.18)',
    text: {
      primary:   '#F1F5F9',
      secondary: '#B0BEC5',
      disabled:  '#607080',
    },
  },
  typography: baseTypography,
  shape: { borderRadius: 8 },
  components: {
    MuiButton: {
      styleOverrides: {
        root: { textTransform: 'none', fontWeight: 600 },
        containedPrimary: {
          backgroundColor: '#2563EB',
          boxShadow: '0 2px 8px rgba(37,99,235,0.40)',
          '&:hover': { backgroundColor: '#1D4ED8', boxShadow: '0 4px 14px rgba(37,99,235,0.50)' },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: '#303C4E',
          boxShadow: '0 4px 24px rgba(0,0,0,0.25)',
          border: '1px solid rgba(148,163,184,0.15)',
          borderRadius: 10,
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          fontWeight: 700,
          backgroundColor: '#1E2636',
          color: '#60A5FA',
          borderBottom: '2px solid #2563EB',
        },
        body: {
          borderBottom: '1px solid rgba(148,163,184,0.12)',
        },
      },
    },
    MuiTableRow: {
      styleOverrides: {
        root: {
          '&:hover': { backgroundColor: 'rgba(96,165,250,0.07)' },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: '#1E2636',
          borderBottom: '1px solid rgba(148,163,184,0.15)',
          boxShadow: 'none',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: '#252D3B',
          borderRight: '1px solid rgba(148,163,184,0.15)',
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          '&.Mui-selected': {
            backgroundColor: 'rgba(96,165,250,0.14)',
            borderLeft: '3px solid #60A5FA',
            '& .MuiListItemIcon-root': { color: '#60A5FA' },
            '& .MuiListItemText-primary': { fontWeight: 700, color: '#60A5FA' },
          },
          '&.Mui-selected:hover': { backgroundColor: 'rgba(96,165,250,0.20)' },
        },
      },
    },
    MuiChip: {
      styleOverrides: { root: { fontWeight: 600 } },
    },
    MuiTabs: {
      styleOverrides: {
        indicator: { backgroundColor: '#60A5FA', height: 3 },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          color: '#94A3B8',
          '&.Mui-selected': { color: '#60A5FA' },
        },
      },
    },
    MuiDivider: {
      styleOverrides: { root: { borderColor: 'rgba(148,163,184,0.18)' } },
    },
    MuiPaper: {
      styleOverrides: { root: { backgroundImage: 'none' } },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          '& fieldset': { borderColor: 'rgba(148,163,184,0.30)' },
          '&:hover fieldset': { borderColor: 'rgba(148,163,184,0.55)' },
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
    background: { default: '#F5F7FF', paper: '#FFFFFF' },
  },
  typography: {
    ...baseTypography,
    h5: { fontWeight: 800, letterSpacing: '-0.03em' },
    h6: { fontWeight: 700, letterSpacing: '-0.02em' },
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

// ─────────────────────────────────────────────────────────────────────────────
// 🇨🇮  ÉCOLE IVOIRIENNE — orange, vert, blanc, chaleureux et lumineux
// ─────────────────────────────────────────────────────────────────────────────
const ivoirien = createTheme({
  palette: {
    primary:    { main: '#E65100', light: '#FF8A50', dark: '#BF360C' },
    secondary:  { main: '#2E7D32', light: '#60AD5E', dark: '#1B5E20' },
    background: { default: '#FFFDF7', paper: '#FFFFFF' },
    success:    { main: '#2E7D32' },
    warning:    { main: '#F57C00' },
  },
  typography: baseTypography,
  shape: { borderRadius: 10 },
  components: {
    MuiButton: {
      styleOverrides: {
        root: { textTransform: 'none', fontWeight: 600 },
        containedPrimary: {
          background: 'linear-gradient(135deg, #FF7043 0%, #E65100 100%)',
          boxShadow: '0 4px 12px rgba(230,81,0,0.30)',
          '&:hover': {
            background: 'linear-gradient(135deg, #E65100 0%, #BF360C 100%)',
            boxShadow: '0 6px 18px rgba(230,81,0,0.40)',
          },
        },
        containedSecondary: {
          background: 'linear-gradient(135deg, #66BB6A 0%, #2E7D32 100%)',
          boxShadow: '0 4px 12px rgba(46,125,50,0.30)',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          boxShadow: '0 2px 12px rgba(230,81,0,0.10)',
          border: '1px solid #FFE8D6',
          borderRadius: 12,
          transition: 'box-shadow 0.2s ease',
          '&:hover': { boxShadow: '0 6px 20px rgba(230,81,0,0.15)' },
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          fontWeight: 700,
          backgroundColor: '#FFF3E0',
          color: '#BF360C',
          borderBottom: '2px solid #E65100',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          background: 'linear-gradient(135deg, #FF7043 0%, #E65100 100%)',
          boxShadow: '0 4px 16px rgba(230,81,0,0.25)',
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          '&.Mui-selected': {
            backgroundColor: '#FFF3E0',
            borderLeft: '3px solid #E65100',
            '& .MuiListItemIcon-root': { color: '#E65100' },
            '& .MuiListItemText-primary': { fontWeight: 700, color: '#E65100' },
          },
          '&.Mui-selected:hover': { backgroundColor: '#FFE8D6' },
        },
      },
    },
    MuiChip: {
      styleOverrides: { root: { fontWeight: 600 } },
    },
    MuiTabs: {
      styleOverrides: {
        indicator: { backgroundColor: '#E65100', height: 3 },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          '&.Mui-selected': { color: '#E65100' },
        },
      },
    },
  },
})

export const THEMES = { corporate, modern, ivoirien }

export const THEME_META = {
  corporate: {
    label: 'Corporatif',
    description: 'Dark mode — ardoise gris',
    colors: ['#252D3B', '#303C4E', '#60A5FA'],
    emoji: '🏢',
  },
  modern: {
    label: 'Moderne',
    description: 'Blanc pur — gradients bling',
    colors: ['#1565C0', '#FF6D00', '#F5F7FF'],
    emoji: '✨',
  },
  ivoirien: {
    label: 'Ivoire',
    description: 'Orange • Blanc • Vert',
    colors: ['#E65100', '#2E7D32', '#FFFDF7'],
    emoji: '🇨🇮',
  },
}

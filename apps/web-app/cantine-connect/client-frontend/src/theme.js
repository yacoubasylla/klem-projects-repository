import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: { main: '#1565C0', light: '#1E88E5', dark: '#0D47A1' },
    secondary: { main: '#2E7D32' },
    background: { default: '#F4F6FA' },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h5: { fontWeight: 700 },
    h6: { fontWeight: 700 },
  },
  shape: { borderRadius: 8 },
  components: {
    MuiButton: {
      styleOverrides: { root: { textTransform: 'none', fontWeight: 600 } },
    },
    MuiCard: {
      styleOverrides: { root: { boxShadow: '0 1px 6px rgba(0,0,0,0.10)' } },
    },
    MuiTableCell: {
      styleOverrides: { head: { fontWeight: 700, backgroundColor: '#F4F6FA' } },
    },
  },
});

export default theme;

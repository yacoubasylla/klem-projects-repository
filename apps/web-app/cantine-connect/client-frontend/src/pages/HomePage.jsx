import { Link as RouterLink } from 'react-router'
import {
  Box, Typography, Button, Stack, Chip, List, ListItem, ListItemIcon, ListItemText, Divider,
} from '@mui/material'
import RestaurantIcon      from '@mui/icons-material/Restaurant'
import PaymentsIcon        from '@mui/icons-material/Payments'
import QrCodeScannerIcon   from '@mui/icons-material/QrCodeScanner'
import HealthAndSafetyIcon from '@mui/icons-material/HealthAndSafety'
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive'
import SchoolIcon          from '@mui/icons-material/School'
import LoginIcon           from '@mui/icons-material/Login'
import HowToRegIcon        from '@mui/icons-material/HowToReg'
import PublicSplitLayout   from '../layouts/PublicSplitLayout'

// Localité affichée sur la page d'accueil : paramétrable par déploiement (VITE_APP_PAYS /
// VITE_APP_VILLE_REFERENCE), pour ne pas figer l'application sur un pays ou une ville.
const PAYS = import.meta.env.VITE_APP_PAYS || "Côte d'Ivoire"
const VILLE_REFERENCE = import.meta.env.VITE_APP_VILLE_REFERENCE || 'Abidjan'

const ATOUTS = [
  { icon: <PaymentsIcon color="primary" />,        text: 'Paiement Mobile Money : Orange, MTN, Moov' },
  { icon: <QrCodeScannerIcon color="primary" />,    text: 'Contrôle d’accès cantine par QR Code en moins d’une seconde' },
  { icon: <HealthAndSafetyIcon color="primary" />,  text: 'Allergies suivies avec certificat médical' },
  { icon: <NotificationsActiveIcon color="primary" />, text: 'Notifications aux parents à chaque étape' },
]

export default function HomePage() {
  return (
    <PublicSplitLayout
      activePage="home"
      cardMaxWidth={420}
      heroSlot={
        <Box sx={{ py: { xs: 1, md: 3 } }}>
          <Chip
            icon={<SchoolIcon sx={{ fontSize: 16 }} />}
            label={`Restauration scolaire — ${PAYS}`}
            size="small"
            sx={{ mb: 3, bgcolor: 'gold.light', color: '#5C4400', fontWeight: 700 }}
          />
          <Typography variant="h2" sx={{ fontSize: { xs: '2.1rem', md: '2.8rem' }, mb: 2, color: 'text.primary' }}>
            La cantine de votre enfant,{' '}
            <Box component="span" sx={{ color: 'primary.main' }}>simple et rassurante</Box>
          </Typography>
          <Typography variant="h6" sx={{ fontWeight: 400, color: 'text.secondary', mb: 4, maxWidth: 520 }}>
            Inscription, paiement Mobile Money, suivi des repas en temps réel et allergies
            en toute sécurité — une seule plateforme pour les familles et les établissements.
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} mb={5}>
            <Button
              component={RouterLink} to="/demande-acces"
              variant="contained" size="large" startIcon={<HowToRegIcon />}
              sx={{ px: 4, py: 1.4, borderRadius: 999, whiteSpace: 'nowrap' }}
            >
              Accès Parent
            </Button>
            <Button
              component={RouterLink} to="/login"
              variant="outlined" size="large" startIcon={<LoginIcon />}
              sx={{ px: 4, py: 1.4, borderRadius: 999, whiteSpace: 'nowrap' }}
            >
              Se connecter
            </Button>
          </Stack>
          <Typography variant="caption" color="text.disabled">
            Déjà utilisé par des établissements scolaires à {VILLE_REFERENCE}
          </Typography>
        </Box>
      }
    >
      <Box sx={{ p: { xs: 3, sm: 4 } }}>
        <Stack direction="row" alignItems="center" spacing={1} mb={2.5}>
          <RestaurantIcon color="primary" />
          <Typography variant="subtitle1" fontWeight={700}>
            Pourquoi Cantine Connect ?
          </Typography>
        </Stack>
        <List disablePadding>
          {ATOUTS.map((a, i) => (
            <ListItem key={i} disableGutters sx={{ py: 1 }}>
              <ListItemIcon sx={{ minWidth: 40 }}>{a.icon}</ListItemIcon>
              <ListItemText primary={a.text} slotProps={{ primary: { fontWeight: 500 } }} />
            </ListItem>
          ))}
        </List>
        <Divider sx={{ my: 3 }} />
        <Typography variant="body2" color="text.secondary">
          Vous êtes parent d'élève ?{' '}
          <Box component={RouterLink} to="/demande-acces" sx={{ color: 'primary.main', fontWeight: 700, textDecoration: 'none' }}>
            Accédez en tant que parent →
          </Box>
        </Typography>
      </Box>
    </PublicSplitLayout>
  )
}

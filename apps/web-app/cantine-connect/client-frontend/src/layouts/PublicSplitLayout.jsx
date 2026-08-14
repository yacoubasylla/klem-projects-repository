import { Link as RouterLink } from 'react-router'
import { Box, Container, Stack, Typography, Button, Paper } from '@mui/material'
import { alpha } from '@mui/material/styles'
import LoginIcon   from '@mui/icons-material/Login'
import HowToRegIcon from '@mui/icons-material/HowToReg'
import { useOrganisationBranding } from '../hooks/useConfig'

// Motif géométrique discret d'inspiration textile ivoirienne — décoratif uniquement,
// SVG léger généré localement (pas d'imagerie stock).
function DecorativePattern() {
  return (
    <Box
      aria-hidden
      sx={{
        position: 'absolute',
        inset: 0,
        overflow: 'hidden',
        pointerEvents: 'none',
        opacity: 0.5,
      }}
    >
      <svg width="100%" height="100%" preserveAspectRatio="xMidYMid slice">
        <defs>
          <pattern id="cc-pattern" width="88" height="88" patternUnits="userSpaceOnUse">
            <circle cx="10" cy="10" r="3" fill="#E8720C" fillOpacity="0.12" />
            <circle cx="54" cy="34" r="2" fill="#0E7C4A" fillOpacity="0.12" />
            <path d="M0 44 L22 22 L44 44 L22 66 Z" fill="none" stroke="#D4A017" strokeOpacity="0.14" strokeWidth="1.5" />
            <circle cx="70" cy="70" r="3" fill="#E8720C" fillOpacity="0.10" />
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#cc-pattern)" />
      </svg>
    </Box>
  )
}

const NAV_LINKS = [
  { key: 'login', label: 'Se connecter', to: '/login', variant: 'outlined', icon: <LoginIcon /> },
  { key: 'acces', label: "Demande d'accès", to: '/demande-acces', variant: 'contained', icon: <HowToRegIcon /> },
]

/**
 * Layout public partagé (Accueil / Connexion / Demande d'accès) : nav du haut
 * avec CTA en pilules, contenu (hero) à gauche, carte d'action à droite —
 * inspiré de la mise en page e-impots.gouv.ci, avec une ambiance chaleureuse
 * propre à la cantine scolaire ivoirienne (pas l'esprit institutionnel froid).
 */
export default function PublicSplitLayout({ activePage, heroSlot, children, cardMaxWidth = 460, heroBackgroundImage }) {
  const { nom: orgNom, logoUrl: orgLogoUrl } = useOrganisationBranding()
  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', bgcolor: 'background.default' }}>
      {/* ── Nav du haut ─────────────────────────────────────── */}
      <Box component="header" sx={{ borderBottom: '1px solid', borderColor: 'divider', bgcolor: 'background.paper' }}>
        <Container maxWidth="lg">
          <Stack
            direction="row"
            alignItems="center"
            justifyContent="space-between"
            sx={{ py: 1.5, flexWrap: 'wrap', gap: 1.5 }}
          >
            <Stack
              component={RouterLink}
              to="/"
              direction="row"
              alignItems="center"
              spacing={1}
              sx={{ textDecoration: 'none', color: 'text.primary' }}
            >
              {orgLogoUrl ? (
                <Box
                  component="img" src={orgLogoUrl} alt={orgNom}
                  sx={{ height: 30, objectFit: 'contain' }}
                  onError={(e) => { e.target.style.display = 'none' }}
                />
              ) : (
                <Typography fontSize={26} lineHeight={1}>🍽️</Typography>
              )}
              <Typography variant="h6" fontWeight={800} letterSpacing="-0.02em" color="text.primary">
                {orgNom}
              </Typography>
            </Stack>

            <Stack direction="row" spacing={1.25}>
              {NAV_LINKS.filter((l) => l.key !== activePage).map((l) => (
                <Button
                  key={l.key}
                  component={RouterLink}
                  to={l.to}
                  variant={l.variant}
                  size="medium"
                  startIcon={l.icon}
                  sx={{ borderRadius: 999, px: 2.5, whiteSpace: 'nowrap' }}
                >
                  {l.label}
                </Button>
              ))}
            </Stack>
          </Stack>
        </Container>
      </Box>

      {/* ── Corps : hero à gauche, carte d'action à droite ─── */}
      <Box sx={{ position: 'relative', flexGrow: 1, display: 'flex', alignItems: 'stretch' }}>
        <Container maxWidth="lg" sx={{ py: { xs: 5, md: 8 } }}>
          <Stack
            direction={{ xs: 'column', md: 'row' }}
            spacing={{ xs: 5, md: 8 }}
            alignItems={{ xs: 'stretch', md: 'center' }}
          >
            {/* Hero */}
            <Box
              sx={{
                position: 'relative',
                flex: '1 1 60%',
                minWidth: 0,
                minHeight: { xs: 320, md: 480 },
                display: 'flex',
                alignItems: 'flex-end',
                borderRadius: 6,
                // Le padding doit rester >= au borderRadius (6 * theme.shape.borderRadius = 24px)
                // : avec overflow hidden, un padding plus petit que le rayon d'arrondi laisse le
                // contenu proche des coins (badge en haut, légende en bas) tomber dans la zone de
                // découpe de l'arrondi -- visible en Firefox, pas en Chromium (bug réel constaté).
                p: { xs: 3, md: heroBackgroundImage ? 4 : 3 },
                overflow: 'hidden',
                ...(heroBackgroundImage && {
                  backgroundImage: (t) =>
                    `linear-gradient(0deg, ${alpha(t.palette.background.default, 0.92)} 0%, ${alpha(t.palette.background.default, 0.65)} 35%, ${alpha(t.palette.background.default, 0.15)} 70%, ${alpha(t.palette.background.default, 0)} 100%), url(${heroBackgroundImage})`,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center',
                }),
              }}
            >
              {!heroBackgroundImage && <DecorativePattern />}
              <Box sx={{ position: 'relative', width: '100%' }}>{heroSlot}</Box>
            </Box>

            {/* Carte d'action */}
            <Box sx={{ flex: '1 1 40%', display: 'flex', justifyContent: { xs: 'center', md: 'flex-end' } }}>
              <Paper
                elevation={0}
                sx={{
                  width: '100%',
                  maxWidth: cardMaxWidth,
                  borderRadius: 5,
                  border: '1px solid',
                  borderColor: (t) => alpha(t.palette.primary.main, 0.12),
                  boxShadow: (t) => `0 20px 60px ${alpha(t.palette.text.primary, 0.10)}`,
                  overflow: 'hidden',
                }}
              >
                {children}
              </Paper>
            </Box>
          </Stack>
        </Container>
      </Box>

      {/* ── Footer ──────────────────────────────────────────── */}
      <Box component="footer" sx={{ borderTop: '1px solid', borderColor: 'divider', py: 2.5 }}>
        <Typography variant="caption" color="text.disabled" display="block" textAlign="center">
          © 2026 KLEM Technologies &amp; Services — Cantine Connect
        </Typography>
      </Box>
    </Box>
  )
}

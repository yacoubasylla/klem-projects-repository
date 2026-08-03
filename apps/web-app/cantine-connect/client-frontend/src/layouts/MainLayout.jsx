import { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router'
import {
  Box, Drawer, AppBar, Toolbar, Typography,
  List, ListItemButton, ListItemIcon, ListItemText, Divider,
  Avatar, Stack, Tooltip, IconButton, Chip, useTheme, useMediaQuery,
} from '@mui/material'
import DashboardIcon      from '@mui/icons-material/Dashboard'
import SchoolIcon         from '@mui/icons-material/School'
import PeopleIcon         from '@mui/icons-material/People'
import FamilyRestroomIcon from '@mui/icons-material/FamilyRestroom'
import RestaurantIcon     from '@mui/icons-material/Restaurant'
import PaymentsIcon       from '@mui/icons-material/Payments'
import QrCodeScannerIcon  from '@mui/icons-material/QrCodeScanner'
import HistoryIcon        from '@mui/icons-material/History'
import AssessmentIcon     from '@mui/icons-material/Assessment'
import ManageAccountsIcon from '@mui/icons-material/ManageAccounts'
import TuneIcon           from '@mui/icons-material/Tune'
import LogoutIcon         from '@mui/icons-material/Logout'
import MenuIcon           from '@mui/icons-material/Menu'
import InfoOutlinedIcon  from '@mui/icons-material/InfoOutlined'
import { useAuth }       from '../hooks/useAuth'
import AProposDialog     from '../components/AProposDialog'
import ThemeSwitcher     from '../components/ThemeSwitcher'

const DRAWER_WIDTH = 240

const STAFF_ROLES = ['ADMIN', 'GESTIONNAIRE', 'CAISSIER']

const NAV_ITEMS = [
  { label: 'Tableau de bord', icon: <DashboardIcon />,      path: '/dashboard',      roles: null },
  { label: 'Établissements',  icon: <SchoolIcon />,         path: '/etablissements', roles: STAFF_ROLES },
  { label: 'Élèves',          icon: <PeopleIcon />,         path: '/eleves',         roles: STAFF_ROLES },
  { label: 'Paiements',       icon: <PaymentsIcon />,       path: '/paiements',      roles: null },
  { label: 'Scan Réfectoire', icon: <QrCodeScannerIcon />,  path: '/scan',           roles: STAFF_ROLES },
  { label: 'Historique',      icon: <HistoryIcon />,        path: '/passages',       roles: null },
  { label: 'Rapports',        icon: <AssessmentIcon />,     path: '/rapports',       roles: STAFF_ROLES },
  { label: 'Utilisateurs',    icon: <ManageAccountsIcon />, path: '/utilisateurs',   roles: ['ADMIN'] },
  { label: 'Parents',         icon: <FamilyRestroomIcon />, path: '/parents',        roles: ['ADMIN'] },
  { label: 'Configuration',   icon: <TuneIcon />,           path: '/configuration',  roles: ['ADMIN'] },
]

const ROLE_LABELS = { ADMIN: 'Administrateur', GESTIONNAIRE: 'Gestionnaire', CAISSIER: 'Caissier', PARENT: 'Parent' }

export default function MainLayout() {
  const navigate    = useNavigate()
  const location    = useLocation()
  const { user, logout } = useAuth()
  const theme       = useTheme()
  const isDesktop   = useMediaQuery(theme.breakpoints.up('lg')) // ≥ 1200px
  const [mobileOpen,   setMobileOpen]   = useState(false)
  const [aProposOpen,  setAProposOpen]  = useState(false)

  const handleLogout = () => { logout(); navigate('/login', { replace: true }) }

  const handleNav = (path) => {
    navigate(path)
    if (!isDesktop) setMobileOpen(false) // ferme le drawer sur mobile après navigation
  }

  const initials = user ? `${user.prenom?.[0] ?? ''}${user.nom?.[0] ?? ''}`.toUpperCase() : '?'

  const drawerContent = (
    <>
      <Toolbar />
      <Divider />
      <List disablePadding>
        {NAV_ITEMS
          .filter((item) => !item.roles || item.roles.includes(user?.role))
          .map((item) => (
            <ListItemButton
              key={item.path}
              selected={location.pathname.startsWith(item.path)}
              onClick={() => handleNav(item.path)}
              sx={{ py: 1.5 }}
            >
              <ListItemIcon sx={{ minWidth: 40 }}>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItemButton>
          ))}
      </List>

      <Box sx={{ mt: 'auto', p: 2, borderTop: '1px solid', borderColor: 'divider' }}>
        {user && (
          <Chip
            label={ROLE_LABELS[user.role] ?? user.role}
            size="small" color="primary" variant="outlined"
            sx={{ width: '100%', mb: 1 }}
          />
        )}
        <Stack direction="row" alignItems="center" justifyContent="center">
          <Tooltip title="À Propos">
            <IconButton size="small" onClick={() => setAProposOpen(true)} sx={{ color: 'text.disabled' }}>
              <InfoOutlinedIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Typography
            variant="caption"
            color="text.disabled"
            sx={{ cursor: 'pointer', '&:hover': { color: 'text.secondary' } }}
            onClick={() => setAProposOpen(true)}
          >
            À Propos
          </Typography>
        </Stack>
      </Box>
    </>
  )

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      {/* ── AppBar ──────────────────────────────────────── */}
      <AppBar position="fixed" sx={{ zIndex: (t) => t.zIndex.drawer + 1 }}>
        <Toolbar sx={{ justifyContent: 'space-between' }}>
          <Stack direction="row" alignItems="center" spacing={1}>
            {/* Hamburger visible uniquement sous lg */}
            {!isDesktop && (
              <IconButton
                color="inherit"
                onClick={() => setMobileOpen(v => !v)}
                edge="start"
                sx={{ mr: 0.5 }}
              >
                <MenuIcon />
              </IconButton>
            )}
            <RestaurantIcon sx={{ display: { xs: 'none', sm: 'block' } }} />
            <Typography variant="h6" noWrap fontWeight={700}>
              🍽️ Cantine Connect
            </Typography>
          </Stack>

          {user && (
            <Stack direction="row" alignItems="center" spacing={1}>
              <Box textAlign="right" sx={{ display: { xs: 'none', md: 'block' } }}>
                <Typography variant="body2" fontWeight={600} lineHeight={1.2}>
                  {user.prenom} {user.nom}
                </Typography>
                <Typography variant="caption" sx={{ opacity: 0.8 }}>
                  {ROLE_LABELS[user.role] ?? user.role}
                </Typography>
              </Box>
              <Avatar sx={{ width: 34, height: 34, bgcolor: 'primary.dark', fontSize: 14 }}>
                {initials}
              </Avatar>
              <ThemeSwitcher />
              <Tooltip title="Déconnexion">
                <IconButton color="inherit" onClick={handleLogout} size="small">
                  <LogoutIcon fontSize="small" />
                </IconButton>
              </Tooltip>
            </Stack>
          )}
        </Toolbar>
      </AppBar>

      {/* ── Drawer mobile (temporaire, < lg) ────────────── */}
      {!isDesktop && (
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={() => setMobileOpen(false)}
          ModalProps={{ keepMounted: true }}
          sx={{
            '& .MuiDrawer-paper': { width: DRAWER_WIDTH, boxSizing: 'border-box' },
          }}
        >
          {drawerContent}
        </Drawer>
      )}

      {/* ── Drawer desktop (permanent, ≥ lg) ────────────── */}
      {isDesktop && (
        <Drawer
          variant="permanent"
          sx={{
            width: DRAWER_WIDTH,
            flexShrink: 0,
            '& .MuiDrawer-paper': { width: DRAWER_WIDTH, boxSizing: 'border-box' },
          }}
        >
          {drawerContent}
        </Drawer>
      )}

      <AProposDialog open={aProposOpen} onClose={() => setAProposOpen(false)} />

      {/* ── Contenu principal ───────────────────────────── */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: { xs: 2, sm: 2.5, md: 3 },
          overflow: 'auto',
          bgcolor: 'background.default',
          minWidth: 0, // évite le dépassement flex
          width: { xs: '100%', lg: `calc(100% - ${DRAWER_WIDTH}px)` },
        }}
      >
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  )
}

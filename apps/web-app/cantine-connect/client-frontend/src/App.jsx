import { Routes, Route, Navigate } from 'react-router'
import { Box, CircularProgress } from '@mui/material'
import { ErrorBoundary } from '@klem/ui'
import MainLayout from './layouts/MainLayout'
import { useAuth } from './hooks/useAuth'
import LoginPage from './pages/auth/LoginPage'
import ChangerMotDePassePage from './pages/auth/ChangerMotDePassePage'
import DemandeAccesPage from './pages/acces/DemandeAccesPage'
import DemandesAccesPage from './pages/acces/DemandesAccesPage'
import ParentOtpAccessPage from './pages/acces/ParentOtpAccessPage'
import MesEnfantsPage from './pages/moi/MesEnfantsPage'
import DashboardPage from './pages/DashboardPage'
import EtablissementsPage from './pages/etablissements/EtablissementsPage'
import ElevesPage from './pages/eleves/ElevesPage'
import PaiementsPage      from './pages/paiements/PaiementsPage'
import ScanPage           from './pages/scan/ScanPage'
import UtilisateursPage   from './pages/utilisateurs/UtilisateursPage'
import ProtectedRoute       from './components/ProtectedRoute'
import AdminRoute           from './components/AdminRoute'
import StaffRoute           from './components/StaffRoute'
import ConfigurationPage    from './pages/configuration/ConfigurationPage'
import PassagesPage         from './pages/passages/PassagesPage'
import ParentsPage          from './pages/parents/ParentsPage'
import RapportsPage         from './pages/rapports/RapportsPage'
import HomePage             from './pages/HomePage'

// Racine : session active -> tableau de bord directement ; aucune session ->
// page d'accueil publique (vitrine), qui reste le point d'entrée pour un
// visiteur non connecté.
function RootRedirect() {
  const { isAuthenticated, loading } = useAuth()

  if (loading) {
    return (
      <Box sx={{ display: 'flex', height: '100vh', alignItems: 'center', justifyContent: 'center' }}>
        <CircularProgress />
      </Box>
    )
  }

  return isAuthenticated ? <Navigate to="/dashboard" replace /> : <HomePage />
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<RootRedirect />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/acces-otp" element={<ParentOtpAccessPage />} />
      <Route path="/demande-acces" element={<DemandeAccesPage />} />
      <Route
        path="/changer-mot-de-passe"
        element={<ProtectedRoute><ChangerMotDePassePage /></ProtectedRoute>}
      />
      <Route
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route path="dashboard"      element={<ErrorBoundary><DashboardPage /></ErrorBoundary>} />
        <Route path="etablissements" element={<ErrorBoundary><StaffRoute><EtablissementsPage /></StaffRoute></ErrorBoundary>} />
        <Route path="eleves"         element={<ErrorBoundary><StaffRoute><ElevesPage /></StaffRoute></ErrorBoundary>} />
        <Route path="mes-enfants"    element={<ErrorBoundary><MesEnfantsPage /></ErrorBoundary>} />
        <Route path="paiements"      element={<ErrorBoundary><PaiementsPage /></ErrorBoundary>} />
        <Route path="scan"           element={<ErrorBoundary><StaffRoute><ScanPage /></StaffRoute></ErrorBoundary>} />
        <Route path="passages"       element={<ErrorBoundary><PassagesPage /></ErrorBoundary>} />
        <Route path="utilisateurs"   element={<ErrorBoundary><AdminRoute><UtilisateursPage /></AdminRoute></ErrorBoundary>} />
        <Route path="parents"        element={<ErrorBoundary><AdminRoute><ParentsPage /></AdminRoute></ErrorBoundary>} />
        <Route path="demandes-acces" element={<ErrorBoundary><AdminRoute><DemandesAccesPage /></AdminRoute></ErrorBoundary>} />
        <Route path="rapports"       element={<ErrorBoundary><StaffRoute><RapportsPage /></StaffRoute></ErrorBoundary>} />
        <Route path="configuration"  element={<ErrorBoundary><AdminRoute><ConfigurationPage /></AdminRoute></ErrorBoundary>} />
      </Route>
    </Routes>
  )
}

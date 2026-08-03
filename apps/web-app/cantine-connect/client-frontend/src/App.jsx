import { Routes, Route, Navigate } from 'react-router'
import MainLayout from './layouts/MainLayout'
import LoginPage from './pages/auth/LoginPage'
import DashboardPage from './pages/DashboardPage'
import EtablissementsPage from './pages/etablissements/EtablissementsPage'
import ElevesPage from './pages/eleves/ElevesPage'
import PaiementsPage      from './pages/paiements/PaiementsPage'
import ScanPage           from './pages/scan/ScanPage'
import UtilisateursPage   from './pages/utilisateurs/UtilisateursPage'
import ProtectedRoute       from './components/ProtectedRoute'
import AdminRoute           from './components/AdminRoute'
import StaffRoute           from './components/StaffRoute'
import ErrorBoundary        from './components/ErrorBoundary'
import ConfigurationPage    from './pages/configuration/ConfigurationPage'
import PassagesPage         from './pages/passages/PassagesPage'
import ParentsPage          from './pages/parents/ParentsPage'
import RapportsPage         from './pages/rapports/RapportsPage'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard"      element={<ErrorBoundary><DashboardPage /></ErrorBoundary>} />
        <Route path="etablissements" element={<ErrorBoundary><StaffRoute><EtablissementsPage /></StaffRoute></ErrorBoundary>} />
        <Route path="eleves"         element={<ErrorBoundary><StaffRoute><ElevesPage /></StaffRoute></ErrorBoundary>} />
        <Route path="paiements"      element={<ErrorBoundary><PaiementsPage /></ErrorBoundary>} />
        <Route path="scan"           element={<ErrorBoundary><StaffRoute><ScanPage /></StaffRoute></ErrorBoundary>} />
        <Route path="passages"       element={<ErrorBoundary><PassagesPage /></ErrorBoundary>} />
        <Route path="utilisateurs"   element={<ErrorBoundary><AdminRoute><UtilisateursPage /></AdminRoute></ErrorBoundary>} />
        <Route path="parents"        element={<ErrorBoundary><AdminRoute><ParentsPage /></AdminRoute></ErrorBoundary>} />
        <Route path="rapports"       element={<ErrorBoundary><StaffRoute><RapportsPage /></StaffRoute></ErrorBoundary>} />
        <Route path="configuration"  element={<ErrorBoundary><AdminRoute><ConfigurationPage /></AdminRoute></ErrorBoundary>} />
      </Route>
    </Routes>
  )
}

import { useState } from 'react'
import { useNavigate } from 'react-router'
import {
  Box, Card, CardContent, Typography, TextField, Button, Stack, Alert, CircularProgress,
} from '@mui/material'
import LockResetIcon from '@mui/icons-material/LockReset'
import { alpha } from '@mui/material/styles'
import { useAuth } from '../../hooks/useAuth'
import { authService } from '../../services/authService'

export default function ChangerMotDePassePage() {
  const { user, updateUser } = useAuth()
  const navigate = useNavigate()

  const [form, setForm]       = useState({ motDePasseActuel: '', nouveauMotDePasse: '', confirmation: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState(null)

  const handleChange = (e) => setForm((p) => ({ ...p, [e.target.name]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.motDePasseActuel || !form.nouveauMotDePasse) { setError('Remplissez tous les champs'); return }
    if (form.nouveauMotDePasse.length < 8) { setError('Le nouveau mot de passe doit contenir au moins 8 caractères'); return }
    if (form.nouveauMotDePasse !== form.confirmation) { setError('La confirmation ne correspond pas'); return }

    setLoading(true); setError(null)
    try {
      await authService.changerMotDePasse(form.motDePasseActuel, form.nouveauMotDePasse)
      updateUser({ doitChangerMotDePasse: false })
      navigate('/dashboard', { replace: true })
    } catch (err) {
      setError(err.message || 'Échec du changement de mot de passe')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Box
      sx={{
        minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', px: 2,
        background: (t) => `linear-gradient(160deg, ${alpha(t.palette.primary.main, 0.10)} 0%, ${t.palette.background.default} 55%, ${alpha(t.palette.secondary.main, 0.06)} 100%)`,
      }}
    >
      <Box sx={{ width: '100%', maxWidth: 440 }}>
        <Stack alignItems="center" spacing={0.5} mb={3}>
          <Typography fontSize={40} lineHeight={1} mb={1}>🔐</Typography>
          <Typography variant="h5" fontWeight={800} letterSpacing="-0.02em" textAlign="center">
            Bienvenue{user?.prenom ? `, ${user.prenom}` : ''} !
          </Typography>
          <Typography variant="body2" color="text.secondary" textAlign="center">
            Pour votre sécurité, choisissez un nouveau mot de passe avant de continuer.
          </Typography>
        </Stack>

        <Card>
          <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
            <Stack direction="row" alignItems="center" spacing={1} mb={2.5}>
              <LockResetIcon fontSize="small" color="primary" />
              <Typography variant="subtitle1" fontWeight={700}>Nouveau mot de passe</Typography>
            </Stack>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>{error}</Alert>}

            <form onSubmit={handleSubmit}>
              <Stack spacing={2.5}>
                <TextField
                  label="Mot de passe temporaire"
                  name="motDePasseActuel"
                  type="password"
                  value={form.motDePasseActuel}
                  onChange={handleChange}
                  fullWidth autoFocus autoComplete="current-password"
                />
                <TextField
                  label="Nouveau mot de passe"
                  name="nouveauMotDePasse"
                  type="password"
                  value={form.nouveauMotDePasse}
                  onChange={handleChange}
                  fullWidth autoComplete="new-password"
                  helperText="8 caractères minimum"
                />
                <TextField
                  label="Confirmer le nouveau mot de passe"
                  name="confirmation"
                  type="password"
                  value={form.confirmation}
                  onChange={handleChange}
                  fullWidth autoComplete="new-password"
                />
                <Button type="submit" variant="contained" size="large" fullWidth disabled={loading} sx={{ py: 1.4, mt: 0.5 }}>
                  {loading ? <CircularProgress size={22} color="inherit" /> : 'Valider mon nouveau mot de passe'}
                </Button>
              </Stack>
            </form>
          </CardContent>
        </Card>
      </Box>
    </Box>
  )
}

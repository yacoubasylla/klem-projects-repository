import { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router'
import {
  Box, Typography, TextField, Button, Stack, Alert, CircularProgress,
  InputAdornment, IconButton, Divider,
} from '@mui/material'
import VisibilityIcon    from '@mui/icons-material/Visibility'
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff'
import LockOutlinedIcon  from '@mui/icons-material/LockOutlined'
import { useAuth } from '../../hooks/useAuth'
import { useOrganisationBranding } from '../../hooks/useConfig'
import { authService } from '../../services/authService'
import apiClient from '../../services/apiClient'
import PublicSplitLayout from '../../layouts/PublicSplitLayout'
import defaultHeroImage from '../../assets/login-hero-default.svg'

const BACKEND_ORIGIN = (apiClient.defaults.baseURL || '').replace(/\/api\/v1\/?$/, '')

export default function LoginPage() {
  const { login }  = useAuth()
  const navigate   = useNavigate()
  const location   = useLocation()
  const { logoUrl: orgLogoUrl } = useOrganisationBranding()

  const [form, setForm]             = useState({ email: '', motDePasse: '' })
  const [showPwd, setShowPwd]       = useState(false)
  const [loading, setLoading]       = useState(false)
  const [error, setError]           = useState(null)
  // Image de secours empaquetée dans le frontend, utilisée tant que la config backend
  // (FOND_ECRAN_LOGIN) n'a pas répondu ou est indisponible — remplacée dès que l'appel aboutit.
  const [bgImage, setBgImage]       = useState(defaultHeroImage)
  const [notice]                    = useState(
    location.state?.reason === 'inactivite' ? 'Vous avez été déconnecté(e) pour inactivité.' : null
  )

  useEffect(() => {
    // Fetch background image config without auth (public config endpoint)
    apiClient.get('/configurations/FOND_ECRAN_LOGIN')
      .then((res) => {
        const url = res.data?.data?.valeur?.trim()
        if (!url) return
        setBgImage(/^https?:\/\//.test(url) ? url : `${BACKEND_ORIGIN}${url}`)
      })
      .catch(() => {/* ignore — background is optional */})
  }, [])

  const handleChange = (e) => setForm((p) => ({ ...p, [e.target.name]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.email || !form.motDePasse) { setError('Remplissez tous les champs'); return }
    setLoading(true); setError(null)
    try {
      const authResponse = await authService.login(form.email, form.motDePasse)
      login(authResponse)
      navigate(authResponse.doitChangerMotDePasse ? '/changer-mot-de-passe' : '/dashboard', { replace: true })
    } catch (err) {
      setError(err.message || 'Email ou mot de passe incorrect')
    } finally {
      setLoading(false)
    }
  }

  return (
    <PublicSplitLayout
      activePage="login"
      heroBackgroundImage={bgImage}
      heroSlot={
        <Box sx={{ py: { xs: 1, md: 3 } }}>
          {orgLogoUrl ? (
            <Box
              component="img" src={orgLogoUrl} alt="" sx={{ height: 44, mb: 2, objectFit: 'contain' }}
              onError={(e) => { e.target.style.display = 'none' }}
            />
          ) : (
            <Typography fontSize={40} lineHeight={1} mb={2}>🍽️</Typography>
          )}
          <Typography variant="h3" sx={{ fontSize: { xs: '1.9rem', md: '2.3rem' }, mb: 2 }}>
            Content de vous revoir
          </Typography>
          <Typography variant="h6" sx={{ fontWeight: 400, color: 'text.secondary', maxWidth: 440 }}>
            Retrouvez le suivi des repas, des paiements et des accès de votre établissement
            en un coup d'œil.
          </Typography>
        </Box>
      }
    >
      <Box sx={{ p: { xs: 3, sm: 4 } }}>
        <Stack direction="row" alignItems="center" spacing={1} mb={2.5}>
          <LockOutlinedIcon fontSize="small" color="primary" />
          <Typography variant="subtitle1" fontWeight={700}>
            Connexion à votre espace
          </Typography>
        </Stack>

        {notice && !error && (
          <Alert severity="info" sx={{ mb: 2 }}>
            {notice}
          </Alert>
        )}

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <Stack spacing={2.5}>
            <TextField
              label="Adresse email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              fullWidth
              autoFocus
              autoComplete="email"
              size="medium"
            />
            <TextField
              label="Mot de passe"
              name="motDePasse"
              type={showPwd ? 'text' : 'password'}
              value={form.motDePasse}
              onChange={handleChange}
              fullWidth
              autoComplete="current-password"
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton onClick={() => setShowPwd(!showPwd)} edge="end" size="small">
                      {showPwd ? <VisibilityOffIcon /> : <VisibilityIcon />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
            <Button
              type="submit"
              variant="contained"
              size="large"
              fullWidth
              disabled={loading}
              sx={{ py: 1.4, mt: 0.5 }}
            >
              {loading
                ? <CircularProgress size={22} color="inherit" />
                : 'Se connecter'
              }
            </Button>
          </Stack>
        </form>

        <Divider sx={{ my: 3 }} />

        <Typography variant="body2" color="text.secondary" textAlign="center">
          Vous êtes parent ?{' '}
          <Box component="a" href="/demande-acces" sx={{ color: 'primary.main', fontWeight: 700, textDecoration: 'none' }}>
            Accès Parent (code WhatsApp/SMS) →
          </Box>
        </Typography>
      </Box>
    </PublicSplitLayout>
  )
}

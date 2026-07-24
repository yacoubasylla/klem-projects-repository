import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Box, Card, CardContent, Typography, TextField,
  Button, Stack, Alert, CircularProgress, InputAdornment, IconButton,
} from '@mui/material'
import { alpha } from '@mui/material/styles'
import VisibilityIcon    from '@mui/icons-material/Visibility'
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff'
import LockOutlinedIcon  from '@mui/icons-material/LockOutlined'
import { useAuth } from '../../hooks/useAuth'
import { authService } from '../../services/authService'
import apiClient from '../../services/apiClient'

export default function LoginPage() {
  const { login }  = useAuth()
  const navigate   = useNavigate()

  const [form, setForm]             = useState({ email: '', motDePasse: '' })
  const [showPwd, setShowPwd]       = useState(false)
  const [loading, setLoading]       = useState(false)
  const [error, setError]           = useState(null)
  const [bgImage, setBgImage]       = useState(null)

  useEffect(() => {
    // Fetch background image config without auth (public config endpoint)
    apiClient.get('/configurations/FOND_ECRAN_LOGIN')
      .then((res) => {
        const url = res.data?.data?.valeur
        if (url && url.trim()) setBgImage(url.trim())
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
      navigate('/dashboard', { replace: true })
    } catch (err) {
      setError(err.message || 'Email ou mot de passe incorrect')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        ...(bgImage
          ? {
              backgroundImage: `url(${bgImage})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center',
              backgroundRepeat: 'no-repeat',
            }
          : {
              background: (t) =>
                `linear-gradient(160deg, ${alpha(t.palette.primary.main, 0.10)} 0%, ${t.palette.background.default} 55%, ${alpha(t.palette.secondary.main, 0.06)} 100%)`,
            }),
        px: 2,
      }}
    >
      <Box sx={{ width: '100%', maxWidth: 420 }}>
        {/* Branding au-dessus de la carte */}
        <Stack alignItems="center" spacing={0.5} mb={3}>
          <Box
            sx={{
              width: 64,
              height: 64,
              borderRadius: '50%',
              background: (t) =>
                `linear-gradient(135deg, ${t.palette.primary.light} 0%, ${t.palette.primary.dark} 100%)`,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              boxShadow: (t) => `0 8px 24px ${alpha(t.palette.primary.main, 0.35)}`,
              mb: 1,
            }}
          >
            <Typography fontSize={28} lineHeight={1}>🍽️</Typography>
          </Box>
          <Typography variant="h5" fontWeight={800} letterSpacing="-0.02em">
            Cantine Connect
          </Typography>
          <Typography variant="caption" color="text.secondary" fontWeight={500}>
            KLEM Technologies &amp; Services
          </Typography>
        </Stack>

        {/* Carte de connexion */}
        <Card sx={{ overflow: 'visible' }}>
          <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
            <Stack direction="row" alignItems="center" spacing={1} mb={2.5}>
              <LockOutlinedIcon fontSize="small" color="primary" />
              <Typography variant="subtitle1" fontWeight={700}>
                Connexion à votre espace
              </Typography>
            </Stack>

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
          </CardContent>
        </Card>

        <Typography variant="caption" color="text.disabled" display="block" textAlign="center" mt={2.5}>
          © 2026 KLEM Technologies &amp; Services
        </Typography>
      </Box>
    </Box>
  )
}

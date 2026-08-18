import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router'
import {
  Box, Typography, TextField, Button, Stack, Alert, CircularProgress,
  Stepper, Step, StepLabel, Link as MuiLink,
} from '@mui/material'
import SmsOutlinedIcon from '@mui/icons-material/SmsOutlined'
import { useAuth } from '../../hooks/useAuth'
import { authService } from '../../services/authService'
import PublicSplitLayout from '../../layouts/PublicSplitLayout'
import defaultHeroImage from '../../assets/login-hero-default.svg'

const RENVOI_DELAI_SECONDES = 60

/**
 * Wizard 2 étapes : connexion parent sans mot de passe par code OTP
 * (WhatsApp/SMS/Email), pour un numéro déjà rattaché à un compte parent
 * approuvé. Un numéro inconnu est redirigé vers la demande d'accès existante
 * (aucun compte créé à la volée depuis cet écran).
 */
export default function ParentOtpAccessPage() {
  const { login } = useAuth()
  const navigate = useNavigate()

  const [whatsappNumber, setWhatsappNumber] = useState('')
  const [otpCode, setOtpCode] = useState('')
  const [codeEnvoye, setCodeEnvoye] = useState(false)
  const [sendingCode, setSendingCode] = useState(false)
  const [verifying, setVerifying] = useState(false)
  const [error, setError] = useState(null)
  const [compteACours, setCompteACours] = useState(0)
  const timerRef = useRef(null)

  useEffect(() => () => clearInterval(timerRef.current), [])

  const demarrerCompteACours = () => {
    setCompteACours(RENVOI_DELAI_SECONDES)
    clearInterval(timerRef.current)
    timerRef.current = setInterval(() => {
      setCompteACours((s) => {
        if (s <= 1) { clearInterval(timerRef.current); return 0 }
        return s - 1
      })
    }, 1000)
  }

  const handleObtenirCode = async () => {
    if (!whatsappNumber.trim()) { setError('Le numéro WhatsApp/téléphone est obligatoire'); return }
    setSendingCode(true); setError(null)
    try {
      await authService.demanderOtpParent(whatsappNumber.trim())
      setCodeEnvoye(true)
      demarrerCompteACours()
    } catch (e) {
      setError(e.message)
    } finally {
      setSendingCode(false)
    }
  }

  const handleVerifier = async (e) => {
    e.preventDefault()
    if (otpCode.trim().length !== 6) { setError('Le code de vérification est obligatoire'); return }
    setVerifying(true); setError(null)
    try {
      const authResponse = await authService.verifierOtpParent(whatsappNumber.trim(), otpCode.trim())
      login(authResponse)
      navigate('/mes-enfants', { replace: true })
    } catch (e) {
      setError(e.message)
    } finally {
      setVerifying(false)
    }
  }

  const compteIntrouvable = Boolean(error) && error.includes("demande d'accès")

  return (
    <PublicSplitLayout
      activePage="login"
      heroBackgroundImage={defaultHeroImage}
      heroSlot={
        <Box sx={{ py: { xs: 1, md: 3 } }}>
          <Typography fontSize={40} lineHeight={1} mb={2}>📱</Typography>
          <Typography variant="h3" sx={{ fontSize: { xs: '1.9rem', md: '2.3rem' }, mb: 2 }}>
            Accès rapide par code
          </Typography>
          <Typography variant="h6" sx={{ fontWeight: 400, color: 'text.secondary', maxWidth: 440 }}>
            Connectez-vous en quelques secondes grâce à un code envoyé sur WhatsApp, par SMS ou par email.
          </Typography>
        </Box>
      }
    >
      <Box sx={{ p: { xs: 3, sm: 4 } }}>
        <Stack direction="row" alignItems="center" spacing={1} mb={2.5}>
          <SmsOutlinedIcon fontSize="small" color="primary" />
          <Typography variant="subtitle1" fontWeight={700}>
            Connexion par code de vérification
          </Typography>
        </Stack>

        <Stepper activeStep={codeEnvoye ? 1 : 0} sx={{ mb: 3 }}>
          <Step><StepLabel>Numéro</StepLabel></Step>
          <Step><StepLabel>Vérification</StepLabel></Step>
        </Stepper>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
            {compteIntrouvable && (
              <>
                {' '}
                <MuiLink href="/demande-acces" fontWeight={700}>Faire une demande d'accès</MuiLink>
              </>
            )}
          </Alert>
        )}

        <form onSubmit={handleVerifier}>
          <Stack spacing={2.5}>
            <TextField
              label="Numéro WhatsApp / téléphone"
              value={whatsappNumber}
              onChange={(e) => setWhatsappNumber(e.target.value)}
              fullWidth
              autoFocus
              disabled={codeEnvoye}
              placeholder="07 00 00 00 01"
            />

            {!codeEnvoye ? (
              <Button
                variant="contained" size="large" fullWidth
                onClick={handleObtenirCode} disabled={sendingCode}
                sx={{ py: 1.4, mt: 0.5 }}
              >
                {sendingCode ? <CircularProgress size={22} color="inherit" /> : 'Obtenir le code'}
              </Button>
            ) : (
              <>
                <TextField
                  label="Code de vérification (OTP)"
                  value={otpCode}
                  onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                  fullWidth
                  autoFocus
                  slotProps={{ htmlInput: { inputMode: 'numeric', maxLength: 6 } }}
                />
                <Button
                  type="submit" variant="contained" size="large" fullWidth
                  disabled={verifying || otpCode.length !== 6}
                  sx={{ py: 1.4 }}
                >
                  {verifying ? <CircularProgress size={22} color="inherit" /> : 'Suivant'}
                </Button>
                <Button
                  variant="text" size="small" fullWidth
                  disabled={compteACours > 0 || sendingCode}
                  onClick={handleObtenirCode}
                >
                  {compteACours > 0 ? `Renvoyer le code (${compteACours}s)` : 'Renvoyer le code'}
                </Button>
              </>
            )}
          </Stack>
        </form>

        <Typography variant="body2" color="text.secondary" textAlign="center" mt={3}>
          <MuiLink href="/login" sx={{ fontWeight: 700 }}>Se connecter avec un mot de passe</MuiLink>
        </Typography>
      </Box>
    </PublicSplitLayout>
  )
}

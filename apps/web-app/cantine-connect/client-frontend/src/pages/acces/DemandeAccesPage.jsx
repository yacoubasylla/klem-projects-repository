import { useState, useEffect, useRef } from 'react'
import {
  Box, Typography, TextField, Button, Stack, Alert, CircularProgress, Container,
  Stepper, Step, StepLabel,
} from '@mui/material'
import SmsOutlinedIcon from '@mui/icons-material/SmsOutlined'
import { useAuth } from '../../hooks/useAuth'
import { authService } from '../../services/authService'
import PublicSplitLayout from '../../layouts/PublicSplitLayout'
import MesEnfantsPage from '../moi/MesEnfantsPage'
import defaultHeroImage from '../../assets/login-hero-default.svg'

const RENVOI_DELAI_SECONDES = 60

/**
 * Accès parent par OTP (remplace l'ancien formulaire "Demande d'accès" avec validation admin,
 * décision du 2026-08-18) : le parent saisit son numéro WhatsApp et son email, reçoit un code
 * par WhatsApp/SMS/Email, puis le code lui donne directement accès à la gestion de ses enfants
 * (page 2) — le compte est créé à la volée s'il n'existait pas encore.
 */
function ParentOtpStep({ onVerified }) {
  const [whatsappNumber, setWhatsappNumber] = useState('')
  const [email, setEmail] = useState('')
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
    if (!email.trim()) { setError("L'email est obligatoire"); return }
    setSendingCode(true); setError(null)
    try {
      await authService.demanderOtpParent(whatsappNumber.trim(), email.trim())
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
      onVerified(authResponse)
    } catch (e) {
      setError(e.message)
    } finally {
      setVerifying(false)
    }
  }

  return (
    <PublicSplitLayout
      activePage="acces"
      cardMaxWidth={600}
      heroBackgroundImage={defaultHeroImage}
      heroSlot={
        <Box sx={{ py: { xs: 1, md: 3 } }}>
          <Typography fontSize={40} lineHeight={1} mb={2}>📱</Typography>
          <Typography variant="h3" sx={{ fontSize: { xs: '1.9rem', md: '2.3rem' }, mb: 2 }}>
            Accès Parent
          </Typography>
          <Typography variant="h6" sx={{ fontWeight: 400, color: 'text.secondary', maxWidth: 460 }}>
            Renseignez votre numéro WhatsApp et votre email, saisissez le code reçu, et gérez
            immédiatement vos enfants — aucun compte à créer séparément.
          </Typography>
        </Box>
      }
    >
      <Box sx={{ p: { xs: 3, sm: 4 } }}>
        <Stack direction="row" alignItems="center" spacing={1} mb={2.5}>
          <SmsOutlinedIcon fontSize="small" color="primary" />
          <Typography variant="subtitle1" fontWeight={700}>Demande d'accès parent</Typography>
        </Stack>

        <Stepper activeStep={codeEnvoye ? 1 : 0} sx={{ mb: 3 }}>
          <Step><StepLabel>Numéro &amp; email</StepLabel></Step>
          <Step><StepLabel>Vérification</StepLabel></Step>
        </Stepper>

        {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>{error}</Alert>}

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
            <TextField
              label="Email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              fullWidth
              disabled={codeEnvoye}
            />

            {!codeEnvoye ? (
              <Button
                variant="contained" size="large" fullWidth
                onClick={handleObtenirCode} disabled={sendingCode}
                sx={{ py: 1.4, mt: 0.5, borderRadius: 999 }}
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
                  sx={{ py: 1.4, borderRadius: 999 }}
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
      </Box>
    </PublicSplitLayout>
  )
}

export default function DemandeAccesPage() {
  const { login } = useAuth()
  const [verifie, setVerifie] = useState(false)

  const handleVerified = (authResponse) => {
    login(authResponse)
    setVerifie(true)
  }

  if (verifie) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <MesEnfantsPage />
      </Container>
    )
  }

  return <ParentOtpStep onVerified={handleVerified} />
}

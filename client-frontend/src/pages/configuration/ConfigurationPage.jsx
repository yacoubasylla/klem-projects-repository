import { useState } from 'react'
import {
  Box, Typography, Paper, Stack, Switch, Alert, CircularProgress,
  Avatar, Chip, TextField, Button, Divider, MenuItem,
} from '@mui/material'
import QrCodeScannerIcon  from '@mui/icons-material/QrCodeScanner'
import TuneIcon           from '@mui/icons-material/Tune'
import EmailIcon          from '@mui/icons-material/Email'
import SmsIcon            from '@mui/icons-material/Sms'
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet'
import WallpaperIcon      from '@mui/icons-material/Wallpaper'
import CloudSyncIcon      from '@mui/icons-material/CloudSync'
import { useConfigurations } from '../../hooks/useConfig'

// Configurations booléennes (toggle Switch)
const TOGGLE_META = {
  SCAN_CAMERA_ENABLED: {
    label:       'Scanner Caméra Réfectoire',
    description: 'Activer la caméra du téléphone pour scanner les QR Codes directement depuis la page de contrôle accès.',
    icon:        <QrCodeScannerIcon />,
    category:    'Scan & Accès',
  },
  SCAN_CACHE_AUTO_REFRESH: {
    label:       'Rafraîchissement automatique du cache hors-ligne',
    description: 'Télécharger automatiquement le cache de secours (24h) à l\'ouverture de la page Scan Réfectoire, si une connexion est disponible. Désactivé : le téléchargement reste possible manuellement via le bouton dédié.',
    icon:        <CloudSyncIcon />,
    category:    'Scan & Accès',
  },
  NOTIFICATIONS_EMAIL_ENABLED: {
    label:       'Notifications Email',
    description: 'Envoyer un email aux parents à chaque paiement accepté et à chaque passage à la cantine.',
    icon:        <EmailIcon />,
    category:    'Notifications',
  },
  NOTIFICATIONS_SMS_ENABLED: {
    label:       'Notifications SMS',
    description: 'Envoyer un SMS aux parents (nécessite un fournisseur SMS configuré côté serveur).',
    icon:        <SmsIcon />,
    category:    'Notifications',
  },
}

// Configurations à valeur libre (champ texte)
const TEXT_META = {
  TARIF_REPAS: {
    label:       'Tarif par repas (FCFA)',
    description: 'Montant débité du solde de l\'élève à chaque passage (mode CREDITS uniquement).',
    icon:        <AccountBalanceWalletIcon />,
    category:    'Paiements',
    type:        'number',
  },
  FOND_ECRAN_LOGIN: {
    label:       'Image de fond — page de connexion',
    description: 'URL d\'une image (JPEG, PNG, WebP) à afficher en arrière-plan de la page de connexion.',
    icon:        <WallpaperIcon />,
    category:    'Apparence',
    type:        'url',
    placeholder: 'https://example.com/image.jpg',
  },
}

const MODE_PAIEMENT_OPTIONS = [
  { value: 'ABONNEMENT', label: 'Abonnement — accès libre après paiement annuel' },
  { value: 'CREDITS',    label: 'Crédits — le solde est débité à chaque repas' },
]

function ToggleRow({ config, meta, onToggle, saving }) {
  const enabled   = config.valeur === 'true'
  const isSaving  = saving === config.cle

  return (
    <Paper variant="outlined" sx={{ p: { xs: 1.5, sm: 2.5 }, mb: 2 }}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        alignItems={{ xs: 'stretch', sm: 'flex-start' }}
        justifyContent="space-between"
        spacing={2}
      >
        <Stack direction="row" spacing={2} alignItems="flex-start" sx={{ flex: 1, minWidth: 0 }}>
          <Avatar sx={{ bgcolor: enabled ? 'primary.light' : 'action.hover', color: enabled ? 'primary.dark' : 'text.secondary', mt: 0.25, flexShrink: 0 }}>
            {meta.icon}
          </Avatar>
          <Box sx={{ minWidth: 0 }}>
            <Stack direction="row" flexWrap="wrap" alignItems="center" spacing={1} mb={0.25}>
              <Typography variant="subtitle1" fontWeight={600}>{meta.label}</Typography>
              <Chip label={meta.category} size="small" variant="outlined" sx={{ height: 18, fontSize: '0.65rem' }} />
            </Stack>
            <Typography variant="body2" color="text.secondary" sx={{ maxWidth: { xs: '100%', sm: 520 }, wordBreak: 'break-word' }}>
              {meta.description}
            </Typography>
            <Typography variant="caption" color="text.disabled" sx={{ mt: 0.75, display: 'block' }}>
              Clé : <code>{config.cle}</code>
              {config.dateModification && (
                <> · Modifié le {new Date(config.dateModification).toLocaleString('fr-FR')}</>
              )}
            </Typography>
          </Box>
        </Stack>
        <Stack direction={{ xs: 'row', sm: 'column' }} alignItems="center" justifyContent={{ xs: 'flex-end', sm: 'flex-start' }} spacing={{ xs: 1.5, sm: 0.25 }} sx={{ flexShrink: 0 }}>
          {isSaving
            ? <CircularProgress size={22} sx={{ my: 0.5 }} />
            : <Switch checked={enabled} onChange={() => onToggle(config.cle, config.valeur)} color="primary" />
          }
          <Typography variant="caption" fontWeight={600} color={enabled ? 'success.main' : 'text.disabled'}>
            {enabled ? 'Activé' : 'Désactivé'}
          </Typography>
        </Stack>
      </Stack>
    </Paper>
  )
}

function TextRow({ config, meta, onSave }) {
  const [draft, setDraft] = useState(config.valeur)
  const [saving, setSaving] = useState(false)
  const dirty = draft !== config.valeur

  const handleSave = async () => {
    setSaving(true)
    await onSave(config.cle, draft)
    setSaving(false)
  }

  return (
    <Paper variant="outlined" sx={{ p: { xs: 1.5, sm: 2.5 }, mb: 2 }}>
      <Stack direction="row" spacing={2} alignItems="flex-start">
        <Avatar sx={{ bgcolor: 'action.hover', color: 'text.secondary', mt: 0.25, flexShrink: 0 }}>
          {meta.icon}
        </Avatar>
        <Box sx={{ flex: 1, minWidth: 0 }}>
          <Stack direction="row" flexWrap="wrap" alignItems="center" spacing={1} mb={0.5}>
            <Typography variant="subtitle1" fontWeight={600}>{meta.label}</Typography>
            <Chip label={meta.category} size="small" variant="outlined" sx={{ height: 18, fontSize: '0.65rem' }} />
          </Stack>
          <Typography variant="body2" color="text.secondary" sx={{ maxWidth: { xs: '100%', sm: 520 }, mb: 1.5, wordBreak: 'break-word' }}>
            {meta.description}
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} alignItems={{ xs: 'stretch', sm: 'center' }}>
            <TextField
              size="small"
              type={meta.type === 'number' ? 'number' : 'text'}
              placeholder={meta.placeholder ?? ''}
              value={draft}
              onChange={(e) => setDraft(e.target.value)}
              sx={{ width: { xs: '100%', sm: 300 } }}
            />
            <Button
              variant="contained"
              size="small"
              disabled={!dirty || saving}
              onClick={handleSave}
              sx={{ alignSelf: { xs: 'flex-end', sm: 'center' } }}
            >
              {saving ? <CircularProgress size={16} color="inherit" /> : 'Enregistrer'}
            </Button>
          </Stack>
          <Typography variant="caption" color="text.disabled" sx={{ mt: 0.75, display: 'block' }}>
            Clé : <code>{config.cle}</code>
          </Typography>
        </Box>
      </Stack>
    </Paper>
  )
}

export default function ConfigurationPage() {
  const { configs, loading, error, modifier } = useConfigurations()
  const [saving, setSaving] = useState(null)
  const [saveError, setSaveError] = useState(null)

  const getConfig = (cle) => configs.find((c) => c.cle === cle)

  const handleToggle = async (cle, currentValeur) => {
    const newValeur = currentValeur === 'true' ? 'false' : 'true'
    setSaving(cle)
    setSaveError(null)
    try {
      await modifier(cle, newValeur)
    } catch (e) {
      setSaveError(e.message)
    } finally {
      setSaving(null)
    }
  }

  const handleSaveText = async (cle, valeur) => {
    try {
      await modifier(cle, valeur)
    } catch (e) {
      setSaveError(e.message)
    }
  }

  return (
    <Box>
      <Stack direction="row" alignItems="center" spacing={1.5} mb={3}>
        <TuneIcon color="primary" />
        <Typography variant="h5" fontWeight={600}>Configuration Système</Typography>
      </Stack>

      {(error || saveError) && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setSaveError(null)}>
          {error || saveError}
        </Alert>
      )}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          {/* ── Fonctionnalités (toggles) ── */}
          <Typography variant="overline" color="text.secondary" sx={{ mb: 1.5, display: 'block' }}>
            Fonctionnalités
          </Typography>
          {Object.entries(TOGGLE_META).map(([cle, meta]) => {
            const config = getConfig(cle)
            if (!config) return null
            return (
              <ToggleRow key={cle} config={config} meta={meta} onToggle={handleToggle} saving={saving} />
            )
          })}

          <Divider sx={{ my: 3 }} />

          {/* ── Mode de paiement ── */}
          <Typography variant="overline" color="text.secondary" sx={{ mb: 1.5, display: 'block' }}>
            Paiements & Accès
          </Typography>
          {(() => {
            const config = getConfig('MODE_PAIEMENT')
            if (!config) return null
            return (
              <Paper variant="outlined" sx={{ p: { xs: 1.5, sm: 2.5 }, mb: 2 }}>
                <Stack direction="row" spacing={2} alignItems="flex-start">
                  <Avatar sx={{ bgcolor: 'action.hover', color: 'text.secondary', mt: 0.25, flexShrink: 0 }}>
                    <AccountBalanceWalletIcon />
                  </Avatar>
                  <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Typography variant="subtitle1" fontWeight={600} mb={0.5}>Mode d'accès cantine</Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ maxWidth: { xs: '100%', sm: 520 }, mb: 1.5, wordBreak: 'break-word' }}>
                      En mode <strong>Abonnement</strong>, l'élève accède librement après paiement annuel.
                      En mode <strong>Crédits</strong>, son solde est débité à chaque repas selon le tarif unitaire défini ci-dessous.
                    </Typography>
                    <TextField
                      select
                      size="small"
                      value={config.valeur}
                      onChange={(e) => handleSaveText('MODE_PAIEMENT', e.target.value)}
                      sx={{ width: { xs: '100%', sm: 360 } }}
                    >
                      {MODE_PAIEMENT_OPTIONS.map((o) => (
                        <MenuItem key={o.value} value={o.value}>{o.label}</MenuItem>
                      ))}
                    </TextField>
                  </Box>
                </Stack>
              </Paper>
            )
          })()}

          {Object.entries(TEXT_META)
            .filter(([cle]) => cle === 'TARIF_REPAS')
            .map(([cle, meta]) => {
              const config = getConfig(cle)
              if (!config) return null
              return <TextRow key={cle} config={config} meta={meta} onSave={handleSaveText} />
            })}

          <Divider sx={{ my: 3 }} />

          {/* ── Apparence ── */}
          <Typography variant="overline" color="text.secondary" sx={{ mb: 1.5, display: 'block' }}>
            Apparence
          </Typography>
          {Object.entries(TEXT_META)
            .filter(([cle]) => cle === 'FOND_ECRAN_LOGIN')
            .map(([cle, meta]) => {
              const config = getConfig(cle)
              if (!config) return null
              return (
                <Box key={cle}>
                  <TextRow config={config} meta={meta} onSave={handleSaveText} />
                  {config.valeur && (
                    <Paper variant="outlined" sx={{ p: 1.5, mb: 2, display: 'inline-block', maxWidth: '100%' }}>
                      <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>Aperçu</Typography>
                      <Box
                        component="img"
                        src={config.valeur}
                        alt="Fond de connexion"
                        sx={{ height: 120, maxWidth: '100%', borderRadius: 1, objectFit: 'cover', display: 'block' }}
                        onError={(e) => { e.target.style.display = 'none' }}
                      />
                    </Paper>
                  )}
                </Box>
              )
            })}
        </>
      )}
    </Box>
  )
}

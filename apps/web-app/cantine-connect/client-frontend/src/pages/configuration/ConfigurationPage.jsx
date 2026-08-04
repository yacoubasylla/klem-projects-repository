import { useState } from 'react'
import {
  Box, Typography, Paper, Stack, Switch, Alert, CircularProgress,
  Avatar, Chip, TextField, Button, MenuItem,
  Accordion, AccordionSummary, AccordionDetails,
} from '@mui/material'
import ExpandMoreIcon     from '@mui/icons-material/ExpandMore'
import QrCodeScannerIcon  from '@mui/icons-material/QrCodeScanner'
import TuneIcon           from '@mui/icons-material/Tune'
import EmailIcon          from '@mui/icons-material/Email'
import SmsIcon            from '@mui/icons-material/Sms'
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive'
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet'
import WallpaperIcon      from '@mui/icons-material/Wallpaper'
import CloudSyncIcon      from '@mui/icons-material/CloudSync'
import BadgeIcon          from '@mui/icons-material/Badge'
import PlaceIcon          from '@mui/icons-material/Place'
import PhoneIcon          from '@mui/icons-material/Phone'
import AlternateEmailIcon from '@mui/icons-material/AlternateEmail'
import PhoneAndroidIcon   from '@mui/icons-material/PhoneAndroid'
import ImageIcon          from '@mui/icons-material/Image'
import UploadIcon         from '@mui/icons-material/Upload'
import { useConfigurations } from '../../hooks/useConfig'
import { configService } from '../../services/configService'
import apiClient from '../../services/apiClient'

const BACKEND_ORIGIN = (apiClient.defaults.baseURL || '').replace(/\/api\/v1\/?$/, '')
const resoudreUrlLogo = (url) => (url ? (/^https?:\/\//.test(url) ? url : `${BACKEND_ORIGIN}${url}`) : '')

// Configurations booléennes (toggle Switch)
const TOGGLE_META = {
  SCAN_CAMERA_ENABLED: {
    label:       'Scanner avec l\'appareil photo du téléphone',
    description: 'Reconnaître les élèves à l\'entrée de la cantine avec l\'appareil photo du téléphone, sans matériel supplémentaire.',
    icon:        <QrCodeScannerIcon />,
    category:    'Contrôle d\'accès cantine',
  },
  SCAN_CACHE_AUTO_REFRESH: {
    label:       'Fonctionnement sans connexion internet',
    description: 'Le poste de contrôle continue à reconnaître les élèves même sans connexion internet, en téléchargeant les informations à l\'avance à chaque ouverture de la page. Désactivé : ce téléchargement reste possible manuellement.',
    icon:        <CloudSyncIcon />,
    category:    'Contrôle d\'accès cantine',
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
    description: 'Montant débité du solde de l\'élève à chaque passage à la cantine (uniquement si le mode « Crédits » est activé ci-dessous).',
    icon:        <AccountBalanceWalletIcon />,
    category:    'Paiements',
    type:        'number',
  },
  FOND_ECRAN_LOGIN: {
    label:       'Image de fond — page de connexion',
    description: 'Lien vers une image à afficher en arrière-plan de la page de connexion (formats courants : JPEG, PNG, WebP).',
    icon:        <WallpaperIcon />,
    category:    'Apparence',
    type:        'url',
    placeholder: 'https://example.com/image.jpg',
  },
  ORGANISATION_NOM: {
    label:       'Nom du client',
    description: 'Affiché en haut de chaque page de l\'application, à la place de « Cantine Connect ».',
    icon:        <BadgeIcon />,
    category:    'Organisation',
    placeholder: 'Cantine Connect',
  },
  ORGANISATION_ADRESSE: {
    label:       'Adresse / lieu',
    description: 'Ville, commune ou adresse du client.',
    icon:        <PlaceIcon />,
    category:    'Organisation',
  },
  ORGANISATION_TELEPHONE: {
    label:       'Téléphone / cellulaire',
    description: 'Numéro de contact du client.',
    icon:        <PhoneIcon />,
    category:    'Organisation',
  },
  ORGANISATION_EMAIL: {
    label:       'Email',
    description: 'Adresse email de contact du client.',
    icon:        <AlternateEmailIcon />,
    category:    'Organisation',
    type:        'email',
  },
  ORGANISATION_MOBILE_MONEY_NUMERO: {
    label:       'Numéro Mobile Money (réception)',
    description: 'Numéro donné aux parents pour un envoi manuel d\'argent — c\'est une information affichée aux parents, elle ne change pas le fonctionnement des paiements en ligne.',
    icon:        <PhoneAndroidIcon />,
    category:    'Organisation',
  },
}

const MODE_PAIEMENT_OPTIONS = [
  { value: 'ABONNEMENT', label: 'Abonnement — accès libre après paiement annuel' },
  { value: 'CREDITS',    label: 'Crédits — le solde est débité à chaque repas' },
]

// Ordre d'affichage des catégories + icône d'en-tête de chaque accordéon.
const CATEGORY_ORDER = [
  { name: 'Contrôle d\'accès cantine', icon: <QrCodeScannerIcon /> },
  { name: 'Notifications', icon: <NotificationsActiveIcon /> },
  { name: 'Paiements',    icon: <AccountBalanceWalletIcon /> },
  { name: 'Apparence',    icon: <WallpaperIcon /> },
  { name: 'Organisation', icon: <BadgeIcon /> },
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
            {config.dateModification && (
              <Typography variant="caption" color="text.disabled" sx={{ mt: 0.75, display: 'block' }}>
                Modifié le {new Date(config.dateModification).toLocaleString('fr-FR')}
              </Typography>
            )}
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
        </Box>
      </Stack>
    </Paper>
  )
}

function LogoUploadRow({ config, onUploaded }) {
  const [uploading, setUploading] = useState(false)
  const [error, setError]         = useState(null)
  const url = resoudreUrlLogo(config?.valeur)

  const handleFichier = async (e) => {
    const fichier = e.target.files?.[0]
    e.target.value = ''
    if (!fichier) return
    setUploading(true); setError(null)
    try {
      const updated = await configService.uploaderLogo(fichier)
      onUploaded(updated)
    } catch (err) {
      setError(err.message)
    } finally {
      setUploading(false)
    }
  }

  return (
    <Paper variant="outlined" sx={{ p: { xs: 1.5, sm: 2.5 }, mb: 2 }}>
      <Stack direction="row" spacing={2} alignItems="flex-start">
        <Avatar sx={{ bgcolor: 'action.hover', color: 'text.secondary', mt: 0.25, flexShrink: 0 }}>
          <ImageIcon />
        </Avatar>
        <Box sx={{ flex: 1, minWidth: 0 }}>
          <Stack direction="row" flexWrap="wrap" alignItems="center" spacing={1} mb={0.5}>
            <Typography variant="subtitle1" fontWeight={600}>Logo du client</Typography>
            <Chip label="Organisation" size="small" variant="outlined" sx={{ height: 18, fontSize: '0.65rem' }} />
          </Stack>
          <Typography variant="body2" color="text.secondary" sx={{ maxWidth: { xs: '100%', sm: 520 }, mb: 1.5 }}>
            Affiché en haut de chaque page de l'application, à la place de l'icône par défaut.
            Formats acceptés : PNG, JPEG, WebP, SVG.
          </Typography>
          <Stack direction="row" spacing={2} alignItems="center" flexWrap="wrap">
            {url && (
              <Box
                component="img" src={url} alt="Logo actuel"
                sx={{ height: 48, maxWidth: 160, objectFit: 'contain', borderRadius: 1, border: '1px solid', borderColor: 'divider', p: 0.5 }}
                onError={(e) => { e.target.style.display = 'none' }}
              />
            )}
            <Button
              component="label" variant="outlined" size="small"
              startIcon={uploading ? <CircularProgress size={16} /> : <UploadIcon />}
              disabled={uploading}
            >
              {url ? 'Remplacer' : 'Importer un logo'}
              <input type="file" accept="image/*" hidden onChange={handleFichier} />
            </Button>
          </Stack>
          {error && <Alert severity="error" sx={{ mt: 1.5 }}>{error}</Alert>}
        </Box>
      </Stack>
    </Paper>
  )
}

export default function ConfigurationPage() {
  const { configs, loading, error, modifier, recharger } = useConfigurations()
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

  // Contenu de chaque catégorie — assemble les mêmes lignes qu'avant (toggles, champs
  // texte, bloc spécial Mode de paiement, upload logo), simplement regroupées par
  // meta.category au lieu de sections codées en dur.
  const renderCategoryContent = (categorie) => {
    switch (categorie) {
      case 'Contrôle d\'accès cantine':
      case 'Notifications':
        return Object.entries(TOGGLE_META)
          .filter(([, meta]) => meta.category === categorie)
          .map(([cle, meta]) => {
            const config = getConfig(cle)
            if (!config) return null
            return <ToggleRow key={cle} config={config} meta={meta} onToggle={handleToggle} saving={saving} />
          })

      case 'Paiements': {
        const modePaiement = getConfig('MODE_PAIEMENT')
        return (
          <>
            {modePaiement && (
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
                      value={modePaiement.valeur}
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
            )}
            {Object.entries(TEXT_META)
              .filter(([, meta]) => meta.category === 'Paiements')
              .map(([cle, meta]) => {
                const config = getConfig(cle)
                if (!config) return null
                return <TextRow key={cle} config={config} meta={meta} onSave={handleSaveText} />
              })}
          </>
        )
      }

      case 'Apparence':
        return Object.entries(TEXT_META)
          .filter(([, meta]) => meta.category === 'Apparence')
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
          })

      case 'Organisation':
        return (
          <>
            <LogoUploadRow config={getConfig('ORGANISATION_LOGO_URL')} onUploaded={recharger} />
            {Object.entries(TEXT_META)
              .filter(([, meta]) => meta.category === 'Organisation')
              .map(([cle, meta]) => {
                const config = getConfig(cle)
                if (!config) return null
                return <TextRow key={cle} config={config} meta={meta} onSave={handleSaveText} />
              })}
          </>
        )

      default:
        return null
    }
  }

  return (
    <Box>
      <Stack direction="row" alignItems="center" spacing={1.5} mb={3}>
        <TuneIcon color="primary" />
        <Typography variant="h5" fontWeight={600}>Paramètres</Typography>
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
        CATEGORY_ORDER.map(({ name, icon }, i) => {
          const contenu = renderCategoryContent(name)
          const estVide = Array.isArray(contenu) ? contenu.every((n) => n === null) : !contenu
          if (estVide) return null
          return (
            <Accordion key={name} defaultExpanded={i === 0} disableGutters sx={{ mb: 1.5, '&:before': { display: 'none' } }} variant="outlined">
              <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                <Stack direction="row" alignItems="center" spacing={1.5}>
                  {icon}
                  <Typography variant="subtitle1" fontWeight={600}>{name}</Typography>
                </Stack>
              </AccordionSummary>
              <AccordionDetails sx={{ pt: 0 }}>
                {contenu}
              </AccordionDetails>
            </Accordion>
          )
        })
      )}
    </Box>
  )
}

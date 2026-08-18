import { useState, useEffect, useCallback } from 'react'
import {
  Box, Typography, Button, Stack, Alert, Chip, Card, CardContent, Grid,
  Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem,
  CircularProgress, Avatar, Skeleton, IconButton, Tooltip,
} from '@mui/material'
import AddIcon         from '@mui/icons-material/Add'
import ChildCareIcon   from '@mui/icons-material/ChildCare'
import EditIcon        from '@mui/icons-material/Edit'
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutlined'
import QrCode2Icon     from '@mui/icons-material/QrCode2'
import { QRCodeSVG } from 'qrcode.react'
import { parentService } from '../../services/parentService'
import { useEtablissements } from '../../hooks/useEtablissements'
import { useClasses } from '../../hooks/useClasses'
import { SuccessSnackbar } from '@klem/ui'

const STATUT_CONFIG = {
  EN_ATTENTE_PAIEMENT: { label: 'En attente de paiement', color: 'warning' },
  AUTORISE:            { label: 'Autorisé',               color: 'success' },
  GRACE:                { label: 'Période de grâce',       color: 'info' },
  SUSPENDU:             { label: 'Suspendu',                color: 'error' },
}

const FORM_INITIAL = {
  etablissementId: '', classeId: '', nom: '', prenom: '',
  sexe: '', dateNaissance: '', ville: '', commune: '', quartier: '',
}

// ── Ajout / modification (formulaire partagé) ───────────────────────────────

function EnfantFormDialog({ open, mode, initialValues, onClose, onSubmit }) {
  const [form, setForm]       = useState(FORM_INITIAL)
  const [saving, setSaving]   = useState(false)
  const [err, setErr]         = useState(null)
  const { etablissements } = useEtablissements(open)
  const { classes } = useClasses(form.etablissementId || null)

  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => { if (open) { setForm(initialValues ?? FORM_INITIAL); setErr(null) } }, [open, initialValues])

  const set = (name, value) => setForm((p) => ({ ...p, [name]: value }))
  const handleChange = (e) => set(e.target.name, e.target.value)

  const validate = () => {
    if (!form.etablissementId) return "L'établissement est obligatoire"
    if (!form.classeId) return 'La classe est obligatoire'
    if (!form.nom.trim()) return 'Le nom est obligatoire'
    if (!form.prenom.trim()) return 'Le prénom est obligatoire'
    if (!form.ville.trim()) return 'La ville est obligatoire'
    if (!form.commune.trim()) return 'La commune est obligatoire'
    return null
  }

  const handleSubmit = async () => {
    const validation = validate()
    if (validation) { setErr(validation); return }
    setSaving(true); setErr(null)
    try {
      await onSubmit({
        ...form,
        etablissementId: Number(form.etablissementId),
        classeId: Number(form.classeId),
        sexe: form.sexe || null,
        dateNaissance: form.dateNaissance || null,
        quartier: form.quartier || null,
      })
      onClose()
    } catch (e) {
      setErr(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{mode === 'modifier' ? 'Modifier un enfant' : 'Ajouter un enfant'}</DialogTitle>
      <DialogContent>
        <Stack spacing={2.5} mt={1}>
          <TextField
            select label="Établissement *" value={form.etablissementId}
            onChange={(e) => { set('etablissementId', e.target.value); set('classeId', '') }}
            fullWidth
          >
            {etablissements.map((e) => <MenuItem key={e.id} value={String(e.id)}>{e.nom}</MenuItem>)}
          </TextField>
          <TextField
            select label="Classe *" name="classeId" value={form.classeId}
            onChange={handleChange} fullWidth disabled={!form.etablissementId}
          >
            {classes.length === 0
              ? <MenuItem disabled value="">Aucune classe disponible</MenuItem>
              : classes.map((c) => <MenuItem key={c.id} value={String(c.id)}>{c.libelle} — {c.anneeScolaire}</MenuItem>)
            }
          </TextField>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField label="Nom *" name="nom" value={form.nom} onChange={handleChange} fullWidth />
            <TextField label="Prénom *" name="prenom" value={form.prenom} onChange={handleChange} fullWidth />
          </Stack>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField select label="Sexe" name="sexe" value={form.sexe} onChange={handleChange} fullWidth>
              <MenuItem value="">—</MenuItem>
              <MenuItem value="M">Masculin</MenuItem>
              <MenuItem value="F">Féminin</MenuItem>
            </TextField>
            <TextField
              label="Date de naissance" name="dateNaissance" type="date" value={form.dateNaissance}
              onChange={handleChange} fullWidth InputLabelProps={{ shrink: true }}
            />
          </Stack>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField label="Ville *" name="ville" value={form.ville} onChange={handleChange} fullWidth />
            <TextField label="Commune *" name="commune" value={form.commune} onChange={handleChange} fullWidth />
          </Stack>
          <TextField label="Quartier" name="quartier" value={form.quartier} onChange={handleChange} fullWidth />
          {err && <Alert severity="error">{err}</Alert>}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={saving}>Annuler</Button>
        <Button variant="contained" onClick={handleSubmit} disabled={saving}>
          {saving ? <CircularProgress size={20} /> : (mode === 'modifier' ? 'Enregistrer' : 'Ajouter')}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

// ── QR Code ──────────────────────────────────────────────────────────────────

function QrCodeDialog({ enfant, onClose }) {
  if (!enfant) return null
  return (
    <Dialog open={Boolean(enfant)} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>QR Code — {enfant.prenom} {enfant.nom}</DialogTitle>
      <DialogContent>
        <Stack alignItems="center" spacing={2} py={1}>
          <QRCodeSVG value={enfant.qrCodeToken} size={220} level="M" includeMargin />
          <Typography variant="body2" color="text.secondary">{enfant.classeLibelle}</Typography>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Fermer</Button>
      </DialogActions>
    </Dialog>
  )
}

// ── Désactivation ────────────────────────────────────────────────────────────

function ConfirmDesactiverDialog({ enfant, onClose, onConfirm }) {
  const [submitting, setSubmitting] = useState(false)
  const [err, setErr] = useState(null)

  const handleConfirm = async () => {
    setSubmitting(true); setErr(null)
    try {
      await onConfirm(enfant.id)
      onClose()
    } catch (e) {
      setErr(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (!enfant) return null
  return (
    <Dialog open={Boolean(enfant)} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Désactiver {enfant.prenom} {enfant.nom} ?</DialogTitle>
      <DialogContent>
        <Typography color="text.secondary">
          L'enfant sera retiré de votre liste et perdra son accès à la cantine.
          Cette action peut être annulée par un administrateur si besoin.
        </Typography>
        {err && <Alert severity="error" sx={{ mt: 2 }}>{err}</Alert>}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Annuler</Button>
        <Button color="error" variant="contained" onClick={handleConfirm} disabled={submitting}>
          {submitting ? <CircularProgress size={20} /> : 'Désactiver'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default function MesEnfantsPage() {
  const [enfants, setEnfants]     = useState([])
  const [loading, setLoading]     = useState(true)
  const [error, setError]         = useState(null)
  const [dialogMode, setDialogMode] = useState(null) // null | 'creer' | 'modifier'
  const [enfantEnEdition, setEnfantEnEdition] = useState(null)
  const [enfantQrCode, setEnfantQrCode] = useState(null)
  const [enfantADesactiver, setEnfantADesactiver] = useState(null)
  const [successMsg, setSuccessMsg] = useState('')

  const charger = useCallback(async () => {
    setLoading(true); setError(null)
    try {
      const moi = await parentService.getMoi()
      setEnfants(moi.enfants ?? [])
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => { charger() }, [charger])

  const handleAjouter = async (dto) => {
    await parentService.ajouterEnfant(dto)
    setSuccessMsg('Enfant ajouté avec succès')
    charger()
  }

  const handleModifier = async (dto) => {
    await parentService.modifierEnfant(enfantEnEdition.id, dto)
    setSuccessMsg('Enfant modifié avec succès')
    charger()
  }

  const handleDesactiver = async (eleveId) => {
    await parentService.desactiverEnfant(eleveId)
    setSuccessMsg('Enfant désactivé')
    charger()
  }

  const ouvrirModification = (enfant) => {
    setEnfantEnEdition(enfant)
    setDialogMode('modifier')
  }

  const fermerFormulaire = () => {
    setDialogMode(null)
    setEnfantEnEdition(null)
  }

  const valeursInitialesEdition = enfantEnEdition
    ? {
        etablissementId: enfantEnEdition.etablissementId != null ? String(enfantEnEdition.etablissementId) : '',
        classeId: enfantEnEdition.classeId != null ? String(enfantEnEdition.classeId) : '',
        nom: enfantEnEdition.nom ?? '',
        prenom: enfantEnEdition.prenom ?? '',
        sexe: enfantEnEdition.sexe ?? '',
        dateNaissance: enfantEnEdition.dateNaissance ?? '',
        ville: enfantEnEdition.ville ?? '',
        commune: enfantEnEdition.commune ?? '',
        quartier: enfantEnEdition.quartier ?? '',
      }
    : FORM_INITIAL

  return (
    <Box>
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between" mb={2} gap={1}>
        <Box>
          <Typography variant="h5" fontWeight={600}>Mes enfants</Typography>
          <Typography variant="caption" color="text.secondary">Élèves rattachés à votre compte</Typography>
        </Box>
        <Button variant="contained" startIcon={<AddIcon />} onClick={() => setDialogMode('creer')}>
          Ajouter un enfant
        </Button>
      </Stack>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading ? (
        <Grid container spacing={2}>
          {Array.from({ length: 2 }).map((_, i) => (
            <Grid key={i} size={{ xs: 12, sm: 6, md: 4 }}>
              <Skeleton variant="rounded" height={140} />
            </Grid>
          ))}
        </Grid>
      ) : enfants.length === 0 ? (
        <Card variant="outlined">
          <CardContent sx={{ textAlign: 'center', py: 6 }}>
            <ChildCareIcon sx={{ fontSize: 48, color: 'text.disabled', mb: 1 }} />
            <Typography color="text.secondary">Aucun enfant rattaché pour l'instant</Typography>
          </CardContent>
        </Card>
      ) : (
        <Grid container spacing={2}>
          {enfants.map((e) => (
            <Grid key={e.id} size={{ xs: 12, sm: 6, md: 4 }}>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" spacing={1.5} alignItems="center" mb={1.5}>
                    <Avatar sx={{ bgcolor: 'primary.light' }}>{e.prenom?.[0]}{e.nom?.[0]}</Avatar>
                    <Box flexGrow={1}>
                      <Typography variant="subtitle2" fontWeight={700}>{e.prenom} {e.nom}</Typography>
                      <Typography variant="caption" color="text.secondary">Matricule {e.matricule}</Typography>
                    </Box>
                  </Stack>
                  <Chip
                    label={STATUT_CONFIG[e.statutAcces]?.label ?? e.statutAcces}
                    color={STATUT_CONFIG[e.statutAcces]?.color ?? 'default'}
                    size="small"
                    sx={{ mb: 1.5 }}
                  />
                  <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                    <Tooltip title="Afficher le QR Code">
                      <IconButton size="small" onClick={() => setEnfantQrCode(e)}>
                        <QrCode2Icon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Modifier">
                      <IconButton size="small" onClick={() => ouvrirModification(e)}>
                        <EditIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Désactiver">
                      <IconButton size="small" color="error" onClick={() => setEnfantADesactiver(e)}>
                        <DeleteOutlineIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <EnfantFormDialog
        open={dialogMode === 'creer'}
        mode="creer"
        initialValues={FORM_INITIAL}
        onClose={fermerFormulaire}
        onSubmit={handleAjouter}
      />
      <EnfantFormDialog
        open={dialogMode === 'modifier'}
        mode="modifier"
        initialValues={valeursInitialesEdition}
        onClose={fermerFormulaire}
        onSubmit={handleModifier}
      />
      <QrCodeDialog enfant={enfantQrCode} onClose={() => setEnfantQrCode(null)} />
      <ConfirmDesactiverDialog
        enfant={enfantADesactiver}
        onClose={() => setEnfantADesactiver(null)}
        onConfirm={handleDesactiver}
      />
      <SuccessSnackbar open={Boolean(successMsg)} message={successMsg} onClose={() => setSuccessMsg('')} />
    </Box>
  )
}

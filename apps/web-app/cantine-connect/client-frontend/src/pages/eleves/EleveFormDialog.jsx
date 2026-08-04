import { useState, useEffect, useRef } from 'react'
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Tabs, Tab, Box, Stack, TextField, Button, Typography, Chip, Link,
  MenuItem, FormControlLabel, Checkbox, Alert, CircularProgress,
  useMediaQuery,
} from '@mui/material'
import { useTheme } from '@mui/material/styles'
import UploadFileIcon from '@mui/icons-material/UploadFile'
import CheckCircleIcon from '@mui/icons-material/CheckCircle'
import { useClasses } from '../../hooks/useClasses'
import { eleveService } from '../../services/eleveService'
import apiClient from '../../services/apiClient'

const STATUTS = [
  { value: 'EN_ATTENTE_PAIEMENT', label: 'En attente de paiement' },
  { value: 'AUTORISE',            label: 'Autorisé' },
  { value: 'GRACE',               label: 'Période de grâce' },
  { value: 'SUSPENDU',            label: 'Suspendu' },
]

const REGIMES = [
  { value: 'STANDARD',          label: 'Standard' },
  { value: 'SANS_PORC',         label: 'Sans porc' },
  { value: 'VEGETARIEN',        label: 'Végétarien' },
  { value: 'ALLERGIE_SPECIFIQUE', label: 'Allergie spécifique' },
]

const FORM_INITIAL = {
  matricule: '', nom: '', prenom: '', dateNaissance: '',
  etablissementId: '', classeId: '',
  statutAcces: 'EN_ATTENTE_PAIEMENT', regimeAlimentaire: 'STANDARD', estBoursier: false,
  parentNom: '', parentTelephone: '', parentEmail: '',
  allergies: '', certificatMedicalUrl: '', notesMedicales: '',
}

// L'API sert /uploads/** à la racine du backend, pas sous /api/v1 — on retire
// ce suffixe de la baseURL pour construire un lien cliquable vers le certificat.
const BACKEND_ORIGIN = (apiClient.defaults.baseURL || '').replace(/\/api\/v1\/?$/, '')

function TabPanel({ children, value, index }) {
  return (
    <Box role="tabpanel" hidden={value !== index} sx={{ pt: 2 }}>
      {value === index && children}
    </Box>
  )
}

export default function EleveFormDialog({ open, onClose, onSuccess, eleveToEdit, etablissements }) {
  const isEdit = Boolean(eleveToEdit)
  const theme = useTheme()
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'))
  const [tab, setTab] = useState(0)
  const [form, setForm] = useState(FORM_INITIAL)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState(null)
  const [uploading, setUploading] = useState(false)
  const [uploadError, setUploadError] = useState(null)
  const fileInputRef = useRef(null)

  const { classes } = useClasses(form.etablissementId || null)

  useEffect(() => {
    if (open) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setTab(0)
      setFormError(null)
      setUploadError(null)
      if (eleveToEdit) {
        setForm({
          matricule: eleveToEdit.matricule ?? '',
          nom: eleveToEdit.nom ?? '',
          prenom: eleveToEdit.prenom ?? '',
          dateNaissance: eleveToEdit.dateNaissance ?? '',
          etablissementId: eleveToEdit.etablissementId ?? '',
          classeId: eleveToEdit.classeId ?? '',
          statutAcces: eleveToEdit.statutAcces ?? 'EN_ATTENTE_PAIEMENT',
          regimeAlimentaire: eleveToEdit.regimeAlimentaire ?? 'STANDARD',
          estBoursier: eleveToEdit.estBoursier ?? false,
          parentNom: eleveToEdit.parentNom ?? '',
          parentTelephone: eleveToEdit.parentTelephone ?? '',
          parentEmail: eleveToEdit.parentEmail ?? '',
          allergies: eleveToEdit.allergies ?? '',
          certificatMedicalUrl: eleveToEdit.certificatMedicalUrl ?? '',
          notesMedicales: eleveToEdit.notesMedicales ?? '',
        })
      } else {
        setForm(FORM_INITIAL)
      }
    }
  }, [open, eleveToEdit])

  const set = (name, value) => setForm((prev) => ({ ...prev, [name]: value }))
  const handleChange = (e) => set(e.target.name, e.target.value)

  const validate = () => {
    if (!form.matricule.trim()) return 'Le matricule est obligatoire (onglet Général)'
    if (!form.nom.trim()) return 'Le nom est obligatoire (onglet Général)'
    if (!form.prenom.trim()) return 'Le prénom est obligatoire (onglet Général)'
    if (!form.etablissementId) return "L'établissement est obligatoire (onglet Cantine)"
    if (!form.classeId) return 'La classe est obligatoire (onglet Cantine)'
    if (!form.parentNom.trim()) return 'Le nom du parent est obligatoire (onglet Contacts)'
    if (!form.parentTelephone.trim()) return 'Le téléphone du parent est obligatoire (onglet Contacts)'
    if (form.allergies.trim() && !isEdit) {
      return "Un certificat médical ne peut être importé qu'après la création de la fiche — enregistrez d'abord sans allergie, puis modifiez la fiche pour l'ajouter (onglet Contacts)"
    }
    if (form.allergies.trim() && !form.certificatMedicalUrl) {
      return "Un certificat médical (allergologue) est obligatoire pour déclarer une allergie — importez-le avant d'enregistrer (onglet Contacts)"
    }
    return null
  }

  const handleUploadClick = () => fileInputRef.current?.click()

  const handleFileSelected = async (e) => {
    const fichier = e.target.files?.[0]
    e.target.value = ''
    if (!fichier || !eleveToEdit) return
    setUploading(true)
    setUploadError(null)
    try {
      const { certificatMedicalUrl } = await eleveService.uploaderCertificatMedical(eleveToEdit.id, fichier)
      set('certificatMedicalUrl', certificatMedicalUrl)
    } catch (err) {
      setUploadError(err.message)
    } finally {
      setUploading(false)
    }
  }

  const handleSubmit = async () => {
    const err = validate()
    if (err) { setFormError(err); return }
    setSaving(true)
    setFormError(null)
    try {
      const payload = {
        ...form,
        etablissementId: Number(form.etablissementId),
        classeId: Number(form.classeId),
        dateNaissance: form.dateNaissance || null,
        certificatMedicalUrl: form.certificatMedicalUrl || null,
      }
      await onSuccess(payload)
      onClose()
    } catch (e) {
      setFormError(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth fullScreen={fullScreen}>
      <DialogTitle>{isEdit ? 'Modifier l\'élève' : 'Nouvel élève'}</DialogTitle>

      <Box sx={{ borderBottom: 1, borderColor: 'divider', px: { xs: 1, sm: 3 } }}>
        <Tabs value={tab} onChange={(_, v) => setTab(v)} variant="scrollable" allowScrollButtonsMobile scrollButtons="auto">
          <Tab label="Général" />
          <Tab label="Cantine / Affectation" />
          <Tab label="Contacts / Allergies" />
        </Tabs>
      </Box>

      <DialogContent sx={{ minHeight: 280 }}>
        {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}

        {/* ── Onglet 0 : Général ─────────────────────────── */}
        <TabPanel value={tab} index={0}>
          <Stack spacing={2}>
            <TextField label="Matricule *" name="matricule" value={form.matricule} onChange={handleChange} fullWidth />
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField label="Nom *" name="nom" value={form.nom} onChange={handleChange} fullWidth />
              <TextField label="Prénom *" name="prenom" value={form.prenom} onChange={handleChange} fullWidth />
            </Stack>
            <TextField
              label="Date de naissance"
              name="dateNaissance"
              type="date"
              value={form.dateNaissance}
              onChange={handleChange}
              fullWidth
              InputLabelProps={{ shrink: true }}
            />
          </Stack>
        </TabPanel>

        {/* ── Onglet 1 : Cantine / Affectation ───────────── */}
        <TabPanel value={tab} index={1}>
          <Stack spacing={2}>
            <TextField
              select label="Établissement *" name="etablissementId"
              value={form.etablissementId} onChange={(e) => { set('etablissementId', e.target.value); set('classeId', '') }}
              fullWidth
            >
              {etablissements.map((e) => (
                <MenuItem key={e.id} value={String(e.id)}>{e.nom}</MenuItem>
              ))}
            </TextField>
            <TextField
              select label="Classe *" name="classeId"
              value={form.classeId} onChange={handleChange}
              fullWidth disabled={!form.etablissementId}
            >
              {classes.length === 0
                ? <MenuItem disabled value="">Aucune classe disponible</MenuItem>
                : classes.map((c) => (
                  <MenuItem key={c.id} value={String(c.id)}>{c.libelle} — {c.anneeScolaire}</MenuItem>
                ))
              }
            </TextField>
            <TextField
              select label="Statut d'accès" name="statutAcces"
              value={form.statutAcces} onChange={handleChange} fullWidth
            >
              {STATUTS.map((s) => <MenuItem key={s.value} value={s.value}>{s.label}</MenuItem>)}
            </TextField>
            <TextField
              select label="Régime alimentaire" name="regimeAlimentaire"
              value={form.regimeAlimentaire} onChange={handleChange} fullWidth
            >
              {REGIMES.map((r) => <MenuItem key={r.value} value={r.value}>{r.label}</MenuItem>)}
            </TextField>
            <FormControlLabel
              control={
                <Checkbox
                  checked={form.estBoursier}
                  onChange={(e) => set('estBoursier', e.target.checked)}
                />
              }
              label="Élève boursier"
            />
          </Stack>
        </TabPanel>

        {/* ── Onglet 2 : Contacts / Allergies ────────────── */}
        <TabPanel value={tab} index={2}>
          <Stack spacing={2}>
            <TextField label="Nom du parent/tuteur *" name="parentNom" value={form.parentNom} onChange={handleChange} fullWidth />
            <TextField label="Téléphone parent *" name="parentTelephone" value={form.parentTelephone} onChange={handleChange} fullWidth />
            <TextField label="Email parent" name="parentEmail" value={form.parentEmail} onChange={handleChange} fullWidth />

            <TextField
              label="Allergies" name="allergies" value={form.allergies} onChange={handleChange}
              fullWidth multiline rows={2}
              disabled={!isEdit}
              helperText={
                !isEdit
                  ? "Enregistrez d'abord la fiche, puis modifiez-la pour déclarer une allergie avec son certificat médical"
                  : "Toute allergie déclarée requiert un certificat médical d'un allergologue (ci-dessous)"
              }
            />

            {isEdit && (
              <Box>
                <Typography variant="caption" color="text.secondary" display="block" mb={0.5}>
                  Certificat médical (allergologue)
                </Typography>
                <Stack direction="row" alignItems="center" spacing={1.5} flexWrap="wrap">
                  {form.certificatMedicalUrl ? (
                    <Chip
                      icon={<CheckCircleIcon />}
                      color="success"
                      variant="outlined"
                      label={
                        <Link href={`${BACKEND_ORIGIN}${form.certificatMedicalUrl}`} target="_blank" rel="noopener" underline="hover" color="inherit">
                          Certificat fourni — consulter
                        </Link>
                      }
                    />
                  ) : (
                    <Typography variant="body2" color="text.disabled">Aucun certificat importé</Typography>
                  )}
                  <Button
                    size="small"
                    variant="outlined"
                    startIcon={uploading ? <CircularProgress size={14} /> : <UploadFileIcon />}
                    onClick={handleUploadClick}
                    disabled={uploading}
                  >
                    {form.certificatMedicalUrl ? 'Remplacer' : 'Importer'}
                  </Button>
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept=".pdf,.jpg,.jpeg,.png"
                    hidden
                    onChange={handleFileSelected}
                  />
                </Stack>
                {uploadError && <Alert severity="error" sx={{ mt: 1 }}>{uploadError}</Alert>}
              </Box>
            )}

            <TextField label="Notes médicales" name="notesMedicales" value={form.notesMedicales} onChange={handleChange} fullWidth multiline rows={2} />
          </Stack>
        </TabPanel>
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose}>Annuler</Button>
        <Button variant="contained" onClick={handleSubmit} disabled={saving}>
          {saving ? <CircularProgress size={20} /> : (isEdit ? 'Enregistrer' : 'Créer')}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

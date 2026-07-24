import { useState, useEffect } from 'react'
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Tabs, Tab, Box, Stack, TextField, Button,
  MenuItem, FormControlLabel, Checkbox, Alert, CircularProgress,
  useMediaQuery,
} from '@mui/material'
import { useTheme } from '@mui/material/styles'
import { useClasses } from '../../hooks/useClasses'

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
  allergies: '', notesMedicales: '',
}

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

  const { classes } = useClasses(form.etablissementId || null)

  useEffect(() => {
    if (open) {
      setTab(0)
      setFormError(null)
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
    return null
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
            <TextField label="Allergies" name="allergies" value={form.allergies} onChange={handleChange} fullWidth multiline rows={2} />
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

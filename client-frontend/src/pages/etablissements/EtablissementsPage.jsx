import { useState } from 'react'
import {
  Box, Typography, Button, Card, CardContent, Stack, Chip,
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, CircularProgress, Alert, IconButton, Tooltip,
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import SchoolIcon from '@mui/icons-material/School'
import LocationOnIcon from '@mui/icons-material/LocationOn'
import PhoneIcon from '@mui/icons-material/Phone'
import AccountTreeIcon from '@mui/icons-material/AccountTree'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import { useEtablissements } from '../../hooks/useEtablissements'
import { useAuth } from '../../hooks/useAuth'
import GestionStructureDialog from './GestionStructureDialog'

const FORM_INITIAL = { nom: '', adresse: '', ville: 'Abidjan', telephone: '' }

export default function EtablissementsPage() {
  const { etablissements, loading, error, creer, modifier, supprimer } = useEtablissements()
  const { user } = useAuth()
  const isAdmin = user?.role === 'ADMIN'

  // dialog création / modification
  const [open, setOpen] = useState(false)
  const [editTarget, setEditTarget] = useState(null) // null = création
  const [form, setForm] = useState(FORM_INITIAL)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState(null)

  // dialog suppression
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleting, setDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState(null)

  // gestion structure
  const [structureEtab, setStructureEtab] = useState(null)

  const handleChange = (e) =>
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }))

  const openCreate = () => {
    setEditTarget(null)
    setForm(FORM_INITIAL)
    setFormError(null)
    setOpen(true)
  }

  const openEdit = (etab) => {
    setEditTarget(etab)
    setForm({ nom: etab.nom, adresse: etab.adresse || '', ville: etab.ville || 'Abidjan', telephone: etab.telephone || '' })
    setFormError(null)
    setOpen(true)
  }

  const handleClose = () => {
    setOpen(false)
    setEditTarget(null)
    setForm(FORM_INITIAL)
    setFormError(null)
  }

  const handleSubmit = async () => {
    if (!form.nom.trim()) { setFormError('Le nom est obligatoire'); return }
    setSaving(true)
    setFormError(null)
    try {
      if (editTarget) {
        await modifier(editTarget.id, form)
      } else {
        await creer(form)
      }
      handleClose()
    } catch (e) {
      setFormError(e.message)
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    setDeleting(true)
    setDeleteError(null)
    try {
      await supprimer(deleteTarget.id)
      setDeleteTarget(null)
    } catch (e) {
      setDeleteError(e.message)
    } finally {
      setDeleting(false)
    }
  }

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5">Établissements</Typography>
        {isAdmin && (
          <Button variant="contained" startIcon={<AddIcon />} onClick={openCreate}>
            Ajouter
          </Button>
        )}
      </Stack>

      {loading && <CircularProgress />}
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
        {etablissements.map((etab) => (
          <Card
            key={etab.id}
            sx={{ flex: '1 1 280px', maxWidth: 360, transition: 'box-shadow .2s', '&:hover': { boxShadow: 6 } }}
          >
            <CardContent>
              <Stack direction="row" spacing={2} alignItems="flex-start">
                <SchoolIcon sx={{ color: 'primary.main', fontSize: 32, mt: 0.5 }} />
                <Box flexGrow={1}>
                  <Stack direction="row" justifyContent="space-between" alignItems="flex-start">
                    <Typography variant="subtitle1" fontWeight={700}>{etab.nom}</Typography>
                    {isAdmin && (
                      <Stack direction="row" spacing={0.5}>
                        <Tooltip title="Modifier">
                          <IconButton size="small" onClick={() => openEdit(etab)}>
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Supprimer">
                          <IconButton size="small" color="error" onClick={() => { setDeleteTarget(etab); setDeleteError(null) }}>
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </Stack>
                    )}
                  </Stack>
                  <Stack direction="row" alignItems="center" spacing={0.5} mt={0.5}>
                    <LocationOnIcon sx={{ fontSize: 14, color: 'text.secondary' }} />
                    <Typography variant="body2" color="text.secondary">{etab.ville}</Typography>
                  </Stack>
                  {etab.telephone && (
                    <Stack direction="row" alignItems="center" spacing={0.5}>
                      <PhoneIcon sx={{ fontSize: 14, color: 'text.secondary' }} />
                      <Typography variant="body2" color="text.secondary">{etab.telephone}</Typography>
                    </Stack>
                  )}
                  <Stack direction="row" spacing={1} mt={1}>
                    <Chip label="Actif" color="success" size="small" />
                  </Stack>
                  <Button
                    size="small" startIcon={<AccountTreeIcon />}
                    onClick={() => setStructureEtab(etab)}
                    sx={{ mt: 1 }}
                  >
                    Gérer les classes
                  </Button>
                </Box>
              </Stack>
            </CardContent>
          </Card>
        ))}

        {!loading && etablissements.length === 0 && (
          <Alert severity="info" sx={{ width: '100%' }}>
            Aucun établissement enregistré. Commencez par en ajouter un.
          </Alert>
        )}
      </Box>

      <GestionStructureDialog
        open={Boolean(structureEtab)}
        onClose={() => setStructureEtab(null)}
        etablissement={structureEtab}
      />

      {/* Dialog création / modification */}
      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>{editTarget ? 'Modifier l\'établissement' : 'Nouvel établissement'}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} mt={1}>
            {formError && <Alert severity="error">{formError}</Alert>}
            <TextField label="Nom *" name="nom" value={form.nom} onChange={handleChange} fullWidth autoFocus />
            <TextField label="Adresse" name="adresse" value={form.adresse} onChange={handleChange} fullWidth />
            <TextField label="Ville" name="ville" value={form.ville} onChange={handleChange} fullWidth />
            <TextField label="Téléphone" name="telephone" value={form.telephone} onChange={handleChange} fullWidth />
          </Stack>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={handleClose}>Annuler</Button>
          <Button variant="contained" onClick={handleSubmit} disabled={saving}>
            {saving ? <CircularProgress size={20} /> : editTarget ? 'Enregistrer' : 'Créer'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog confirmation suppression */}
      <Dialog open={Boolean(deleteTarget)} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Supprimer l'établissement ?</DialogTitle>
        <DialogContent>
          {deleteError && <Alert severity="error" sx={{ mb: 2 }}>{deleteError}</Alert>}
          <Typography>
            Voulez-vous vraiment supprimer <strong>{deleteTarget?.nom}</strong> ?
            Cette action est impossible tant que l'établissement a des niveaux, classes ou élèves associés.
          </Typography>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setDeleteTarget(null)}>Annuler</Button>
          <Button variant="contained" color="error" onClick={handleDelete} disabled={deleting}>
            {deleting ? <CircularProgress size={20} /> : 'Supprimer'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

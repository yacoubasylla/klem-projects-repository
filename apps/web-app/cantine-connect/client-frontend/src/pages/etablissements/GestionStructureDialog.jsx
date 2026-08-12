import { useState, useEffect, useCallback } from 'react'
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Box, Typography, Button, Stack, TextField,
  List, ListItem, ListItemText, Divider,
  CircularProgress, Alert, Chip, IconButton, Collapse, Tooltip,
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'
import ExpandLessIcon from '@mui/icons-material/ExpandLess'
import ClassIcon from '@mui/icons-material/Class'
import CheckCircleIcon from '@mui/icons-material/CheckCircle'
import DeleteIcon from '@mui/icons-material/Delete'
import EditIcon from '@mui/icons-material/Edit'
import CheckIcon from '@mui/icons-material/Check'
import CloseIcon from '@mui/icons-material/Close'
import { etablissementService } from '../../services/etablissementService'

// ── Ligne de classe avec édition inline ──────────────────────
function ClasseRow({ classe, onEdit, onDelete }) {
  const [editing, setEditing] = useState(false)
  const [libelle, setLibelle] = useState(classe.libelle)
  const [annee, setAnnee] = useState(classe.anneeScolaire)
  const [saving, setSaving] = useState(false)

  const handleSave = async () => {
    if (!libelle.trim()) return
    setSaving(true)
    try {
      await onEdit(classe.id, { libelle: libelle.trim(), anneeScolaire: annee })
      setEditing(false)
    } finally {
      setSaving(false)
    }
  }

  const handleCancel = () => {
    setLibelle(classe.libelle)
    setAnnee(classe.anneeScolaire)
    setEditing(false)
  }

  if (editing) {
    return (
      <ListItem sx={{ py: 0.5, pl: 0, gap: 1 }}>
        <ClassIcon sx={{ fontSize: 15, color: 'primary.light', flexShrink: 0 }} />
        <TextField
          size="small" value={libelle} autoFocus
          onChange={(e) => setLibelle(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter') handleSave(); if (e.key === 'Escape') handleCancel() }}
          sx={{ flex: 1 }}
        />
        <TextField
          size="small" value={annee}
          onChange={(e) => setAnnee(e.target.value)}
          sx={{ width: 120 }}
        />
        <Tooltip title="Enregistrer">
          <IconButton size="small" color="primary" onClick={handleSave} disabled={saving}>
            {saving ? <CircularProgress size={14} /> : <CheckIcon fontSize="small" />}
          </IconButton>
        </Tooltip>
        <Tooltip title="Annuler">
          <IconButton size="small" onClick={handleCancel}><CloseIcon fontSize="small" /></IconButton>
        </Tooltip>
      </ListItem>
    )
  }

  return (
    <ListItem
      sx={{ py: 0.2, pl: 0 }}
      secondaryAction={
        <Stack direction="row" spacing={0}>
          <Tooltip title="Modifier">
            <IconButton size="small" onClick={() => setEditing(true)}
              sx={{ opacity: 0.5, '&:hover': { opacity: 1 } }}>
              <EditIcon sx={{ fontSize: 15 }} />
            </IconButton>
          </Tooltip>
          <Tooltip title="Supprimer">
            <IconButton size="small" color="error" onClick={() => onDelete(classe)}
              sx={{ opacity: 0.5, '&:hover': { opacity: 1 } }}>
              <DeleteIcon sx={{ fontSize: 15 }} />
            </IconButton>
          </Tooltip>
        </Stack>
      }
    >
      <ClassIcon sx={{ fontSize: 15, mr: 1, color: 'primary.light' }} />
      <ListItemText
        primary={classe.libelle}
        secondary={classe.anneeScolaire}
        primaryTypographyProps={{ variant: 'body2', fontWeight: 500 }}
        secondaryTypographyProps={{ variant: 'caption' }}
      />
    </ListItem>
  )
}

// ── Bloc niveau ───────────────────────────────────────────────
function NiveauItem({ niveau, onAddClasses, onEditClasse, onEditNiveau, onDeleteNiveau, onDeleteClasse }) {
  const [open, setOpen] = useState(true)
  const [editingNiveau, setEditingNiveau] = useState(false)
  const [niveauLabel, setNiveauLabel] = useState(niveau.libelle)
  const [savingNiveau, setSavingNiveau] = useState(false)
  const [input, setInput] = useState('')
  const [annee, setAnnee] = useState('2025-2026')
  const [saving, setSaving] = useState(false)
  const [err, setErr] = useState(null)
  const [successCount, setSuccessCount] = useState(0)

  const handleSaveNiveau = async () => {
    if (!niveauLabel.trim()) return
    setSavingNiveau(true)
    try {
      await onEditNiveau(niveau.id, { libelle: niveauLabel.trim(), ordre: niveau.ordre })
      setEditingNiveau(false)
    } finally {
      setSavingNiveau(false)
    }
  }

  const handleCancelNiveau = () => {
    setNiveauLabel(niveau.libelle)
    setEditingNiveau(false)
  }

  const handleAdd = async () => {
    const noms = input.split(',').map((s) => s.trim()).filter(Boolean)
    if (noms.length === 0) { setErr('Saisissez au moins un libellé'); return }
    setSaving(true); setErr(null); setSuccessCount(0)
    try {
      await Promise.all(
        noms.map((libelle) => onAddClasses(niveau.id, { libelle, anneeScolaire: annee }))
      )
      setSuccessCount(noms.length)
      setInput('')
      setTimeout(() => setSuccessCount(0), 2500)
    } catch (e) {
      setErr(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <Box sx={{ mb: 1.5 }}>
      <Stack direction="row" alignItems="center" justifyContent="space-between"
        sx={{ bgcolor: 'primary.main', color: 'white', px: 2, py: 1, borderRadius: 1 }}
      >
        {editingNiveau ? (
          <Stack direction="row" spacing={0.5} alignItems="center" flexGrow={1} mr={1}>
            <TextField
              size="small" value={niveauLabel} autoFocus
              onChange={(e) => setNiveauLabel(e.target.value)}
              onKeyDown={(e) => { if (e.key === 'Enter') handleSaveNiveau(); if (e.key === 'Escape') handleCancelNiveau() }}
              sx={{ flex: 1, '& .MuiInputBase-input': { color: 'white', fontWeight: 600 }, '& .MuiOutlinedInput-notchedOutline': { borderColor: 'rgba(255,255,255,0.5)' } }}
            />
            <Tooltip title="Enregistrer">
              <IconButton size="small" sx={{ color: 'white' }} onClick={handleSaveNiveau} disabled={savingNiveau}>
                {savingNiveau ? <CircularProgress size={14} sx={{ color: 'white' }} /> : <CheckIcon fontSize="small" />}
              </IconButton>
            </Tooltip>
            <Tooltip title="Annuler">
              <IconButton size="small" sx={{ color: 'rgba(255,255,255,0.7)' }} onClick={handleCancelNiveau}>
                <CloseIcon fontSize="small" />
              </IconButton>
            </Tooltip>
          </Stack>
        ) : (
          <Typography fontWeight={600}>{niveau.libelle}</Typography>
        )}
        <Stack direction="row" spacing={0.5} alignItems="center">
          <Chip
            label={`${niveau.classes.length} classe(s)`} size="small"
            sx={{ bgcolor: 'rgba(255,255,255,0.2)', color: 'white' }}
          />
          {!editingNiveau && (
            <Tooltip title="Modifier le niveau">
              <IconButton size="small" sx={{ color: 'rgba(255,255,255,0.8)', '&:hover': { color: 'white' } }}
                onClick={() => setEditingNiveau(true)}>
                <EditIcon fontSize="small" />
              </IconButton>
            </Tooltip>
          )}
          <IconButton size="small" sx={{ color: 'white' }} onClick={() => setOpen(!open)}>
            {open ? <ExpandLessIcon /> : <ExpandMoreIcon />}
          </IconButton>
          <IconButton
            size="small" sx={{ color: 'rgba(255,255,255,0.8)', '&:hover': { color: '#ff8a80' } }}
            onClick={() => onDeleteNiveau(niveau)}
          >
            <DeleteIcon fontSize="small" />
          </IconButton>
        </Stack>
      </Stack>

      <Collapse in={open}>
        <Box sx={{ pl: 2, pr: 1, pt: 1, pb: 1.5, bgcolor: 'grey.50', borderRadius: '0 0 6px 6px' }}>
          {niveau.classes.length > 0 && (
            <List dense disablePadding sx={{ mb: 1 }}>
              {niveau.classes.map((c) => (
                <ClasseRow
                  key={c.id}
                  classe={c}
                  onEdit={onEditClasse}
                  onDelete={onDeleteClasse}
                />
              ))}
            </List>
          )}

          <Stack spacing={1}>
            <TextField
              size="small"
              label="Noms des classes *"
              placeholder="Ex : CP A, CP B, CP C"
              value={input}
              onChange={(e) => { setInput(e.target.value); setErr(null) }}
              onKeyDown={(e) => e.key === 'Enter' && handleAdd()}
              error={Boolean(err)}
              helperText={err || 'Séparez par des virgules pour créer plusieurs classes en une fois'}
              fullWidth
              autoComplete="off"
            />
            <Stack direction="row" spacing={1} alignItems="center">
              <TextField
                size="small" label="Année scolaire"
                value={annee} onChange={(e) => setAnnee(e.target.value)}
                sx={{ width: 140 }}
              />
              <Button
                variant="contained" size="small"
                startIcon={saving ? <CircularProgress size={14} color="inherit" /> : <AddIcon />}
                onClick={handleAdd} disabled={saving}
                sx={{ whiteSpace: 'nowrap' }}
              >
                {saving ? 'Création…' : 'Ajouter'}
              </Button>
              {successCount > 0 && (
                <Stack direction="row" alignItems="center" spacing={0.5} sx={{ color: 'success.main' }}>
                  <CheckCircleIcon fontSize="small" />
                  <Typography variant="caption" fontWeight={600}>
                    {successCount} classe{successCount > 1 ? 's' : ''} créée{successCount > 1 ? 's' : ''}
                  </Typography>
                </Stack>
              )}
            </Stack>
          </Stack>
        </Box>
      </Collapse>
      <Divider />
    </Box>
  )
}

// ── Dialog principal ──────────────────────────────────────────
export default function GestionStructureDialog({ open, onClose, etablissement }) {
  const [niveaux, setNiveaux] = useState([])
  const [loading, setLoading] = useState(false)
  const [newNiveau, setNewNiveau] = useState('')
  const [addingNiveau, setAddingNiveau] = useState(false)
  const [niveauErr, setNiveauErr] = useState(null)

  const chargerNiveaux = useCallback(async () => {
    if (!etablissement) return
    setLoading(true)
    try {
      setNiveaux(await etablissementService.getNiveaux(etablissement.id))
    } catch (e) {
      console.error(e)
    } finally {
      setLoading(false)
    }
  }, [etablissement])

  useEffect(() => { queueMicrotask(() => { if (open) chargerNiveaux() }) }, [open, chargerNiveaux])

  const handleAddNiveau = async () => {
    const noms = newNiveau.split(',').map((s) => s.trim()).filter(Boolean)
    if (noms.length === 0) { setNiveauErr('Le libellé est obligatoire'); return }
    setAddingNiveau(true); setNiveauErr(null)
    try {
      await Promise.all(
        noms.map((libelle, i) =>
          etablissementService.creerNiveau(etablissement.id, { libelle, ordre: niveaux.length + i })
        )
      )
      setNewNiveau('')
      await chargerNiveaux()
    } catch (e) {
      setNiveauErr(e.message)
    } finally {
      setAddingNiveau(false)
    }
  }

  const handleAddClasses = async (niveauId, dto) => {
    await etablissementService.creerClasse(niveauId, dto)
    await chargerNiveaux()
  }

  const handleEditNiveau = async (niveauId, dto) => {
    await etablissementService.modifierNiveau(niveauId, dto)
    await chargerNiveaux()
  }

  const handleEditClasse = async (classeId, dto) => {
    await etablissementService.modifierClasse(classeId, dto)
    await chargerNiveaux()
  }

  const handleDeleteNiveau = async (niveau) => {
    if (niveau.classes.length > 0) {
      alert(`Impossible de supprimer "${niveau.libelle}" : supprimez d'abord ses ${niveau.classes.length} classe(s).`)
      return
    }
    if (!window.confirm(`Supprimer le niveau "${niveau.libelle}" ?`)) return
    try {
      await etablissementService.supprimerNiveau(niveau.id)
      await chargerNiveaux()
    } catch (e) {
      alert(e.message)
    }
  }

  const handleDeleteClasse = async (classe) => {
    if (!window.confirm(`Supprimer la classe "${classe.libelle}" ?`)) return
    try {
      await etablissementService.supprimerClasse(classe.id)
      await chargerNiveaux()
    } catch (e) {
      alert(e.message)
    }
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Structure — {etablissement?.nom}</DialogTitle>

      <DialogContent>
        {loading ? (
          <Box textAlign="center" py={4}><CircularProgress /></Box>
        ) : (
          <Box>
            {niveaux.length === 0 && (
              <Alert severity="info" sx={{ mb: 2 }}>
                Commencez par ajouter un niveau (ex: CP, CE1, CM2…)
              </Alert>
            )}

            {niveaux.map((n) => (
              <NiveauItem
                key={n.id} niveau={n}
                onAddClasses={handleAddClasses}
                onEditNiveau={handleEditNiveau}
                onEditClasse={handleEditClasse}
                onDeleteNiveau={handleDeleteNiveau}
                onDeleteClasse={handleDeleteClasse}
              />
            ))}

            <Box sx={{ mt: 2, p: 2, bgcolor: 'primary.50', border: '1px dashed', borderColor: 'primary.light', borderRadius: 1 }}>
              <Typography variant="subtitle2" mb={1} color="primary.dark">
                Ajouter un ou plusieurs niveaux
              </Typography>
              <Stack direction="row" spacing={1} alignItems="flex-start">
                <TextField
                  size="small"
                  label="Niveau(x)"
                  placeholder="Ex : CP  ou  CP, CE1, CM1, CM2"
                  value={newNiveau}
                  onChange={(e) => { setNewNiveau(e.target.value); setNiveauErr(null) }}
                  onKeyDown={(e) => e.key === 'Enter' && handleAddNiveau()}
                  error={Boolean(niveauErr)}
                  helperText={niveauErr || 'Séparez par des virgules pour créer plusieurs niveaux'}
                  sx={{ flex: 1 }}
                  autoComplete="off"
                />
                <Button
                  variant="outlined" startIcon={<AddIcon />}
                  onClick={handleAddNiveau} disabled={addingNiveau}
                  sx={{ mt: 0.25, whiteSpace: 'nowrap' }}
                >
                  {addingNiveau ? <CircularProgress size={18} /> : 'Ajouter'}
                </Button>
              </Stack>
            </Box>
          </Box>
        )}
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button variant="contained" onClick={onClose}>Fermer</Button>
      </DialogActions>
    </Dialog>
  )
}

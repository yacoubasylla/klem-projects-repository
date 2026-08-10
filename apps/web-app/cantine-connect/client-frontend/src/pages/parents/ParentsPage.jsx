import { useState, useEffect } from 'react'
import {
  Box, Typography, Button, Stack, Alert, CircularProgress,
  Table, TableHead, TableBody, TableRow, TableCell, TableContainer,
  TablePagination, Paper, IconButton, Tooltip, Chip,
  Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions,
  TextField, Autocomplete,
} from '@mui/material'
import AddIcon            from '@mui/icons-material/Add'
import EditIcon           from '@mui/icons-material/Edit'
import DeleteIcon         from '@mui/icons-material/Delete'
import FamilyRestroomIcon from '@mui/icons-material/FamilyRestroom'
import { useParents }          from '../../hooks/useParents'
import { utilisateurService }  from '../../services/utilisateurService'
import { eleveService }        from '../../services/eleveService'
import SuccessSnackbar         from '../../components/SuccessSnackbar'

// ── helpers ───────────────────────────────────────────────────────────────────

const eleveLabel = (e) =>
  e?.matricule ? `${e.matricule} — ${e.prenom} ${e.nom}` : `${e?.prenom ?? ''} ${e?.nom ?? ''}`

// ── Dialog création / modification ───────────────────────────────────────────

function ParentFormDialog({ open, onClose, onSuccess, editTarget }) {
  const isEdit = Boolean(editTarget)

  // Compte parent (création uniquement)
  const [selectedParent, setSelectedParent] = useState(null)
  const [parentOptions, setParentOptions]   = useState([])
  const [parentLoading, setParentLoading]   = useState(false)
  const [parentInput, setParentInput]       = useState('')

  // Élèves associés
  const [selectedEleves, setSelectedEleves] = useState([])
  const [eleveOptions, setEleveOptions]     = useState([])
  const [eleveLoading, setEleveLoading]     = useState(false)
  const [eleveInput, setEleveInput]         = useState('')

  const [saving, setSaving] = useState(false)
  const [err, setErr]       = useState(null)

  // Recherche des comptes PARENT par numéro ou nom/prénom, avec debounce 300ms
  useEffect(() => {
    if (!open || isEdit) return
    const q = parentInput?.trim()
    let active = true
    const timer = setTimeout(() => {
      setParentLoading(true)
      utilisateurService.lister({ role: 'PARENT', actif: true, search: q || undefined, size: 20, sort: 'nom' })
        .then((r) => { if (active) setParentOptions(r?.content ?? []) })
        .catch(() => { if (active) setParentOptions([]) })
        .finally(() => { if (active) setParentLoading(false) })
    }, 300)
    return () => { active = false; clearTimeout(timer) }
  }, [open, isEdit, parentInput])

  // Remise à zéro à chaque ouverture
  useEffect(() => {
    if (open) {
      setSelectedParent(null)
      setSelectedEleves(editTarget ? (editTarget.enfants ?? []) : [])
      setParentInput('')
      setParentOptions([])
      setEleveInput('')
      setEleveOptions([])
      setErr(null)
    }
  }, [open, editTarget])

  // Recherche d'élèves avec debounce 300ms
  useEffect(() => {
    const q = eleveInput?.trim()
    if (!q) { setEleveOptions([]); return }
    let active = true
    const timer = setTimeout(() => {
      setEleveLoading(true)
      eleveService.lister({ search: q, size: 10 })
        .then((r) => { if (active) setEleveOptions(r?.content ?? []) })
        .catch(() => {})
        .finally(() => { if (active) setEleveLoading(false) })
    }, 300)
    return () => { active = false; clearTimeout(timer) }
  }, [eleveInput])

  const handleSubmit = async () => {
    if (!isEdit && !selectedParent) { setErr('Sélectionnez un compte parent'); return }
    setSaving(true); setErr(null)
    const ids = selectedEleves.map((e) => e.id)
    try {
      if (isEdit) {
        await onSuccess(editTarget.id, ids)
      } else {
        await onSuccess({ utilisateurId: selectedParent.id, eleveIds: ids })
      }
      onClose()
    } catch (e) {
      setErr(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Modifier les enfants associés' : 'Nouveau compte parent'}</DialogTitle>
      <DialogContent>
        <Stack spacing={2.5} mt={1}>
          {err && <Alert severity="error">{err}</Alert>}

          {/* Sélection du compte parent — création uniquement */}
          {!isEdit ? (
            <Autocomplete
              filterOptions={(x) => x}
              options={parentOptions}
              loading={parentLoading}
              loadingText="Recherche en cours…"
              noOptionsText={parentInput?.trim() ? 'Aucun compte PARENT trouvé' : 'Aucun compte avec le rôle PARENT. Créez-en un dans Utilisateurs d\'abord.'}
              value={selectedParent}
              onChange={(_, v) => setSelectedParent(v)}
              inputValue={parentInput}
              onInputChange={(_, v) => setParentInput(v)}
              getOptionLabel={(u) => `${u.prenom} ${u.nom} — ${u.telephone}`}
              isOptionEqualToValue={(a, b) => a.id === b.id}
              renderOption={(props, u) => (
                <li {...props} key={u.id}>
                  <Stack>
                    <Typography variant="body2" fontWeight={600}>
                      {u.prenom} {u.nom}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">{u.telephone} · {u.email}</Typography>
                  </Stack>
                </li>
              )}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Compte parent"
                  placeholder="Rechercher par numéro, nom ou prénom…"
                  helperText="Seuls les comptes avec le rôle PARENT sont listés ici."
                />
              )}
            />
          ) : (
            <Alert severity="info" sx={{ py: 0.5 }}>
              Modification des enfants de <strong>{editTarget.prenom} {editTarget.nom}</strong>
            </Alert>
          )}

          {/* Sélection des élèves */}
          <Autocomplete
            multiple
            filterOptions={(x) => x}
            options={eleveOptions}
            loading={eleveLoading}
            loadingText="Recherche en cours…"
            noOptionsText={eleveInput?.trim() ? 'Aucun élève trouvé' : 'Tapez un matricule ou un nom…'}
            value={selectedEleves}
            onChange={(_, v) => setSelectedEleves(v)}
            onInputChange={(_, v) => setEleveInput(v)}
            getOptionLabel={eleveLabel}
            isOptionEqualToValue={(a, b) => a.id === b.id}
            renderOption={(props, e) => (
              <li {...props} key={e.id}>
                <Stack direction="row" spacing={1} alignItems="center">
                  <Chip label={e.matricule} size="small" variant="outlined" sx={{ fontFamily: 'monospace', fontSize: 11 }} />
                  <Typography variant="body2">{e.prenom} {e.nom}</Typography>
                  {e.classeLibelle && (
                    <Typography variant="caption" color="text.secondary">· {e.classeLibelle}</Typography>
                  )}
                </Stack>
              </li>
            )}
            renderTags={(value, getTagProps) =>
              value.map((e, i) => (
                <Chip
                  key={e.id}
                  label={eleveLabel(e)}
                  size="small"
                  variant="filled"
                  color="primary"
                  {...getTagProps({ index: i })}
                />
              ))
            }
            renderInput={(params) => (
              <TextField
                {...params}
                label="Élèves associés"
                placeholder="Tapez un matricule ou un nom…"
                helperText="Recherchez et sélectionnez plusieurs élèves."
              />
            )}
          />
        </Stack>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} disabled={saving}>Annuler</Button>
        <Button variant="contained" onClick={handleSubmit} disabled={saving}>
          {saving ? <CircularProgress size={20} color="inherit" /> : (isEdit ? 'Enregistrer' : 'Créer')}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

// ── Page principale ───────────────────────────────────────────────────────────

export default function ParentsPage() {
  const [searchFiltre, setSearchFiltre] = useState('')
  const filtres = {}
  if (searchFiltre.trim()) filtres.search = searchFiltre.trim()

  const {
    parents, total, page, setPage, rowsPerPage, setRowsPerPage,
    loading, error, creer, modifierEnfants, supprimer,
  } = useParents(filtres)

  const [formOpen, setFormOpen]         = useState(false)
  const [editTarget, setEditTarget]     = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [successMsg, setSuccessMsg]     = useState('')

  const handleAdd  = () => { setEditTarget(null); setFormOpen(true) }
  const handleEdit = (parent) => { setEditTarget(parent); setFormOpen(true) }

  const handleSuccess = async (payloadOrId, eleveIds) => {
    if (editTarget) {
      await modifierEnfants(payloadOrId, eleveIds)
      setSuccessMsg('Enfants associés mis à jour avec succès')
    } else {
      await creer(payloadOrId)
      setSuccessMsg('Compte parent créé avec succès')
    }
  }

  const handleConfirmDelete = async () => {
    try {
      await supprimer(deleteTarget.id)
    } catch (e) {
      alert(e.message)
    } finally {
      setDeleteTarget(null)
    }
  }

  return (
    <Box>
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ xs: 'stretch', sm: 'center' }} mb={3} gap={1}>
        <Stack direction="row" spacing={1.5} alignItems="center">
          <FamilyRestroomIcon color="primary" />
          <Typography variant="h5" fontWeight={600}>Comptes Parents</Typography>
        </Stack>
        <Button variant="contained" startIcon={<AddIcon />} onClick={handleAdd} sx={{ flex: { xs: 1, sm: '0 0 auto' } }}>
          Ajouter
        </Button>
      </Stack>

      {/* ── Filtre ──────────────────────────────────────── */}
      <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
        <TextField
          size="small"
          label="Rechercher"
          placeholder="Nom, prénom, téléphone ou email…"
          value={searchFiltre}
          onChange={(e) => { setSearchFiltre(e.target.value); setPage(0) }}
          sx={{ width: { xs: '100%', sm: 320 } }}
        />
      </Paper>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {/* Colonnes secondaires (#/Email) masquées sous sm — repliées en sous-titre
          dans la cellule Parent pour éviter le défilement horizontal. */}
      <TableContainer component={Paper} sx={{ boxShadow: 1 }}>
        <Table size="small" sx={{ '& .MuiTableCell-root': { px: { xs: 0.75, sm: 2 } } }}>
          <TableHead>
            <TableRow>
              <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>#</TableCell>
              <TableCell>Parent</TableCell>
              <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Email</TableCell>
              <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Téléphone (WhatsApp)</TableCell>
              <TableCell>Enfants inscrits</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                  <CircularProgress size={28} />
                </TableCell>
              </TableRow>
            ) : parents.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                  Aucun compte parent enregistré
                </TableCell>
              </TableRow>
            ) : (
              parents.map((p) => (
                <TableRow key={p.id} hover>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' }, fontFamily: 'monospace', fontSize: 12 }}>{p.id}</TableCell>
                  <TableCell>
                    <Typography variant="body2" fontWeight={600}>{p.prenom} {p.nom}</Typography>
                    <Typography variant="caption" color="text.secondary" sx={{ display: { xs: 'block', sm: 'none' }, wordBreak: 'break-word' }}>
                      #{p.id} · {p.email} · {p.telephone}
                    </Typography>
                  </TableCell>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                    <Typography variant="body2" color="text.secondary">{p.email}</Typography>
                  </TableCell>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                    <Typography variant="body2" color="text.secondary">{p.telephone}</Typography>
                  </TableCell>
                  <TableCell>
                    <Stack direction="row" spacing={0.5} flexWrap="wrap" gap={0.5}>
                      {(p.enfants ?? []).length === 0
                        ? <Typography variant="caption" color="text.disabled">Aucun</Typography>
                        : (p.enfants ?? []).map((e) => (
                            <Chip
                              key={e.id}
                              label={eleveLabel(e)}
                              size="small"
                              variant="outlined"
                              sx={{ fontFamily: 'monospace', fontSize: '0.7rem' }}
                            />
                          ))
                      }
                    </Stack>
                  </TableCell>
                  <TableCell align="center" sx={{ whiteSpace: 'nowrap' }}>
                    <Tooltip title="Modifier les enfants">
                      <IconButton size="small" color="primary" onClick={() => handleEdit(p)}>
                        <EditIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Supprimer">
                      <IconButton size="small" color="error" onClick={() => setDeleteTarget(p)}>
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
        <TablePagination
          component="div"
          count={total}
          page={page}
          rowsPerPage={rowsPerPage}
          onPageChange={(_, p) => setPage(p)}
          onRowsPerPageChange={(e) => { setRowsPerPage(parseInt(e.target.value)); setPage(0) }}
          rowsPerPageOptions={[10, 25, 50]}
          labelRowsPerPage="Lignes par page"
          labelDisplayedRows={({ from, to, count }) => `${from}–${to} sur ${count}`}
        />
      </TableContainer>

      <ParentFormDialog
        open={formOpen}
        onClose={() => setFormOpen(false)}
        onSuccess={handleSuccess}
        editTarget={editTarget}
      />

      {/* Dialog suppression */}
      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Confirmer la suppression</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Supprimer le compte parent de <strong>{deleteTarget?.prenom} {deleteTarget?.nom}</strong> ?
            Les élèves associés ne seront pas supprimés.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteTarget(null)}>Annuler</Button>
          <Button onClick={handleConfirmDelete} color="error" variant="contained">Supprimer</Button>
        </DialogActions>
      </Dialog>

      <SuccessSnackbar open={Boolean(successMsg)} message={successMsg} onClose={() => setSuccessMsg('')} />
    </Box>
  )
}

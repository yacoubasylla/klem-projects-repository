import { useState } from 'react'
import {
  Box, Typography, Button, Stack, Alert, Chip, Tooltip, IconButton,
  Table, TableHead, TableBody, TableRow, TableCell, TableContainer,
  TablePagination, Paper, Skeleton, Menu,
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Select, MenuItem, FormControl, InputLabel,
  CircularProgress, Divider,
} from '@mui/material'
import AddIcon           from '@mui/icons-material/Add'
import EditIcon          from '@mui/icons-material/Edit'
import DeleteForeverIcon from '@mui/icons-material/DeleteForever'
import RefreshIcon       from '@mui/icons-material/Refresh'
import PersonOffIcon     from '@mui/icons-material/PersonOff'
import PersonAddIcon     from '@mui/icons-material/PersonAdd'
import MoreVertIcon      from '@mui/icons-material/MoreVert'
import { useUtilisateurs } from '../../hooks/useUtilisateurs'
import { useAuth }          from '../../hooks/useAuth'
import SuccessSnackbar      from '../../components/SuccessSnackbar'

// ── Constantes ────────────────────────────────────────────────────────────────

const ROLES = ['ADMIN', 'GESTIONNAIRE', 'CAISSIER', 'PARENT']

const ROLE_CONFIG = {
  ADMIN:        { label: 'Administrateur', color: 'error'   },
  GESTIONNAIRE: { label: 'Gestionnaire',   color: 'primary' },
  CAISSIER:     { label: 'Caissier',       color: 'default' },
  PARENT:       { label: 'Parent',        color: 'secondary' },
}

const formatDate = (dt) =>
  dt ? new Date(dt).toLocaleDateString('fr-FR') : '—'

// ── Dialog : créer ────────────────────────────────────────────────────────────

function CreerDialog({ open, onClose, onSubmit }) {
  const INIT = { nom: '', prenom: '', email: '', telephone: '', motDePasse: '', role: 'GESTIONNAIRE' }
  const [form,       setForm]  = useState(INIT)
  const [submitting, setSub]   = useState(false)
  const [err,        setErr]   = useState(null)

  const handleClose = () => { setForm(INIT); setErr(null); onClose() }
  const field = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }))

  const handleSubmit = async () => {
    if (!form.nom || !form.prenom || !form.email || !form.telephone || !form.motDePasse) {
      setErr('Tous les champs sont obligatoires'); return
    }
    if (form.motDePasse.length < 8) {
      setErr('Mot de passe : 8 caractères minimum'); return
    }
    setSub(true); setErr(null)
    try { await onSubmit(form); handleClose() }
    catch (e) { setErr(e.response?.data?.message ?? e.message) }
    finally   { setSub(false) }
  }

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>Créer un compte utilisateur</DialogTitle>
      <DialogContent>
        <Stack spacing={2.5} mt={1}>
          <Stack direction="row" spacing={2}>
            <TextField label="Nom *"    size="small" fullWidth value={form.nom}    onChange={field('nom')} />
            <TextField label="Prénom *" size="small" fullWidth value={form.prenom} onChange={field('prenom')} />
          </Stack>
          <TextField label="Email *" type="email" size="small" fullWidth value={form.email} onChange={field('email')} />
          <TextField
            label="Numéro de cellulaire *" size="small" fullWidth
            value={form.telephone} onChange={field('telephone')}
            placeholder="07XXXXXXXX"
            helperText="Utilisé pour les notifications SMS (parents)"
          />
          <TextField
            label="Mot de passe *" type="password" size="small" fullWidth
            value={form.motDePasse} onChange={field('motDePasse')}
            helperText="Minimum 8 caractères"
          />
          <FormControl size="small" fullWidth>
            <InputLabel>Rôle *</InputLabel>
            <Select value={form.role} label="Rôle *" onChange={field('role')}>
              {ROLES.map((r) => (
                <MenuItem key={r} value={r}>
                  <Chip label={ROLE_CONFIG[r].label} color={ROLE_CONFIG[r].color} size="small" sx={{ cursor: 'pointer' }} />
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          {err && <Alert severity="error">{err}</Alert>}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} disabled={submitting}>Annuler</Button>
        <Button variant="contained" onClick={handleSubmit} disabled={submitting}
          startIcon={submitting ? <CircularProgress size={16} /> : null}>
          Créer le compte
        </Button>
      </DialogActions>
    </Dialog>
  )
}

// ── Dialog : modifier ─────────────────────────────────────────────────────────

function ModifierDialog({ utilisateur, onClose, onSubmit }) {
  const [form,       setForm]  = useState({
    nom:              utilisateur?.nom    ?? '',
    prenom:           utilisateur?.prenom ?? '',
    email:            utilisateur?.email  ?? '',
    telephone:        utilisateur?.telephone ?? '',
    nouveauMotDePasse: '',
  })
  const [submitting, setSub]  = useState(false)
  const [err,        setErr]  = useState(null)

  const field = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }))

  const handleSubmit = async () => {
    if (!form.nom || !form.prenom || !form.email || !form.telephone) {
      setErr('Nom, prénom, email et téléphone sont obligatoires'); return
    }
    if (form.nouveauMotDePasse && form.nouveauMotDePasse.length < 8) {
      setErr('Nouveau mot de passe : 8 caractères minimum'); return
    }
    setSub(true); setErr(null)
    try {
      await onSubmit(utilisateur.id, {
        nom:               form.nom,
        prenom:            form.prenom,
        email:             form.email,
        telephone:         form.telephone,
        nouveauMotDePasse: form.nouveauMotDePasse || null,
      })
      onClose()
    } catch (e) {
      setErr(e.response?.data?.message ?? e.message)
    } finally {
      setSub(false)
    }
  }

  return (
    <Dialog open={!!utilisateur} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Modifier le compte — {utilisateur?.prenom} {utilisateur?.nom}</DialogTitle>
      <DialogContent>
        <Stack spacing={2.5} mt={1}>
          <Stack direction="row" spacing={2}>
            <TextField label="Nom *"    size="small" fullWidth value={form.nom}    onChange={field('nom')} />
            <TextField label="Prénom *" size="small" fullWidth value={form.prenom} onChange={field('prenom')} />
          </Stack>
          <TextField label="Email *" type="email" size="small" fullWidth value={form.email} onChange={field('email')} />
          <TextField label="Numéro de cellulaire *" size="small" fullWidth value={form.telephone} onChange={field('telephone')} />
          <Divider />
          <TextField
            label="Nouveau mot de passe"
            type="password"
            size="small"
            fullWidth
            value={form.nouveauMotDePasse}
            onChange={field('nouveauMotDePasse')}
            helperText="Laisser vide pour conserver le mot de passe actuel"
          />
          {err && <Alert severity="error">{err}</Alert>}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Annuler</Button>
        <Button variant="contained" onClick={handleSubmit} disabled={submitting}
          startIcon={submitting ? <CircularProgress size={16} /> : <EditIcon />}>
          Enregistrer
        </Button>
      </DialogActions>
    </Dialog>
  )
}

// ── Dialog : confirmer suppression définitive ─────────────────────────────────

function ConfirmSupprimerDialog({ utilisateur, onClose, onConfirm }) {
  const [submitting, setSub] = useState(false)
  const [err,        setErr] = useState(null)

  const handleConfirm = async () => {
    setSub(true); setErr(null)
    try { await onConfirm(utilisateur.id); onClose() }
    catch (e) { setErr(e.response?.data?.message ?? e.message) }
    finally   { setSub(false) }
  }

  return (
    <Dialog open={!!utilisateur} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ color: 'error.main' }}>Supprimer définitivement</DialogTitle>
      <DialogContent>
        <Typography variant="body2" mb={1}>
          Vous allez supprimer <strong>{utilisateur?.prenom} {utilisateur?.nom}</strong> ({utilisateur?.email}).
        </Typography>
        <Alert severity="warning" sx={{ mt: 1 }}>
          Cette action est irréversible — toutes les données de ce compte seront perdues.
        </Alert>
        {err && <Alert severity="error" sx={{ mt: 1 }}>{err}</Alert>}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Annuler</Button>
        <Button
          variant="contained"
          color="error"
          onClick={handleConfirm}
          disabled={submitting}
          startIcon={submitting ? <CircularProgress size={16} color="inherit" /> : <DeleteForeverIcon />}
        >
          Supprimer définitivement
        </Button>
      </DialogActions>
    </Dialog>
  )
}

// ── Page principale ───────────────────────────────────────────────────────────

const STATUTS = [
  { value: '',      label: 'Tous'    },
  { value: 'true',  label: 'Actif'   },
  { value: 'false', label: 'Inactif' },
]

export default function UtilisateursPage() {
  const [creerOpen,     setCreerOpen]     = useState(false)
  const [userToEdit,    setUserToEdit]    = useState(null)
  const [userToDelete,  setUserToDelete]  = useState(null)
  const [actionError,   setActionError]   = useState(null)
  const [successMsg,    setSuccessMsg]    = useState('')
  const [menuAnchor,    setMenuAnchor]    = useState(null)
  const [menuUser,      setMenuUser]      = useState(null)
  const { user: currentUser } = useAuth()

  const [searchFiltre, setSearchFiltre] = useState('')
  const [roleFiltre,   setRoleFiltre]   = useState('')
  const [statutFiltre, setStatutFiltre] = useState('')
  const [dateDebut,    setDateDebut]    = useState('')
  const [dateFin,      setDateFin]      = useState('')

  const openMenu  = (e, u) => { setMenuAnchor(e.currentTarget); setMenuUser(u) }
  const closeMenu = () => { setMenuAnchor(null); setMenuUser(null) }

  const filtres = {}
  if (searchFiltre.trim()) filtres.search = searchFiltre.trim()
  if (roleFiltre) filtres.role = roleFiltre
  if (statutFiltre) filtres.actif = statutFiltre
  if (dateDebut) filtres.dateDebut = dateDebut
  if (dateFin) filtres.dateFin = dateFin

  const {
    utilisateurs, total, page, setPage, rowsPerPage, setRowsPerPage,
    loading, error, creer, modifier, changerRole, desactiver, reactiver, supprimer, recharger,
  } = useUtilisateurs(filtres)

  const handleCreerSuccess = async (form) => {
    await creer(form)
    setSuccessMsg('Compte utilisateur créé avec succès')
  }

  const handleModifierSuccess = async (id, dto) => {
    await modifier(id, dto)
    setSuccessMsg('Compte utilisateur modifié avec succès')
  }

  const handleDesactiver = async (u) => {
    if (!window.confirm(`Désactiver le compte de ${u.prenom} ${u.nom} ?`)) return
    try { setActionError(null); await desactiver(u.id) }
    catch (e) { setActionError(e.response?.data?.message ?? e.message) }
  }

  const handleReactiver = async (u) => {
    try { setActionError(null); await reactiver(u.id) }
    catch (e) { setActionError(e.response?.data?.message ?? e.message) }
  }

  const handleChangerRole = async (id, role) => {
    try { setActionError(null); await changerRole(id, role) }
    catch (e) { setActionError(e.response?.data?.message ?? e.message) }
  }

  return (
    <Box>
      {/* ── En-tête ─────────────────────────────────────── */}
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between" mb={2} gap={1}>
        <Box>
          <Typography variant="h5" fontWeight={600}>Gestion des Utilisateurs</Typography>
          <Typography variant="caption" color="text.secondary">Accès réservé aux Administrateurs</Typography>
        </Box>
        <Stack direction="row" spacing={1} flexWrap="wrap">
          <Tooltip title="Actualiser">
            <IconButton onClick={recharger} disabled={loading} size="small"><RefreshIcon /></IconButton>
          </Tooltip>
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setCreerOpen(true)} sx={{ flex: { xs: 1, sm: '0 0 auto' } }}>
            Nouveau compte
          </Button>
        </Stack>
      </Stack>

      {/* ── Filtres ─────────────────────────────────────── */}
      <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} flexWrap="wrap" gap={1.5} alignItems={{ xs: 'stretch', sm: 'center' }}>
          <TextField
            size="small"
            label="Recherche"
            placeholder="Email, nom, prénom, téléphone…"
            value={searchFiltre}
            onChange={(e) => { setSearchFiltre(e.target.value); setPage(0) }}
            sx={{ minWidth: { xs: '100%', sm: 220 }, flex: 1 }}
          />

          <TextField
            select
            label="Rôle"
            size="small"
            value={roleFiltre}
            onChange={(e) => { setRoleFiltre(e.target.value); setPage(0) }}
            sx={{ minWidth: { xs: '100%', sm: 170 } }}
          >
            <MenuItem value="">Tous les rôles</MenuItem>
            {ROLES.map((r) => (
              <MenuItem key={r} value={r}>{ROLE_CONFIG[r].label}</MenuItem>
            ))}
          </TextField>

          <TextField
            select
            label="Statut"
            size="small"
            value={statutFiltre}
            onChange={(e) => { setStatutFiltre(e.target.value); setPage(0) }}
            sx={{ minWidth: { xs: '100%', sm: 140 } }}
          >
            {STATUTS.map((s) => (
              <MenuItem key={s.value} value={s.value}>{s.label}</MenuItem>
            ))}
          </TextField>

          <TextField
            label="Créé depuis"
            type="date"
            size="small"
            value={dateDebut}
            onChange={(e) => { setDateDebut(e.target.value); setPage(0) }}
            slotProps={{ inputLabel: { shrink: true } }}
            sx={{ minWidth: { xs: '100%', sm: 160 } }}
          />

          <TextField
            label="Créé jusqu'au"
            type="date"
            size="small"
            value={dateFin}
            onChange={(e) => { setDateFin(e.target.value); setPage(0) }}
            slotProps={{ inputLabel: { shrink: true } }}
            sx={{ minWidth: { xs: '100%', sm: 160 } }}
          />
        </Stack>
      </Paper>

      {(error || actionError) && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setActionError(null)}>
          {error || actionError}
        </Alert>
      )}

      {/* ── Table ───────────────────────────────────────── */}
      {/* Colonnes secondaires (Email/Téléphone/Créé le) masquées sous sm — repliées
          en sous-titre dans la cellule Nom/Prénom pour éviter le défilement horizontal. */}
      <Paper variant="outlined">
        <TableContainer>
          <Table size="small" sx={{ '& .MuiTableCell-root': { px: { xs: 0.75, sm: 2 } } }}>
            <TableHead>
              <TableRow sx={{ bgcolor: 'action.hover' }}>
                <TableCell>Nom / Prénom</TableCell>
                <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Email</TableCell>
                <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Téléphone</TableCell>
                <TableCell>Rôle</TableCell>
                <TableCell align="center" sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Statut</TableCell>
                <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Créé le</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading
                ? Array.from({ length: 4 }).map((_, i) => (
                    <TableRow key={i}>
                      {Array.from({ length: 7 }).map((__, j) => (
                        <TableCell key={j}><Skeleton /></TableCell>
                      ))}
                    </TableRow>
                  ))
                : utilisateurs.length === 0
                  ? (
                    <TableRow>
                      <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                        Aucun utilisateur
                      </TableCell>
                    </TableRow>
                  )
                  : utilisateurs.map((u) => {
                    const isSelf = u.id === currentUser?.id
                    return (
                      <TableRow key={u.id} hover sx={{ opacity: u.actif ? 1 : 0.55 }}>

                        {/* Nom / Prénom */}
                        <TableCell>
                          <Typography variant="body2" fontWeight={600}>{u.nom} {u.prenom}</Typography>
                          {isSelf && <Typography variant="caption" color="primary.main">(vous)</Typography>}
                          <Typography variant="caption" color="text.secondary" sx={{ display: { xs: 'block', sm: 'none' }, wordBreak: 'break-word' }}>
                            {u.email} · {u.telephone} · {u.actif ? 'Actif' : 'Inactif'}
                          </Typography>
                        </TableCell>

                        {/* Email */}
                        <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                          <Typography variant="body2" sx={{ fontFamily: 'monospace', fontSize: 12 }}>
                            {u.email}
                          </Typography>
                        </TableCell>

                        {/* Téléphone */}
                        <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>
                          <Typography variant="body2" sx={{ fontFamily: 'monospace', fontSize: 12 }}>
                            {u.telephone}
                          </Typography>
                        </TableCell>

                        {/* Rôle inline */}
                        <TableCell sx={{ minWidth: { xs: 90, sm: 160 } }}>
                          <Select
                            value={u.role}
                            size="small"
                            variant="standard"
                            disabled={isSelf || !u.actif}
                            onChange={(e) => handleChangerRole(u.id, e.target.value)}
                            renderValue={(val) => (
                              <Chip
                                label={ROLE_CONFIG[val]?.label ?? val}
                                color={ROLE_CONFIG[val]?.color ?? 'default'}
                                size="small"
                                sx={{
                                  maxWidth: { xs: 84, sm: 'none' },
                                  '& .MuiChip-label': { overflow: 'hidden', textOverflow: 'ellipsis' },
                                }}
                              />
                            )}
                            sx={{ '& .MuiSelect-select': { py: 0 } }}
                          >
                            {ROLES.map((r) => (
                              <MenuItem key={r} value={r}>
                                <Chip label={ROLE_CONFIG[r].label} color={ROLE_CONFIG[r].color} size="small" sx={{ cursor: 'pointer' }} />
                              </MenuItem>
                            ))}
                          </Select>
                        </TableCell>

                        {/* Statut */}
                        <TableCell align="center" sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                          <Chip
                            label={u.actif ? 'Actif' : 'Inactif'}
                            color={u.actif ? 'success' : 'default'}
                            variant={u.actif ? 'filled' : 'outlined'}
                            size="small"
                          />
                        </TableCell>

                        {/* Date création */}
                        <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                          <Typography variant="caption" color="text.secondary">{formatDate(u.createdAt)}</Typography>
                        </TableCell>

                        {/* Actions */}
                        <TableCell align="center" sx={{ whiteSpace: 'nowrap' }}>
                          <Stack direction="row" justifyContent="center" spacing={0.5}>

                            {/* Modifier — toujours visible */}
                            <Tooltip title="Modifier les informations">
                              <IconButton size="small" color="primary" onClick={() => setUserToEdit(u)}>
                                <EditIcon fontSize="small" />
                              </IconButton>
                            </Tooltip>

                            {/* Désactiver/Réactiver + Supprimer — repliés en menu sous sm pour éviter le débordement */}
                            <Box sx={{ display: { xs: 'none', sm: 'flex' } }}>
                              {u.actif ? (
                                <Tooltip title={isSelf ? 'Impossible de se désactiver soi-même' : 'Désactiver'}>
                                  <span>
                                    <IconButton size="small" color="warning" disabled={isSelf} onClick={() => handleDesactiver(u)}>
                                      <PersonOffIcon fontSize="small" />
                                    </IconButton>
                                  </span>
                                </Tooltip>
                              ) : (
                                <Tooltip title="Réactiver">
                                  <IconButton size="small" color="success" onClick={() => handleReactiver(u)}>
                                    <PersonAddIcon fontSize="small" />
                                  </IconButton>
                                </Tooltip>
                              )}
                              <Tooltip title={isSelf ? 'Impossible de se supprimer soi-même' : 'Supprimer définitivement'}>
                                <span>
                                  <IconButton size="small" color="error" disabled={isSelf} onClick={() => setUserToDelete(u)}>
                                    <DeleteForeverIcon fontSize="small" />
                                  </IconButton>
                                </span>
                              </Tooltip>
                            </Box>

                            <Tooltip title="Plus d'actions">
                              <IconButton size="small" onClick={(e) => openMenu(e, u)} sx={{ display: { xs: 'inline-flex', sm: 'none' } }}>
                                <MoreVertIcon fontSize="small" />
                              </IconButton>
                            </Tooltip>

                          </Stack>
                        </TableCell>
                      </TableRow>
                    )
                  })
              }
            </TableBody>
          </Table>
        </TableContainer>

        <TablePagination
          component="div"
          count={total}
          page={page}
          rowsPerPage={rowsPerPage}
          onPageChange={(_, v) => setPage(v)}
          onRowsPerPageChange={(e) => { setRowsPerPage(+e.target.value); setPage(0) }}
          rowsPerPageOptions={[10, 20, 50]}
          labelRowsPerPage="Lignes :"
          labelDisplayedRows={({ from, to, count }) => `${from}–${to} sur ${count}`}
        />
      </Paper>

      {/* ── Dialogs ─────────────────────────────────────── */}
      <CreerDialog
        open={creerOpen}
        onClose={() => setCreerOpen(false)}
        onSubmit={handleCreerSuccess}
      />

      {userToEdit && (
        <ModifierDialog
          utilisateur={userToEdit}
          onClose={() => setUserToEdit(null)}
          onSubmit={handleModifierSuccess}
        />
      )}

      {userToDelete && (
        <ConfirmSupprimerDialog
          utilisateur={userToDelete}
          onClose={() => setUserToDelete(null)}
          onConfirm={supprimer}
        />
      )}

      {/* Menu compact (mobile) — Désactiver/Réactiver + Supprimer */}
      <Menu anchorEl={menuAnchor} open={Boolean(menuAnchor)} onClose={closeMenu}>
        {menuUser?.actif ? (
          <MenuItem
            disabled={menuUser?.id === currentUser?.id}
            onClick={() => { handleDesactiver(menuUser); closeMenu() }}
          >
            <PersonOffIcon fontSize="small" sx={{ mr: 1 }} /> Désactiver
          </MenuItem>
        ) : (
          <MenuItem onClick={() => { handleReactiver(menuUser); closeMenu() }}>
            <PersonAddIcon fontSize="small" sx={{ mr: 1 }} /> Réactiver
          </MenuItem>
        )}
        <MenuItem
          disabled={menuUser?.id === currentUser?.id}
          sx={{ color: 'error.main' }}
          onClick={() => { setUserToDelete(menuUser); closeMenu() }}
        >
          <DeleteForeverIcon fontSize="small" sx={{ mr: 1 }} /> Supprimer définitivement
        </MenuItem>
      </Menu>

      <SuccessSnackbar open={Boolean(successMsg)} message={successMsg} onClose={() => setSuccessMsg('')} />
    </Box>
  )
}

import { useState, useEffect } from 'react'
import {
  Box, Typography, Button, Stack, Chip, Alert,
  Table, TableHead, TableBody, TableRow, TableCell, TableContainer,
  TablePagination, Paper, Skeleton, Tooltip, IconButton,
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Select, MenuItem, FormControl, InputLabel,
  Autocomplete, CircularProgress, Link,
  useMediaQuery,
} from '@mui/material'
import { useTheme } from '@mui/material/styles'
import AddIcon          from '@mui/icons-material/Add'
import RefreshIcon      from '@mui/icons-material/Refresh'
import OpenInNewIcon    from '@mui/icons-material/OpenInNew'
import EditIcon         from '@mui/icons-material/Edit'
import DeleteIcon       from '@mui/icons-material/Delete'
import PaymentsIcon     from '@mui/icons-material/Payments'
import FileDownloadIcon from '@mui/icons-material/FileDownload'
import { usePaiements }     from '../../hooks/usePaiements'
import { eleveService }     from '../../services/eleveService'
import { parentService }    from '../../services/parentService'
import { useAuth }          from '../../hooks/useAuth'

// ── Constantes ────────────────────────────────────────────────────────────────

const STATUTS = [
  { value: '',            label: 'Tous'         },
  { value: 'EN_ATTENTE',  label: 'En attente'   },
  { value: 'ACCEPTE',     label: 'Accepté'      },
  { value: 'REFUSE',      label: 'Refusé'       },
  { value: 'ANNULE',      label: 'Annulé'       },
]

const STATUT_COLOR = {
  EN_ATTENTE: 'warning',
  ACCEPTE:    'success',
  REFUSE:     'error',
  ANNULE:     'default',
}

const OPERATEURS = [
  { value: 'ORANGE_MONEY', label: 'Orange Money', color: '#FF6600' },
  { value: 'MTN_MONEY',    label: 'MTN Money',    color: '#FFCC00' },
  { value: 'MOOV_MONEY',   label: 'Moov Money',   color: '#0066CC' },
  { value: 'WAVE',         label: 'Wave',         color: '#0099CC' },
]

const operateurLabel = (val) => OPERATEURS.find((o) => o.value === val)?.label ?? val
const operateurColor = (val) => OPERATEURS.find((o) => o.value === val)?.color ?? '#888'

const formatMontant = (val) =>
  new Intl.NumberFormat('fr-FR', { style: 'decimal' }).format(val) + ' XOF'

const formatDate = (dt) =>
  dt ? new Date(dt).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' }) : '—'

// ── Export CSV ────────────────────────────────────────────────────────────────

function exportCsv(paiements) {
  const header = ['Date', 'Élève', 'Opérateur', 'Montant', 'Téléphone', 'Statut', 'Référence']
  const rows = paiements.map((p) => [
    formatDate(p.dateCreation),
    p.eleveNomComplet ?? '',
    operateurLabel(p.operateur),
    p.montant,
    p.telephonePayeur ?? '',
    STATUTS.find((s) => s.value === p.statut)?.label ?? p.statut,
    p.referenceInterne ?? '',
  ])
  const csv = [header, ...rows].map((r) => r.map((c) => `"${c}"`).join(';')).join('\n')
  const blob = new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `paiements_${new Date().toISOString().split('T')[0]}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

// ── Dialogue : Initier un paiement ────────────────────────────────────────────

function InitierDialog({ open, onClose, onSubmit, isParent }) {
  const theme = useTheme()
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'))
  const FORM_INIT = { eleve: null, operateur: '', montant: '', telephonePayeur: '' }
  const [form,    setForm]    = useState(FORM_INIT)
  const [submitting, setSub]  = useState(false)
  const [formError, setFErr]  = useState(null)
  const [paymentUrl, setUrl]  = useState(null)

  // Autocomplete élève
  const [inputEleve, setInputEleve]   = useState('')
  const [optEleves, setOptEleves]     = useState([])
  const [loadingEleves, setLdEleves]  = useState(false)

  useEffect(() => {
    queueMicrotask(() => {
      if (!open) { setForm(FORM_INIT); setFErr(null); setUrl(null); setInputEleve(''); setOptEleves([]); return }
      if (isParent) {
        setLdEleves(true)
        parentService.getMoi()
          .then((moi) => setOptEleves(
            (moi?.enfants ?? []).map((e) => ({
              id: e.id,
              matricule: e.matricule,
              nomComplet: `${e.prenom} ${e.nom}`,
              classeLibelle: e.classeLibelle ?? '—',
              label: `${e.matricule} — ${e.prenom} ${e.nom}`,
            }))
          ))
          .catch(() => setOptEleves([]))
          .finally(() => setLdEleves(false))
      }
    })
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, isParent])

  useEffect(() => {
    if (isParent) return
    if (inputEleve.length < 2) {
      queueMicrotask(() => setOptEleves([]))
      return
    }
    const timer = setTimeout(async () => {
      setLdEleves(true)
      try {
        const res = await eleveService.lister({ search: inputEleve, size: 10 })
        setOptEleves(
          res.content.map((e) => ({
            id: e.id,
            matricule: e.matricule,
            nomComplet: `${e.prenom} ${e.nom}`,
            classeLibelle: e.classeLibelle ?? e.etablissementNom ?? '—',
            label: `${e.matricule} — ${e.prenom} ${e.nom}`,
          }))
        )
      } catch { setOptEleves([]) }
      finally  { setLdEleves(false) }
    }, 300)
    return () => clearTimeout(timer)
  }, [inputEleve, isParent])

  const field = (k) => (v) => setForm((f) => ({ ...f, [k]: v }))

  const handleSubmit = async () => {
    if (!form.eleve)              { setFErr('Sélectionne un élève'); return }
    if (!form.operateur)          { setFErr('Choisis un opérateur'); return }
    if (!form.montant || Number(form.montant) < 100) { setFErr('Montant minimum : 100 XOF'); return }
    if (!form.telephonePayeur)    { setFErr('Numéro de téléphone requis'); return }

    setSub(true); setFErr(null)
    try {
      const result = await onSubmit({
        eleveId:          form.eleve.id,
        operateur:        form.operateur,
        montant:          Number(form.montant),
        telephonePayeur:  form.telephonePayeur,
      })
      setUrl(result.paymentUrl)
    } catch (e) {
      setFErr(e.message)
    } finally {
      setSub(false)
    }
  }

  return (
    <Dialog open={open} onClose={paymentUrl ? onClose : undefined} maxWidth="sm" fullWidth fullScreen={fullScreen}>
      <DialogTitle>Initier un paiement Mobile Money</DialogTitle>
      <DialogContent>
        <Stack spacing={2.5} mt={1}>

          {paymentUrl ? (
            <Alert severity="success"
              action={
                <IconButton size="small" component="a" href={paymentUrl} target="_blank" rel="noopener">
                  <OpenInNewIcon fontSize="small" />
                </IconButton>
              }>
              Paiement initié avec succès !
              <br />
              <Link href={paymentUrl} target="_blank" rel="noopener" variant="body2">
                Ouvrir la page de paiement CinetPay
              </Link>
            </Alert>
          ) : (
            <>
              <Autocomplete
                options={optEleves}
                loading={loadingEleves}
                loadingText="Chargement..."
                value={form.eleve}
                onChange={(_, v) => field('eleve')(v)}
                inputValue={inputEleve}
                onInputChange={(_, v) => setInputEleve(v)}
                isOptionEqualToValue={(o, v) => o.id === v?.id}
                noOptionsText={
                  isParent
                    ? 'Aucun enfant associé à votre compte'
                    : (inputEleve.length < 2 ? 'Saisir au moins 2 caractères' : 'Aucun résultat')
                }
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label={isParent ? 'Votre enfant *' : 'Élève (nom ou matricule) *'}
                    size="small"
                    slotProps={{
                      ...params.slotProps,
                      input: {
                        ...params.slotProps?.input,
                        endAdornment: (
                          <>
                            {loadingEleves && <CircularProgress size={16} />}
                            {params.slotProps?.input?.endAdornment}
                          </>
                        ),
                      },
                    }}
                  />
                )}
              />

              {form.eleve && (
                <Paper variant="outlined" sx={{ p: 1.5, bgcolor: 'action.hover' }}>
                  <Stack direction="row" spacing={3} flexWrap="wrap">
                    <Box>
                      <Typography variant="caption" color="text.secondary" display="block">Matricule</Typography>
                      <Typography variant="body2" fontWeight={600}>{form.eleve.matricule ?? '—'}</Typography>
                    </Box>
                    <Box>
                      <Typography variant="caption" color="text.secondary" display="block">Nom et prénom</Typography>
                      <Typography variant="body2" fontWeight={600}>{form.eleve.nomComplet ?? '—'}</Typography>
                    </Box>
                    <Box>
                      <Typography variant="caption" color="text.secondary" display="block">Classe</Typography>
                      <Typography variant="body2" fontWeight={600}>{form.eleve.classeLibelle ?? '—'}</Typography>
                    </Box>
                  </Stack>
                </Paper>
              )}

              <FormControl size="small" fullWidth>
                <InputLabel>Opérateur *</InputLabel>
                <Select
                  value={form.operateur}
                  label="Opérateur *"
                  onChange={(e) => field('operateur')(e.target.value)}
                >
                  {OPERATEURS.map((op) => (
                    <MenuItem key={op.value} value={op.value}>
                      <Stack direction="row" alignItems="center" spacing={1}>
                        <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: op.color }} />
                        <span>{op.label}</span>
                      </Stack>
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              <TextField
                label="Montant (XOF) *"
                type="number"
                size="small"
                value={form.montant}
                onChange={(e) => field('montant')(e.target.value)}
                slotProps={{ htmlInput: { min: 100, step: 50 } }}
              />

              <TextField
                label="Téléphone du payeur *"
                size="small"
                placeholder="07XXXXXXXX"
                value={form.telephonePayeur}
                onChange={(e) => field('telephonePayeur')(e.target.value)}
              />
            </>
          )}

          {formError && <Alert severity="error">{formError}</Alert>}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>
          {paymentUrl ? 'Fermer' : 'Annuler'}
        </Button>
        {!paymentUrl && (
          <Button
            variant="contained"
            onClick={handleSubmit}
            disabled={submitting}
            startIcon={submitting ? <CircularProgress size={16} /> : null}
          >
            Initier le paiement
          </Button>
        )}
      </DialogActions>
    </Dialog>
  )
}

// ── Dialog : Modifier un paiement ────────────────────────────────────────────

function ModifierDialog({ paiement, onClose, onSubmit }) {
  const [form, setForm] = useState({})
  const [saving, setSaving] = useState(false)
  const [err, setErr] = useState(null)

  useEffect(() => {
    queueMicrotask(() => {
      if (paiement) setForm({
        statut:          paiement.statut,
        montant:         paiement.montant,
        operateur:       paiement.operateur,
        telephonePayeur: paiement.telephonePayeur ?? '',
      })
    })
  }, [paiement])

  const handleSave = async () => {
    setSaving(true); setErr(null)
    try {
      await onSubmit(paiement.id, form)
      onClose()
    } catch (e) {
      setErr(e.message)
    } finally {
      setSaving(false)
    }
  }

  if (!paiement) return null
  return (
    <Dialog open={!!paiement} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Modifier le paiement</DialogTitle>
      <DialogContent>
        <Stack spacing={2} mt={1}>
          {err && <Alert severity="error">{err}</Alert>}
          <FormControl size="small" fullWidth>
            <InputLabel>Statut</InputLabel>
            <Select value={form.statut ?? ''} label="Statut"
              onChange={(e) => setForm((f) => ({ ...f, statut: e.target.value }))}>
              {['EN_ATTENTE','ACCEPTE','REFUSE','ANNULE'].map((s) => (
                <MenuItem key={s} value={s}>{STATUTS.find((x) => x.value === s)?.label ?? s}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <FormControl size="small" fullWidth>
            <InputLabel>Opérateur</InputLabel>
            <Select value={form.operateur ?? ''} label="Opérateur"
              onChange={(e) => setForm((f) => ({ ...f, operateur: e.target.value }))}>
              {OPERATEURS.map((op) => (
                <MenuItem key={op.value} value={op.value}>
                  <Stack direction="row" alignItems="center" spacing={1}>
                    <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: op.color }} />
                    <span>{op.label}</span>
                  </Stack>
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField size="small" fullWidth label="Montant (XOF)" type="number"
            value={form.montant ?? ''}
            onChange={(e) => setForm((f) => ({ ...f, montant: e.target.value }))} />
          <TextField size="small" fullWidth label="Téléphone payeur"
            value={form.telephonePayeur ?? ''}
            onChange={(e) => setForm((f) => ({ ...f, telephonePayeur: e.target.value }))} />
        </Stack>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose}>Annuler</Button>
        <Button variant="contained" onClick={handleSave} disabled={saving}>
          {saving ? <CircularProgress size={20} /> : 'Enregistrer'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

// ── Page principale ───────────────────────────────────────────────────────────

export default function PaiementsPage() {
  const [statutFiltre,    setStatutFiltre]    = useState('')
  const [operateurFiltre, setOperateurFiltre] = useState('')
  const [dateDebut,       setDateDebut]       = useState('')
  const [dateFin,         setDateFin]         = useState('')
  const [searchFiltre,    setSearchFiltre]    = useState('')
  const [dialogOpen,   setDialogOpen]   = useState(false)
  const [editTarget,   setEditTarget]   = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleting,     setDeleting]     = useState(false)

  const { user } = useAuth()
  const isAdmin  = user?.role === 'ADMIN'
  const isParent = user?.role === 'PARENT'

  const filtres = {}
  if (statutFiltre) filtres.statut = statutFiltre
  if (operateurFiltre) filtres.operateur = operateurFiltre
  if (dateDebut) filtres.dateDebut = dateDebut
  if (dateFin) filtres.dateFin = dateFin
  if (searchFiltre.trim()) filtres.search = searchFiltre.trim()

  const {
    paiements, total, page, setPage, rowsPerPage, setRowsPerPage,
    loading, error, initier, modifier, supprimer, recharger,
  } = usePaiements(filtres)

  const handleDelete = async () => {
    setDeleting(true)
    try { await supprimer(deleteTarget.id) } catch { /* swallow */ }
    finally { setDeleting(false); setDeleteTarget(null) }
  }

  return (
    <Box>
      {/* ── En-tête ─────────────────────────────────────── */}
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between" mb={2} gap={1}>
        <Stack direction="row" alignItems="center" spacing={1.5}>
          <PaymentsIcon color="primary" />
          <Typography variant="h5" fontWeight={600}>Paiements Mobile Money</Typography>
        </Stack>
        <Stack direction="row" spacing={1} flexWrap="wrap">
          <Tooltip title="Exporter CSV (page courante)">
            <span>
              <Button
                size="small"
                variant="outlined"
                startIcon={<FileDownloadIcon />}
                disabled={paiements.length === 0}
                onClick={() => exportCsv(paiements)}
              >
                CSV
              </Button>
            </span>
          </Tooltip>
          <Tooltip title="Actualiser">
            <IconButton onClick={recharger} disabled={loading} size="small">
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setDialogOpen(true)} sx={{ flex: { xs: 1, sm: '0 0 auto' } }}>
            Initier un paiement
          </Button>
        </Stack>
      </Stack>

      {/* ── Filtres ─────────────────────────────────────── */}
      <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} flexWrap="wrap" gap={1.5} alignItems={{ xs: 'stretch', sm: 'center' }}>
          <TextField
            size="small"
            label="Recherche élève"
            placeholder="Nom, prénom, matricule…"
            value={searchFiltre}
            onChange={(e) => { setSearchFiltre(e.target.value); setPage(0) }}
            sx={{ minWidth: { xs: '100%', sm: 220 }, flex: 1 }}
          />

          <TextField
            label="Date début"
            type="date"
            size="small"
            value={dateDebut}
            onChange={(e) => { setDateDebut(e.target.value); setPage(0) }}
            slotProps={{ inputLabel: { shrink: true } }}
            sx={{ minWidth: { xs: '100%', sm: 160 } }}
          />

          <TextField
            label="Date fin"
            type="date"
            size="small"
            value={dateFin}
            onChange={(e) => { setDateFin(e.target.value); setPage(0) }}
            slotProps={{ inputLabel: { shrink: true } }}
            sx={{ minWidth: { xs: '100%', sm: 160 } }}
          />

          <TextField
            select
            label="Opérateur"
            size="small"
            value={operateurFiltre}
            onChange={(e) => { setOperateurFiltre(e.target.value); setPage(0) }}
            sx={{ minWidth: { xs: '100%', sm: 170 } }}
          >
            <MenuItem value="">Tous les opérateurs</MenuItem>
            {OPERATEURS.map((op) => (
              <MenuItem key={op.value} value={op.value}>
                <Stack direction="row" alignItems="center" spacing={1}>
                  <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: op.color }} />
                  <span>{op.label}</span>
                </Stack>
              </MenuItem>
            ))}
          </TextField>

          <Stack direction="row" spacing={1} flexWrap="wrap">
            {STATUTS.map((s) => (
              <Chip
                key={s.value}
                label={s.label}
                onClick={() => { setStatutFiltre(s.value); setPage(0) }}
                color={statutFiltre === s.value ? (STATUT_COLOR[s.value] || 'primary') : 'default'}
                variant={statutFiltre === s.value ? 'filled' : 'outlined'}
                size="small"
              />
            ))}
          </Stack>
        </Stack>
      </Paper>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {/* ── Table ───────────────────────────────────────── */}
      <Paper variant="outlined">
        <TableContainer>
          <Table size="small" sx={{ '& .MuiTableCell-root': { px: { xs: 0.75, sm: 2 } } }}>
            <TableHead>
              <TableRow sx={{ bgcolor: 'action.hover' }}>
                <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Date</TableCell>
                <TableCell>Élève</TableCell>
                <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Opérateur</TableCell>
                <TableCell align="right">Montant</TableCell>
                <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Téléphone</TableCell>
                <TableCell>Statut</TableCell>
                <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Référence</TableCell>
                {isAdmin && <TableCell align="center">Actions</TableCell>}
              </TableRow>
            </TableHead>
            <TableBody>
              {loading
                ? Array.from({ length: 5 }).map((_, i) => (
                    <TableRow key={i}>
                      {Array.from({ length: 7 }).map((__, j) => (
                        <TableCell key={j}><Skeleton /></TableCell>
                      ))}
                    </TableRow>
                  ))
                : paiements.length === 0
                  ? (
                    <TableRow>
                      <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                        Aucune transaction
                        {statutFiltre && ` avec le statut "${STATUTS.find(s => s.value === statutFiltre)?.label}"`}
                      </TableCell>
                    </TableRow>
                  )
                  : paiements.map((p) => (
                    <TableRow key={p.id} hover>
                      <TableCell sx={{ whiteSpace: 'nowrap', display: { xs: 'none', sm: 'table-cell' } }}>{formatDate(p.dateCreation)}</TableCell>
                      <TableCell>
                        {p.eleveNomComplet}
                        <Typography variant="caption" color="text.secondary" sx={{ display: { xs: 'block', sm: 'none' } }}>
                          {formatDate(p.dateCreation)} · {operateurLabel(p.operateur)}
                        </Typography>
                      </TableCell>
                      <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>
                        <Stack direction="row" alignItems="center" spacing={0.75}>
                          <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: operateurColor(p.operateur), flexShrink: 0 }} />
                          <Typography variant="caption">{operateurLabel(p.operateur)}</Typography>
                        </Stack>
                      </TableCell>
                      <TableCell align="right" sx={{ fontWeight: 600, whiteSpace: 'nowrap' }}>
                        {formatMontant(p.montant)}
                      </TableCell>
                      <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>{p.telephonePayeur ?? '—'}</TableCell>
                      <TableCell>
                        <Chip
                          label={STATUTS.find((s) => s.value === p.statut)?.label ?? p.statut}
                          color={STATUT_COLOR[p.statut] ?? 'default'}
                          size="small"
                          variant="outlined"
                        />
                      </TableCell>
                      <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                        <Typography
                          variant="caption"
                          fontFamily="monospace"
                          title={p.referenceInterne}
                        >
                          {p.referenceInterne?.slice(0, 8)}…
                        </Typography>
                      </TableCell>
                      {isAdmin && (
                        <TableCell align="center" sx={{ whiteSpace: 'nowrap' }}>
                          <Tooltip title="Modifier">
                            <IconButton size="small" onClick={() => setEditTarget(p)}>
                              <EditIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Supprimer">
                            <IconButton size="small" color="error" onClick={() => setDeleteTarget(p)}>
                              <DeleteIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        </TableCell>
                      )}
                    </TableRow>
                  ))
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

      {/* ── Dialogue initier ────────────────────────────── */}
      <InitierDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        onSubmit={initier}
        isParent={isParent}
      />

      {/* ── Dialogue modifier ───────────────────────────── */}
      <ModifierDialog
        paiement={editTarget}
        onClose={() => setEditTarget(null)}
        onSubmit={modifier}
      />

      {/* ── Dialogue supprimer ──────────────────────────── */}
      <Dialog open={Boolean(deleteTarget)} onClose={() => setDeleteTarget(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Supprimer ce paiement ?</DialogTitle>
        <DialogContent>
          <Typography>
            Voulez-vous supprimer définitivement le paiement de&nbsp;
            <strong>{deleteTarget ? formatMontant(deleteTarget.montant) : ''}</strong> pour&nbsp;
            <strong>{deleteTarget?.eleveNomComplet}</strong> ?
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

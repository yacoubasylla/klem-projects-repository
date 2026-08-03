import { useState, useEffect, useCallback } from 'react'
import {
  Box, Typography, Button, Stack, Alert, Chip, Tooltip, IconButton,
  Table, TableHead, TableBody, TableRow, TableCell, TableContainer,
  TablePagination, Paper, Skeleton,
  Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem,
  CircularProgress,
} from '@mui/material'
import RefreshIcon      from '@mui/icons-material/Refresh'
import CheckCircleIcon  from '@mui/icons-material/CheckCircle'
import CancelIcon       from '@mui/icons-material/Cancel'
import ContentCopyIcon  from '@mui/icons-material/ContentCopy'
import { demandeAccesService } from '../../services/demandeAccesService'
import SuccessSnackbar from '../../components/SuccessSnackbar'

const STATUT_CONFIG = {
  EN_ATTENTE: { label: 'En attente', color: 'warning' },
  VALIDEE:    { label: 'Validée',    color: 'success' },
  REJETEE:    { label: 'Rejetée',    color: 'default' },
}

const formatDate = (dt) => dt ? new Date(dt).toLocaleString('fr-FR') : '—'

function RejeterDialog({ demande, onClose, onConfirm }) {
  const [motif, setMotif]         = useState('')
  const [submitting, setSub]      = useState(false)
  const [err, setErr]             = useState(null)

  const handleConfirm = async () => {
    if (!motif.trim()) { setErr('Le motif est obligatoire'); return }
    setSub(true); setErr(null)
    try { await onConfirm(demande.id, motif.trim()); onClose() }
    catch (e) { setErr(e.message) }
    finally { setSub(false) }
  }

  return (
    <Dialog open={Boolean(demande)} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Rejeter la demande</DialogTitle>
      <DialogContent>
        <Typography variant="body2" mb={2}>
          {demande?.prenom} {demande?.nom} — {demande?.telephonePrincipal}
        </Typography>
        <TextField
          label="Motif du rejet *" fullWidth multiline rows={3}
          value={motif} onChange={(e) => setMotif(e.target.value)} autoFocus
        />
        {err && <Alert severity="error" sx={{ mt: 2 }}>{err}</Alert>}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Annuler</Button>
        <Button variant="contained" color="error" onClick={handleConfirm} disabled={submitting}
          startIcon={submitting ? <CircularProgress size={16} color="inherit" /> : <CancelIcon />}>
          Rejeter
        </Button>
      </DialogActions>
    </Dialog>
  )
}

function IdentifiantsDialog({ resultat, onClose }) {
  const copier = () => {
    navigator.clipboard?.writeText(
      `Identifiant : ${resultat.identifiantGenere}\nMot de passe temporaire : ${resultat.motDePasseTemporaire}`
    )
  }
  return (
    <Dialog open={Boolean(resultat)} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>Compte créé</DialogTitle>
      <DialogContent>
        <Alert severity="success" sx={{ mb: 2 }}>
          Le compte parent a été créé. Communiquez ces identifiants si les notifications
          automatiques (email/SMS) ne sont pas activées.
        </Alert>
        <Stack spacing={1.5}>
          <TextField label="Identifiant de connexion" value={resultat?.identifiantGenere ?? ''} fullWidth InputProps={{ readOnly: true }} />
          <TextField label="Mot de passe temporaire" value={resultat?.motDePasseTemporaire ?? ''} fullWidth InputProps={{ readOnly: true }} />
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={copier} startIcon={<ContentCopyIcon />}>Copier</Button>
        <Button variant="contained" onClick={onClose}>Fermer</Button>
      </DialogActions>
    </Dialog>
  )
}

export default function DemandesAccesPage() {
  const [demandes, setDemandes]     = useState([])
  const [total, setTotal]           = useState(0)
  const [page, setPage]             = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(20)
  const [statutFiltre, setStatutFiltre] = useState('EN_ATTENTE')
  const [loading, setLoading]       = useState(true)
  const [error, setError]           = useState(null)
  const [actionError, setActionError] = useState(null)
  const [demandeARejeter, setDemandeARejeter] = useState(null)
  const [resultatValidation, setResultatValidation] = useState(null)
  const [successMsg, setSuccessMsg] = useState('')
  const [validatingId, setValidatingId] = useState(null)

  const charger = useCallback(async () => {
    setLoading(true); setError(null)
    try {
      const params = { page, size: rowsPerPage }
      if (statutFiltre) params.statut = statutFiltre
      const data = await demandeAccesService.lister(params)
      setDemandes(data.content)
      setTotal(data.totalElements)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [page, rowsPerPage, statutFiltre])

  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => { charger() }, [charger])

  const handleValider = async (demande) => {
    setValidatingId(demande.id); setActionError(null)
    try {
      const resultat = await demandeAccesService.valider(demande.id)
      setResultatValidation(resultat)
      setSuccessMsg('Demande validée — compte parent créé')
      charger()
    } catch (e) {
      setActionError(e.message)
    } finally {
      setValidatingId(null)
    }
  }

  const handleRejeter = async (id, motif) => {
    await demandeAccesService.rejeter(id, motif)
    setSuccessMsg('Demande rejetée')
    charger()
  }

  return (
    <Box>
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between" mb={2} gap={1}>
        <Box>
          <Typography variant="h5" fontWeight={600}>Demandes d'accès parent</Typography>
          <Typography variant="caption" color="text.secondary">Validez ou rejetez les demandes soumises depuis la page publique</Typography>
        </Box>
        <Stack direction="row" spacing={1}>
          <TextField
            select size="small" label="Statut" value={statutFiltre}
            onChange={(e) => { setStatutFiltre(e.target.value); setPage(0) }}
            sx={{ minWidth: 160 }}
          >
            <MenuItem value="">Toutes</MenuItem>
            <MenuItem value="EN_ATTENTE">En attente</MenuItem>
            <MenuItem value="VALIDEE">Validées</MenuItem>
            <MenuItem value="REJETEE">Rejetées</MenuItem>
          </TextField>
          <Tooltip title="Actualiser">
            <IconButton onClick={charger} disabled={loading} size="small"><RefreshIcon /></IconButton>
          </Tooltip>
        </Stack>
      </Stack>

      {(error || actionError) && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setActionError(null)}>{error || actionError}</Alert>
      )}

      <Paper variant="outlined">
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow sx={{ bgcolor: 'action.hover' }}>
                <TableCell>Demandeur</TableCell>
                <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Contact</TableCell>
                <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Résidence</TableCell>
                <TableCell>Statut</TableCell>
                <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Soumise le</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading
                ? Array.from({ length: 4 }).map((_, i) => (
                    <TableRow key={i}>
                      {Array.from({ length: 6 }).map((__, j) => <TableCell key={j}><Skeleton /></TableCell>)}
                    </TableRow>
                  ))
                : demandes.length === 0
                  ? (
                    <TableRow><TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>Aucune demande</TableCell></TableRow>
                  )
                  : demandes.map((d) => (
                    <TableRow key={d.id} hover>
                      <TableCell>
                        <Typography variant="body2" fontWeight={600}>{d.prenom} {d.nom}</Typography>
                        {d.fonction && <Typography variant="caption" color="text.secondary">{d.fonction}</Typography>}
                      </TableCell>
                      <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                        <Typography variant="body2">{d.telephonePrincipal}</Typography>
                        {d.email && <Typography variant="caption" color="text.secondary">{d.email}</Typography>}
                      </TableCell>
                      <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>
                        <Typography variant="body2">{d.commune}, {d.ville}</Typography>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={STATUT_CONFIG[d.statut]?.label ?? d.statut}
                          color={STATUT_CONFIG[d.statut]?.color ?? 'default'}
                          size="small"
                        />
                      </TableCell>
                      <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                        <Typography variant="caption" color="text.secondary">{formatDate(d.dateSoumission)}</Typography>
                      </TableCell>
                      <TableCell align="center" sx={{ whiteSpace: 'nowrap' }}>
                        {d.statut === 'EN_ATTENTE' && (
                          <Stack direction="row" justifyContent="center" spacing={0.5}>
                            <Tooltip title="Valider — créer le compte parent">
                              <span>
                                <IconButton size="small" color="success" disabled={validatingId === d.id} onClick={() => handleValider(d)}>
                                  {validatingId === d.id ? <CircularProgress size={18} /> : <CheckCircleIcon fontSize="small" />}
                                </IconButton>
                              </span>
                            </Tooltip>
                            <Tooltip title="Rejeter">
                              <IconButton size="small" color="error" onClick={() => setDemandeARejeter(d)}>
                                <CancelIcon fontSize="small" />
                              </IconButton>
                            </Tooltip>
                          </Stack>
                        )}
                        {d.statut === 'REJETEE' && d.motifRejet && (
                          <Tooltip title={d.motifRejet}>
                            <Typography variant="caption" color="text.secondary">Motif ⓘ</Typography>
                          </Tooltip>
                        )}
                      </TableCell>
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

      {demandeARejeter && (
        <RejeterDialog demande={demandeARejeter} onClose={() => setDemandeARejeter(null)} onConfirm={handleRejeter} />
      )}
      {resultatValidation && (
        <IdentifiantsDialog resultat={resultatValidation} onClose={() => setResultatValidation(null)} />
      )}

      <SuccessSnackbar open={Boolean(successMsg)} message={successMsg} onClose={() => setSuccessMsg('')} />
    </Box>
  )
}

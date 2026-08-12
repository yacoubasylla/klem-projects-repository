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
import VisibilityIcon   from '@mui/icons-material/Visibility'
import { demandeAccesService } from '../../services/demandeAccesService'
import { SuccessSnackbar } from '@klem/ui'

const STATUT_CONFIG = {
  EN_ATTENTE: { label: 'En attente', color: 'warning' },
  VALIDEE:    { label: 'Validée',    color: 'success' },
  REJETEE:    { label: 'Rejetée',    color: 'default' },
}

const formatDate = (dt) => dt ? new Date(dt).toLocaleString('fr-FR') : '—'

function ChampDetail({ label, value }) {
  return (
    <Box>
      <Typography variant="caption" color="text.secondary">{label}</Typography>
      <Typography variant="body2">{value || '—'}</Typography>
    </Box>
  )
}

function DetailsDialog({ demande, onClose, onValider, onRejeter, validatingId }) {
  if (!demande) return null
  return (
    <Dialog open onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Détails de la demande</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ mt: 0.5 }}>
          <Stack direction="row" alignItems="center" justifyContent="space-between">
            <Typography variant="h6" fontWeight={600}>{demande.prenom} {demande.nom}</Typography>
            <Chip
              label={STATUT_CONFIG[demande.statut]?.label ?? demande.statut}
              color={STATUT_CONFIG[demande.statut]?.color ?? 'default'}
              size="small"
            />
          </Stack>

          <ChampDetail label="Fonction" value={demande.fonction} />

          <Box>
            <Typography variant="subtitle2" fontWeight={600} mb={1}>Contact</Typography>
            <Stack spacing={1}>
              <ChampDetail label="Téléphone principal" value={demande.telephonePrincipal} />
              <ChampDetail
                label="WhatsApp"
                value={demande.telephoneWhatsapp || `${demande.telephonePrincipal} (identique au principal)`}
              />
              <ChampDetail label="Second téléphone" value={demande.telephoneSecondaire} />
              <ChampDetail label="Email" value={demande.email} />
            </Stack>
          </Box>

          <Box>
            <Typography variant="subtitle2" fontWeight={600} mb={1}>Résidence</Typography>
            <ChampDetail
              label="Ville / Commune / Quartier"
              value={[demande.ville, demande.commune, demande.quartier].filter(Boolean).join(', ')}
            />
          </Box>

          <Box>
            <Typography variant="subtitle2" fontWeight={600} mb={1}>Suivi</Typography>
            <Stack spacing={1}>
              <ChampDetail label="Soumise le" value={formatDate(demande.dateSoumission)} />
              {demande.dateTraitement && <ChampDetail label="Traitée le" value={formatDate(demande.dateTraitement)} />}
            </Stack>
            {demande.motifRejet && <Alert severity="warning" sx={{ mt: 1.5 }}>Motif du rejet : {demande.motifRejet}</Alert>}
          </Box>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Fermer</Button>
        {demande.statut === 'EN_ATTENTE' && (
          <>
            <Button color="error" variant="outlined" onClick={() => onRejeter(demande)} startIcon={<CancelIcon />}>
              Rejeter
            </Button>
            <Button
              color="success" variant="contained" onClick={() => onValider(demande)}
              disabled={validatingId === demande.id}
              startIcon={validatingId === demande.id ? <CircularProgress size={16} color="inherit" /> : <CheckCircleIcon />}
            >
              Valider
            </Button>
          </>
        )}
      </DialogActions>
    </Dialog>
  )
}

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
  const [searchInput, setSearchInput]   = useState('')
  const [searchFiltre, setSearchFiltre] = useState('')
  const [loading, setLoading]       = useState(true)
  const [error, setError]           = useState(null)
  const [actionError, setActionError] = useState(null)
  const [demandeARejeter, setDemandeARejeter] = useState(null)
  const [resultatValidation, setResultatValidation] = useState(null)
  const [successMsg, setSuccessMsg] = useState('')
  const [validatingId, setValidatingId] = useState(null)
  const [demandeDetails, setDemandeDetails] = useState(null)

  const charger = useCallback(async () => {
    setLoading(true); setError(null)
    try {
      const params = { page, size: rowsPerPage }
      if (statutFiltre) params.statut = statutFiltre
      if (searchFiltre) params.search = searchFiltre
      const data = await demandeAccesService.lister(params)
      setDemandes(data.content)
      setTotal(data.totalElements)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [page, rowsPerPage, statutFiltre, searchFiltre])

  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => { charger() }, [charger])

  // Recherche débouncée (300ms) : évite un appel réseau à chaque frappe
  useEffect(() => {
    const timer = setTimeout(() => {
      setSearchFiltre(searchInput.trim())
      setPage(0)
    }, 300)
    return () => clearTimeout(timer)
  }, [searchInput])

  const handleValider = async (demande) => {
    setValidatingId(demande.id); setActionError(null)
    try {
      const resultat = await demandeAccesService.valider(demande.id)
      setDemandeDetails(null)
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

  const ouvrirRejetDepuisDetails = (demande) => {
    setDemandeDetails(null)
    setDemandeARejeter(demande)
  }

  return (
    <Box>
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between" mb={2} gap={1}>
        <Box>
          <Typography variant="h5" fontWeight={600}>Demandes d'accès parent</Typography>
          <Typography variant="caption" color="text.secondary">Validez ou rejetez les demandes soumises depuis la page publique</Typography>
        </Box>
        <Stack direction="row" spacing={1} flexWrap="wrap" gap={1}>
          <TextField
            size="small" label="Rechercher"
            placeholder="Nom, prénom, téléphone, email, résidence…"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            sx={{ minWidth: 260 }}
          />
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
                        <Tooltip title={d.statut === 'EN_ATTENTE' ? 'Consulter et traiter la demande' : 'Voir les détails'}>
                          <IconButton size="small" onClick={() => setDemandeDetails(d)}>
                            <VisibilityIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
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

      {demandeDetails && (
        <DetailsDialog
          demande={demandeDetails}
          onClose={() => setDemandeDetails(null)}
          onValider={handleValider}
          onRejeter={ouvrirRejetDepuisDetails}
          validatingId={validatingId}
        />
      )}
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

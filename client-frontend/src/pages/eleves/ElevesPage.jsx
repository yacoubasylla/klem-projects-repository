import { useState } from 'react'
import {
  Box, Typography, Button, Stack, TextField, MenuItem,
  Table, TableHead, TableBody, TableRow, TableCell,
  TablePagination, TableContainer, Paper,
  IconButton, CircularProgress, Alert, Tooltip,
  Dialog, DialogTitle, DialogContent, DialogActions,
} from '@mui/material'
import AddIcon    from '@mui/icons-material/Add'
import EditIcon   from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import QrCode2Icon from '@mui/icons-material/QrCode2'
import PrintIcon  from '@mui/icons-material/Print'
import ContentCopyIcon from '@mui/icons-material/ContentCopy'
import PeopleIcon from '@mui/icons-material/People'
import FileDownloadIcon from '@mui/icons-material/FileDownload'
import RefreshIcon from '@mui/icons-material/Refresh'
import { QRCodeSVG } from 'qrcode.react'
import { useEleves } from '../../hooks/useEleves'
import { useEtablissements } from '../../hooks/useEtablissements'
import { useAuth } from '../../hooks/useAuth'
import { useConfigValeur } from '../../hooks/useConfig'
import StatutBadge from '../../components/StatutBadge'
import SuccessSnackbar from '../../components/SuccessSnackbar'
import EleveFormDialog from './EleveFormDialog'

const formatSolde = (val) =>
  new Intl.NumberFormat('fr-FR', { style: 'decimal' }).format(val ?? 0) + ' XOF'

// ── Export CSV ────────────────────────────────────────────────────────────────

function exportCsv(eleves, isCredits) {
  const header = ['Matricule', 'Nom', 'Prénom', 'Établissement', 'Classe', 'Statut']
  if (isCredits) header.push('Solde')
  const rows = eleves.map((e) => {
    const row = [
      e.matricule ?? '',
      e.nom ?? '',
      e.prenom ?? '',
      e.etablissementNom ?? '',
      e.classeLibelle ?? '',
      e.statutAcces ?? '',
    ]
    if (isCredits) row.push(e.solde ?? 0)
    return row
  })
  const csv = [header, ...rows].map((r) => r.map((c) => `"${c}"`).join(';')).join('\n')
  const blob = new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `eleves_${new Date().toISOString().split('T')[0]}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

function QrCodeDialog({ eleve, onClose }) {
  if (!eleve) return null
  return (
    <Dialog open={!!eleve} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>QR Code — {eleve.prenom} {eleve.nom}</DialogTitle>
      <DialogContent>
        <Stack alignItems="center" spacing={2} py={1}>
          <QRCodeSVG
            value={eleve.qrCodeToken}
            size={220}
            level="M"
            includeMargin
          />
          <Typography variant="body2" color="text.secondary">
            {eleve.classeLibelle} · {eleve.etablissementNom}
          </Typography>
          <Typography variant="caption" fontFamily="monospace" sx={{ wordBreak: 'break-all', textAlign: 'center', color: 'text.disabled' }}>
            {eleve.qrCodeToken}
          </Typography>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Tooltip title="Copier le token">
          <IconButton onClick={() => navigator.clipboard.writeText(eleve.qrCodeToken)}>
            <ContentCopyIcon fontSize="small" />
          </IconButton>
        </Tooltip>
        <Tooltip title="Imprimer">
          <IconButton onClick={() => window.print()}>
            <PrintIcon fontSize="small" />
          </IconButton>
        </Tooltip>
        <Button onClick={onClose}>Fermer</Button>
      </DialogActions>
    </Dialog>
  )
}

const STATUTS_FILTRE = [
  { value: '', label: 'Tous les statuts' },
  { value: 'AUTORISE', label: 'Autorisé' },
  { value: 'EN_ATTENTE_PAIEMENT', label: 'En attente' },
  { value: 'GRACE', label: 'Grâce' },
  { value: 'SUSPENDU', label: 'Suspendu' },
]

export default function ElevesPage() {
  const [filtres, setFiltres] = useState({ etablissementId: '', statut: '', search: '' })
  const [formOpen, setFormOpen]     = useState(false)
  const [eleveToEdit, setEleveToEdit] = useState(null)
  const [qrEleve,    setQrEleve]    = useState(null)
  const [successMsg, setSuccessMsg] = useState('')

  const { user } = useAuth()
  const isAdmin = user?.role === 'ADMIN'

  const { valeur: modePaiement } = useConfigValeur('MODE_PAIEMENT', 'ABONNEMENT')
  const isCredits = modePaiement === 'CREDITS'

  const { etablissements } = useEtablissements()
  const {
    eleves, total, page, setPage, rowsPerPage, setRowsPerPage,
    loading, error, creer, modifier, changerStatut, supprimer, recharger,
  } = useEleves(filtres)

  const setFiltre = (name, value) =>
    setFiltres((prev) => ({ ...prev, [name]: value }))

  const handleAdd = () => { setEleveToEdit(null); setFormOpen(true) }
  const handleEdit = (eleve) => { setEleveToEdit(eleve); setFormOpen(true) }

  const handleSuccess = async (payload) => {
    if (eleveToEdit) {
      const { statutAcces, ...dto } = payload
      await modifier(eleveToEdit.id, dto)
      if (statutAcces && statutAcces !== eleveToEdit.statutAcces) {
        await changerStatut(eleveToEdit.id, statutAcces)
      }
      setSuccessMsg('Élève modifié avec succès')
    } else {
      const { statutAcces, ...dto } = payload
      const created = await creer(dto)
      if (statutAcces && statutAcces !== 'EN_ATTENTE_PAIEMENT') {
        await changerStatut(created.id, statutAcces)
      }
      setSuccessMsg('Élève créé avec succès')
    }
  }

  const handleDelete = async (id, nom, prenom) => {
    if (!window.confirm(`Supprimer ${prenom} ${nom} ?`)) return
    try { await supprimer(id) } catch (e) { alert(e.message) }
  }

  return (
    <Box>
      {/* ── En-tête ─────────────────────────────────────── */}
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between" mb={3} gap={1}>
        <Stack direction="row" alignItems="center" spacing={1.5}>
          <PeopleIcon color="primary" />
          <Typography variant="h5" fontWeight={600}>Élèves</Typography>
        </Stack>
        <Stack direction="row" spacing={1} flexWrap="wrap">
          <Tooltip title="Exporter CSV (page courante)">
            <span>
              <Button
                size="small"
                variant="outlined"
                startIcon={<FileDownloadIcon />}
                disabled={eleves.length === 0}
                onClick={() => exportCsv(eleves, isCredits)}
              >
                CSV
              </Button>
            </span>
          </Tooltip>
          <Tooltip title="Actualiser">
            <IconButton size="small" onClick={recharger} disabled={loading}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          {isAdmin && (
            <Button variant="contained" startIcon={<AddIcon />} onClick={handleAdd} sx={{ flex: { xs: 1, sm: '0 0 auto' } }}>
              Ajouter
            </Button>
          )}
        </Stack>
      </Stack>

      {/* ── Filtres ─────────────────────────────────────── */}
      <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} flexWrap="wrap" gap={1.5} alignItems={{ xs: 'stretch', sm: 'flex-end' }}>
          <TextField
            size="small" label="Recherche" placeholder="Nom, prénom, matricule…"
            value={filtres.search}
            onChange={(e) => setFiltre('search', e.target.value)}
            sx={{ minWidth: { xs: '100%', sm: 220 }, flex: 1 }}
          />
          <TextField
            select size="small" label="Établissement"
            value={filtres.etablissementId}
            onChange={(e) => setFiltre('etablissementId', e.target.value)}
            sx={{ minWidth: { xs: '100%', sm: 200 } }}
          >
            <MenuItem value="">Tous</MenuItem>
            {etablissements.map((e) => (
              <MenuItem key={e.id} value={String(e.id)}>{e.nom}</MenuItem>
            ))}
          </TextField>
          <TextField
            select size="small" label="Statut"
            value={filtres.statut}
            onChange={(e) => setFiltre('statut', e.target.value)}
            sx={{ minWidth: { xs: '100%', sm: 180 } }}
          >
            {STATUTS_FILTRE.map((s) => (
              <MenuItem key={s.value} value={s.value}>{s.label}</MenuItem>
            ))}
          </TextField>
        </Stack>
      </Paper>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {/* ── Tableau ─────────────────────────────────────── */}
      {/* Colonnes secondaires (Matricule/Établissement/Classe) masquées sous sm — repliées
          en sous-titre dans la cellule Nom/Prénom pour éviter le défilement horizontal. */}
      <TableContainer component={Paper} sx={{ boxShadow: 1 }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Matricule</TableCell>
              <TableCell>Nom / Prénom</TableCell>
              <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Établissement</TableCell>
              <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>Classe</TableCell>
              <TableCell>Statut</TableCell>
              {isCredits && <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }} align="right">Solde</TableCell>}
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={isCredits ? 7 : 6} align="center" sx={{ py: 4 }}>
                  <CircularProgress size={28} />
                </TableCell>
              </TableRow>
            ) : eleves.length === 0 ? (
              <TableRow>
                <TableCell colSpan={isCredits ? 7 : 6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                  Aucun élève trouvé
                </TableCell>
              </TableRow>
            ) : (
              eleves.map((eleve) => (
                <TableRow key={eleve.id} hover>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' }, fontFamily: 'monospace', fontSize: 13 }}>
                    {eleve.matricule}
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" fontWeight={600}>{eleve.nom} {eleve.prenom}</Typography>
                    <Typography variant="caption" color="text.secondary" sx={{ display: { xs: 'block', sm: 'none' } }}>
                      {eleve.matricule} · {eleve.classeLibelle} · {eleve.etablissementNom}
                    </Typography>
                  </TableCell>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                    <Typography variant="body2" color="text.secondary">{eleve.etablissementNom}</Typography>
                  </TableCell>
                  <TableCell sx={{ display: { xs: 'none', sm: 'table-cell' } }}>
                    <Typography variant="body2">{eleve.classeLibelle}</Typography>
                  </TableCell>
                  <TableCell><StatutBadge statut={eleve.statutAcces} /></TableCell>
                  {isCredits && (
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }} align="right">
                      <Typography variant="body2" fontFamily="monospace">{formatSolde(eleve.solde)}</Typography>
                    </TableCell>
                  )}
                  <TableCell align="center" sx={{ whiteSpace: 'nowrap' }}>
                    <Tooltip title="Afficher le QR Code">
                      <IconButton size="small" onClick={() => setQrEleve(eleve)}>
                        <QrCode2Icon fontSize="small" color="action" />
                      </IconButton>
                    </Tooltip>
                    {isAdmin && (
                      <>
                        <Tooltip title="Modifier">
                          <IconButton size="small" onClick={() => handleEdit(eleve)}>
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Supprimer">
                          <IconButton size="small" color="error" onClick={() => handleDelete(eleve.id, eleve.nom, eleve.prenom)}>
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </>
                    )}
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

      <EleveFormDialog
        open={formOpen}
        onClose={() => setFormOpen(false)}
        onSuccess={handleSuccess}
        eleveToEdit={eleveToEdit}
        etablissements={etablissements}
      />

      <QrCodeDialog eleve={qrEleve} onClose={() => setQrEleve(null)} />

      <SuccessSnackbar open={Boolean(successMsg)} message={successMsg} onClose={() => setSuccessMsg('')} />
    </Box>
  )
}

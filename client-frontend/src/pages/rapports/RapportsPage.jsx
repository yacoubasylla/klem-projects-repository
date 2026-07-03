import { useState } from 'react'
import {
  Box, Typography, Stack, Paper, TextField, MenuItem, Button, Tabs, Tab,
  Table, TableHead, TableBody, TableRow, TableCell, TableContainer,
  Card, CardContent, Alert, CircularProgress, GlobalStyles, Divider,
} from '@mui/material'
import AssessmentIcon   from '@mui/icons-material/Assessment'
import PrintIcon        from '@mui/icons-material/Print'
import FileDownloadIcon from '@mui/icons-material/FileDownload'
import { useRapports } from '../../hooks/useRapports'
import { useEtablissements } from '../../hooks/useEtablissements'
import { useAuth } from '../../hooks/useAuth'
import { exporterRapportExcel, RAPPORT_LABELS } from '../../services/rapportExportService'

const { STATUT_LABELS, OPERATEUR_LABELS, RESULTAT_LABELS } = RAPPORT_LABELS

const premierJourDuMois = () => {
  const d = new Date()
  return new Date(d.getFullYear(), d.getMonth(), 1).toISOString().split('T')[0]
}
const aujourdhui = () => new Date().toISOString().split('T')[0]

const formatMontant = (v) => new Intl.NumberFormat('fr-FR').format(v ?? 0) + ' XOF'
const formatDateHeure = (dt) => dt ? new Date(dt).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' }) : '—'
const formatDate  = (d)  => d  ? new Date(d).toLocaleDateString('fr-FR')  : '—'
const formatHeure = (dt) => dt ? new Date(dt).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) : '—'

function KpiCard({ label, valeur, couleur }) {
  return (
    <Card variant="outlined" sx={{ flex: '1 1 180px', minWidth: 160 }}>
      <CardContent sx={{ pb: '16px !important' }}>
        <Typography variant="caption" color="text.secondary">{label}</Typography>
        <Typography variant="h5" fontWeight={700} color={couleur ?? 'text.primary'}>{valeur}</Typography>
      </CardContent>
    </Card>
  )
}

export default function RapportsPage() {
  const { user } = useAuth()
  const { etablissements } = useEtablissements()

  const [filtres, setFiltres] = useState({
    dateDebut: premierJourDuMois(),
    dateFin: aujourdhui(),
    etablissementId: '',
  })
  const [tab, setTab] = useState(0)
  const [genere, setGenere] = useState(false)
  const [exportingExcel, setExportingExcel] = useState(false)

  const { paiements, passages, resume, tronque, loading, error, generer } = useRapports()

  const handleGenerer = async () => {
    await generer(filtres)
    setGenere(true)
  }

  const handleExportExcel = async () => {
    setExportingExcel(true)
    try {
      await exporterRapportExcel({ periode: filtres, resume, paiements, passages })
    } finally {
      setExportingExcel(false)
    }
  }

  const periodeLabel = `${formatDate(filtres.dateDebut)} au ${formatDate(filtres.dateFin)}`
  const genereLe = new Date().toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' })
  const genereParLabel = `${user?.prenom ?? ''} ${user?.nom ?? ''}`.trim()

  return (
    <Box>
      <GlobalStyles
        styles={{
          '@media print': {
            'body *': { visibility: 'hidden' },
            '.print-area, .print-area *': { visibility: 'visible' },
            '.print-area': { position: 'absolute', top: 0, left: 0, width: '100%' },
          },
        }}
      />

      {/* ── En-tête ─────────────────────────────────────── */}
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between" mb={2} gap={1} className="no-print">
        <Box>
          <Stack direction="row" alignItems="center" spacing={1.5}>
            <AssessmentIcon color="primary" />
            <Typography variant="h5" fontWeight={600}>Rapports</Typography>
          </Stack>
          <Typography variant="caption" color="text.secondary">
            Version exploratoire — états financiers, statistiques, paiements et passages
          </Typography>
        </Box>
      </Stack>

      {/* ── Filtres ─────────────────────────────────────── */}
      <Paper variant="outlined" sx={{ p: 2, mb: 2 }} className="no-print">
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} flexWrap="wrap" gap={1.5} alignItems={{ xs: 'stretch', sm: 'center' }}>
          <TextField
            label="Date début"
            type="date"
            size="small"
            value={filtres.dateDebut}
            onChange={(e) => setFiltres((f) => ({ ...f, dateDebut: e.target.value }))}
            slotProps={{ inputLabel: { shrink: true } }}
            sx={{ minWidth: { xs: '100%', sm: 160 } }}
          />
          <TextField
            label="Date fin"
            type="date"
            size="small"
            value={filtres.dateFin}
            onChange={(e) => setFiltres((f) => ({ ...f, dateFin: e.target.value }))}
            slotProps={{ inputLabel: { shrink: true } }}
            sx={{ minWidth: { xs: '100%', sm: 160 } }}
          />
          <TextField
            select
            label="Établissement"
            size="small"
            value={filtres.etablissementId}
            onChange={(e) => setFiltres((f) => ({ ...f, etablissementId: e.target.value }))}
            sx={{ minWidth: { xs: '100%', sm: 200 } }}
            helperText="Filtre uniquement les passages"
          >
            <MenuItem value="">Tous les établissements</MenuItem>
            {etablissements.map((e) => (
              <MenuItem key={e.id} value={String(e.id)}>{e.nom}</MenuItem>
            ))}
          </TextField>
          <Button
            variant="contained"
            onClick={handleGenerer}
            disabled={loading}
            startIcon={loading ? <CircularProgress size={16} color="inherit" /> : <AssessmentIcon />}
            sx={{ flex: { xs: 1, sm: '0 0 auto' } }}
          >
            {loading ? 'Génération…' : 'Générer le rapport'}
          </Button>
          {genere && (
            <Button
              variant="outlined"
              onClick={handleExportExcel}
              disabled={exportingExcel || loading}
              startIcon={exportingExcel ? <CircularProgress size={16} /> : <FileDownloadIcon />}
            >
              Exporter Excel
            </Button>
          )}
        </Stack>
      </Paper>

      {error && <Alert severity="error" sx={{ mb: 2 }} className="no-print">{error}</Alert>}
      {tronque && (
        <Alert severity="warning" sx={{ mb: 2 }} className="no-print">
          La période sélectionnée contient un volume de données très important — le rapport a été tronqué. Réduisez la plage de dates pour un résultat complet.
        </Alert>
      )}

      {!genere ? (
        <Paper variant="outlined" sx={{ p: 4, textAlign: 'center', color: 'text.secondary' }} className="no-print">
          Choisissez une période puis cliquez sur « Générer le rapport ».
        </Paper>
      ) : (
        <>
          <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }} className="no-print">
            <Tab label="Résumé" />
            <Tab label="Paiements" />
            <Tab label="Passages" />
          </Tabs>

          {/* ── Onglet Résumé ─────────────────────────────── */}
          {tab === 0 && (
            <Box className="print-area">
              <PrintHeader titre="Rapport financier et statistique" periodeLabel={periodeLabel} genereLe={genereLe} genereParLabel={genereParLabel} />
              <Stack direction="row" spacing={2} flexWrap="wrap" gap={2} mb={2}>
                <KpiCard label="Montant encaissé" valeur={formatMontant(resume.montantEncaisse)} couleur="success.main" />
                <KpiCard label="Paiements acceptés" valeur={resume.nbParStatut.ACCEPTE ?? 0} />
                <KpiCard label="Passages accordés" valeur={resume.nbPassagesAccordes} couleur="success.main" />
                <KpiCard label="Taux d'accès" valeur={resume.tauxAcces != null ? `${resume.tauxAcces}%` : '—'} />
              </Stack>

              <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
                <Typography variant="subtitle2" fontWeight={600} mb={1}>Détail des paiements par statut</Typography>
                <Stack direction="row" spacing={3} flexWrap="wrap">
                  <Typography variant="body2">En attente : <strong>{resume.nbParStatut.EN_ATTENTE ?? 0}</strong></Typography>
                  <Typography variant="body2">Acceptés : <strong>{resume.nbParStatut.ACCEPTE ?? 0}</strong></Typography>
                  <Typography variant="body2">Refusés : <strong>{resume.nbParStatut.REFUSE ?? 0}</strong></Typography>
                  <Typography variant="body2">Annulés : <strong>{resume.nbParStatut.ANNULE ?? 0}</strong></Typography>
                </Stack>
              </Paper>

              <Paper variant="outlined" sx={{ p: 2 }}>
                <Typography variant="subtitle2" fontWeight={600} mb={1}>Détail des passages réfectoire</Typography>
                <Stack direction="row" spacing={3} flexWrap="wrap">
                  <Typography variant="body2">Total : <strong>{resume.nbPassages}</strong></Typography>
                  <Typography variant="body2">Accordés : <strong>{resume.nbPassagesAccordes}</strong></Typography>
                  <Typography variant="body2">Refusés : <strong>{resume.nbPassagesRefuses}</strong></Typography>
                </Stack>
              </Paper>

              <PrintButton />
            </Box>
          )}

          {/* ── Onglet Paiements ──────────────────────────── */}
          {tab === 1 && (
            <Box className="print-area">
              <PrintHeader titre="État des paiements" periodeLabel={periodeLabel} genereLe={genereLe} genereParLabel={genereParLabel} />
              <Paper variant="outlined">
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow sx={{ bgcolor: 'action.hover' }}>
                        <TableCell>Date</TableCell>
                        <TableCell>Élève</TableCell>
                        <TableCell>Opérateur</TableCell>
                        <TableCell align="right">Montant</TableCell>
                        <TableCell>Statut</TableCell>
                        <TableCell>Référence</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {paiements.length === 0 ? (
                        <TableRow>
                          <TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                            Aucun paiement sur cette période
                          </TableCell>
                        </TableRow>
                      ) : paiements.map((p) => (
                        <TableRow key={p.id} hover>
                          <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatDateHeure(p.dateCreation)}</TableCell>
                          <TableCell>{p.eleveNomComplet}</TableCell>
                          <TableCell>{OPERATEUR_LABELS[p.operateur] ?? p.operateur}</TableCell>
                          <TableCell align="right" sx={{ fontWeight: 600 }}>{formatMontant(p.montant)}</TableCell>
                          <TableCell>{STATUT_LABELS[p.statut] ?? p.statut}</TableCell>
                          <TableCell sx={{ fontFamily: 'monospace', fontSize: 12 }}>{p.referenceInterne?.slice(0, 8)}…</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Paper>
              <PrintButton />
            </Box>
          )}

          {/* ── Onglet Passages ───────────────────────────── */}
          {tab === 2 && (
            <Box className="print-area">
              <PrintHeader titre="État des passages réfectoire" periodeLabel={periodeLabel} genereLe={genereLe} genereParLabel={genereParLabel} />
              <Paper variant="outlined">
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow sx={{ bgcolor: 'action.hover' }}>
                        <TableCell>Date</TableCell>
                        <TableCell>Heure</TableCell>
                        <TableCell>Matricule</TableCell>
                        <TableCell>Élève</TableCell>
                        <TableCell>Classe</TableCell>
                        <TableCell>Établissement</TableCell>
                        <TableCell>Résultat</TableCell>
                        <TableCell>Motif</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {passages.length === 0 ? (
                        <TableRow>
                          <TableCell colSpan={8} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                            Aucun passage sur cette période
                          </TableCell>
                        </TableRow>
                      ) : passages.map((p) => (
                        <TableRow key={p.passageId ?? p.id} hover>
                          <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatDate(p.datePassage)}</TableCell>
                          <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatHeure(p.heurePassage)}</TableCell>
                          <TableCell sx={{ fontFamily: 'monospace', fontSize: 12 }}>{p.eleveMatricule ?? '—'}</TableCell>
                          <TableCell>{p.eleveNomComplet}</TableCell>
                          <TableCell>{p.classeNom ?? '—'}</TableCell>
                          <TableCell>{p.etablissementNom ?? '—'}</TableCell>
                          <TableCell>{RESULTAT_LABELS[p.resultat] ?? p.resultat}</TableCell>
                          <TableCell>{p.motifRefus ?? '—'}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Paper>
              <PrintButton />
            </Box>
          )}
        </>
      )}
    </Box>
  )
}

function PrintHeader({ titre, periodeLabel, genereLe, genereParLabel }) {
  return (
    <Box mb={2}>
      <Typography variant="h6" fontWeight={700}>🍽️ Cantine Connect — {titre}</Typography>
      <Typography variant="body2" color="text.secondary">
        Période du {periodeLabel} · Généré le {genereLe}{genereParLabel ? ` par ${genereParLabel}` : ''}
      </Typography>
      <Divider sx={{ mt: 1.5 }} />
    </Box>
  )
}

function PrintButton() {
  return (
    <Stack direction="row" justifyContent="flex-end" mt={2} className="no-print">
      <Button variant="outlined" startIcon={<PrintIcon />} onClick={() => window.print()}>
        Imprimer / PDF
      </Button>
    </Stack>
  )
}

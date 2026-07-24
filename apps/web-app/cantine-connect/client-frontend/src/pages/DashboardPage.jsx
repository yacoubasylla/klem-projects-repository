import {
  Box, Typography, Card, CardContent, Stack,
  Skeleton, Chip, Divider, Alert, IconButton, Tooltip,
  Table, TableHead, TableBody, TableRow, TableCell, TableContainer,
  LinearProgress,
} from '@mui/material'
import SchoolIcon        from '@mui/icons-material/School'
import PeopleIcon        from '@mui/icons-material/People'
import RestaurantIcon    from '@mui/icons-material/Restaurant'
import PaymentsIcon      from '@mui/icons-material/Payments'
import CheckCircleIcon   from '@mui/icons-material/CheckCircle'
import CancelIcon        from '@mui/icons-material/Cancel'
import HourglassIcon     from '@mui/icons-material/HourglassEmpty'
import RefreshIcon       from '@mui/icons-material/Refresh'
import TrendingUpIcon    from '@mui/icons-material/TrendingUp'
import { useDashboard }  from '../hooks/useDashboard'

const STATUT_CONFIG = {
  AUTORISE:            { label: 'Autorisés',  color: 'success' },
  GRACE:               { label: 'Grâce',      color: 'info'    },
  EN_ATTENTE_PAIEMENT: { label: 'En attente', color: 'warning' },
  SUSPENDU:            { label: 'Suspendus',  color: 'error'   },
}

function KpiCard({ label, value, icon: Icon, color, loading, sub }) {
  return (
    <Card sx={{ flex: '1 1 180px', minWidth: 160 }}>
      <CardContent sx={{ pb: '16px !important' }}>
        <Stack direction="row" alignItems="flex-start" spacing={2}>
          <Icon sx={{ fontSize: 38, color, mt: 0.5 }} />
          <Box>
            {loading
              ? <Skeleton width={60} height={40} />
              : <Typography variant="h4" fontWeight={700} lineHeight={1}>{value}</Typography>
            }
            <Typography variant="caption" color="text.secondary">{label}</Typography>
            {sub && !loading && (
              <Typography variant="caption" display="block" color="text.disabled" mt={0.25}>
                {sub}
              </Typography>
            )}
          </Box>
        </Stack>
      </CardContent>
    </Card>
  )
}

function StatutChip({ statut, count, loading }) {
  const { label, color } = STATUT_CONFIG[statut]
  return (
    <Card variant="outlined" sx={{ flex: '1 1 120px', minWidth: 110 }}>
      <CardContent sx={{ py: 1.5, pb: '12px !important', textAlign: 'center' }}>
        {loading
          ? <Skeleton width={50} height={30} sx={{ mx: 'auto' }} />
          : <Typography variant="h5" fontWeight={700}>{count}</Typography>
        }
        <Chip label={label} color={color} size="small" sx={{ mt: 0.5 }} />
      </CardContent>
    </Card>
  )
}

const JOURS_FR = ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam']

function TendanceChart({ tendance, loading }) {
  const maxTotal = Math.max(...(tendance ?? []).map((j) => j.accordes + j.refuses), 1)

  return (
    <Card>
      <CardContent sx={{ pb: '16px !important' }}>
        <Stack direction="row" alignItems="center" spacing={1} mb={2}>
          <TrendingUpIcon color="primary" fontSize="small" />
          <Typography variant="subtitle1" fontWeight={600}>
            Passages — 7 derniers jours
          </Typography>
        </Stack>
        <Divider sx={{ mb: 2 }} />

        {loading ? (
          <Skeleton variant="rectangular" height={90} />
        ) : (
          <Box sx={{ display: 'flex', alignItems: 'flex-end', gap: 1, height: 90 }}>
            {(tendance ?? []).map((jour) => {
              const total = jour.accordes + jour.refuses
              const heightPct = (total / maxTotal) * 100
              const accordesPct = total > 0 ? (jour.accordes / total) * 100 : 0
              const dateObj = new Date(jour.date + 'T00:00:00')
              const jourLabel = JOURS_FR[dateObj.getDay()]
              const dateLabel = dateObj.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' })

              return (
                <Tooltip
                  key={jour.date}
                  title={`${jourLabel} ${dateLabel} — ✓ ${jour.accordes} accordés / ✗ ${jour.refuses} refusés`}
                  arrow
                >
                  <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 0.5 }}>
                    <Box
                      sx={{
                        width: '100%',
                        height: `${Math.max(heightPct, total > 0 ? 8 : 0)}%`,
                        minHeight: total > 0 ? 4 : 0,
                        borderRadius: 1,
                        overflow: 'hidden',
                        display: 'flex',
                        flexDirection: 'column',
                        cursor: 'default',
                      }}
                    >
                      <Box sx={{ flex: accordesPct, bgcolor: 'success.light' }} />
                      <Box sx={{ flex: 100 - accordesPct, bgcolor: 'error.light' }} />
                    </Box>
                    {total > 0 && (
                      <Typography variant="caption" color="text.secondary" fontWeight={600} lineHeight={1}>
                        {total}
                      </Typography>
                    )}
                    <Typography variant="caption" color="text.disabled" fontSize={10} lineHeight={1}>
                      {jourLabel}
                    </Typography>
                  </Box>
                </Tooltip>
              )
            })}
          </Box>
        )}

        <Stack direction="row" spacing={2} mt={1.5} justifyContent="center">
          <Stack direction="row" alignItems="center" spacing={0.5}>
            <Box sx={{ width: 12, height: 12, bgcolor: 'success.light', borderRadius: 0.5 }} />
            <Typography variant="caption" color="text.secondary">Accordés</Typography>
          </Stack>
          <Stack direction="row" alignItems="center" spacing={0.5}>
            <Box sx={{ width: 12, height: 12, bgcolor: 'error.light', borderRadius: 0.5 }} />
            <Typography variant="caption" color="text.secondary">Refusés</Typography>
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  )
}

function AccesAujourdhui({ accordes, refuses, loading }) {
  const total = accordes + refuses
  const tauxPct = total > 0 ? Math.round((accordes / total) * 100) : 0

  return (
    <Card sx={{ flex: '1 1 260px' }}>
      <CardContent sx={{ pb: '16px !important' }}>
        <Typography variant="subtitle2" color="text.secondary" mb={1.5}>
          Accès réfectoire aujourd'hui
        </Typography>
        {loading ? (
          <Skeleton height={60} />
        ) : (
          <>
            <Stack direction="row" justifyContent="space-between" mb={0.5}>
              <Stack direction="row" alignItems="center" spacing={0.5}>
                <CheckCircleIcon color="success" fontSize="small" />
                <Typography variant="body2" fontWeight={600}>{accordes} accordés</Typography>
              </Stack>
              <Stack direction="row" alignItems="center" spacing={0.5}>
                <CancelIcon color="error" fontSize="small" />
                <Typography variant="body2" fontWeight={600}>{refuses} refusés</Typography>
              </Stack>
            </Stack>
            <LinearProgress
              variant="determinate"
              value={tauxPct}
              color="success"
              sx={{ height: 8, borderRadius: 4, bgcolor: 'error.light' }}
            />
            <Typography variant="caption" color="text.secondary" mt={0.5} display="block">
              Taux d'accès : {tauxPct}% ({total} passages au total)
            </Typography>
          </>
        )}
      </CardContent>
    </Card>
  )
}

function PaiementsCard({ nbMois, montantMois, nbEnAttente, loading }) {
  const montantFormate = loading ? '—' : new Intl.NumberFormat('fr-FR').format(montantMois ?? 0)

  return (
    <Card sx={{ flex: '1 1 260px' }}>
      <CardContent sx={{ pb: '16px !important' }}>
        <Typography variant="subtitle2" color="text.secondary" mb={1.5}>
          Paiements — mois en cours
        </Typography>
        {loading ? (
          <Skeleton height={60} />
        ) : (
          <>
            <Typography variant="h5" fontWeight={700} color="success.main">
              {montantFormate} FCFA
            </Typography>
            <Typography variant="caption" color="text.secondary">
              {nbMois} transaction{nbMois > 1 ? 's' : ''} acceptée{nbMois > 1 ? 's' : ''}
            </Typography>
            <Divider sx={{ my: 1 }} />
            <Stack direction="row" alignItems="center" spacing={1}>
              <HourglassIcon fontSize="small" color="warning" />
              <Typography variant="body2">
                <strong>{nbEnAttente}</strong> paiement{nbEnAttente > 1 ? 's' : ''} en attente
              </Typography>
            </Stack>
          </>
        )}
      </CardContent>
    </Card>
  )
}

function PassagesTable({ passages, loading }) {
  return (
    <Card>
      <CardContent sx={{ pb: '16px !important' }}>
        <Typography variant="subtitle1" fontWeight={600} mb={1}>
          Derniers passages du jour
        </Typography>
        <Divider sx={{ mb: 1 }} />
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Heure</TableCell>
                <TableCell>Élève</TableCell>
                <TableCell>Classe</TableCell>
                <TableCell align="center">Résultat</TableCell>
                <TableCell>Motif refus</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading
                ? Array.from({ length: 3 }).map((_, i) => (
                    <TableRow key={i}>
                      {Array.from({ length: 5 }).map((__, j) => (
                        <TableCell key={j}><Skeleton /></TableCell>
                      ))}
                    </TableRow>
                  ))
                : passages.length === 0
                  ? (
                    <TableRow>
                      <TableCell colSpan={5} align="center" sx={{ color: 'text.secondary', py: 2 }}>
                        Aucun passage enregistré aujourd&apos;hui
                      </TableCell>
                    </TableRow>
                  )
                  : passages.map((p) => (
                    <TableRow key={p.id ?? p.passageId} hover>
                      <TableCell sx={{ whiteSpace: 'nowrap' }}>
                        {p.heurePassage
                          ? new Date(p.heurePassage).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
                          : '—'
                        }
                      </TableCell>
                      <TableCell>{p.eleveNomComplet}</TableCell>
                      <TableCell>{p.classeNom ?? '—'}</TableCell>
                      <TableCell align="center">
                        {p.resultat === 'ACCORDE'
                          ? <CheckCircleIcon color="success" fontSize="small" />
                          : <CancelIcon color="error" fontSize="small" />
                        }
                      </TableCell>
                      <TableCell>
                        {p.motifRefus
                          ? <Chip label={p.motifRefus.replace(/_/g, ' ')} size="small" color="error" variant="outlined" />
                          : '—'
                        }
                      </TableCell>
                    </TableRow>
                  ))
              }
            </TableBody>
          </Table>
        </TableContainer>
      </CardContent>
    </Card>
  )
}

export default function DashboardPage() {
  const { stats, loading, error, recharger } = useDashboard()

  return (
    <Box>
      <Stack direction="row" alignItems="center" justifyContent="space-between" mb={3}>
        <Typography variant="h5" fontWeight={600}>Tableau de bord</Typography>
        <Tooltip title="Actualiser">
          <IconButton onClick={recharger} disabled={loading} size="small">
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </Stack>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={recharger}>
          {error}
        </Alert>
      )}

      {/* KPI Cards */}
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 3 }}>
        <KpiCard
          label="Établissements"
          value={stats?.nbEtablissements ?? 0}
          icon={SchoolIcon}
          color="primary.main"
          loading={loading}
        />
        <KpiCard
          label="Élèves actifs"
          value={stats?.totalEleves ?? 0}
          icon={PeopleIcon}
          color="secondary.main"
          loading={loading}
          sub={`${stats?.autorises ?? 0} autorisés`}
        />
        <KpiCard
          label="Passages aujourd'hui"
          value={stats?.passagesAujourdhui ?? 0}
          icon={RestaurantIcon}
          color="warning.main"
          loading={loading}
          sub={`✓ ${stats?.passagesAccordes ?? 0}  ✗ ${stats?.passagesRefuses ?? 0}`}
        />
        <KpiCard
          label="Paiements du mois"
          value={loading ? '…' : new Intl.NumberFormat('fr-FR').format(stats?.montantPaiementsMois ?? 0) + ' FCFA'}
          icon={PaymentsIcon}
          color="success.main"
          loading={loading}
          sub={`${stats?.nbPaiementsMois ?? 0} transactions`}
        />
      </Box>

      {/* Répartition statuts */}
      <Typography variant="subtitle2" color="text.secondary" mb={1}>
        Répartition des élèves par statut d'accès
      </Typography>
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1.5, mb: 3 }}>
        <StatutChip statut="AUTORISE"            count={stats?.autorises ?? 0} loading={loading} />
        <StatutChip statut="GRACE"               count={stats?.grace    ?? 0} loading={loading} />
        <StatutChip statut="EN_ATTENTE_PAIEMENT" count={stats?.enAttente ?? 0} loading={loading} />
        <StatutChip statut="SUSPENDU"            count={stats?.suspendus ?? 0} loading={loading} />
      </Box>

      {/* Accès + Paiements (ligne d'analyse) */}
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 3 }}>
        <AccesAujourdhui
          accordes={stats?.passagesAccordes ?? 0}
          refuses={stats?.passagesRefuses ?? 0}
          loading={loading}
        />
        <PaiementsCard
          nbMois={stats?.nbPaiementsMois ?? 0}
          montantMois={stats?.montantPaiementsMois ?? 0}
          nbEnAttente={stats?.nbPaiementsEnAttente ?? 0}
          loading={loading}
        />
      </Box>

      {/* Tendance 7 jours */}
      <Box sx={{ mb: 3 }}>
        <TendanceChart tendance={stats?.tendance7Jours} loading={loading} />
      </Box>

      {/* Derniers passages */}
      <PassagesTable
        passages={stats?.derniersPassages ?? []}
        loading={loading}
      />
    </Box>
  )
}

import { useState, useRef, useEffect, useCallback } from 'react'
import {
  Box, Typography, Stack, Chip, Alert, IconButton, Tooltip,
  TextField, Button, Paper, Divider, CircularProgress,
  List, ListItem, ListItemText, ListItemIcon,
} from '@mui/material'
import QrCodeScannerIcon  from '@mui/icons-material/QrCodeScanner'
import CloudDownloadIcon  from '@mui/icons-material/CloudDownload'
import CloudOffIcon       from '@mui/icons-material/CloudOff'
import WifiIcon           from '@mui/icons-material/Wifi'
import RefreshIcon        from '@mui/icons-material/Refresh'
import CheckCircleIcon    from '@mui/icons-material/CheckCircle'
import CancelIcon         from '@mui/icons-material/Cancel'
import CloseIcon          from '@mui/icons-material/Close'
import CameraAltIcon      from '@mui/icons-material/CameraAlt'
import VideocamOffIcon    from '@mui/icons-material/VideocamOff'
import { useScan }             from '../../hooks/useScan'
import { scanService }         from '../../services/scanService'
import { cacheOfflineService } from '../../services/cacheOfflineService'
import { useConfigValeur }     from '../../hooks/useConfig'
import QrCameraScanner         from '../../components/QrCameraScanner'

// ── Formatters ────────────────────────────────────────────────────────────────

const MOTIF_LABELS = {
  STATUT_SUSPENDU:            'Compte suspendu',
  STATUT_EN_ATTENTE_PAIEMENT: 'Paiement en attente',
  DOUBLON_PASSAGE:            'Déjà passé aujourd\'hui',
  QR_CODE_INCONNU:            'QR Code non reconnu',
}

const formatHeure = (dt) =>
  dt ? new Date(dt).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit', second: '2-digit' }) : '—'

// ── Carte résultat ────────────────────────────────────────────────────────────

function ResultCard({ result, onClear }) {
  const accorded = result.resultat === 'ACCORDE'
  return (
    <Paper
      elevation={3}
      sx={{
        p: 3, mt: 2, borderRadius: 3, position: 'relative',
        background: accorded
          ? 'linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%)'
          : 'linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%)',
        border: '2px solid',
        borderColor: accorded ? 'success.main' : 'error.main',
      }}
    >
      <IconButton
        size="small"
        onClick={onClear}
        sx={{ position: 'absolute', top: 8, right: 8, opacity: 0.6 }}
      >
        <CloseIcon fontSize="small" />
      </IconButton>

      <Stack direction="row" alignItems="center" spacing={2} mb={1}>
        {accorded
          ? <CheckCircleIcon sx={{ fontSize: 48, color: 'success.main' }} />
          : <CancelIcon      sx={{ fontSize: 48, color: 'error.main' }} />
        }
        <Box>
          <Typography variant="h4" fontWeight={800} color={accorded ? 'success.dark' : 'error.dark'}>
            {result.acces}
          </Typography>
          {result.source === 'offline' && (
            <Chip label="Mode hors ligne" size="small" color="warning" sx={{ mt: 0.5 }} />
          )}
        </Box>
      </Stack>

      <Divider sx={{ my: 1.5 }} />

      <Typography variant="h6" fontWeight={700}>{result.nomComplet}</Typography>
      <Typography variant="body2" color="text.secondary">
        {result.classeNom} · {result.etablissementNom}
      </Typography>
      {result.matricule && (
        <Typography variant="caption" color="text.secondary">
          Matricule : {result.matricule}
        </Typography>
      )}

      {!accorded && result.motifRefus && (
        <Chip
          label={MOTIF_LABELS[result.motifRefus] ?? result.motifRefus}
          color="error"
          size="small"
          sx={{ mt: 1.5, display: 'block', width: 'fit-content' }}
        />
      )}

      {result.heurePassage && (
        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>
          {accorded ? 'Accès accordé à' : 'Refus enregistré à'} {formatHeure(result.heurePassage)}
          {result.passageId && ` · #${result.passageId}`}
        </Typography>
      )}
    </Paper>
  )
}

// ── Panel passages du jour ────────────────────────────────────────────────────

function PassagesPanel({ passages, loading, onRefresh }) {
  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Stack direction="row" alignItems="center" justifyContent="space-between" mb={1}>
        <Typography variant="subtitle1" fontWeight={600}>
          Passages du jour
          {!loading && (
            <Typography component="span" variant="caption" color="text.secondary" ml={1}>
              ({passages.length})
            </Typography>
          )}
        </Typography>
        <Tooltip title="Actualiser">
          <IconButton size="small" onClick={onRefresh} disabled={loading}>
            <RefreshIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      </Stack>
      <Divider />
      <Box sx={{ flex: 1, overflow: 'auto', mt: 1 }}>
        {loading
          ? <Box textAlign="center" py={3}><CircularProgress size={24} /></Box>
          : passages.length === 0
            ? <Typography variant="body2" color="text.secondary" textAlign="center" py={3}>
                Aucun passage aujourd&apos;hui
              </Typography>
            : (
              <List dense disablePadding>
                {passages.map((p, i) => (
                  <ListItem key={p.passageId ?? i} disablePadding sx={{ py: 0.25 }}>
                    <ListItemIcon sx={{ minWidth: 32 }}>
                      {p.resultat === 'ACCORDE'
                        ? <CheckCircleIcon color="success" fontSize="small" />
                        : <CancelIcon color="error" fontSize="small" />
                      }
                    </ListItemIcon>
                    <ListItemText
                      primary={
                        <Typography variant="body2" fontWeight={500} noWrap>
                          {p.eleveNomComplet}
                        </Typography>
                      }
                      secondary={
                        <Typography variant="caption" color="text.secondary">
                          {formatHeure(p.heurePassage)} · {p.classeNom ?? '—'}
                        </Typography>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            )
        }
      </Box>
    </Box>
  )
}

// ── Page principale ───────────────────────────────────────────────────────────

export default function ScanPage() {
  const [token,        setToken]        = useState('')
  const [passages,     setPassages]     = useState([])
  const [loadingP,     setLoadingP]     = useState(false)
  const [cameraActive, setCameraActive] = useState(false)
  const inputRef = useRef(null)

  const { valeur: cameraEnabled } = useConfigValeur('SCAN_CAMERA_ENABLED', 'false')
  const { valeur: cacheAutoRefresh, loading: loadingAutoRefreshCfg } = useConfigValeur('SCAN_CACHE_AUTO_REFRESH', 'true')
  const autoRefreshDone = useRef(false)

  const {
    result, scanning, error,
    isOnline, cache, refreshingCache,
    scanner, rafraichirCache, reinitialiser,
  } = useScan()

  // Rafraîchit automatiquement le cache offline à l'ouverture de la page,
  // une seule fois, si activé en configuration et si une connexion est disponible.
  useEffect(() => {
    if (autoRefreshDone.current || loadingAutoRefreshCfg) return
    if (cacheAutoRefresh === 'true' && isOnline) {
      autoRefreshDone.current = true
      rafraichirCache()
    }
  }, [cacheAutoRefresh, loadingAutoRefreshCfg, isOnline, rafraichirCache])

  const chargerPassages = useCallback(async () => {
    setLoadingP(true)
    try {
      const today = new Date().toISOString().split('T')[0]
      const data = await scanService.getPassages({ date: today, size: 50, sort: 'heurePassage,desc' })
      setPassages(data.content ?? [])
    } catch { /* silencieux */ }
    finally { setLoadingP(false) }
  }, [])

  useEffect(() => { chargerPassages() }, [chargerPassages])

  // Recharger les passages après chaque scan réussi
  useEffect(() => {
    if (result?.source === 'online') chargerPassages()
  }, [result, chargerPassages])

  const handleScan = async (e) => {
    e?.preventDefault()
    await scanner(token)
    setToken('')
    inputRef.current?.focus()
  }

  const handleCameraDetected = useCallback(async (decodedText) => {
    setToken(decodedText)
    await scanner(decodedText)
    setToken('')
  }, [scanner])

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleScan()
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: 0 }}>
      {/* ── Barre statut ──────────────────────────────── */}
      <Stack direction="row" alignItems="center" justifyContent="space-between" mb={2} flexWrap="wrap" gap={1}>
        <Typography variant="h5" fontWeight={600}>Contrôle Accès Réfectoire</Typography>
        <Stack direction="row" spacing={1} alignItems="center">
          <Chip
            icon={isOnline ? <WifiIcon /> : <CloudOffIcon />}
            label={isOnline ? 'En ligne' : 'Hors ligne'}
            color={isOnline ? 'success' : 'warning'}
            size="small"
            variant="outlined"
          />
          <Chip
            label={
              cache
                ? `Cache : ${cache.data.length} élèves · ${cacheOfflineService.ageTexte(cache.savedAt)}`
                : 'Cache absent'
            }
            size="small"
            color={cache ? 'default' : 'error'}
            variant="outlined"
          />
          <Tooltip title="Télécharger le cache hors-ligne">
            <span>
              <IconButton
                size="small"
                onClick={rafraichirCache}
                disabled={refreshingCache || !isOnline}
              >
                {refreshingCache ? <CircularProgress size={18} /> : <CloudDownloadIcon fontSize="small" />}
              </IconButton>
            </span>
          </Tooltip>
        </Stack>
      </Stack>

      {error && (
        <Alert severity="error" onClose={reinitialiser} sx={{ mb: 1.5 }}>
          {error}
        </Alert>
      )}

      {/* ── Layout : deux colonnes sur md+, une colonne sur mobile ── */}
      <Box sx={{ display: 'flex', gap: 3, flexDirection: { xs: 'column', md: 'row' }, alignItems: 'flex-start' }}>

        {/* Colonne gauche : scan + résultat */}
        <Box sx={{ flex: { xs: '1 1 auto', md: '0 0 55%' }, width: { xs: '100%', md: 'auto' }, display: 'flex', flexDirection: 'column' }}>
          <Paper variant="outlined" sx={{ p: 2.5 }}>
            <Stack direction="row" spacing={1} alignItems="center" mb={0.5} justifyContent="space-between">
              <Stack direction="row" spacing={1} alignItems="center">
                <QrCodeScannerIcon color="primary" />
                <Typography variant="subtitle1" fontWeight={600}>
                  Scanner le QR Code
                </Typography>
              </Stack>
              {cameraEnabled === 'true' && (
                <Button
                  size="small"
                  variant={cameraActive ? 'outlined' : 'contained'}
                  color={cameraActive ? 'error' : 'secondary'}
                  startIcon={cameraActive ? <VideocamOffIcon /> : <CameraAltIcon />}
                  onClick={() => setCameraActive(v => !v)}
                >
                  {cameraActive ? 'Désactiver caméra' : 'Activer caméra'}
                </Button>
              )}
            </Stack>
            <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
              {cameraActive
                ? 'Pointez la caméra vers le QR Code — scan automatique.'
                : 'Collez le token QR ou utilisez un lecteur de code-barres USB — appuyez sur Entrée pour valider.'
              }
            </Typography>
            <Stack direction="row" spacing={1}>
              <TextField
                inputRef={inputRef}
                fullWidth
                size="small"
                placeholder="Coller ou saisir le token QR Code…"
                value={token}
                onChange={(e) => setToken(e.target.value)}
                onKeyDown={handleKeyDown}
                autoFocus
                disabled={scanning}
                slotProps={{ input: { sx: { fontFamily: 'monospace', fontSize: '0.85rem' } } }}
              />
              <Button
                variant="contained"
                onClick={handleScan}
                disabled={scanning || !token.trim()}
                sx={{ whiteSpace: 'nowrap', minWidth: 110 }}
                startIcon={scanning ? <CircularProgress size={16} color="inherit" /> : <QrCodeScannerIcon />}
              >
                {scanning ? 'Scan…' : 'Scanner'}
              </Button>
            </Stack>

            <QrCameraScanner active={cameraActive} onDetected={handleCameraDetected} />
          </Paper>

          {result && <ResultCard result={result} onClear={reinitialiser} />}

          {!result && !error && (
            <Box
              sx={{
                flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center',
                color: 'text.disabled', flexDirection: 'column', gap: 1, mt: 4,
              }}
            >
              <QrCodeScannerIcon sx={{ fontSize: 64, opacity: 0.3 }} />
              <Typography variant="body2">En attente d&apos;un scan…</Typography>
            </Box>
          )}
        </Box>

        {/* Colonne droite : passages */}
        <Paper variant="outlined" sx={{ flex: 1, minWidth: 0, p: 2, display: 'flex', flexDirection: 'column', maxHeight: { xs: 400, md: 'none' }, overflow: 'auto' }}>
          <PassagesPanel passages={passages} loading={loadingP} onRefresh={chargerPassages} />
        </Paper>
      </Box>
    </Box>
  )
}

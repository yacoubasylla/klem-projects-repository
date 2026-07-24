import { useEffect, useRef, useState } from 'react'
import { Box, Typography, Alert, CircularProgress, Stack } from '@mui/material'
import CameraAltIcon from '@mui/icons-material/CameraAlt'
import { Html5Qrcode } from 'html5-qrcode'

const SCANNER_ID = 'cc-qr-camera-feed'

export default function QrCameraScanner({ onDetected, active }) {
  const scannerRef  = useRef(null)
  const lastScanRef = useRef(0)
  const [starting, setStarting] = useState(false)
  const [camError, setCamError] = useState(null)

  useEffect(() => {
    if (!active) return

    let cancelled = false

    const start = async () => {
      setStarting(true)
      setCamError(null)
      try {
        const cameras = await Html5Qrcode.getCameras()
        if (!cameras.length) { setCamError('Aucune caméra détectée sur cet appareil.'); return }

        // Préférer la caméra arrière sur mobile
        const camera = cameras.find(c => /back|rear|environment/i.test(c.label))
                       ?? cameras[cameras.length - 1]

        if (cancelled) return

        const scanner = new Html5Qrcode(SCANNER_ID)
        scannerRef.current = scanner

        await scanner.start(
          camera.id,
          { fps: 10, qrbox: { width: 240, height: 240 } },
          (decodedText) => {
            const now = Date.now()
            if (now - lastScanRef.current < 3000) return // anti-doublon 3s
            lastScanRef.current = now
            onDetected(decodedText)
          },
          () => {} // erreurs de parsing - ignorées
        )
      } catch (err) {
        if (!cancelled) {
          setCamError(
            /[Pp]ermission|[Dd]enied|[Nn]ot [Aa]llowed/.test(err.message)
              ? 'Accès caméra refusé. Autorisez l\'accès dans les paramètres du navigateur.'
              : `Impossible d'activer la caméra : ${err.message}`
          )
        }
      } finally {
        if (!cancelled) setStarting(false)
      }
    }

    start()

    return () => {
      cancelled = true
      if (scannerRef.current) {
        scannerRef.current.stop()
          .then(() => scannerRef.current?.clear())
          .catch(() => {})
          .finally(() => { scannerRef.current = null })
      }
    }
  }, [active, onDetected])

  if (!active) return null

  return (
    <Box sx={{ mt: 2 }}>
      {starting && (
        <Stack direction="row" alignItems="center" spacing={1} py={1.5}>
          <CircularProgress size={18} />
          <Typography variant="caption" color="text.secondary">Activation de la caméra…</Typography>
        </Stack>
      )}
      {camError && <Alert severity="error" sx={{ mb: 1 }}>{camError}</Alert>}

      {/* Zone caméra injectée par html5-qrcode */}
      <Box
        id={SCANNER_ID}
        sx={{
          borderRadius: 2,
          overflow: 'hidden',
          border: '2px dashed',
          borderColor: 'primary.light',
          minHeight: starting ? 0 : 300,
          '& video': { display: 'block', width: '100% !important', borderRadius: 1 },
          '& canvas': { display: 'none' },
          // Masquer les éléments UI de la lib qu'on ne veut pas afficher
          '& #cc-qr-camera-feed__header_message': { display: 'none' },
          '& select': { display: 'none !important' },
          '& button': { display: 'none !important' },
        }}
      />

      {!starting && !camError && (
        <Stack direction="row" alignItems="center" spacing={0.5} mt={0.75}>
          <CameraAltIcon sx={{ fontSize: 14, color: 'success.main' }} />
          <Typography variant="caption" color="success.main">
            Caméra active — pointez vers le QR Code de l&apos;élève
          </Typography>
        </Stack>
      )}
    </Box>
  )
}

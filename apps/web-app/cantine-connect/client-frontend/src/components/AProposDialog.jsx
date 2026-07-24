import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Button, Typography, Stack, Box, Chip, Divider, Link,
} from '@mui/material'
import PhoneIcon    from '@mui/icons-material/Phone'
import LanguageIcon from '@mui/icons-material/Language'
import EmailIcon    from '@mui/icons-material/Email'
import InfoIcon     from '@mui/icons-material/Info'

export default function AProposDialog({ open, onClose }) {
  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ pb: 1 }}>
        <Stack direction="row" alignItems="center" spacing={1.5}>
          <InfoIcon color="primary" />
          <Typography variant="h6" fontWeight={700}>À Propos</Typography>
        </Stack>
      </DialogTitle>

      <DialogContent sx={{ pt: 0 }}>
        {/* En-tête produit */}
        <Box sx={{ textAlign: 'center', py: 2 }}>
          <Typography variant="h5" fontWeight={800} color="primary.main" letterSpacing={1}>
            KLEM Technologies &amp; Services
          </Typography>
          <Typography variant="body2" color="text.secondary" mt={0.5}>
            Cantine Connect — Gestion multi-établissements
          </Typography>
          <Chip
            label="Version 1.0.0-beta"
            size="small"
            variant="outlined"
            color="primary"
            sx={{ mt: 1.5 }}
          />
        </Box>

        <Divider sx={{ mb: 2 }} />

        {/* Coordonnées */}
        <Stack spacing={1.5}>
          <Stack direction="row" alignItems="center" spacing={1.5}>
            <PhoneIcon fontSize="small" color="action" />
            <Link href="tel:+22507588924 77" underline="hover" color="text.primary" variant="body2">
              +225 07 58 89 24 77
            </Link>
          </Stack>

          <Stack direction="row" alignItems="center" spacing={1.5}>
            <LanguageIcon fontSize="small" color="action" />
            <Link
              href="https://www.klemtech.net"
              target="_blank"
              rel="noopener noreferrer"
              underline="hover"
              color="text.primary"
              variant="body2"
            >
              www.klemtech.net
            </Link>
          </Stack>

          <Stack direction="row" alignItems="center" spacing={1.5}>
            <EmailIcon fontSize="small" color="action" />
            <Link href="mailto:infos@klemtech.net" underline="hover" color="text.primary" variant="body2">
              infos@klemtech.net
            </Link>
          </Stack>
        </Stack>

        <Divider sx={{ mt: 2, mb: 1.5 }} />

        <Typography variant="caption" color="text.disabled" display="block" textAlign="center">
          © 2026 KLEM Technologies &amp; Services. Tous droits réservés.
        </Typography>
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} variant="contained" size="small" fullWidth>
          Fermer
        </Button>
      </DialogActions>
    </Dialog>
  )
}

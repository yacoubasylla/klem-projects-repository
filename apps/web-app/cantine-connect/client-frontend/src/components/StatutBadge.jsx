import { Chip } from '@mui/material'

const CONFIG = {
  AUTORISE:             { label: 'Autorisé',    color: 'success' },
  EN_ATTENTE_PAIEMENT:  { label: 'En attente',  color: 'warning' },
  GRACE:                { label: 'Grâce',        color: 'info'    },
  SUSPENDU:             { label: 'Suspendu',     color: 'error'   },
}

export default function StatutBadge({ statut }) {
  const cfg = CONFIG[statut] ?? { label: statut, color: 'default' }
  return <Chip label={cfg.label} color={cfg.color} size="small" />
}

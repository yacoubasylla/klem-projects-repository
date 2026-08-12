// packages/ui/src/components/SuccessSnackbar.tsx
import { Snackbar, Alert } from '@mui/material';

interface SuccessSnackbarProps {
  open: boolean;
  message: string;
  onClose: () => void;
}

/**
 * Notification de succès générique KLEM (Snackbar + Alert MUI), positionnée
 * en bas de l'écran et masquée automatiquement après 4 secondes.
 */
export function SuccessSnackbar({ open, message, onClose }: SuccessSnackbarProps) {
  return (
    <Snackbar
      open={open}
      autoHideDuration={4000}
      onClose={onClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
    >
      <Alert onClose={onClose} severity="success" variant="filled" sx={{ width: '100%' }}>
        {message}
      </Alert>
    </Snackbar>
  );
}

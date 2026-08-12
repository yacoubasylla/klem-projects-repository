// packages/ui/src/components/ErrorBoundary.tsx
import { Component, type ErrorInfo, type ReactNode } from 'react';
import { Box, Typography, Button, Alert } from '@mui/material';

interface ErrorBoundaryProps {
  children: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
}

/**
 * Barrière d'erreur générique KLEM : capture les erreurs de rendu de ses
 * enfants et affiche un état de repli avec un bouton de nouvelle tentative,
 * plutôt que de faire crasher toute l'application.
 */
export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('[ErrorBoundary]', error, info);
  }

  render() {
    if (this.state.hasError) {
      return (
        <Box sx={{ p: 4 }}>
          <Alert severity="error" sx={{ mb: 2 }}>
            <Typography variant="subtitle1" fontWeight={600}>Erreur de rendu</Typography>
            <Typography variant="body2" fontFamily="monospace" sx={{ mt: 1 }}>
              {this.state.error?.message}
            </Typography>
          </Alert>
          <Button variant="outlined" onClick={() => this.setState({ hasError: false, error: null })}>
            Réessayer
          </Button>
        </Box>
      );
    }
    return this.props.children;
  }
}

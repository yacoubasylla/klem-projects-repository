import { Component } from 'react'
import { Box, Typography, Button, Alert } from '@mui/material'

export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error }
  }

  componentDidCatch(error, info) {
    console.error('[ErrorBoundary]', error, info)
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
      )
    }
    return this.props.children
  }
}

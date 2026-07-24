import { useState } from 'react'
import {
  Popover, IconButton, Tooltip, Box, Typography, Stack, Paper,
} from '@mui/material'
import PaletteIcon from '@mui/icons-material/Palette'
import CheckIcon   from '@mui/icons-material/Check'
import { useThemeMode } from '../context/ThemeContext'
import { THEME_META }   from '../theme/themes'

export default function ThemeSwitcher() {
  const { themeName, changeTheme } = useThemeMode()
  const [anchor, setAnchor] = useState(null)

  return (
    <>
      <Tooltip title="Changer de thème">
        <IconButton
          color="inherit"
          size="small"
          onClick={(e) => setAnchor(e.currentTarget)}
          aria-label="sélecteur de thème"
        >
          <PaletteIcon fontSize="small" />
        </IconButton>
      </Tooltip>

      <Popover
        open={Boolean(anchor)}
        anchorEl={anchor}
        onClose={() => setAnchor(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
        PaperProps={{ sx: { p: 2, width: 270, mt: 1 } }}
      >
        <Typography variant="subtitle2" fontWeight={700} mb={1.5}>
          🎨 Apparence
        </Typography>

        <Stack spacing={1}>
          {Object.entries(THEME_META).map(([key, meta]) => {
            const selected = themeName === key
            return (
              <Paper
                key={key}
                variant="outlined"
                onClick={() => { changeTheme(key); setAnchor(null) }}
                sx={{
                  p: 1.5,
                  cursor: 'pointer',
                  borderWidth: selected ? 2 : 1,
                  borderColor: selected ? 'primary.main' : 'divider',
                  bgcolor: selected ? 'action.selected' : 'background.paper',
                  '&:hover': { borderColor: 'primary.main', bgcolor: 'action.hover' },
                  transition: 'all 0.15s ease',
                  userSelect: 'none',
                }}
              >
                <Stack direction="row" alignItems="center" justifyContent="space-between">
                  <Stack direction="row" alignItems="center" spacing={1.5}>
                    <Typography fontSize={22} lineHeight={1}>{meta.emoji}</Typography>
                    <Box>
                      <Typography variant="body2" fontWeight={600} lineHeight={1.3}>
                        {meta.label}
                      </Typography>
                      <Typography variant="caption" color="text.secondary" lineHeight={1.4}>
                        {meta.description}
                      </Typography>
                    </Box>
                  </Stack>

                  <Stack direction="row" alignItems="center" spacing={0.5}>
                    {meta.colors.map((c, i) => (
                      <Box
                        key={i}
                        sx={{
                          width: 13,
                          height: 13,
                          borderRadius: '50%',
                          bgcolor: c,
                          border: '1px solid rgba(0,0,0,0.12)',
                          flexShrink: 0,
                        }}
                      />
                    ))}
                    {selected && (
                      <CheckIcon sx={{ fontSize: 16, color: 'primary.main', ml: 0.25 }} />
                    )}
                  </Stack>
                </Stack>
              </Paper>
            )
          })}
        </Stack>
      </Popover>
    </>
  )
}

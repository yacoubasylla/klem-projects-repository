---
name: klem-mui-design-system
description: ALWAYS invoke when creating, refactoring, or styling React/MUI components for Cantine Connect. Enforces KLEM brand identity, 3-theme system, and MUI-only constraints.
---

# Guide Design System — KLEM Technologies & Services (MUI v9)

Tu es l'architecte frontend référent de **Cantine Connect**. Toute interface que tu génères doit respecter l'identité visuelle KLEM et les contraintes MUI strictes décrites ci-dessous. Le stack est **React 18 + Vite + Material UI v9**. Tailwind, CSS Modules, styled-components et tout CSS externe sont **interdits**.

---

## 1. Identité Visuelle KLEM

| Rôle | Couleur | Hex |
|------|---------|-----|
| Primaire | Bleu KLEM | `#1565C0` |
| Primaire clair | Bleu ciel | `#42A5F5` |
| Primaire foncé | Bleu marine | `#0D47A1` |
| Secondaire | Orange KLEM | `#FF6D00` |
| Secondaire clair | Orange vif | `#FF9E40` |
| Secondaire foncé | Orange brûlé | `#E65100` |
| Fond principal | Gris bleuté | `#F5F7FF` |
| Surface carte | Blanc pur | `#FFFFFF` |

Ces valeurs sont définies dans `client-frontend/src/theme/themes.js` via `createTheme()`. **Ne jamais les écrire en dur dans les composants** — utiliser `theme.palette.primary.main`, `theme.palette.secondary.main`, etc.

---

## 2. Système de Thèmes (3 variantes)

Le thème actif est persisté dans `localStorage` sous la clé `klem-theme` et géré par `ThemeModeContext` (`client-frontend/src/context/ThemeContext.jsx`).

### 🏢 Corporatif (`corporate`)
Dark mode premium. Sobre, focus données. Pour environnements de bureau / lumière tamisée.
```js
palette: {
  mode: 'dark',
  primary:    { main: '#60A5FA', dark: '#2563EB' },
  secondary:  { main: '#FB923C' },
  background: { default: '#0F172A', paper: '#1E293B' },
  text:       { primary: '#F1F5F9', secondary: '#94A3B8' },
}
// Cards  → backgroundColor '#1E293B', boxShadow '0 4px 24px rgba(0,0,0,0.40)'
// AppBar → backgroundColor '#0A1628', boxShadow 'none'
// Drawer → backgroundColor '#0F172A'
// MuiPaper → backgroundImage: 'none'  ← supprime le gradient MUI dark auto
```

### ✨ Moderne (`modern`) — thème par défaut
Light bling. Gradients, élévation au hover. Pour présentations et usage quotidien.
```js
palette: {
  primary:    { main: '#1565C0', light: '#42A5F5', dark: '#0D47A1' },
  secondary:  { main: '#FF6D00' },
  background: { default: '#F5F7FF', paper: '#FFFFFF' },
}
shape: { borderRadius: 16 }
// containedPrimary → gradient '#42A5F5 → #1565C0' + lift hover translateY(-2px)
// Cards  → borderRadius 20, hover translateY(-4px)
// AppBar → gradient '#1565C0 → #0D47A1'
// Tabs indicator → gradient '#1565C0 → #FF6D00'
```

### 🇨🇮 École Ivoirienne (`ivoirien`)
Chaleureux, lisible. Pour affichages publics et écrans d'entrée.
```js
palette: {
  primary:   { main: '#E65100', light: '#FF8A50', dark: '#BF360C' },
  secondary: { main: '#2E7D32', light: '#60AD5E' },
  background: { default: '#FFFDF7', paper: '#FFFFFF' },
}
// Cards → border '1px solid #FFE8D6', hover intensifie l'ombre orange
// AppBar → gradient '#FF7043 → #E65100'
```

---

## 3. Patterns de Composants MUI — Cantine Connect

### 3.1 Cards KPI (Dashboard)
```jsx
<Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
  <CardContent>
    <Stack direction="row" alignItems="center" justifyContent="space-between" mb={1}>
      <Typography variant="subtitle2" color="text.secondary">
        Titre KPI
      </Typography>
      <Avatar sx={{ bgcolor: 'primary.light', width: 40, height: 40 }}>
        <IconComponent fontSize="small" />
      </Avatar>
    </Stack>
    <Typography variant="h4" fontWeight={700} color="primary.main">
      {valeur}
    </Typography>
    <Typography variant="caption" color="text.secondary">
      Sous-information contextuelle
    </Typography>
  </CardContent>
</Card>
```

### 3.2 Tableaux de données paginés
```jsx
<TableContainer component={Paper} sx={{ borderRadius: 2 }}>
  <Table size="small">
    <TableHead>
      <TableRow>
        {/* Les styles head sont gérés par le thème — ne pas surcharger */}
        <TableCell>Colonne</TableCell>
      </TableRow>
    </TableHead>
    <TableBody>
      {rows.map((row) => (
        <TableRow key={row.id} hover>
          <TableCell>{row.data}</TableCell>
        </TableRow>
      ))}
    </TableBody>
  </Table>
</TableContainer>
<TablePagination
  component="div"
  count={totalElements}
  page={page}
  rowsPerPage={rowsPerPage}
  onPageChange={handleChangePage}
  onRowsPerPageChange={handleChangeRowsPerPage}
  rowsPerPageOptions={[10, 25, 50]}
  labelRowsPerPage="Lignes :"
  labelDisplayedRows={({ from, to, count }) => `${from}–${to} sur ${count}`}
/>
```

### 3.3 Formulaires à 3 onglets (zéro scroll)
Contrainte projet : tout formulaire de plus de 5 champs doit utiliser ce pattern.
```jsx
<Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
  <DialogTitle>Titre du formulaire</DialogTitle>
  <Tabs value={onglet} onChange={(_, v) => setOnglet(v)} sx={{ px: 3 }}>
    <Tab label="1 — Général" />
    <Tab label="2 — Spécifique" />
    <Tab label="3 — Contacts" />
  </Tabs>
  <Divider />
  <DialogContent sx={{ minHeight: 340, pt: 3 }}>
    {onglet === 0 && <OngletGeneral />}
    {onglet === 1 && <OngletSpecifique />}
    {onglet === 2 && <OngletContacts />}
  </DialogContent>
  <DialogActions sx={{ px: 3, pb: 2 }}>
    <Button onClick={onClose}>Annuler</Button>
    <Button variant="contained" onClick={handleSubmit}>Enregistrer</Button>
  </DialogActions>
</Dialog>
```

### 3.4 Chips de statut métier
```jsx
const STATUT_COLORS = {
  AUTORISE:          { color: 'success',  label: 'Autorisé' },
  GRACE:             { color: 'info',     label: 'Grâce' },
  EN_ATTENTE_PAIE:   { color: 'warning',  label: 'En attente' },
  SUSPENDU:          { color: 'error',    label: 'Suspendu' },
  ACCORDE:           { color: 'success',  label: '✓ Accordé' },
  REFUSE:            { color: 'error',    label: '✗ Refusé' },
  ACCEPTE:           { color: 'success',  label: 'Accepté' },
  EN_ATTENTE:        { color: 'warning',  label: 'En attente' },
}

<Chip
  label={STATUT_COLORS[statut]?.label ?? statut}
  color={STATUT_COLORS[statut]?.color ?? 'default'}
  size="small"
  sx={{ fontWeight: 600 }}
/>
```

### 3.5 Skeleton de chargement (jamais de spinner pleine page)
```jsx
// Pendant le chargement des données
{loading ? (
  <Stack spacing={2}>
    {[...Array(5)].map((_, i) => (
      <Skeleton key={i} variant="rectangular" height={52} sx={{ borderRadius: 1 }} />
    ))}
  </Stack>
) : (
  <DataComponent data={data} />
)}
```

### 3.6 Alertes de feedback CRUD
```jsx
{error && (
  <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
    {error}
  </Alert>
)}
{success && (
  <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
    {success}
  </Alert>
)}
```

---

## 4. Layout & Responsive (Breakpoints MUI)

| Breakpoint | Largeur | Comportement |
|------------|---------|-------------|
| `xs` | 0–600px | Colonne unique, Drawer overlay, boutons fullWidth |
| `sm` | 600–900px | 2 colonnes KPI, Drawer overlay |
| `md` | 900–1200px | 2 colonnes, Drawer overlay |
| `lg`+ | ≥ 1200px | 3–4 colonnes KPI, Drawer permanent 240px |

```jsx
// MainLayout — Drawer permanent/temporaire selon breakpoint lg
const drawerPermanent = useMediaQuery(theme.breakpoints.up('lg'));

// Grille responsive des KPI cards
<Grid container spacing={3}>
  <Grid item xs={12} sm={6} lg={3}><KpiCard /></Grid>
  <Grid item xs={12} sm={6} lg={3}><KpiCard /></Grid>
  <Grid item xs={12} sm={6} lg={3}><KpiCard /></Grid>
  <Grid item xs={12} sm={6} lg={3}><KpiCard /></Grid>
</Grid>
```

---

## 5. Règles d'Architecture Frontend

### Custom Hooks — séparation obligatoire
```
pages/          → Rendu uniquement, zéro logique métier
hooks/          → useXxx() : état + appels API + gestion erreurs
services/       → appelXxxService() : axios uniquement, pas d'état
components/     → composants réutilisables sans dépendance aux services
```

### Appels API (axios via apiClient)
```js
// ✅ Correct
const { data, loading, error } = useEtablissements();

// ❌ Interdit — appel direct dans un composant
const res = await axios.get('/api/v1/etablissements');
```

### Gestion du token JWT
Le token est stocké sous la clé `cc_token` dans `localStorage`. L'intercepteur `apiClient.js` l'injecte automatiquement dans chaque requête (`Authorization: Bearer`). Ne jamais accéder au token hors de `AuthContext` ou `apiClient`.

---

## 6. Interdictions formelles

| Interdit | Alternative MUI |
|----------|----------------|
| Fichiers `.css` / `.scss` | `sx={{ }}` ou `ThemeProvider` |
| `styled-components` | `MUI styled()` si absolument nécessaire |
| Couleurs hardcodées (`#1565C0`) dans les JSX | `theme.palette.primary.main` |
| `useEffect` brut pour fetch | Custom hook + service |
| `any` TypeScript (si migration TS) | Interface explicite |
| Hauteurs fixes en px sur conteneurs | `flex: 1`, `minHeight`, `maxHeight` |
| Overflow horizontal visible | `overflowX: 'auto'` sur le conteneur |
| Emoji excessifs | Max 1 par titre de section, 1 par nav item |
| Spinner pleine page | `Skeleton` MUI dimensionné au contenu |
| Dialog sans bouton d'annulation | Toujours proposer une sortie à l'utilisateur |

---

## 7. Emojis par module (référence sidebar)

| Module | Emoji | Icône MUI |
|--------|-------|-----------|
| Dashboard | 📊 | `DashboardIcon` |
| Établissements | 🏫 | `SchoolIcon` |
| Élèves | 👨‍🎓 | `PeopleIcon` |
| Paiements | 💳 | `PaymentsIcon` |
| Scan Réfectoire | 📷 | `QrCodeScannerIcon` |
| Historique | 📋 | `HistoryIcon` |
| Utilisateurs | 👥 | `ManageAccountsIcon` |
| Configuration | ⚙️ | `TuneIcon` |
| À Propos | ℹ️ | `InfoOutlinedIcon` |
| Thème | 🎨 | `PaletteIcon` |

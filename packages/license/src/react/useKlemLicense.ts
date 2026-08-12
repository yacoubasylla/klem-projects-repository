// packages/license/src/react/useKlemLicense.ts
import { useContext } from 'react';
import { KlemLicenseContext } from './KlemLicenseContext';
import type { KlemLicenseResult } from '../types';

/**
 * Lit le statut de licence KLEM courant depuis le `KlemProvider` englobant. Ne bloque jamais :
 * un composant consommateur peut choisir d'adapter son affichage (ex: badge discret) mais ne
 * doit pas empêcher le rendu sur la base de ce statut — voir `packages/license/CLAUDE.md`.
 */
export function useKlemLicense(): KlemLicenseResult {
  return useContext(KlemLicenseContext);
}

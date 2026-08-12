// packages/license/src/react/KlemProvider.tsx
import { useEffect, useState, type ReactNode } from 'react';
import { validateLicense } from '../validator';
import type { KlemLicenseResult } from '../types';
import { KlemLicenseContext, KLEM_LICENSE_PENDING } from './KlemLicenseContext';

const STATUS_MESSAGES: Record<KlemLicenseResult['status'], string> = {
  valid: 'Licence KLEM valide.',
  missing: 'Licence KLEM absente (KTS_LICENSE_KEY non fournie).',
  malformed: 'Licence KLEM illisible (format de clé invalide).',
  'invalid-signature': 'Licence KLEM non valide (signature invalide).',
  expired: 'Licence KLEM expirée.',
  'wrong-app': "Licence KLEM valide pour une autre application (appId ne correspond pas).",
};

interface KlemProviderProps {
  licenseKey?: string;
  appId: string;
  children: ReactNode;
}

/**
 * Fournit le statut de licence KLEM à l'application via le contexte `useKlemLicense`.
 *
 * Mode « avertissement seul » (décision KLEM du 2026-08-12) : ce composant rend toujours
 * `children`, quel que soit le statut de la licence. En cas de licence absente/invalide/expirée,
 * il journalise un avertissement (`console.warn`) et, en développement uniquement, affiche une
 * bannière non bloquante en haut de l'écran. Aucun blocage de rendu n'est effectué — voir
 * `packages/license/CLAUDE.md`.
 */
export function KlemProvider({ licenseKey, appId, children }: KlemProviderProps) {
  const [result, setResult] = useState<KlemLicenseResult>(KLEM_LICENSE_PENDING);

  useEffect(() => {
    let cancelled = false;

    validateLicense(licenseKey, appId).then((validated) => {
      if (cancelled) return;
      setResult(validated);
      if (validated.status !== 'valid') {
        console.warn(`[KLEM License] ${STATUS_MESSAGES[validated.status]}`, { appId, status: validated.status });
      }
    });

    return () => {
      cancelled = true;
    };
  }, [licenseKey, appId]);

  const isDev = typeof import.meta !== 'undefined' && Boolean((import.meta as ImportMeta & { env?: { DEV?: boolean } }).env?.DEV);
  const showDevBanner = isDev && result.status !== 'valid';

  return (
    <KlemLicenseContext.Provider value={result}>
      {showDevBanner && (
        <div
          style={{
            padding: '6px 12px',
            background: '#7a1f1f',
            color: '#fff',
            fontSize: 12,
            fontFamily: 'monospace',
            textAlign: 'center',
          }}
        >
          KLEM UNLICENSED COMPONENT — {STATUS_MESSAGES[result.status]}
        </div>
      )}
      {children}
    </KlemLicenseContext.Provider>
  );
}

// packages/license/src/types.ts

/**
 * Contenu signé d'une clé de licence KLEM (`KTS_LICENSE_KEY`).
 */
export interface KlemLicensePayload {
  clientId: string;
  appId: string;
  issuedAt: string;
  expiresAt: string;
}

/**
 * Statut de validation d'une clé de licence. `'valid'` est le seul statut nominal ; tous les
 * autres décrivent une raison de non-conformité (mais n'entraînent jamais de blocage — voir
 * `KlemProvider`).
 */
export type KlemLicenseStatus =
  | 'valid'
  | 'missing'
  | 'malformed'
  | 'invalid-signature'
  | 'expired'
  | 'wrong-app';

/**
 * Résultat de la validation d'une clé de licence pour une application donnée.
 */
export interface KlemLicenseResult {
  status: KlemLicenseStatus;
  payload: KlemLicensePayload | null;
}

/**
 * Props de configuration attendues par `KlemProvider`.
 */
export interface KlemLicenseConfig {
  licenseKey?: string;
  appId: string;
}

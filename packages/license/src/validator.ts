// packages/license/src/validator.ts
import type { KlemLicensePayload, KlemLicenseResult } from './types';

/**
 * Clé publique ECDSA P-256 de démonstration (générée par `scripts/generate-demo-license.mjs`).
 * ⚠️ À remplacer par la vraie clé publique KLEM avant tout usage en production — la clé privée
 * correspondante n'existe que sur le poste qui a produit ce JWK et n'est pas dans ce dépôt.
 */
const KLEM_DEMO_PUBLIC_KEY_JWK: JsonWebKey = {
  key_ops: ['verify'],
  ext: true,
  kty: 'EC',
  x: '3ZgDwTK3Mh_phvQg-bb0Nd1NyRROUnCOtRh2Ph_jSMA',
  y: 'ffreewu2j3Il-W2hNDjpXx01H6S2q7cyHzSrE2s83M4',
  crv: 'P-256',
};

let cachedPublicKey: Promise<CryptoKey> | null = null;

function importKlemPublicKey(): Promise<CryptoKey> {
  cachedPublicKey ??= crypto.subtle.importKey(
    'jwk',
    KLEM_DEMO_PUBLIC_KEY_JWK,
    { name: 'ECDSA', namedCurve: 'P-256' },
    true,
    ['verify'],
  );
  return cachedPublicKey;
}

function base64urlToBytes(value: string): Uint8Array {
  const padded = value.replace(/-/g, '+').replace(/_/g, '/').padEnd(Math.ceil(value.length / 4) * 4, '=');
  const binary = atob(padded);
  return Uint8Array.from(binary, (c) => c.charCodeAt(0));
}

interface ParsedLicenseKey {
  payload: KlemLicensePayload;
  payloadBytes: Uint8Array;
  signatureBytes: Uint8Array;
}

function parseLicenseKey(licenseKey: string): ParsedLicenseKey | null {
  const parts = licenseKey.split('.');
  if (parts.length !== 2) return null;

  const [payloadSegment, signatureSegment] = parts;
  try {
    const payloadBytes = base64urlToBytes(payloadSegment);
    const signatureBytes = base64urlToBytes(signatureSegment);
    const payload = JSON.parse(new TextDecoder().decode(payloadBytes)) as KlemLicensePayload;
    if (!payload.clientId || !payload.appId || !payload.expiresAt) return null;
    return { payload, payloadBytes, signatureBytes };
  } catch {
    return null;
  }
}

async function verifyLicenseSignature(payloadBytes: Uint8Array, signatureBytes: Uint8Array): Promise<boolean> {
  const publicKey = await importKlemPublicKey();
  return crypto.subtle.verify(
    { name: 'ECDSA', hash: 'SHA-256' },
    publicKey,
    signatureBytes as BufferSource,
    payloadBytes as BufferSource,
  );
}

/**
 * Valide une clé de licence KLEM pour une application donnée. Ne lève jamais d'exception : tout
 * problème (absence, format invalide, signature invalide, expiration, mauvaise application) est
 * renvoyé comme un statut — c'est à l'appelant (`KlemProvider`) de décider du comportement, qui
 * est aujourd'hui « avertissement seul » (jamais de blocage de rendu).
 */
export async function validateLicense(
  licenseKey: string | undefined,
  appId: string,
): Promise<KlemLicenseResult> {
  if (!licenseKey) {
    return { status: 'missing', payload: null };
  }

  const parsed = parseLicenseKey(licenseKey);
  if (!parsed) {
    return { status: 'malformed', payload: null };
  }

  const signatureValid = await verifyLicenseSignature(parsed.payloadBytes, parsed.signatureBytes);
  if (!signatureValid) {
    return { status: 'invalid-signature', payload: null };
  }

  if (parsed.payload.appId !== appId) {
    return { status: 'wrong-app', payload: parsed.payload };
  }

  if (new Date(parsed.payload.expiresAt).getTime() < Date.now()) {
    return { status: 'expired', payload: parsed.payload };
  }

  return { status: 'valid', payload: parsed.payload };
}

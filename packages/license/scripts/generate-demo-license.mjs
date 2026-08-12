#!/usr/bin/env node
// Génère une paire de clés ECDSA P-256 et des jetons KTS_LICENSE_KEY de démonstration pour
// tester @klem/license sans dépendre d'une vraie clé privée KLEM.
// ⚠️ La clé privée générée ici n'est JAMAIS écrite sur disque ni committée : ce script sert de
// modèle pour signer offline (sur un poste sécurisé), seuls la clé publique et les jetons signés
// imprimés en sortie doivent être distribués/collés dans le code.

import { webcrypto } from 'node:crypto';

const { subtle } = webcrypto;

function base64url(bytes) {
  return Buffer.from(bytes).toString('base64url');
}

async function signPayload(payload, privateKey) {
  const payloadBytes = new TextEncoder().encode(JSON.stringify(payload));
  const signature = await subtle.sign({ name: 'ECDSA', hash: 'SHA-256' }, privateKey, payloadBytes);
  return `${base64url(payloadBytes)}.${base64url(new Uint8Array(signature))}`;
}

const { publicKey, privateKey } = await subtle.generateKey(
  { name: 'ECDSA', namedCurve: 'P-256' },
  true,
  ['sign', 'verify'],
);

const publicJwk = await subtle.exportKey('jwk', publicKey);

const now = new Date();
const in1Year = new Date(now.getTime() + 365 * 24 * 60 * 60 * 1000);
const yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);

const validKey = await signPayload(
  { clientId: 'KLEM-DEMO', appId: 'parcauto', issuedAt: now.toISOString(), expiresAt: in1Year.toISOString() },
  privateKey,
);

const expiredKey = await signPayload(
  { clientId: 'KLEM-DEMO', appId: 'parcauto', issuedAt: yesterday.toISOString(), expiresAt: yesterday.toISOString() },
  privateKey,
);

console.log('--- Clé publique (KLEM_DEMO_PUBLIC_KEY_JWK dans validator.ts) ---');
console.log(JSON.stringify(publicJwk, null, 2));
console.log('\n--- Licence valide de démo (appId=parcauto, 1 an) ---');
console.log(validKey);
console.log('\n--- Licence expirée de démo (appId=parcauto) ---');
console.log(expiredKey);

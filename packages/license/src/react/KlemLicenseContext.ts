// packages/license/src/react/KlemLicenseContext.ts
import { createContext } from 'react';
import type { KlemLicenseResult } from '../types';

export const KLEM_LICENSE_PENDING: KlemLicenseResult = { status: 'missing', payload: null };

export const KlemLicenseContext = createContext<KlemLicenseResult>(KLEM_LICENSE_PENDING);

// packages/utils/src/index.ts

/**
 * Formate une date pour l'affichage dans les applications KLEM
 */
export const formatDate = (date: Date): string => {
  return new Intl.DateTimeFormat('fr-FR').format(date);
};

/**
 * Log sécurisé pour nos agents IA
 */
export const logKlemAction = (action: string, metadata?: any) => {
  console.log(`[KLEM LOG] ${new Date().toISOString()} - ${action}`, metadata || '');
};
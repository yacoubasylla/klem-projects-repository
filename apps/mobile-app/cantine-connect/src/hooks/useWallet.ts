import { useQuery } from '@tanstack/react-query'
import { parentService } from '../services/parentService'

// "Wallet" = les enfants du parent connecté, chacun avec son solde et son
// qrCodeToken réels (server-backend/EnfantDTO) — pas de nouvelle notion de
// portefeuille côté serveur, juste une lecture de /parents/moi.
export function useWallet() {
  return useQuery({
    queryKey: ['parent-moi'],
    queryFn: parentService.getMoi,
    staleTime: 30_000,
  })
}

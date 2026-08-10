import { View, Text, FlatList, ActivityIndicator } from 'react-native'
import { useQuery } from '@tanstack/react-query'
import { paiementService, type PaiementDTO } from '../../src/services/paiementService'

const STATUT_LABEL: Record<string, string> = {
  ACCEPTE: 'Accepté',
  EN_ATTENTE: 'En attente',
  REFUSE: 'Refusé',
  ANNULE: 'Annulé',
}

function PaiementRow({ item }: { item: PaiementDTO }) {
  return (
    <View className="bg-white rounded-xl border border-neutral-200 p-3 mb-2 flex-row justify-between">
      <View>
        <Text className="font-medium text-neutral-900">{item.eleveNomComplet}</Text>
        <Text className="text-xs text-neutral-500">
          {item.operateur} · {new Date(item.dateCreation).toLocaleDateString('fr-FR')}
        </Text>
      </View>
      <View className="items-end">
        <Text className="font-semibold text-neutral-900">
          {Number(item.montant).toLocaleString('fr-FR')} {item.devise}
        </Text>
        <Text className="text-xs text-neutral-500">{STATUT_LABEL[item.statut] ?? item.statut}</Text>
      </View>
    </View>
  )
}

export default function HistoryScreen() {
  const { data, isLoading, error } = useQuery({
    queryKey: ['paiements'],
    queryFn: () => paiementService.lister({ size: 50 }),
  })

  return (
    <View className="flex-1 bg-cream p-4">
      <Text className="text-2xl font-bold text-neutral-900 mb-4">Historique des paiements</Text>

      {isLoading && <ActivityIndicator />}
      {error && <Text className="text-red-700">{error.message}</Text>}

      <FlatList
        data={data?.content ?? []}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => <PaiementRow item={item} />}
        ListEmptyComponent={!isLoading ? <Text className="text-neutral-500">Aucun paiement pour le moment.</Text> : null}
      />
    </View>
  )
}

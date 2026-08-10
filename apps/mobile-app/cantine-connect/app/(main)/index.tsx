import { useState } from 'react'
import { View, Text, ScrollView, Pressable, ActivityIndicator } from 'react-native'
import QRCode from 'react-native-qrcode-svg'
import { useWallet } from '../../src/hooks/useWallet'
import type { EnfantDTO } from '../../src/services/parentService'

function EnfantCard({ enfant }: { enfant: EnfantDTO }) {
  const [showQr, setShowQr] = useState(false)

  return (
    <View className="bg-white rounded-2xl border border-neutral-200 p-4 mb-3">
      <Text className="text-lg font-semibold text-neutral-900">{enfant.prenom} {enfant.nom}</Text>
      <Text className="text-neutral-500 mb-3">
        {enfant.matricule} {enfant.classeLibelle ? `· ${enfant.classeLibelle}` : ''}
      </Text>

      <View className="flex-row justify-between items-center mb-3">
        <Text className="text-neutral-500">Solde</Text>
        <Text className="text-2xl font-bold text-secondary">
          {Number(enfant.solde ?? 0).toLocaleString('fr-FR')} FCFA
        </Text>
      </View>

      {enfant.qrCodeToken ? (
        <>
          <Pressable
            onPress={() => setShowQr((v) => !v)}
            className="bg-primary/10 rounded-xl py-2.5 items-center"
          >
            <Text className="text-primary font-semibold">
              {showQr ? 'Masquer le Pass QR' : 'Afficher le Pass QR'}
            </Text>
          </Pressable>
          {showQr && (
            <View className="items-center mt-4 pb-2">
              <QRCode value={enfant.qrCodeToken} size={180} />
              <Text className="text-xs text-neutral-400 mt-2">
                Présenter ce QR Code au réfectoire pour le contrôle d'accès.
              </Text>
            </View>
          )}
        </>
      ) : (
        <Text className="text-xs text-neutral-400">Pass QR indisponible pour cet élève.</Text>
      )}
    </View>
  )
}

export default function DashboardScreen() {
  const { data, isLoading, error } = useWallet()

  return (
    <ScrollView className="flex-1 bg-cream" contentContainerClassName="p-4">
      <Text className="text-2xl font-bold text-neutral-900 mb-4">Mes enfants</Text>

      {isLoading && <ActivityIndicator />}

      {error && (
        <View className="bg-red-50 border border-red-200 rounded-xl p-3 mb-3">
          <Text className="text-red-700">{error.message}</Text>
        </View>
      )}

      {data?.enfants.length === 0 && (
        <Text className="text-neutral-500">Aucun enfant associé à ce compte pour le moment.</Text>
      )}

      {data?.enfants.map((enfant) => <EnfantCard key={enfant.id} enfant={enfant} />)}
    </ScrollView>
  )
}

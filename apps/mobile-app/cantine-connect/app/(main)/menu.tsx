import { View, Text } from 'react-native'

/**
 * Placeholder honnête : il n'existe aujourd'hui aucune fonctionnalité "menus du
 * jour" côté server-backend (aucune entité/endpoint Menu). Ne pas câbler cet
 * écran sur un endpoint imaginaire — à construire seulement si cette
 * fonctionnalité est un jour décidée côté backend.
 */
export default function MenuScreen() {
  return (
    <View className="flex-1 bg-cream items-center justify-center px-8">
      <Text className="text-xl font-semibold text-neutral-900 mb-2 text-center">
        Menus du jour — à venir
      </Text>
      <Text className="text-neutral-500 text-center">
        Cette fonctionnalité n'existe pas encore côté serveur. Elle sera activée ici dès
        qu'elle sera disponible.
      </Text>
    </View>
  )
}

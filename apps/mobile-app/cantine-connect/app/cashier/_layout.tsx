import { Redirect, Stack } from 'expo-router'
import { ActivityIndicator, View } from 'react-native'
import { useAuth } from '../../src/context/AuthContext'

// Même règle que le backend (@PreAuthorize("!hasRole('PARENT')") sur /scan/{token}) :
// un compte PARENT n'a pas accès à l'espace caissier.
export default function CashierLayout() {
  const { isAuthenticated, isLoading, role } = useAuth()

  if (isLoading) {
    return (
      <View className="flex-1 items-center justify-center bg-black">
        <ActivityIndicator color="#fff" />
      </View>
    )
  }

  if (!isAuthenticated) return <Redirect href="/(auth)/login" />
  if (role === 'PARENT') return <Redirect href="/(main)" />

  return <Stack screenOptions={{ headerShown: false }} />
}

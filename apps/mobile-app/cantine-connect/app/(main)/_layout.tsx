import { Redirect, Tabs } from 'expo-router'
import { ActivityIndicator, View } from 'react-native'
import { useAuth } from '../../src/context/AuthContext'

export default function MainLayout() {
  const { isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return (
      <View className="flex-1 items-center justify-center bg-cream">
        <ActivityIndicator />
      </View>
    )
  }

  if (!isAuthenticated) return <Redirect href="/(auth)/login" />

  return (
    <Tabs screenOptions={{ tabBarActiveTintColor: '#E8720C' }}>
      <Tabs.Screen name="index" options={{ title: 'Accueil' }} />
      <Tabs.Screen name="history" options={{ title: 'Paiements' }} />
      <Tabs.Screen name="menu" options={{ title: 'Menu' }} />
    </Tabs>
  )
}

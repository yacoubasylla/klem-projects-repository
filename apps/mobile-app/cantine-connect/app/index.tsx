import { ActivityIndicator, View } from 'react-native'
import { Redirect } from 'expo-router'
import { useAuth } from '../src/context/AuthContext'

export default function RootIndex() {
  const { isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return (
      <View className="flex-1 items-center justify-center bg-cream">
        <ActivityIndicator />
      </View>
    )
  }

  return <Redirect href={isAuthenticated ? '/(main)' : '/(auth)/login'} />
}

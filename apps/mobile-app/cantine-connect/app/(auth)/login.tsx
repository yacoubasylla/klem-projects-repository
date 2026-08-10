import { useState } from 'react'
import { View, Text, TextInput, Pressable, ActivityIndicator } from 'react-native'
import { router } from 'expo-router'
import { useAuth } from '../../src/context/AuthContext'

export default function LoginScreen() {
  const { login } = useAuth()
  const [email, setEmail] = useState('')
  const [motDePasse, setMotDePasse] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async () => {
    if (!email || !motDePasse) { setError('Remplissez tous les champs'); return }
    setLoading(true); setError(null)
    try {
      await login(email, motDePasse)
      router.replace('/(main)')
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Email ou mot de passe incorrect')
    } finally {
      setLoading(false)
    }
  }

  return (
    <View className="flex-1 bg-cream justify-center px-6">
      <Text className="text-3xl font-bold text-neutral-900 mb-1">Content de vous revoir</Text>
      <Text className="text-neutral-500 mb-8">Connectez-vous à votre espace Cantine Connect.</Text>

      {error && (
        <View className="bg-red-50 border border-red-200 rounded-xl p-3 mb-4">
          <Text className="text-red-700">{error}</Text>
        </View>
      )}

      <Text className="text-sm font-medium text-neutral-700 mb-1">Adresse email</Text>
      <TextInput
        value={email}
        onChangeText={setEmail}
        autoCapitalize="none"
        keyboardType="email-address"
        className="border border-neutral-300 rounded-xl px-4 py-3 mb-4 bg-white"
      />

      <Text className="text-sm font-medium text-neutral-700 mb-1">Mot de passe</Text>
      <TextInput
        value={motDePasse}
        onChangeText={setMotDePasse}
        secureTextEntry
        className="border border-neutral-300 rounded-xl px-4 py-3 mb-6 bg-white"
      />

      <Pressable
        onPress={handleSubmit}
        disabled={loading}
        className="bg-primary rounded-xl py-3.5 items-center"
      >
        {loading ? <ActivityIndicator color="#fff" /> : <Text className="text-white font-semibold">Se connecter</Text>}
      </Pressable>
    </View>
  )
}

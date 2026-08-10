import { useState, useRef } from 'react'
import { View, Text, Pressable } from 'react-native'
import { CameraView, useCameraPermissions, type BarcodeScanningResult } from 'expo-camera'
import { scanService, type ScanResultDTO } from '../../src/services/scanService'

export default function CashierScanScreen() {
  const [permission, requestPermission] = useCameraPermissions()
  const [result, setResult] = useState<ScanResultDTO | null>(null)
  const [scanning, setScanning] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const lockRef = useRef(false)

  const handleScan = async ({ data }: BarcodeScanningResult) => {
    if (lockRef.current) return
    lockRef.current = true
    setScanning(true); setError(null)
    try {
      const res = await scanService.scanner(data)
      setResult(res)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Erreur de scan')
      setResult(null)
    } finally {
      setScanning(false)
      // Anti-doublon 2s avant d'autoriser un nouveau scan (même détecteur QR
      // sinon redéclenché à chaque frame tant que le code reste dans le champ).
      setTimeout(() => { lockRef.current = false }, 2000)
    }
  }

  if (!permission) return <View className="flex-1 bg-black" />

  if (!permission.granted) {
    return (
      <View className="flex-1 items-center justify-center bg-black px-8">
        <Text className="text-white text-center mb-4">
          L'accès à la caméra est nécessaire pour scanner les QR codes repas.
        </Text>
        <Pressable onPress={requestPermission} className="bg-primary rounded-xl px-5 py-3">
          <Text className="text-white font-semibold">Autoriser la caméra</Text>
        </Pressable>
      </View>
    )
  }

  return (
    <View className="flex-1 bg-black">
      <CameraView
        className="flex-1"
        barcodeScannerSettings={{ barcodeTypes: ['qr'] }}
        onBarcodeScanned={scanning ? undefined : handleScan}
      />

      {result && (
        <View
          className={`absolute bottom-0 left-0 right-0 p-5 ${
            result.resultat === 'ACCORDE' ? 'bg-secondary' : 'bg-red-600'
          }`}
        >
          <Text className="text-white text-lg font-bold">{result.acces}</Text>
          <Text className="text-white">{result.nomComplet} — {result.matricule}</Text>
          <Text className="text-white/80 text-sm">{result.classeNom}</Text>
          {result.motifRefus && <Text className="text-white/90 mt-1">{result.motifRefus}</Text>}
        </View>
      )}

      {error && (
        <View className="absolute bottom-0 left-0 right-0 p-5 bg-red-600">
          <Text className="text-white font-semibold">{error}</Text>
        </View>
      )}
    </View>
  )
}

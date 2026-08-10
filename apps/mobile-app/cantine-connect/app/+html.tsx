import { ScrollViewStyleReset } from 'expo-router/html'
import type { PropsWithChildren } from 'react'

// Gabarit HTML utilisé uniquement par le build statique PWA (`expo export -p web`).
export default function Root({ children }: PropsWithChildren) {
  return (
    <html lang="fr">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
        <ScrollViewStyleReset />
      </head>
      <body>{children}</body>
    </html>
  )
}

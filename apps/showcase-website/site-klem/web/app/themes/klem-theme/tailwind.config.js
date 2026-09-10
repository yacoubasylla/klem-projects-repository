/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './*.php',
    './template-parts/**/*.php',
  ],
  theme: {
    extend: {
      colors: {
        'klem-blue':   '#13294B',   // Marine KLEM — couleur d'autorité (fonds, titres)
        'klem-steel':  '#1B446A',   // Bleu acier KLEM — teinte intermédiaire (dégradés, survols)
        'klem-red':    '#E42313',   // Rouge KLEM officiel (dégradé #F7715A → #A3140B)
        'klem-orange': '#E42313',   // Alias historique → Rouge KLEM (aucun orange dans la charte)
        'klem-slate':  '#6B7280',   // Textes secondaires
      },
      fontFamily: {
        // Montserrat = typo de marque (kit 2026). Archivo/Questrial conservés en repli
        // pour ne pas casser le rendu tant que Montserrat n'est pas chargée.
        logo: ['Montserrat', 'Archivo', 'sans-serif'],
        heading: ['Montserrat', 'Questrial', 'sans-serif'],
      },
      maxWidth: {
        // Largeur du conteneur de contenu du site (header, footer, sections d'accueil).
        // Les fonds de section restent pleine largeur ; seul le contenu est centré.
        site: '1200px',
      },
    },
  },
  plugins: [require('@tailwindcss/typography')],
};

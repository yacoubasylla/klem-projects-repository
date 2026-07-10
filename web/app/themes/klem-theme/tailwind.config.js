/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './*.php',
    './template-parts/**/*.php',
  ],
  theme: {
    extend: {
      colors: {
        'klem-blue':   '#13294B',   // Bleu KLEM — charte logo Chevron
        'klem-orange': '#E42313',   // Alias → Rouge KLEM (aucun orange dans la charte)
        'klem-red':    '#E42313',   // Rouge KLEM officiel
        'klem-slate':  '#6B7280',   // Textes secondaires
      },
      fontFamily: {
        logo: ['Archivo', 'sans-serif'],
      },
    },
  },
  plugins: [require('@tailwindcss/typography')],
};

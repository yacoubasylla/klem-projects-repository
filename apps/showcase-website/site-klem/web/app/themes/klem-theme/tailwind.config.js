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
        'klem-orange': '#D6301F',   // Alias → Rouge KLEM (aucun orange dans la charte)
        'klem-red':    '#D6301F',   // Rouge KLEM officiel — assourdi (ex: #E42313, jugé trop vif)
        'klem-slate':  '#6B7280',   // Textes secondaires
      },
      fontFamily: {
        logo: ['Archivo', 'sans-serif'],
        heading: ['Questrial', 'sans-serif'],
      },
    },
  },
  plugins: [require('@tailwindcss/typography')],
};

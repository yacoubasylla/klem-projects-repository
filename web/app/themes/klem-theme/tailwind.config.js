/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './*.php',
    './template-parts/**/*.php',
  ],
  theme: {
    extend: {
      colors: {
        'klem-blue':   '#271C70',   // Bleu KLEM officiel (indigo profond)
        'klem-orange': '#E42313',   // Alias → Rouge KLEM (aucun orange dans la charte)
        'klem-red':    '#E42313',   // Rouge KLEM officiel
        'klem-slate':  '#6B7280',   // Textes secondaires (gris neutre)
      },
      fontFamily: {
        // Verdana est une police système — aucun import Google Fonts requis
        logo: ['Verdana', 'Tahoma', "'DejaVu Sans'", 'Geneva', 'sans-serif'],
      },
    },
  },
  plugins: [],
};

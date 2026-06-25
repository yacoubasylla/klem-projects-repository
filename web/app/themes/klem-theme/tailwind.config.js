/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './*.php',
    './template-parts/**/*.php',
  ],
  theme: {
    extend: {
      colors: {
        'klem-blue':   '#16212E',   // encre
        'klem-orange': '#FF6500',   // CTA / boutons
        'klem-red':    '#E2241B',   // symbole KlemMark
        'klem-slate':  '#5A6B7B',   // signature / textes secondaires
      },
      fontFamily: {
        // Verdana est une police système — aucun import Google Fonts requis
        logo: ['Verdana', 'Tahoma', "'DejaVu Sans'", 'Geneva', 'sans-serif'],
      },
    },
  },
  plugins: [],
};

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./app/**/*.{js,jsx,ts,tsx}', './src/**/*.{js,jsx,ts,tsx}'],
  presets: [require('nativewind/preset')],
  theme: {
    extend: {
      colors: {
        // Reprend la palette du thème "Premium" du web (client-frontend/src/theme/themes.js)
        // pour une cohérence visuelle entre le web et le mobile.
        primary: { DEFAULT: '#E8720C', light: '#FFA94D', dark: '#B85400' },
        secondary: { DEFAULT: '#0E7C4A', light: '#3FA873', dark: '#075A34' },
        cream: '#FFF9F2',
      },
    },
  },
  plugins: [],
}

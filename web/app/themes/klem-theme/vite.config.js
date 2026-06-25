import { defineConfig } from 'vite';

export default defineConfig({
  appType: 'custom',
  server: {
    host: 'localhost',
    port: 5173,
    cors: true,
    origin: 'http://localhost:5173',
  },
  build: {
    outDir: 'dist',
    manifest: true,
    rollupOptions: {
      input: {
        main: 'src/main.js',
      },
    },
  },
});

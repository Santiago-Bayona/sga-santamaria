import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // En desarrollo, sin VITE_API_URL, /api se reenvía al backend local.
  server: {
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
  // mapbox-gl va en su propio archivo (~1.8 MB) y solo se descarga en las páginas con mapa
  build: { chunkSizeWarningLimit: 2000 },
})

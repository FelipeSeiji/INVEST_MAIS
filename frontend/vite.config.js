import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false,
      },
      '/auth': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false,
      }
    },
  },
})

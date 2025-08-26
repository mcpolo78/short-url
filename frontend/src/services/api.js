import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor for adding auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for handling errors
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      // Ne pas rediriger automatiquement, laisser le composant gérer
      console.warn('Authentication required - redirect to login if needed')
    }
    return Promise.reject(error)
  }
)

export const linkService = {
  // Create a new short link
  createLink: async (linkData) => {
    const response = await api.post('/links', linkData)
    return response.data
  },

  // Get user's links with pagination
  getLinks: async (page = 0, size = 10, sortBy = 'createdAt', sortDir = 'desc') => {
    const response = await api.get('/links', {
      params: { page, size, sortBy, sortDir }
    })
    return response.data
  },

  // Get a specific link by ID
  getLink: async (id) => {
    const response = await api.get(`/links/${id}`)
    return response.data
  },

  // Update a link
  updateLink: async (id, linkData) => {
    const response = await api.put(`/links/${id}`, linkData)
    return response.data
  },

  // Delete a link
  deleteLink: async (id) => {
    await api.delete(`/links/${id}`)
  },

  // Toggle link active status
  toggleLink: async (id) => {
    await api.patch(`/links/${id}/toggle`)
  },

  // Search links
  searchLinks: async (query) => {
    const response = await api.get('/links/search', {
      params: { q: query }
    })
    return response.data
  },

  // Get top performing links
  getTopLinks: async (limit = 5) => {
    const response = await api.get('/links/top', {
      params: { limit }
    })
    return response.data
  },

  // Get recent links
  getRecentLinks: async (days = 7) => {
    const response = await api.get('/links/recent', {
      params: { days }
    })
    return response.data
  },

  // Get link analytics
  getLinkAnalytics: async (id, days = 30) => {
    const response = await api.get(`/links/${id}/analytics`, {
      params: { days }
    })
    return response.data
  },

  // Bulk create links
  bulkCreateLinks: async (links) => {
    const response = await api.post('/links/bulk', links)
    return response.data
  },
}

export const dashboardService = {
  // Get dashboard statistics
  getStats: async () => {
    const response = await api.get('/dashboard/stats')
    return response.data
  },
}

export const qrService = {
  // Get QR code URL for a link
  getQRCodeUrl: (linkId) => {
    return `${API_BASE_URL}/qr/${linkId}`
  },

  // Get QR code download URL
  getQRCodeDownloadUrl: (linkId) => {
    return `${API_BASE_URL}/qr/${linkId}/download`
  },
}

export default api

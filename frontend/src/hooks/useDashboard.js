import { useState, useEffect } from 'react'
import { dashboardService } from '../services/api'
import toast from 'react-hot-toast'

export const useDashboard = () => {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const fetchStats = async () => {
    try {
      setLoading(true)
      const data = await dashboardService.getStats()
      setStats(data)
      setError(null)
    } catch (err) {
      console.error('Dashboard stats error:', err)
      
      setError(err.message || 'Failed to fetch dashboard stats')
      toast.error('Erreur lors du chargement des statistiques')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchStats()
  }, [])

  return { stats, loading, error, refetch: fetchStats }
}

import { useState, useEffect } from 'react'
import { linkService } from '../services/api'
import toast from 'react-hot-toast'

export const useLinks = (page = 0, size = 10) => {
  const [links, setLinks] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  const fetchLinks = async () => {
    try {
      setLoading(true)
      const data = await linkService.getLinks(page, size)
      setLinks(data.content || [])
      setTotalPages(data.totalPages || 0)
      setTotalElements(data.totalElements || 0)
      setError(null)
    } catch (err) {
      setError(err.message)
      toast.error('Failed to fetch links')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchLinks()
  }, [page, size])

  const createLink = async (linkData) => {
    try {
      const newLink = await linkService.createLink(linkData)
      setLinks(prev => [newLink, ...prev.slice(0, size - 1)])
      setTotalElements(prev => prev + 1)
      toast.success('Link created successfully!')
      return newLink
    } catch (err) {
      toast.error('Failed to create link')
      throw err
    }
  }

  const updateLink = async (id, linkData) => {
    try {
      const updatedLink = await linkService.updateLink(id, linkData)
      setLinks(prev => prev.map(link => 
        link.id === id ? updatedLink : link
      ))
      toast.success('Link updated successfully!')
      return updatedLink
    } catch (err) {
      toast.error('Failed to update link')
      throw err
    }
  }

  const deleteLink = async (id) => {
    try {
      await linkService.deleteLink(id)
      setLinks(prev => prev.filter(link => link.id !== id))
      setTotalElements(prev => prev - 1)
      toast.success('Link deleted successfully!')
    } catch (err) {
      toast.error('Failed to delete link')
      throw err
    }
  }

  const toggleLink = async (id) => {
    try {
      await linkService.toggleLink(id)
      setLinks(prev => prev.map(link => 
        link.id === id ? { ...link, isActive: !link.isActive } : link
      ))
      toast.success('Link status updated!')
    } catch (err) {
      toast.error('Failed to update link status')
      throw err
    }
  }

  return {
    links,
    loading,
    error,
    totalPages,
    totalElements,
    createLink,
    updateLink,
    deleteLink,
    toggleLink,
    refetch: fetchLinks,
  }
}

export const useLinkAnalytics = (linkId, days = 30) => {
  const [analytics, setAnalytics] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchAnalytics = async () => {
      if (!linkId) return

      try {
        setLoading(true)
        const data = await linkService.getLinkAnalytics(linkId, days)
        setAnalytics(data)
        setError(null)
      } catch (err) {
        setError(err.message)
        toast.error('Failed to fetch analytics')
      } finally {
        setLoading(false)
      }
    }

    fetchAnalytics()
  }, [linkId, days])

  return { analytics, loading, error }
}

import React, { useState } from 'react'
import { useLinks } from '../hooks/useLinks'
import { QrCode, Copy, Check, ExternalLink, BarChart3, Calendar, Users, MousePointer } from 'lucide-react'
import { copyToClipboard } from '../utils/helpers'

const LinkCard = ({ link }) => {
  const [copiedId, setCopiedId] = useState(null)

  const handleCopy = async (text, id) => {
    const success = await copyToClipboard(text)
    if (success) {
      setCopiedId(id)
      setTimeout(() => setCopiedId(null), 2000)
    }
  }

  const shortUrl = `${window.location.origin}/${link.shortCode}`

  return (
    <div className="bg-white rounded-xl shadow-md p-6 border border-gray-100 hover:shadow-lg transition-shadow">
      <div className="flex items-start justify-between mb-4">
        <div className="flex-1">
          <h3 className="text-lg font-semibold text-gray-900 mb-2 truncate">
            {link.title || 'Lien sans titre'}
          </h3>
          <p className="text-sm text-gray-600 mb-1">URL originale :</p>
          <a 
            href={link.originalUrl} 
            target="_blank" 
            rel="noopener noreferrer"
            className="text-blue-600 hover:text-blue-800 text-sm break-all"
          >
            {link.originalUrl}
          </a>
          <p className="text-sm text-gray-600 mt-2 mb-1">Lien court :</p>
          <div className="flex items-center space-x-2">
            <a 
              href={shortUrl} 
              target="_blank" 
              rel="noopener noreferrer"
              className="text-blue-600 hover:text-blue-800 text-sm"
            >
              {shortUrl}
            </a>
            <button
              onClick={() => handleCopy(shortUrl, `short-${link.id}`)}
              className="p-1 text-gray-400 hover:text-gray-600 transition-colors"
            >
              {copiedId === `short-${link.id}` ? (
                <Check className="h-4 w-4 text-green-500" />
              ) : (
                <Copy className="h-4 w-4" />
              )}
            </button>
            <button
              onClick={() => window.open(`/qr/${link.shortCode}`, '_blank')}
              className="p-1 text-gray-400 hover:text-gray-600 transition-colors"
              title="QR Code"
            >
              <QrCode className="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mt-4 pt-4 border-t border-gray-100">
        <div className="text-center">
          <div className="flex items-center justify-center mb-1">
            <MousePointer className="h-4 w-4 text-blue-600 mr-1" />
            <span className="text-lg font-bold text-gray-900">{link.clickCount || 0}</span>
          </div>
          <p className="text-xs text-gray-600">Clics totaux</p>
        </div>
        <div className="text-center">
          <div className="flex items-center justify-center mb-1">
            <Users className="h-4 w-4 text-green-600 mr-1" />
            <span className="text-lg font-bold text-gray-900">{link.uniqueClicks || 0}</span>
          </div>
          <p className="text-xs text-gray-600">Visiteurs uniques</p>
        </div>
        <div className="text-center">
          <div className="flex items-center justify-center mb-1">
            <Calendar className="h-4 w-4 text-purple-600 mr-1" />
            <span className="text-lg font-bold text-gray-900">{link.todayClicks || 0}</span>
          </div>
          <p className="text-xs text-gray-600">Aujourd'hui</p>
        </div>
        <div className="text-center">
          <div className="flex items-center justify-center mb-1">
            <BarChart3 className="h-4 w-4 text-orange-600 mr-1" />
            <span className="text-lg font-bold text-gray-900">
              {link.clickCount > 0 ? ((link.todayClicks || 0) / link.clickCount * 100).toFixed(1) : 0}%
            </span>
          </div>
          <p className="text-xs text-gray-600">% aujourd'hui</p>
        </div>
      </div>

      <div className="flex items-center justify-between mt-4 pt-4 border-t border-gray-100">
        <div className="text-sm text-gray-500">
          Créé le {new Date(link.createdAt).toLocaleDateString('fr-FR')}
        </div>
        <div className="flex items-center space-x-2">
          <button
            onClick={() => window.open(`/analytics/${link.shortCode}`, '_blank')}
            className="inline-flex items-center px-3 py-1 text-sm text-blue-600 hover:text-blue-800 transition-colors"
          >
            <BarChart3 className="h-4 w-4 mr-1" />
            Détails
          </button>
          <button
            onClick={() => window.open(shortUrl, '_blank')}
            className="inline-flex items-center px-3 py-1 text-sm text-gray-600 hover:text-gray-800 transition-colors"
          >
            <ExternalLink className="h-4 w-4 mr-1" />
            Visiter
          </button>
        </div>
      </div>
    </div>
  )
}

const Analytics = () => {
  const { data: links, isLoading, error } = useLinks()
  const [filter, setFilter] = useState('all')
  const [sortBy, setSortBy] = useState('createdAt')

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 text-lg">Erreur lors du chargement des liens</p>
          <button 
            onClick={() => window.location.reload()} 
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Réessayer
          </button>
        </div>
      </div>
    )
  }

  const filteredAndSortedLinks = (links || [])
    .filter(link => {
      if (filter === 'all') return true
      if (filter === 'active') return link.clickCount > 0
      if (filter === 'inactive') return link.clickCount === 0
      return true
    })
    .sort((a, b) => {
      if (sortBy === 'createdAt') return new Date(b.createdAt) - new Date(a.createdAt)
      if (sortBy === 'clicks') return (b.clickCount || 0) - (a.clickCount || 0)
      if (sortBy === 'title') return (a.title || '').localeCompare(b.title || '')
      return 0
    })

  const totalClicks = (links || []).reduce((sum, link) => sum + (link.clickCount || 0), 0)
  const activeLinks = (links || []).filter(link => link.clickCount > 0).length

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Analytics des Liens</h1>
          <p className="text-gray-600 mt-2">Analysez la performance de tous vos liens courts</p>
        </div>

        {/* Summary Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-xl shadow-md p-6 border border-gray-100">
            <div className="flex items-center">
              <div className="h-12 w-12 bg-blue-50 rounded-xl flex items-center justify-center">
                <BarChart3 className="h-6 w-6 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Total Liens</p>
                <p className="text-2xl font-bold text-gray-900">{links?.length || 0}</p>
              </div>
            </div>
          </div>
          <div className="bg-white rounded-xl shadow-md p-6 border border-gray-100">
            <div className="flex items-center">
              <div className="h-12 w-12 bg-green-50 rounded-xl flex items-center justify-center">
                <MousePointer className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Total Clics</p>
                <p className="text-2xl font-bold text-gray-900">{totalClicks.toLocaleString()}</p>
              </div>
            </div>
          </div>
          <div className="bg-white rounded-xl shadow-md p-6 border border-gray-100">
            <div className="flex items-center">
              <div className="h-12 w-12 bg-purple-50 rounded-xl flex items-center justify-center">
                <BarChart3 className="h-6 w-6 text-purple-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Liens Actifs</p>
                <p className="text-2xl font-bold text-gray-900">{activeLinks}</p>
              </div>
            </div>
          </div>
          <div className="bg-white rounded-xl shadow-md p-6 border border-gray-100">
            <div className="flex items-center">
              <div className="h-12 w-12 bg-orange-50 rounded-xl flex items-center justify-center">
                <BarChart3 className="h-6 w-6 text-orange-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Clics Moyens</p>
                <p className="text-2xl font-bold text-gray-900">
                  {links?.length > 0 ? Math.round(totalClicks / links.length) : 0}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-xl shadow-md p-6 border border-gray-100 mb-8">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div className="flex items-center space-x-4">
              <div>
                <label className="text-sm font-medium text-gray-700 mr-2">Filtrer par:</label>
                <select
                  value={filter}
                  onChange={(e) => setFilter(e.target.value)}
                  className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="all">Tous les liens</option>
                  <option value="active">Liens actifs</option>
                  <option value="inactive">Liens inactifs</option>
                </select>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-700 mr-2">Trier par:</label>
                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="createdAt">Date de création</option>
                  <option value="clicks">Nombre de clics</option>
                  <option value="title">Titre</option>
                </select>
              </div>
            </div>
            <div className="text-sm text-gray-600">
              {filteredAndSortedLinks.length} lien(s) affiché(s)
            </div>
          </div>
        </div>

        {/* Links Grid */}
        {filteredAndSortedLinks.length === 0 ? (
          <div className="bg-white rounded-xl shadow-md p-12 border border-gray-100 text-center">
            <BarChart3 className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">Aucun lien trouvé</h3>
            <p className="text-gray-600">
              {filter === 'all' 
                ? "Vous n'avez pas encore créé de liens."
                : `Aucun lien ne correspond au filtre "${filter}".`
              }
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {filteredAndSortedLinks.map((link) => (
              <LinkCard key={link.id} link={link} />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default Analytics

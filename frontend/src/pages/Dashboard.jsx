import React from 'react'
import { useDashboard } from '../hooks/useDashboard'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts'
import { Link2, Users, MousePointer, Clock } from 'lucide-react'

const StatCard = ({ title, value, icon: Icon, change }) => (
  <div className="bg-white rounded-xl shadow-md p-6 border border-gray-100 hover:shadow-lg transition-shadow">
    <div className="flex items-center justify-between">
      <div>
        <p className="text-sm font-medium text-gray-600">{title}</p>
        <p className="text-3xl font-bold text-gray-900 mt-1">{value?.toLocaleString() || 0}</p>
        {change && (
          <p className={`text-sm mt-1 ${change > 0 ? 'text-green-600' : 'text-red-600'}`}>
            {change > 0 ? '+' : ''}{change}% depuis hier
          </p>
        )}
      </div>
      <div className="h-12 w-12 bg-blue-50 rounded-xl flex items-center justify-center">
        <Icon className="h-6 w-6 text-blue-600" />
      </div>
    </div>
  </div>
)

const Dashboard = () => {
  const { stats: analytics, loading: isLoading, error } = useDashboard()

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600" data-testid="loading-spinner"></div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 text-lg">Erreur lors du chargement du tableau de bord</p>
          <p className="text-gray-600 mt-2">Détails : {error}</p>
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

  const stats = [
    { title: "Total des liens", value: analytics?.totalLinks || 0, icon: Link2, change: analytics?.linksGrowth || 0 },
    { title: "Total des clics", value: analytics?.totalClicks || 0, icon: MousePointer, change: analytics?.clicksGrowth || 0 },
    { title: "Clics aujourd'hui", value: analytics?.clicksToday || 0, icon: Clock, change: analytics?.todayGrowth || 0 },
    { title: "Visiteurs uniques", value: analytics?.uniqueVisitors || 0, icon: Users, change: analytics?.visitorsGrowth || 0 }
  ]

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Tableau de bord</h1>
          <p className="mt-2 text-gray-600">
            Surveillez les performances de vos liens et obtenez des insights détaillés
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {stats.map((stat, index) => (
            <StatCard key={index} {...stat} />
          ))}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
          <div className="bg-white p-6 rounded-xl shadow-md border border-gray-100">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Clics quotidiens</h3>
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={analytics?.dailyClicks || []}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Line type="monotone" dataKey="clicks" stroke="#3b82f6" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="bg-white p-6 rounded-xl shadow-md border border-gray-100">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Clics par heure</h3>
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={analytics?.hourlyClicks || []}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="hour" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="clicks" fill="#3b82f6" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-md border border-gray-100 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-6">Liens les plus populaires</h3>
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-3 px-4 font-semibold text-gray-900">Lien court</th>
                  <th className="text-left py-3 px-4 font-semibold text-gray-900">URL originale</th>
                  <th className="text-left py-3 px-4 font-semibold text-gray-900">Clics</th>
                  <th className="text-left py-3 px-4 font-semibold text-gray-900">Créé le</th>
                </tr>
              </thead>
              <tbody>
                {(analytics?.topLinks || []).map((link, index) => (
                  <tr key={index} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="py-3 px-4">
                      <div className="text-blue-600 font-medium">{link.shortCode}</div>
                    </td>
                    <td className="py-3 px-4">
                      <div className="text-gray-900 truncate max-w-xs" title={link.originalUrl}>
                        {link.originalUrl}
                      </div>
                    </td>
                    <td className="py-3 px-4">
                      <div className="text-gray-900 font-semibold">{link.clickCount}</div>
                    </td>
                    <td className="py-3 px-4">
                      <div className="text-gray-600">
                        {new Date(link.createdAt).toLocaleDateString('fr-FR')}
                      </div>
                    </td>
                  </tr>
                ))}
                {(!analytics?.topLinks || analytics.topLinks.length === 0) && (
                  <tr>
                    <td colSpan="4" className="py-8 px-4 text-center text-gray-500">
                      Aucun lien trouvé. Créez votre premier lien depuis la page d'accueil !
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard

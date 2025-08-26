import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link2, QrCode, Copy, Check, Sparkles, BarChart3 } from 'lucide-react'
import { linkService } from '../services/api'
import { copyToClipboard, isValidUrl } from '../utils/helpers'
import toast from 'react-hot-toast'

const HomePage = () => {
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [copied, setCopied] = useState(false)

  const { register, handleSubmit, formState: { errors }, reset } = useForm()

  const onSubmit = async (data) => {
    if (!isValidUrl(data.originalUrl)) {
      toast.error('Veuillez entrer une URL valide')
      return
    }

    setLoading(true)
    try {
      const newLink = await linkService.createLink(data)
      setResult(newLink)
      reset()
      toast.success('Lien raccourci avec succès !')
    } catch (error) {
      toast.error('Échec du raccourcissement du lien')
    } finally {
      setLoading(false)
    }
  }

  const handleCopy = async (text) => {
    const success = await copyToClipboard(text)
    if (success) {
      setCopied(true)
      toast.success('Copié dans le presse-papier !')
      setTimeout(() => setCopied(false), 2000)
    } else {
      toast.error('Échec de la copie')
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 via-white to-purple-50">
      {/* Hero Section */}
      <div className="relative overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-16 pb-20">
          <div className="text-center">
            <h1 className="text-4xl md:text-6xl font-bold text-gray-900 mb-6">
              Raccourcissez vos URLs avec
              <span className="text-gradient block mt-2">Analytics Intelligents</span>
            </h1>
            <p className="text-xl text-gray-600 mb-12 max-w-3xl mx-auto">
              Créez des liens courts, suivez les clics, analysez le trafic et optimisez vos campagnes marketing 
              avec notre puissante plateforme de raccourcissement d'URL et d'analytics.
            </p>

            {/* URL Shortener Form */}
            <div className="max-w-4xl mx-auto">
              <form onSubmit={handleSubmit(onSubmit)} className="mb-8">
                <div className="flex flex-col md:flex-row gap-4 p-2 bg-white rounded-2xl shadow-lg border border-gray-200">
                  <div className="flex-1">
                    <input
                      {...register('originalUrl', { 
                        required: 'L\'URL est requise',
                        pattern: {
                          value: /^https?:\/\/.+/,
                          message: 'Veuillez entrer une URL valide commençant par http:// ou https://'
                        }
                      })}
                      type="url"
                      placeholder="Collez votre URL longue ici..."
                      className="w-full px-6 py-4 text-lg border-0 focus:outline-none focus:ring-0 rounded-xl"
                    />
                  </div>
                  <button
                    type="submit"
                    disabled={loading}
                    className="btn btn-primary px-8 py-4 text-lg font-semibold rounded-xl disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {loading ? (
                      <div className="flex items-center space-x-2">
                        <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                        <span>Raccourcissement...</span>
                      </div>
                    ) : (
                      <div className="flex items-center space-x-2">
                        <Link2 className="h-5 w-5" />
                        <span>Raccourcir l'URL</span>
                      </div>
                    )}
                  </button>
                </div>
                {errors.originalUrl && (
                  <p className="text-red-600 text-sm mt-2">{errors.originalUrl.message}</p>
                )}
              </form>

              {/* Advanced Options */}
              <details className="mb-8">
                <summary className="cursor-pointer text-gray-600 hover:text-gray-900 font-medium">
                  ⚙️ Options Avancées
                </summary>
                <div className="mt-4 p-6 bg-white rounded-xl shadow-sm border border-gray-200">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Alias Personnalisé (Optionnel)
                      </label>
                      <input
                        {...register('customAlias')}
                        type="text"
                        placeholder="mon-lien-personnalise"
                        className="input"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Titre (Optionnel)
                      </label>
                      <input
                        {...register('title')}
                        type="text"
                        placeholder="Titre du lien"
                        className="input"
                      />
                    </div>
                    <div className="md:col-span-2">
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Description (Optionnelle)
                      </label>
                      <textarea
                        {...register('description')}
                        placeholder="Description du lien"
                        rows={3}
                        className="input"
                      />
                    </div>
                  </div>
                </div>
              </details>

              {/* Result */}
              {result && (
                <div className="bg-white rounded-2xl shadow-lg border border-gray-200 p-8 animate-slide-up">
                  <div className="text-center mb-6">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
                      <Check className="h-8 w-8 text-green-600" />
                    </div>
                    <h3 className="text-2xl font-bold text-gray-900 mb-2">Votre lien est prêt !</h3>
                    <p className="text-gray-600">Partagez-le où vous voulez</p>
                  </div>

                  <div className="space-y-4">
                    {/* Short URL */}
                    <div className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                      <div className="flex-1">
                        <p className="text-sm text-gray-600 mb-1">URL Courte</p>
                        <p className="text-lg font-semibold text-primary-600 break-all">
                          {result.shortUrl}
                        </p>
                      </div>
                      <button
                        onClick={() => handleCopy(result.shortUrl)}
                        className="btn btn-secondary ml-4"
                      >
                        {copied ? <Check className="h-4 w-4" /> : <Copy className="h-4 w-4" />}
                      </button>
                    </div>

                    {/* QR Code */}
                    {result.qrCodeUrl && (
                      <div className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
                        <div className="flex-1">
                          <p className="text-sm text-gray-600 mb-1">Code QR</p>
                          <p className="text-gray-700">Scannez ou téléchargez le code QR</p>
                        </div>
                        <div className="flex space-x-2 ml-4">
                          <img 
                            src={result.qrCodeUrl} 
                            alt="QR Code" 
                            className="w-16 h-16 rounded-lg"
                          />
                          <button className="btn btn-secondary">
                            <QrCode className="h-4 w-4" />
                          </button>
                        </div>
                      </div>
                    )}
                  </div>

                  <div className="mt-6 pt-6 border-t border-gray-200 text-center">
                    <p className="text-sm text-gray-500">
                      Voulez-vous voir les statistiques ? {' '}
                      <a href="/dashboard" className="text-primary-600 hover:text-primary-700 font-medium">
                        Voir le tableau de bord
                      </a>
                    </p>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="bg-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Pourquoi choisir notre raccourcisseur d'URL ?
            </h2>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto">
              Obtenez des insights puissants et des fonctionnalités avancées pour maximiser les performances de vos liens
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center p-6">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-full mb-6">
                <BarChart3 className="h-8 w-8 text-primary-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-4">
                Analytiques Avancées
              </h3>
              <p className="text-gray-600">
                Suivez les clics, données géographiques, appareils, navigateurs et plus avec des analytics détaillées
              </p>
            </div>

            <div className="text-center p-6">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-purple-100 rounded-full mb-6">
                <QrCode className="h-8 w-8 text-purple-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-4">
                Génération de Code QR
              </h3>
              <p className="text-gray-600">
                Générez automatiquement des codes QR pour tous vos liens courts pour un partage facile
              </p>
            </div>

            <div className="text-center p-6">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-6">
                <Sparkles className="h-8 w-8 text-green-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-4">
                Marque Personnalisée
              </h3>
              <p className="text-gray-600">
                Créez des alias personnalisés et des liens courts de marque qui reflètent votre image
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default HomePage

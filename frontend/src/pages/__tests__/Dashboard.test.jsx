import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import Dashboard from '../Dashboard'

// Mock des hooks
vi.mock('../../hooks/useDashboard', () => ({
  useDashboard: vi.fn()
}))

// Mock des composants Recharts
vi.mock('recharts', () => ({
  ResponsiveContainer: ({ children }) => <div data-testid="responsive-container">{children}</div>,
  LineChart: ({ children }) => <div data-testid="line-chart">{children}</div>,
  BarChart: ({ children }) => <div data-testid="bar-chart">{children}</div>,
  XAxis: () => <div data-testid="x-axis" />,
  YAxis: () => <div data-testid="y-axis" />,
  CartesianGrid: () => <div data-testid="cartesian-grid" />,
  Tooltip: () => <div data-testid="tooltip" />,
  Line: () => <div data-testid="line" />,
  Bar: () => <div data-testid="bar" />
}))

describe('Dashboard', () => {
  it('affiche le loader pendant le chargement', () => {
    const { useDashboard } = require('../../hooks/useDashboard')
    useDashboard.mockReturnValue({
      stats: null,
      loading: true,
      error: null
    })

    render(<Dashboard />)
    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument()
  })

  it('affiche une erreur en cas de problème', () => {
    const { useDashboard } = require('../../hooks/useDashboard')
    useDashboard.mockReturnValue({
      stats: null,
      loading: false,
      error: 'Erreur de connexion'
    })

    render(<Dashboard />)
    expect(screen.getByText('Erreur lors du chargement du tableau de bord')).toBeInTheDocument()
    expect(screen.getByText('Détails : Erreur de connexion')).toBeInTheDocument()
  })

  it('affiche les statistiques quand elles sont chargées', () => {
    const { useDashboard } = require('../../hooks/useDashboard')
    const mockStats = {
      totalLinks: 42,
      totalClicks: 1337,
      clicksToday: 25,
      uniqueVisitors: 89,
      dailyClicks: [
        { date: '2025-08-25', clicks: 10 },
        { date: '2025-08-26', clicks: 15 }
      ],
      hourlyClicks: [
        { hour: '10h', clicks: 5 },
        { hour: '11h', clicks: 8 }
      ],
      topLinks: [
        {
          shortCode: 'abc123',
          originalUrl: 'https://example.com',
          clickCount: 50,
          createdAt: '2025-08-26T10:00:00Z'
        }
      ]
    }

    useDashboard.mockReturnValue({
      stats: mockStats,
      loading: false,
      error: null
    })

    render(<Dashboard />)

    // Vérifie le titre
    expect(screen.getByText('Tableau de bord')).toBeInTheDocument()
    
    // Vérifie les statistiques
    expect(screen.getByText('42')).toBeInTheDocument()
    expect(screen.getByText('1,337')).toBeInTheDocument()
    expect(screen.getByText('25')).toBeInTheDocument()
    expect(screen.getByText('89')).toBeInTheDocument()

    // Vérifie les titres des cartes
    expect(screen.getByText('Total des liens')).toBeInTheDocument()
    expect(screen.getByText('Total des clics')).toBeInTheDocument()
    expect(screen.getByText('Clics aujourd\'hui')).toBeInTheDocument()
    expect(screen.getByText('Visiteurs uniques')).toBeInTheDocument()

    // Vérifie les graphiques
    expect(screen.getByText('Clics quotidiens')).toBeInTheDocument()
    expect(screen.getByText('Clics par heure')).toBeInTheDocument()

    // Vérifie le tableau des liens
    expect(screen.getByText('Liens les plus populaires')).toBeInTheDocument()
    expect(screen.getByText('abc123')).toBeInTheDocument()
    expect(screen.getByText('https://example.com')).toBeInTheDocument()
  })

  it('affiche un message quand il n\'y a pas de liens', () => {
    const { useDashboard } = require('../../hooks/useDashboard')
    useDashboard.mockReturnValue({
      stats: { topLinks: [] },
      loading: false,
      error: null
    })

    render(<Dashboard />)
    expect(screen.getByText('Aucun lien trouvé. Créez votre premier lien depuis la page d\'accueil !')).toBeInTheDocument()
  })
})

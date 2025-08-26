import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import { linkService, dashboardService } from '../api'

// Mock axios
vi.mock('axios')
const mockedAxios = vi.mocked(axios)

// Mock axios.create pour retourner l'objet axios mocké
mockedAxios.create.mockReturnValue(mockedAxios)

describe('linkService', () => {
  beforeEach(() => {
    vi.resetAllMocks()
    mockedAxios.create.mockReturnValue(mockedAxios)
  })

  it('crée un lien court avec succès', async () => {
    const mockResponse = {
      data: {
        id: 1,
        shortCode: 'abc123',
        originalUrl: 'https://example.com',
        shortUrl: 'http://localhost:3001/abc123'
      }
    }

    mockedAxios.post.mockResolvedValue(mockResponse)

    const linkData = {
      originalUrl: 'https://example.com',
      title: 'Example'
    }

    const result = await linkService.createLink(linkData)

    expect(mockedAxios.post).toHaveBeenCalledWith('/links', linkData)
    expect(result).toEqual(mockResponse.data)
  })

  it('gère les erreurs lors de la création d\'un lien', async () => {
    const mockError = new Error('URL invalide')
    mockedAxios.post.mockRejectedValue(mockError)

    await expect(linkService.createLink({ originalUrl: 'invalid-url' })).rejects.toThrow('URL invalide')
  })

  it('récupère les liens avec pagination', async () => {
    const mockResponse = {
      data: {
        content: [
          {
            id: 1,
            shortCode: 'abc123',
            originalUrl: 'https://example.com',
            clickCount: 10
          }
        ],
        totalElements: 1,
        totalPages: 1,
        number: 0
      }
    }

    mockedAxios.get.mockResolvedValue(mockResponse)

    const result = await linkService.getLinks(0, 10)

    expect(mockedAxios.get).toHaveBeenCalledWith('/links', {
      params: { page: 0, size: 10, sortBy: 'createdAt', sortDir: 'desc' }
    })
    expect(result).toEqual(mockResponse.data)
  })

  it('récupère un lien spécifique par ID', async () => {
    const mockResponse = {
      data: {
        id: 1,
        shortCode: 'abc123',
        originalUrl: 'https://example.com',
        clickCount: 10
      }
    }

    mockedAxios.get.mockResolvedValue(mockResponse)

    const result = await linkService.getLink(1)

    expect(mockedAxios.get).toHaveBeenCalledWith('/links/1')
    expect(result).toEqual(mockResponse.data)
  })
})

describe('dashboardService', () => {
  beforeEach(() => {
    vi.resetAllMocks()
    mockedAxios.create.mockReturnValue(mockedAxios)
  })

  it('récupère les statistiques du dashboard', async () => {
    const mockResponse = {
      data: {
        totalLinks: 42,
        totalClicks: 1337,
        clicksToday: 25,
        uniqueVisitors: 89,
        dailyClicks: [
          { date: '2025-08-25', clicks: 10 },
          { date: '2025-08-26', clicks: 15 }
        ]
      }
    }

    mockedAxios.get.mockResolvedValue(mockResponse)

    const result = await dashboardService.getStats()

    expect(mockedAxios.get).toHaveBeenCalledWith('/dashboard/stats')
    expect(result).toEqual(mockResponse.data)
  })

  it('gère les erreurs lors de la récupération des statistiques', async () => {
    const mockError = new Error('Erreur serveur')
    mockedAxios.get.mockRejectedValue(mockError)

    await expect(dashboardService.getStats()).rejects.toThrow('Erreur serveur')
  })
})

import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import HomePage from '../HomePage'

// Mock des services API
vi.mock('../../services/api', () => ({
  linkService: {
    createLink: vi.fn().mockResolvedValue({
      id: 1,
      shortCode: 'abc123',
      originalUrl: 'https://example.com',
      shortUrl: 'http://localhost:3001/abc123'
    })
  }
}))

// Mock react-hot-toast
vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  }
}))

// Mock des utilitaires
vi.mock('../../utils/helpers', () => ({
  copyToClipboard: vi.fn().mockResolvedValue(true),
  isValidUrl: vi.fn().mockImplementation((url) => {
    return url && (url.startsWith('http://') || url.startsWith('https://'))
  })
}))

const HomePageWithRouter = () => (
  <BrowserRouter>
    <HomePage />
  </BrowserRouter>
)

describe('HomePage', () => {
  it('affiche le titre principal', () => {
    render(<HomePageWithRouter />)
    expect(screen.getByText('Raccourcissez vos liens en un clic')).toBeInTheDocument()
  })

  it('affiche le formulaire de création de lien', () => {
    render(<HomePageWithRouter />)
    expect(screen.getByPlaceholderText('Collez votre URL longue ici...')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /raccourcir l'url/i })).toBeInTheDocument()
  })

  it('affiche les fonctionnalités', () => {
    render(<HomePageWithRouter />)
    expect(screen.getByText('Pourquoi choisir notre raccourcisseur d\'URL ?')).toBeInTheDocument()
    expect(screen.getByText('Analytiques Avancées')).toBeInTheDocument()
    expect(screen.getByText('Génération de Code QR')).toBeInTheDocument()
    expect(screen.getByText('Marque Personnalisée')).toBeInTheDocument()
  })

  it('valide que le champ URL est requis', async () => {
    render(<HomePageWithRouter />)
    
    const submitButton = screen.getByRole('button', { name: /raccourcir l'url/i })
    fireEvent.click(submitButton)

    await waitFor(() => {
      expect(screen.getByText('L\'URL est requise')).toBeInTheDocument()
    })
  })

  it('affiche une erreur pour une URL invalide', async () => {
    render(<HomePageWithRouter />)
    
    const urlInput = screen.getByPlaceholderText('Collez votre URL longue ici...')
    const submitButton = screen.getByRole('button', { name: /raccourcir l'url/i })

    fireEvent.change(urlInput, { target: { value: 'invalid-url' } })
    fireEvent.click(submitButton)

    await waitFor(() => {
      expect(screen.getByText('Veuillez entrer une URL valide')).toBeInTheDocument()
    })
  })
})

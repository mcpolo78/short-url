import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import Navbar from '../Navbar'

const NavbarWithRouter = () => (
  <BrowserRouter>
    <Navbar />
  </BrowserRouter>
)

describe('Navbar', () => {
  it('affiche le nom de l\'application', () => {
    render(<NavbarWithRouter />)
    expect(screen.getByText('RaccourcirLiens')).toBeInTheDocument()
  })

  it('affiche le lien Accueil', () => {
    render(<NavbarWithRouter />)
    expect(screen.getByText('Accueil')).toBeInTheDocument()
  })

  it('affiche le lien Tableau de bord', () => {
    render(<NavbarWithRouter />)
    expect(screen.getByText('Tableau de bord')).toBeInTheDocument()
  })

  it('affiche le lien Analytics', () => {
    render(<NavbarWithRouter />)
    expect(screen.getByText('Analytics')).toBeInTheDocument()
  })

  it('contient les liens de navigation corrects', () => {
    render(<NavbarWithRouter />)
    const homeLink = screen.getByRole('link', { name: /accueil/i })
    const dashboardLink = screen.getByRole('link', { name: /tableau de bord/i })
    const analyticsLink = screen.getByRole('link', { name: /analytics/i })

    expect(homeLink).toHaveAttribute('href', '/')
    expect(dashboardLink).toHaveAttribute('href', '/dashboard')
    expect(analyticsLink).toHaveAttribute('href', '/analytics')
  })
})

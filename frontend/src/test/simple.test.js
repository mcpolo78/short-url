import { describe, it, expect } from 'vitest'

describe('Test simple', () => {
  it('devrait passer ce test basique', () => {
    expect(1 + 1).toBe(2)
  })
  
  it('devrait valider les strings', () => {
    expect('hello').toBe('hello')
  })
})

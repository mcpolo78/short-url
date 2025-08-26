#!/usr/bin/env node

// Script pour exécuter les tests sans mode watch
import { exec } from 'child_process'
import { promisify } from 'util'

const execAsync = promisify(exec)

async function runTests() {
  try {
    console.log('🧪 Exécution des tests frontend...\n')
    
    const { stdout, stderr } = await execAsync('npx vitest run --no-watch --reporter=verbose', {
      cwd: process.cwd()
    })
    
    console.log(stdout)
    if (stderr) console.error(stderr)
    
    console.log('\n✅ Tests terminés')
  } catch (error) {
    console.error('❌ Erreur lors de l\'exécution des tests:', error.message)
    process.exit(1)
  }
}

runTests()

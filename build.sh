#!/bin/bash
# Script de build pour Railway

echo "🚀 Building LinkShortener Backend for Railway..."

# Se placer dans le dossier backend
cd backend || exit 1

echo "📦 Cleaning previous builds..."
./mvnw clean

echo "🔨 Building application..."
./mvnw package -DskipTests -Dmaven.compiler.target=21 -Dmaven.compiler.source=21

echo "✅ Build completed successfully!"
echo "📁 JAR location: backend/target/*.jar"

# Vérifier que le JAR existe
if ls target/*.jar 1> /dev/null 2>&1; then
    echo "✅ JAR file found: $(ls target/*.jar)"
else
    echo "❌ JAR file not found!"
    exit 1
fi

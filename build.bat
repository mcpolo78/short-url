@echo off
REM Script de build pour Railway (Windows)

echo 🚀 Building LinkShortener Backend for Railway...

cd backend
if %errorlevel% neq 0 (
    echo ❌ Error: Cannot find backend directory
    exit /b 1
)

echo 📦 Cleaning previous builds...
call mvnw.cmd clean

echo 🔨 Building application...
call mvnw.cmd package -DskipTests -Dmaven.compiler.target=21 -Dmaven.compiler.source=21

if %errorlevel% neq 0 (
    echo ❌ Build failed!
    exit /b 1
)

echo ✅ Build completed successfully!
echo 📁 JAR location: backend/target/*.jar

REM Vérifier que le JAR existe
dir target\*.jar >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ JAR file not found!
    exit /b 1
) else (
    echo ✅ JAR file found!
)

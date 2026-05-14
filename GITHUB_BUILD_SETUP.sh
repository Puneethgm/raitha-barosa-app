#!/bin/bash

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  Raitha-Bharosa Hub - GitHub Build Setup                      ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Check if git is installed
if ! command -v git &> /dev/null; then
    echo "❌ Git not installed. Installing..."
    sudo apt-get update && sudo apt-get install -y git
fi

# Initialize git repo
echo "📦 Initializing git repository..."
git init
git config user.email "dev@raithabharosa.hub"
git config user.name "Raitha Bharosa Dev"

# Add all files
echo "📄 Adding all files..."
git add .

# Create initial commit
echo "💾 Creating initial commit..."
git commit -m "Initial commit - Raitha-Bharosa Hub Complete Project"

echo ""
echo "✅ Git repository initialized!"
echo ""
echo "════════════════════════════════════════════════════════════════"
echo ""
echo "📋 NEXT STEPS (Do these manually on github.com):"
echo ""
echo "1. Go to: https://github.com/new"
echo "2. Create new repository named: raitha-bharosa-hub"
echo "3. Copy the command shown:"
echo "   git remote add origin https://github.com/YOUR_USERNAME/raitha-bharosa-hub.git"
echo "   git branch -M main"
echo "   git push -u origin main"
echo ""
echo "4. Paste those commands here in terminal"
echo ""
echo "5. GitHub will automatically build APK!"
echo ""
echo "════════════════════════════════════════════════════════════════"


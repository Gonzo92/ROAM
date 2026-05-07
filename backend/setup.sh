#!/bin/bash

echo
echo "=========================================="
echo "   Saily Backend Setup"
echo "=========================================="
echo

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "ERROR: Node.js is not installed!"
    echo "Please download and install Node.js from: https://nodejs.org/"
    exit 1
fi

echo "Checking Node.js version..."
node --version

echo
echo "Installing dependencies..."
npm install

echo
echo "Creating .env file..."
if [ ! -f .env ]; then
    cp .env.example .env
    echo "✓ .env file created"
    echo
    echo "TODO: Edit .env and add your GOOGLE_API_KEY"
else
    echo "✓ .env already exists"
fi

echo
echo "=========================================="
echo "Setup complete!"
echo
echo "Next steps:"
echo "1. Edit .env and add your GOOGLE_API_KEY from https://ai.google.dev"
echo "2. Run: npm start"
echo "=========================================="
echo

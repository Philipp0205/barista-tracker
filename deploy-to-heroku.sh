#!/bin/bash
# Heroku Deployment Script for Coffee Tracker
# This script helps you deploy the application to Heroku

set -e  # Exit on any error

echo "=========================================="
echo "Coffee Tracker - Heroku Deployment Script"
echo "=========================================="
echo ""

# Check if Heroku CLI is installed
if ! command -v heroku &> /dev/null; then
    echo "❌ Error: Heroku CLI is not installed."
    echo "Please install it from: https://devcenter.heroku.com/articles/heroku-cli"
    exit 1
fi

echo "✓ Heroku CLI is installed"
echo ""

# Check if logged in to Heroku
if ! heroku auth:whoami &> /dev/null; then
    echo "❌ Not logged in to Heroku. Please run: heroku login"
    exit 1
fi

echo "✓ Logged in to Heroku as: $(heroku auth:whoami)"
echo ""

# Step 1: Clean and build with production profile
echo "Step 1: Building production JAR..."
echo "Running: mvn clean package -Pproduction"
echo ""

mvn clean package -Pproduction

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please fix the errors and try again."
    exit 1
fi

echo ""
echo "✓ Production build successful"
echo ""

# Check if JAR exists
JAR_FILE="target/coffee-tracker-1.0-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ Error: JAR file not found at $JAR_FILE"
    exit 1
fi

echo "✓ JAR file created: $JAR_FILE"
echo ""

# Step 2: Git commit (if there are changes)
echo "Step 2: Checking Git status..."
if [ -n "$(git status --porcelain)" ]; then
    echo "Uncommitted changes detected. Committing all changes..."
    git add .
    git commit -m "Deploy to Heroku - $(date +'%Y-%m-%d %H:%M:%S')"
    echo "✓ Changes committed"
else
    echo "✓ No uncommitted changes"
fi
echo ""

# Step 3: Check if Heroku remote exists
if ! git remote | grep -q "heroku"; then
    echo "⚠️  No Heroku remote found. Please create a Heroku app first:"
    echo "   heroku create your-app-name"
    echo "   OR"
    echo "   heroku git:remote -a your-existing-app-name"
    echo ""
    exit 1
fi

APP_NAME=$(heroku apps:info -r heroku | grep "=== " | cut -d' ' -f2)
echo "✓ Deploying to Heroku app: $APP_NAME"
echo ""

# Step 4: Check environment variables
echo "Step 3: Checking environment variables..."
echo ""

if ! heroku config:get DATABASE_URL -r heroku &> /dev/null; then
    echo "⚠️  Warning: DATABASE_URL not set."
    echo "   Set it with: heroku config:set DATABASE_URL=jdbc:postgresql://..."
    echo "   OR add Heroku Postgres: heroku addons:create heroku-postgresql:essential-0"
    echo ""
fi

# Step 5: Deploy
echo "Step 4: Deploying to Heroku..."
echo "Running: git push heroku main"
echo ""

BRANCH=$(git rev-parse --abbrev-ref HEAD)
git push heroku $BRANCH:main

if [ $? -ne 0 ]; then
    echo "❌ Deployment failed."
    exit 1
fi

echo ""
echo "=========================================="
echo "✓ Deployment successful!"
echo "=========================================="
echo ""
echo "View logs:       heroku logs --tail"
echo "Open app:        heroku open"
echo "View config:     heroku config"
echo "View status:     heroku ps"
echo ""

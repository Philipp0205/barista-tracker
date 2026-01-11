# Heroku Deployment Quick Reference

## Initial Setup (One Time)

1. **Install Heroku CLI**
   ```bash
   # Download from: https://devcenter.heroku.com/articles/heroku-cli
   heroku login
   ```

2. **Create Heroku App**
   ```bash
   heroku create coffee-tracker-yourname
   ```

3. **Add PostgreSQL Database**
   ```bash
   heroku addons:create heroku-postgresql:essential-0
   ```

4. **Set Environment Variables** (if using external DB)
   ```bash
   heroku config:set DATABASE_URL="jdbc:postgresql://host:port/db"
   heroku config:set DATABASE_USERNAME="username"
   heroku config:set DATABASE_PASSWORD="password"
   ```

## Deploy Application

### Option 1: Use the Deploy Script (Easiest)
```bash
./deploy-to-heroku.sh
```

### Option 2: Manual Deployment
```bash
# Build production JAR
mvn clean package -Pproduction

# Commit changes
git add .
git commit -m "Deploy to production"

# Push to Heroku
git push heroku main
```

## Common Commands

```bash
# View logs in real-time
heroku logs --tail

# Open application in browser
heroku open

# Check dyno status
heroku ps

# Restart application
heroku restart

# View environment variables
heroku config

# Access database
heroku pg:psql

# Run bash on Heroku
heroku run bash
```

## Important Files

- **`Procfile`** - Defines how to start the app
- **`system.properties`** - Specifies Java version (21)
- **`pom.xml`** - Contains production profile
- **`application-prod.properties`** - Production configuration

## Production Build Command

The production profile (`-Pproduction`) does:
- ✅ Excludes development dependencies
- ✅ Optimizes frontend bundle
- ✅ Minifies JavaScript/CSS
- ✅ Enables Vaadin production mode
- ✅ Creates optimized JAR

## Troubleshooting

**Build fails:**
```bash
mvn clean package -Pproduction -X  # Verbose output
```

**App crashes on startup:**
```bash
heroku logs --tail  # Check logs
heroku config       # Verify environment variables
```

**Database connection issues:**
```bash
heroku config:get DATABASE_URL  # Check DB URL
heroku pg:info                  # Check DB status
```

**Need to rebuild frontend:**
```bash
rm -rf target/
mvn clean package -Pproduction
```

## Security Checklist

- ✅ Production mode enabled (`vaadin.productionMode=true`)
- ✅ Development dependencies excluded
- ✅ Database credentials in environment variables (not in code)
- ✅ HTTPS enabled automatically by Heroku
- ✅ Secure session management via Spring Security
- ✅ Connection pooling configured

## Cost Estimate

- **Eco Dyno**: ~$5/month (sleeps after 30 min inactivity)
- **Basic Dyno**: ~$7/month (never sleeps)
- **PostgreSQL Essential-0**: ~$5/month
- **Total**: ~$10-12/month for basic production app

## More Information

See `HEROKU_DEPLOYMENT.md` for detailed instructions.

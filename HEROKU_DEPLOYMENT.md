# Deploying Coffee Tracker to Heroku

This guide provides step-by-step instructions for deploying the Coffee Tracker application to Heroku in production mode.

## Prerequisites

1. **Heroku Account**: Sign up at https://signup.heroku.com/ if you don't have an account
2. **Heroku CLI**: Download and install from https://devcenter.heroku.com/articles/heroku-cli
3. **Git**: Ensure Git is installed on your system

## Configuration Files

The following files have been configured for Heroku deployment:

### 1. `system.properties`
Specifies Java 21 runtime for Heroku:
```properties
java.runtime.version=21
```

### 2. `Procfile`
Defines how Heroku should start the application:
```
web: java -Dserver.port=$PORT -Dspring.profiles.active=prod $JAVA_OPTS -jar target/coffee-tracker-1.0-SNAPSHOT.jar
```

This configuration:
- Sets the server port from Heroku's `$PORT` environment variable
- Activates the `prod` Spring profile
- Uses Heroku's `$JAVA_OPTS` for JVM tuning
- Runs the production JAR file

### 3. `pom.xml` - Production Profile
A production profile has been added to optimize the build:
- Excludes development dependencies (`vaadin-dev`)
- Builds and optimizes the frontend assets
- Creates a production-ready JAR

### 4. `application-prod.properties`
Production-specific configuration:
- Enables Vaadin production mode
- Disables hot deployment
- Configures proper logging levels
- Sets up proxy headers for HTTPS

## Deployment Steps

### Step 1: Install and Login to Heroku CLI

```bash
# Login to Heroku
heroku login
```

This will open a browser window for authentication.

### Step 2: Create a Production Build

Build the application with the production profile:

```bash
mvn clean package -Pproduction
```

This command:
- Cleans previous builds
- Compiles the application
- Optimizes and bundles frontend resources
- Creates a production-ready JAR in `target/`

**Note**: The first production build may take several minutes as it optimizes all frontend assets.

### Step 3: Initialize Git Repository (if not already done)

```bash
# Initialize Git if not already initialized
git init

# Add all files
git add .

# Commit
git commit -m "Prepare for Heroku deployment"
```

### Step 4: Create a Heroku Application

```bash
# Create a new Heroku app (generates a random name)
heroku create

# OR specify your own app name
heroku create coffee-tracker-yourname
```

Take note of the generated app name (e.g., `coffee-tracker-yourname` or `blooming-beach-34155`).

### Step 5: Configure Environment Variables

Set up your PostgreSQL database credentials as environment variables:

```bash
# Set database connection details
heroku config:set DATABASE_URL="jdbc:postgresql://your-db-host:5432/your-db-name"
heroku config:set DATABASE_USERNAME="your-username"
heroku config:set DATABASE_PASSWORD="your-password"

# Alternatively, use Heroku Postgres addon (recommended)
heroku addons:create heroku-postgresql:mini
```

**Important**: If using Heroku Postgres addon, the `DATABASE_URL` is automatically set, but you may need to parse it in your application or use it directly.

### Step 6: Add a PostgreSQL Database (Heroku Postgres)

The easiest option is to use Heroku's PostgreSQL addon:

```bash
# Add Heroku Postgres (mini tier is paid, but hobby-dev is available)
heroku addons:create heroku-postgresql:essential-0

# Get database credentials
heroku pg:credentials:url
```

**Note**: Heroku sets `DATABASE_URL` environment variable automatically. You might need to adjust your `application-prod.properties` to use this URL directly or parse it.

### Step 7: Deploy the Application

#### Option A: Using Git (Recommended)

```bash
# Push to Heroku
git push heroku main

# Or if your branch is named master
git push heroku master
```

Heroku will automatically:
- Detect it's a Java/Maven project
- Run `mvn clean install -Pproduction` (if configured)
- Build and deploy the application

#### Option B: Using Heroku Deploy Plugin

If you prefer to deploy the JAR directly:

```bash
# Install the Heroku deploy plugin
heroku plugins:install java

# Deploy the JAR file
heroku deploy:jar target/coffee-tracker-1.0-SNAPSHOT.jar --app your-app-name
```

### Step 8: Set JPA Configuration for Production

For production, you should use proper database migrations. Update your `application-prod.properties` to use `validate` instead of `update`:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Then use Flyway or Liquibase for database migrations (recommended for production).

### Step 9: Open Your Application

```bash
# Open the application in your browser
heroku open
```

### Step 10: Monitor Logs

```bash
# View real-time logs
heroku logs --tail

# View recent logs
heroku logs
```

## Database Configuration Options

### Option 1: External PostgreSQL Database

If you have an external PostgreSQL database:

```bash
heroku config:set DATABASE_URL="jdbc:postgresql://host:port/database"
heroku config:set DATABASE_USERNAME="username"
heroku config:set DATABASE_PASSWORD="password"
```

### Option 2: Heroku Postgres (Recommended)

Heroku Postgres automatically sets `DATABASE_URL` in a different format. You may need to adjust your configuration to parse it or use Spring's datasource URL parser.

Add this to your `application-prod.properties`:

```properties
# Let Spring parse Heroku's DATABASE_URL
spring.datasource.url=${DATABASE_URL}
```

**Note**: Heroku's `DATABASE_URL` format is `postgres://user:password@host:port/database`, while JDBC expects `jdbc:postgresql://...`. You might need a custom configuration to convert it.

## Security Considerations

1. **HTTPS**: Heroku provides HTTPS automatically for `*.herokuapp.com` domains
2. **Environment Variables**: Never commit sensitive credentials to Git
3. **Database**: Use strong passwords and restrict access
4. **Session Security**: The app uses Spring Security with secure session management
5. **Production Mode**: Vaadin production mode disables development tools

## Scaling

```bash
# Scale to multiple dynos (for paid plans)
heroku ps:scale web=2

# Scale back to 1 dyno
heroku ps:scale web=1
```

## Troubleshooting

### Check Application Status
```bash
heroku ps
```

### View Configuration
```bash
heroku config
```

### Restart Application
```bash
heroku restart
```

### Run One-off Dynos
```bash
# Open a bash session on Heroku
heroku run bash
```

### Database Access
```bash
# Connect to PostgreSQL
heroku pg:psql
```

## Cost Considerations

- **Eco Dyno**: $5/month for basic apps (sleeps after 30 min of inactivity)
- **Basic Dyno**: $7/month (doesn't sleep)
- **Heroku Postgres**: Starting from $5/month for essential-0 plan
- **Free Tier**: No longer available as of November 2022

## Updating the Application

To deploy updates:

```bash
# Make your changes
git add .
git commit -m "Update description"

# Build production version
mvn clean package -Pproduction

# Deploy
git push heroku main
```

## Alternative: Using Docker on Heroku

If you prefer Docker deployment, you can use Heroku's container registry:

```bash
# Login to Heroku container registry
heroku container:login

# Build and push (requires Dockerfile)
heroku container:push web

# Release
heroku container:release web
```

## Additional Resources

- [Heroku Java Documentation](https://devcenter.heroku.com/categories/java-support)
- [Vaadin Deployment Documentation](https://vaadin.com/docs/latest/production)
- [Heroku Postgres Documentation](https://devcenter.heroku.com/articles/heroku-postgresql)
- [Spring Boot on Heroku](https://devcenter.heroku.com/articles/deploying-spring-boot-apps-to-heroku)

## Production Checklist

- [ ] Production build successfully completes
- [ ] Database credentials configured
- [ ] Application starts without errors
- [ ] HTTPS is working
- [ ] User registration and login work
- [ ] Database persists data correctly
- [ ] Logs show no critical errors
- [ ] Application accessible from public URL

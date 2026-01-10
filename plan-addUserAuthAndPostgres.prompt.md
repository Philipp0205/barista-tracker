# Plan: Add User Authentication and PostgreSQL Database

**Status: ✅ IMPLEMENTED**

This plan adds Spring Security-based user authentication with login/registration views and migrates from H2 to PostgreSQL database, based on the implementation patterns from the kubri3 project. The coffee-tracker data will be user-scoped so each user sees only their own coffee beans and espresso shots.

## Steps

1. ✅ **Update pom.xml dependencies** - Added Spring Security, PostgreSQL driver dependencies (H2 scoped to test only).

2. ✅ **Create security package with core classes** - Created `com.kurrle.security` package with:
   - `User` entity (implements `UserDetails`)
   - `Role` enum
   - `UserRepository`
   - `UserService`
   - `UserDetailsServiceImpl`
   - `AuthenticatedUser` (session-scoped component)

3. ✅ **Implement Spring Security configuration** - Created `SecurityConfig` using `VaadinSecurityConfigurer` to configure authentication, password encoding, and protect all routes except login/register.

4. ✅ **Create login and registration views** - Added `LoginView` with Vaadin `LoginForm` and `RegisterView` with form fields for first name, last name, email, and password.

5. ✅ **Add user relationships to entities** - Updated `CoffeeBean` and `EspressoShot` entities with `@ManyToOne` relationship to `User`, and modified services/repositories to filter by authenticated user.

6. ✅ **Configure PostgreSQL in application.properties** - Updated application.properties with PostgreSQL datasource configuration, created profile-specific files (`application-local.properties` and `application-prod.properties`) for different environments.

7. ✅ **Add security annotations to views** - Added `@PermitAll` to all views (`BeansView`, `ShotTrackerView`, `ReviewView`, `RecommendationView`) and `MainLayout`.

8. ✅ **Add logout functionality** - Added logout button and user info display to `MainLayout` footer.

## Files Created/Modified

### New Files:
- `src/main/java/com/kurrle/security/Role.java`
- `src/main/java/com/kurrle/security/User.java`
- `src/main/java/com/kurrle/security/UserRepository.java`
- `src/main/java/com/kurrle/security/UserService.java`
- `src/main/java/com/kurrle/security/UserDetailsServiceImpl.java`
- `src/main/java/com/kurrle/security/AuthenticatedUser.java`
- `src/main/java/com/kurrle/security/SecurityConfig.java`
- `src/main/java/com/kurrle/security/LoginView.java`
- `src/main/java/com/kurrle/security/RegisterView.java`
- `src/main/resources/application-local.properties`
- `src/main/resources/application-prod.properties`

### Modified Files:
- `pom.xml` - Added security and PostgreSQL dependencies
- `src/main/resources/application.properties` - PostgreSQL configuration
- `src/main/java/com/kurrle/coffee/CoffeeBean.java` - Added User relationship
- `src/main/java/com/kurrle/coffee/EspressoShot.java` - Added User relationship
- `src/main/java/com/kurrle/coffee/CoffeeBeanRepository.java` - User-filtered queries
- `src/main/java/com/kurrle/coffee/EspressoShotRepository.java` - User-filtered queries
- `src/main/java/com/kurrle/coffee/CoffeeBeanService.java` - User-scoped operations
- `src/main/java/com/kurrle/coffee/EspressoShotService.java` - User-scoped operations
- `src/main/java/com/kurrle/base/ui/MainLayout.java` - Added logout and user info
- `src/main/java/com/kurrle/base/ui/beans/BeansView.java` - Added @PermitAll
- `src/main/java/com/kurrle/base/ui/shottracker/ShotTrackerView.java` - Added @PermitAll
- `src/main/java/com/kurrle/base/ui/review/ReviewView.java` - Added @PermitAll
- `src/main/java/com/kurrle/base/ui/recommendation/RecommendationView.java` - Added @PermitAll

## Setup Instructions

### Local Development
1. Install PostgreSQL if not already installed
2. Create the database: `CREATE DATABASE coffee_tracker;`
3. Update credentials in `application-local.properties` if needed
4. Run with: `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`

### Production
Set environment variables:
- `DATABASE_URL` - PostgreSQL connection URL
- `DATABASE_USERNAME` - Database username
- `DATABASE_PASSWORD` - Database password
- `SPRING_PROFILES_ACTIVE=prod`

## Further Considerations (Not Implemented)

1. **WebAuthn/Passkey support** - The kubri3 project includes passkey authentication. This could be added as a future enhancement.

2. **Email verification** - Consider adding email verification for new registrations.

3. **Password reset** - Add forgot password functionality.
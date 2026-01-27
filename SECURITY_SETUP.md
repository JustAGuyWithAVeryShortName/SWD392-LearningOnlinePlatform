# Security Configuration Setup

## Important: Configure Your Local Environment

This repository uses sensitive configuration files that are **NOT** tracked by git for security reasons.

### Required Setup Steps

1. **Copy the example configuration file:**
   ```bash
   cd Backend/src/main/resources
   cp application.properties.example application.properties
   ```

2. **Update `application.properties` with your actual credentials:**
   - Database credentials
   - JWT secret key
   - Google OAuth credentials
   - Email credentials  
   - Cloudinary credentials

3. **Create `credentials.json`** for Google API access in `Backend/src/main/resources/`

4. **Never commit these files** - they are already in `.gitignore`

### Files Excluded from Git

The following files contain sensitive data and are excluded:
- `Backend/src/main/resources/application.properties`
- `Backend/src/main/resources/credentials.json`
- `Backend/tokens/`
- `tokens/`

### Security Best Practices

- **Never commit secrets** to version control
- Use environment variables for production deployments
- Rotate any credentials that may have been exposed
- Review `.gitignore` before committing

## Credential Rotation Required

⚠️ **IMPORTANT**: The OAuth credentials and tokens that were previously committed have been exposed and should be rotated immediately:

1. **Google OAuth Credentials**: 
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Navigate to APIs & Credentials
   - Delete the exposed Client ID and Secret
   - Create new OAuth 2.0 credentials

2. **Gmail App Password**:
   - Revoke the exposed app password
   - Generate a new app password

3. **Cloudinary Credentials**:
   - Rotate your API key and secret in Cloudinary dashboard

4. **JWT Secret**:
   - Generate a new random secret key

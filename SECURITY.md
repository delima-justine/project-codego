# Security Guidelines

## API Key Management

### Overview
This project uses `local.properties` to store sensitive API keys. This file is **gitignored** and will not be committed to version control.

### Current API Keys

1. **News API Key** (`NEWS_API_KEY`)
   - Used for: Fetching news articles from NewsAPI.org
   - Location: `local.properties`
   - Access in code: `BuildConfig.NEWS_API_KEY`

### Setup Instructions

1. Copy `local.properties.example` to `local.properties`
2. Add your actual API keys to `local.properties`
3. Never commit `local.properties` to Git

### How It Works

1. **local.properties** - Stores the actual API keys (gitignored)
2. **build.gradle.kts** - Reads from `local.properties` and creates BuildConfig fields
3. **Code** - Uses `BuildConfig.NEWS_API_KEY` to access the key

### Adding New API Keys

To add a new API key:

1. Add to `local.properties`:
   ```properties
   YOUR_NEW_API_KEY=your_actual_key_here
   ```

2. Add to `app/build.gradle.kts` in the `defaultConfig` block:
   ```kotlin
   buildConfigField("String", "YOUR_NEW_API_KEY", "\"${properties.getProperty("YOUR_NEW_API_KEY", "")}\"")
   ```

3. Use in code:
   ```kotlin
   val apiKey = BuildConfig.YOUR_NEW_API_KEY
   ```

4. Update `local.properties.example` with placeholder:
   ```properties
   YOUR_NEW_API_KEY=your_key_here
   ```

### Security Best Practices

✅ **DO:**
- Keep API keys in `local.properties`
- Use BuildConfig to access keys
- Add new keys to `local.properties.example` as placeholders
- Rotate keys if they are compromised

❌ **DON'T:**
- Hardcode API keys in source code
- Commit `local.properties` to Git
- Share API keys publicly
- Log API keys to console

### What to Do If a Key Is Leaked

1. **Immediately revoke the compromised key** from the API provider's dashboard
2. **Generate a new key** from the API provider
3. **Update `local.properties`** with the new key
4. **Notify team members** to update their local files
5. **Review commit history** to ensure the key wasn't committed
6. If committed, consider using tools like `git-filter-branch` or BFG Repo-Cleaner to remove it from history

### Current Status

⚠️ **IMPORTANT:** The News API key `bcad6e37557b454d41130d680f6ec8b9` was previously hardcoded and may be exposed in Git history.

**Action Required:**
1. Generate a new API key from https://newsapi.org/
2. Update `local.properties` with the new key
3. Revoke the old key from the NewsAPI dashboard

### CI/CD Setup (Future)

When setting up CI/CD:
- Add API keys as environment variables or secrets in your CI/CD platform
- Use build scripts to inject them into `local.properties` during build
- Never expose keys in build logs

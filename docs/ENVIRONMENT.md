# Environment Variables

Keep it simple: copy `.env.example` to `.env` and fill in values. Do not commit your real secrets.

## Required
- DB_URL (e.g., jdbc:postgresql://localhost:5432/postgres)
- DB_USERNAME (e.g., postgres)
- DB_PASSWORD (e.g., secret)
- JWT_SECRET (base64-encoded, at least 32 bytes after decoding)
- MAIL_ENABLED (true/false)
- MAIL_HOST, MAIL_PORT
- MAIL_USERNAME, MAIL_PASSWORD (use provider-specific app password for Gmail)
- Optional OAuth2: LINKEDIN_CLIENT_ID/SECRET, GOOGLE_CLIENT_ID/SECRET, FACEBOOK_CLIENT_ID/SECRET

## Generate a secure JWT secret (base64)
Pick one:
```
# OpenSSL
openssl rand -base64 32

# Python
python - <<'PY'
import os, base64; print(base64.b64encode(os.urandom(32)).decode())
PY

# Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

## Example .env
```
DB_URL=jdbc:postgresql://localhost:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=secret

# Must be base64
JWT_SECRET=REPLACE_WITH_BASE64_SECRET

MAIL_ENABLED=false
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-app-password

# OAuth (optional)
LINKEDIN_CLIENT_ID=your-linkedin-client-id
LINKEDIN_CLIENT_SECRET=your-linkedin-client-secret
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
FACEBOOK_CLIENT_ID=your-facebook-app-id
FACEBOOK_CLIENT_SECRET=your-facebook-app-secret
```

Tip: export in your shell or use an env manager in your IDE.

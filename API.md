# WeSports Backend API Documentation

## Authentication Flow

**Registration Steps:**
1. Email Registration → OTP Verification → Password Setup → Role Selection → Profile Form → Onboarding

**Authentication Logic:**
1. Backend checks **cookies first** (web clients)
2. Then checks **Authorization header** (mobile clients)
3. Returns error if neither found

---

## 1. Registration Endpoints

### 1.1 Start Registration
**POST** `/api/auth/register/start`

**Send:**
```json
{
  "email": "user@example.com",
  "language": "en"  // Optional: "en", "fr", "ar"
}
```

**Get:**
```json
{
  "message": "Registration OTP sent successfully",
  "status": "success",
  "nextStep": "verify-otp"
}
```

### 1.2 Verify OTP
**POST** `/api/auth/register/verify-otp`

**Send:**
```json
{
  "email": "user@example.com",
  "otpCode": "123456"
}
```

**Get:**
```json
{
  "message": "OTP verified successfully",
  "status": "success", 
  "nextStep": "setup-password",
  "registrationToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

### 1.3 Setup Password (Becomes Authenticated)
**POST** `/api/auth/register/setup-password`

**Send:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "registrationToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Get (Web):**
```json
{
  "status": "success",
  "message": "Registration completed successfully",
  "accessToken": null,     // Tokens are in cookies
  "refreshToken": null,
  "user": { "id": "123...", "email": "user@example.com" }
}
```

**Cookies Set (Web):**
```
accessToken=eyJhbGci... (15 min)
refresh_token=eyJhbGci... (30 days)
```

**Get (Mobile with X-Client-Type: mobile):**
```json
{
  "status": "success",
  "message": "Registration completed successfully",
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",    // Use this token
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "user": { "id": "123...", "email": "user@example.com" }
}
```

### 1.4 Role Selection (Requires Auth)
**POST** `/api/auth/register/select-role`

**Auth:** Cookies (web) OR `Authorization: Bearer <token>` (mobile)

**Send:**
```json
{
  "role": "PLAYER"  // PLAYER, CLUB, AGENT, COACH, REPRESENTANT
}
```

**Get:**
```json
{
  "message": "Role selected successfully",
  "status": "success",
  "nextStep": "profile-form"
}
```

### 1.5 Profile Form (Requires Auth)
**POST** `/api/auth/register/profile-form`

**Auth:** Cookies (web) OR `Authorization: Bearer <token>` (mobile)

**Send:**
```json
{
  "firstName": "John",
  "lastName": "Doe", 
  "birthDate": "1995-06-15",
  "nationality": "French",
  "lieuDeResidence": "Paris, France",
  "languages": ["French", "English"]
}
```

**Get:**
```json
{
  "message": "Profile form submitted successfully",
  "status": "success",
  "nextStep": "onboarding"
}
```

---

## 2. Onboarding (Player Only, Requires Auth)

### 2.1 Select Categories
**POST** `/api/onboarding/categories`

**Send:**
```json
{
  "categoryCodes": ["U19"]
}
```

### 2.2 Select Position
**POST** `/api/onboarding/position`

**Send:**
```json
{
  "positionCodes": ["ST"]
}
```

### 2.3 Get Available Options
**GET** `/api/onboarding/positions`
**GET** `/api/onboarding/categories`

---

## 3. Available Data

### Roles
`PLAYER`, `CLUB`, `AGENT`, `COACH`, `REPRESENTANT`

### Positions
`GK`, `CB`, `LB`, `RB`, `LWB`, `RWB`, `CDM`, `CM`, `CAM`, `LM`, `RM`, `LW`, `RW`, `ST`, `CF`, `LF`, `RF`

### Categories
`U13`, `U14`, `U15`, `U16`, `U17`, `U18`, `U19`, `SENIOR`

### Languages
`en`, `fr`, `ar`

---

## 5. Error Handling

**Error Response:**
```json
{
  "status": "error",
  "message": "Detailed error message"
}
```

**HTTP Codes:**
- `200` - Success
- `400` - Bad Request
- `401` - Need Authentication
- `500` - Server Error


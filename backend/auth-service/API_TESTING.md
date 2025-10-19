# Auth Service API Testing Guide

## Setup

Base URL: `http://localhost:8081`

## Test Sequence

### 1. Register a New User

**Endpoint:** `POST /api/auth/register`

**Request:**
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "TestPass123",
  "displayName": "Test User"
}
```

**Expected Response (200):**
```json
{
  "user": {
    "id": "user-id",
    "username": "testuser",
    "email": "test@example.com",
    "displayName": "Test User",
    "status": "offline",
    "roles": ["USER"],
    "emailVerified": false
  },
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token",
  "message": "Registration successful. Please verify your email."
}
```

**Save:** `accessToken` and `refreshToken` for subsequent requests

---

### 2. Login

**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "emailOrUsername": "test@example.com",
  "password": "TestPass123"
}
```

**Expected Response (200):**
```json
{
  "user": {
    "id": "user-id",
    "username": "testuser",
    "email": "test@example.com",
    "displayName": "Test User",
    "status": "online",
    "roles": ["USER"]
  },
  "accessToken": "new-jwt-access-token",
  "refreshToken": "new-jwt-refresh-token",
  "message": "Login successful"
}
```

---

### 3. Get User Profile

**Endpoint:** `GET /api/users/profile`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Expected Response (200):**
```json
{
  "id": "user-id",
  "username": "testuser",
  "email": "test@example.com",
  "displayName": "Test User",
  "avatarUrl": null,
  "status": "online",
  "roles": ["USER"],
  "settings": {},
  "createdAt": "2024-01-01T00:00:00",
  "lastSeen": "2024-01-01T00:00:00",
  "emailVerified": false
}
```

---

### 4. Update Profile

**Endpoint:** `PUT /api/users/profile`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Request:**
```json
{
  "displayName": "Updated Test User",
  "avatarUrl": "https://example.com/avatar.jpg",
  "status": "online",
  "settings": {
    "theme": "dark",
    "notifications": true
  }
}
```

**Expected Response (200):**
```json
{
  "id": "user-id",
  "username": "testuser",
  "email": "test@example.com",
  "displayName": "Updated Test User",
  "avatarUrl": "https://example.com/avatar.jpg",
  "status": "online",
  "roles": ["USER"],
  "settings": {
    "theme": "dark",
    "notifications": true
  }
}
```

---

### 5. Search Users

**Endpoint:** `GET /api/users/search?q=test`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Expected Response (200):**
```json
[
  {
    "id": "user-id",
    "username": "testuser",
    "displayName": "Updated Test User",
    "avatarUrl": "https://example.com/avatar.jpg",
    "status": "online"
  }
]
```

---

### 6. Get User Basic Info

**Endpoint:** `GET /api/users/{userId}/basic`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Expected Response (200):**
```json
{
  "id": "user-id",
  "username": "testuser",
  "displayName": "Updated Test User",
  "avatarUrl": "https://example.com/avatar.jpg",
  "status": "online"
}
```

---

### 7. Change Password

**Endpoint:** `PUT /api/users/password`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Request:**
```json
{
  "currentPassword": "TestPass123",
  "newPassword": "NewTestPass123"
}
```

**Expected Response (200):**
```json
{
  "message": "Password changed successfully",
  "success": true
}
```

---

### 8. Get Active Sessions

**Endpoint:** `GET /api/users/sessions`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Expected Response (200):**
```json
[
  {
    "id": "session-id",
    "deviceInfo": {
      "userAgent": "Mozilla/5.0..."
    },
    "ipAddress": "127.0.0.1",
    "createdAt": "2024-01-01T00:00:00",
    "lastAccessedAt": "2024-01-01T00:00:00",
    "isActive": true
  }
]
```

---

### 9. Refresh Token

**Endpoint:** `POST /api/auth/refresh-token`

**Request:**
```json
{
  "refreshToken": "your-refresh-token"
}
```

**Expected Response (200):**
```json
{
  "accessToken": "new-access-token",
  "refreshToken": "new-refresh-token"
}
```

---

### 10. Forgot Password

**Endpoint:** `POST /api/auth/forgot-password`

**Request:**
```json
{
  "email": "test@example.com"
}
```

**Expected Response (200):**
```json
{
  "message": "Password reset link sent to your email",
  "success": true
}
```

---

### 11. Reset Password

**Endpoint:** `POST /api/auth/reset-password`

**Request:**
```json
{
  "token": "reset-token-from-email",
  "newPassword": "NewSecurePass123"
}
```

**Expected Response (200):**
```json
{
  "message": "Password reset successful",
  "success": true
}
```

---

### 12. Delete Session

**Endpoint:** `DELETE /api/users/sessions/{sessionId}`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Expected Response (200):**
```json
{
  "message": "Session deleted successfully",
  "success": true
}
```

---

### 13. Logout

**Endpoint:** `POST /api/auth/logout`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Request:**
```json
{
  "refreshToken": "your-refresh-token"
}
```

**Expected Response (200):**
```json
{
  "message": "Logout successful",
  "success": true
}
```

---

### 14. Logout All Devices

**Endpoint:** `POST /api/auth/logout-all`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Expected Response (200):**
```json
{
  "message": "Logged out from all devices",
  "success": true
}
```

---

## Admin Endpoints (Requires ADMIN role)

### 15. Register Admin User

First, register a user and manually update their role in MongoDB:

```javascript
// In MongoDB shell
db.users.updateOne(
  { username: "admin" },
  { $set: { roles: ["USER", "ADMIN"] } }
)
```

### 16. Get All Users (Paginated)

**Endpoint:** `GET /api/admin/users?page=0&size=10&search=test`

**Headers:**
```
Authorization: Bearer {admin-accessToken}
```

**Expected Response (200):**
```json
{
  "content": [
    {
      "id": "user-id",
      "username": "testuser",
      "email": "test@example.com",
      "displayName": "Test User",
      "status": "online",
      "roles": ["USER"]
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

---

### 17. Update User Roles

**Endpoint:** `PUT /api/admin/users/{userId}/roles`

**Headers:**
```
Authorization: Bearer {admin-accessToken}
```

**Request:**
```json
{
  "roles": ["USER", "MODERATOR"]
}
```

**Expected Response (200):**
```json
{
  "id": "user-id",
  "username": "testuser",
  "email": "test@example.com",
  "roles": ["USER", "MODERATOR"]
}
```

---

### 18. Get Statistics

**Endpoint:** `GET /api/admin/statistics`

**Headers:**
```
Authorization: Bearer {admin-accessToken}
```

**Expected Response (200):**
```json
{
  "totalUsers": 10,
  "activeUsers": 5,
  "newUsersToday": 2
}
```

---

### 19. Delete User

**Endpoint:** `DELETE /api/admin/users/{userId}`

**Headers:**
```
Authorization: Bearer {admin-accessToken}
```

**Expected Response (200):**
```json
{
  "message": "User deleted successfully",
  "success": true
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "message": "Email already registered",
  "success": false
}
```

### 401 Unauthorized
```json
{
  "message": "Invalid credentials",
  "success": false
}
```

### 404 Not Found
```json
{
  "message": "User not found",
  "success": false
}
```

### Validation Error
```json
{
  "username": "Username must be between 3 and 30 characters",
  "email": "Email should be valid",
  "password": "Password must be at least 8 characters"
}
```

---

## Testing Tips

1. **Use Postman or Thunder Client** for testing
2. **Save tokens** in environment variables for easy reuse
3. **Test error cases** (invalid credentials, expired tokens, etc.)
4. **Check MongoDB** to verify data persistence
5. **Monitor Kafka topics** to verify events are being published
6. **Check Redis** to verify caching is working
7. **Test rate limiting** by making multiple rapid requests
8. **Test account lockout** by failing login 5 times

## Curl Examples

### Register
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "TestPass123",
    "displayName": "Test User"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrUsername": "test@example.com",
    "password": "TestPass123"
  }'
```

### Get Profile
```bash
curl -X GET http://localhost:8081/api/users/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

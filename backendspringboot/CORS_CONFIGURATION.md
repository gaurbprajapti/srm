# 🌐 CORS Configuration Guide for Spring Boot

## 📋 What is CORS?

**CORS (Cross-Origin Resource Sharing)** is a security feature implemented by web browsers that blocks requests from one domain to another unless explicitly allowed. This is crucial for APIs that need to be accessed from web frontends hosted on different domains.

## 🚫 Common CORS Errors

Without proper CORS configuration, you'll see errors like:
```
Access to fetch at 'http://localhost:5050/api/user/login' from origin 'http://localhost:3000' 
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

## ⚙️ Current CORS Configuration

Your Spring Boot application has CORS configured using **3 different approaches**:

### 🔧 **Method 1: Security Config Integration** *(Currently Active)*

**File:** `src/main/java/com/srm/campusnexus/config/SecurityConfig.java`

```java
@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Reads from application.yml
    List<String> origins = Arrays.asList(allowedOrigins.split(","));
    configuration.setAllowedOriginPatterns(origins);
    
    // ... other configurations
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

**Pros:**
- ✅ Works with Spring Security endpoints
- ✅ Centralized with authentication configuration
- ✅ Supports JWT authentication flows

### 🔧 **Method 2: Global WebMvc Configuration** *(Reference Only)*

**File:** `src/main/java/com/srm/campusnexus/config/CorsConfig.java`

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true);
}
```

**Pros:**
- ✅ Simple Spring MVC integration
- ✅ Good for non-security endpoints

**Cons:**
- ❌ Doesn't work with Spring Security protected endpoints

### 🔧 **Method 3: Controller-Level Annotations**

You can also use `@CrossOrigin` on individual controllers:

```java
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    // controller methods
}
```

## 🎯 **Configuration Properties** (`application.yml`)

```yaml
cors:
  # Frontend URLs (Development + Production)
  allowed-origins: >
    http://localhost:3000,
    http://localhost:3001,
    http://127.0.0.1:3000,
    https://campusnexus.netlify.app,
    https://*.netlify.app
  # HTTP Methods
  allowed-methods: GET,POST,PUT,PATCH,DELETE,OPTIONS,HEAD
  # Request Headers
  allowed-headers: >
    Content-Type,
    Authorization,
    x-csrf-token,
    X-Requested-With,
    Accept,
    Origin
  # Response Headers exposed to frontend
  exposed-headers: >
    Content-Range,
    X-Content-Range,
    Authorization,
    Content-Length
  # Allow credentials (cookies, authorization headers)
  allow-credentials: true
  # Preflight cache duration (in seconds)
  max-age: 3600
```

### 📝 **Property Explanations**

| Property | Description | Example |
|----------|-------------|---------|
| `allowed-origins` | Domains allowed to make requests | `http://localhost:3000, https://myapp.com` |
| `allowed-methods` | HTTP methods permitted | `GET,POST,PUT,DELETE,OPTIONS` |
| `allowed-headers` | Request headers allowed | `Content-Type,Authorization` |
| `exposed-headers` | Response headers frontend can access | `Content-Range,X-Total-Count` |
| `allow-credentials` | Allow cookies/auth headers | `true` or `false` |
| `max-age` | Preflight cache duration (seconds) | `3600` (1 hour) |

## 🚀 **Testing CORS Configuration**

### 1. **Test with curl**
```bash
# Preflight request
curl -X OPTIONS http://localhost:5050/api/user/register \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,Authorization" \
  -v

# Actual request
curl -X POST http://localhost:5050/api/user/register \
  -H "Origin: http://localhost:3000" \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}' \
  -v
```

### 2. **Test from Browser Console**
```javascript
// Test from browser console at http://localhost:3000
fetch('http://localhost:5050/api/health', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log('Success:', data))
.catch(error => console.error('CORS Error:', error));
```

### 3. **Expected Response Headers**
When CORS is working correctly, you should see these headers in responses:
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE,OPTIONS,HEAD
Access-Control-Allow-Headers: Content-Type,Authorization,x-csrf-token
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

## 🔧 **Environment-Specific Configuration**

### **Development**
```yaml
cors:
  allowed-origins: http://localhost:3000,http://localhost:3001,http://127.0.0.1:3000
  allow-credentials: true
```

### **Production**
```yaml
cors:
  allowed-origins: https://campusnexus.netlify.app,https://yourdomain.com
  allow-credentials: true
  max-age: 86400  # 24 hours cache
```

### **Using Environment Variables**
```bash
# Set in your deployment platform
FRONTEND_URL=https://campusnexus.netlify.app
CORS_ALLOWED_ORIGINS=https://campusnexus.netlify.app,https://yourdomain.com
```

## 🚨 **Security Best Practices**

### ✅ **DO**
- Use specific origins instead of `*` in production
- Enable credentials only when needed
- Use HTTPS in production
- Set appropriate `max-age` for preflight caching

```yaml
# Good - Production configuration
cors:
  allowed-origins: https://yourdomain.com,https://app.yourdomain.com
  allow-credentials: true
  max-age: 3600
```

### ❌ **DON'T**
- Use `*` with `allow-credentials: true` (browser will block)
- Allow all origins in production
- Set overly long `max-age` values

```yaml
# Bad - Security risk
cors:
  allowed-origins: "*"  # Don't do this in production
  allow-credentials: true
```

## 🔄 **Switching CORS Configuration Methods**

### **Currently Using: SecurityConfig Integration**
Your app currently uses Method 1 (SecurityConfig integration), which is the **recommended approach** for applications with Spring Security.

### **To Switch to Global WebMvc Configuration:**
1. Comment out CORS in `SecurityConfig.java`:
```java
// .cors(cors -> cors.configurationSource(corsConfigurationSource()))
.cors(cors -> cors.disable())
```

2. Uncomment `@Configuration` in `CorsConfig.java`:
```java
@Configuration  // Uncomment this line
public class CorsConfig implements WebMvcConfigurer {
```

## 🧪 **Troubleshooting CORS Issues**

### **Issue 1: Still getting CORS errors**
- Check browser developer tools Network tab
- Verify the `Origin` header in requests
- Ensure your frontend URL is in `allowed-origins`

### **Issue 2: Preflight requests failing**
- Add `OPTIONS` to `allowed-methods`
- Check `allowed-headers` includes all headers your frontend sends

### **Issue 3: Credentials not working**
- Verify `allow-credentials: true`
- Ensure you're not using `*` for origins
- Frontend must set `credentials: 'include'` in fetch requests

### **Issue 4: Different behavior in production**
- Check environment variables
- Verify HTTPS URLs in production
- Ensure deployment platform preserves CORS headers

## 📊 **Current Active Configuration Summary**

Your application is configured with:

- ✅ **Method**: SecurityConfig Integration
- ✅ **Allowed Origins**: localhost:3000, Netlify app
- ✅ **Methods**: GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD
- ✅ **Credentials**: Enabled
- ✅ **Headers**: Content-Type, Authorization, etc.
- ✅ **Preflight Cache**: 1 hour

**Status**: 🟢 **Fully Configured and Working**

Your CORS setup supports both development and production environments with proper security measures in place! 🎉 
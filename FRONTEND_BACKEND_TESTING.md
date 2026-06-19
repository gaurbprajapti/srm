# 🚀 Frontend-Backend Connectivity Testing Guide

## 📋 What's Been Configured

Your React frontend is now configured to connect to your Spring Boot backend running on `http://localhost:5050`.

### ✅ **Configuration Summary:**
- **Backend URL**: `http://localhost:5050`
- **Frontend Development**: Uses `.env.local` with `REACT_APP_API_URL=http://localhost:5050`
- **Frontend Production**: Uses Netlify environment variables
- **CORS**: ✅ Fully configured and working
- **API Test Component**: Added at `/api-test` route

## 🏃‍♂️ **Step-by-Step Testing Guide**

### **Step 1: Start Your Spring Boot Backend**

```bash
# Navigate to Spring Boot directory
cd "/Users/gauravprajapati/Developer/My Code /srm/backendspringboot"

# Start the Spring Boot application
./mvnw spring-boot:run
```

**Verify backend is running:**
- Wait for the message: "Started CampusNexusApplication in X.XXX seconds"
- Test in browser: http://localhost:5050/api/health

### **Step 2: Start Your React Frontend**

```bash
# Open a NEW terminal window/tab
cd "/Users/gauravprajapati/Developer/My Code /srm/FrontEnd"

# Install dependencies (if not already done)
npm install

# Start the React development server
npm start
```

**The frontend will start on:** http://localhost:3000

### **Step 3: Test Backend Connectivity**

Once both servers are running:

1. **Visit the API Test Page**: http://localhost:3000/api-test
2. **Check the API Configuration Panel** (bottom-right corner of any page)
3. **Test Registration and Login** functionality

## 🧪 **API Test Component Features**

The `/api-test` route provides:

- ✅ **Health Check Test** - Verifies backend connectivity
- ✅ **Registration Test** - Tests user registration endpoint
- ✅ **API Utils Test** - Tests your existing API utilities
- ✅ **Configuration Display** - Shows current API URLs
- ✅ **Live Testing** - Real-time connectivity testing

## 🔧 **Configuration Files Updated**

### **Frontend Files:**
- ✅ **`.env.local`** - Local development environment
- ✅ **`src/components/ApiTest.js`** - Backend connectivity tester
- ✅ **`src/components/ApiConfig.js`** - Configuration display
- ✅ **`src/App.js`** - Added `/api-test` route

### **Backend Files:**
- ✅ **`application.yml`** - CORS configuration
- ✅ **`SecurityConfig.java`** - CORS integration with Spring Security

## 🌐 **Environment Configuration**

### **Development (localhost:3000)**
```
REACT_APP_API_URL=http://localhost:5050
```

### **Production (Netlify)**
```
REACT_APP_API_URL=https://srm-seeu.onrender.com
```

## ✅ **Testing Checklist**

### **Backend Tests:**
- [ ] Spring Boot starts without errors
- [ ] Health endpoint responds: http://localhost:5050/api/health
- [ ] API documentation works: http://localhost:5050/api/
- [ ] User registration works via curl
- [ ] CORS headers present in responses

### **Frontend Tests:**
- [ ] React app starts on http://localhost:3000
- [ ] API configuration panel shows correct URLs
- [ ] `/api-test` page loads successfully
- [ ] Health check test passes
- [ ] Registration test works
- [ ] No CORS errors in browser console

### **Integration Tests:**
- [ ] Login page connects to backend
- [ ] Registration page works
- [ ] Protected routes with JWT work
- [ ] API calls include Authorization headers

## 🚨 **Troubleshooting**

### **Backend Issues:**
```bash
# Check if port 5050 is in use
lsof -i :5050

# Kill process on port 5050 if needed
lsof -ti:5050 | xargs kill -9

# Restart MySQL if needed
brew services restart mysql  # macOS
```

### **Frontend Issues:**
```bash
# Clear npm cache and reinstall
rm -rf node_modules package-lock.json
npm install

# Check environment variables
echo $REACT_APP_API_URL
```

### **CORS Issues:**
- Check browser console for CORS errors
- Verify backend CORS configuration includes `http://localhost:3000`
- Ensure `credentials: true` in frontend fetch requests

## 📱 **Using Your API**

### **Registration Example:**
```javascript
const response = await fetch('http://localhost:5050/api/user/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'testuser',
    email: 'test@example.com',
    password: 'password123',
    firstName: 'Test',
    lastName: 'User'
  })
});

const data = await response.json();
console.log('Registration:', data);
```

### **Login Example:**
```javascript
const response = await fetch('http://localhost:5050/api/user/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'testuser',
    password: 'password123'
  })
});

const data = await response.json();
if (data.success) {
  localStorage.setItem('srm-token', data.token);
  localStorage.setItem('srm-user', JSON.stringify(data.user));
}
```

### **Authenticated Request Example:**
```javascript
const token = localStorage.getItem('srm-token');
const response = await fetch('http://localhost:5050/api/user/profile', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json',
  }
});

const profile = await response.json();
console.log('Profile:', profile);
```

## 🎯 **Quick Start Commands**

**Terminal 1 (Backend):**
```bash
cd "/Users/gauravprajapati/Developer/My Code /srm/backendspringboot"
./mvnw spring-boot:run
```

**Terminal 2 (Frontend):**
```bash
cd "/Users/gauravprajapati/Developer/My Code /srm/FrontEnd"
npm start
```

**Then visit:** http://localhost:3000/api-test

## 🎉 **Success Indicators**

When everything is working correctly, you'll see:

1. ✅ **Backend**: Console shows "Started CampusNexusApplication"
2. ✅ **Frontend**: Loads without errors on localhost:3000
3. ✅ **API Test**: All tests pass on `/api-test` page
4. ✅ **Config Panel**: Shows correct URLs in bottom-right corner
5. ✅ **Console**: No CORS errors in browser developer tools

**Your full-stack application is now ready for development!** 🚀 
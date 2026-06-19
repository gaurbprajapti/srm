# 🗄️ MySQL Database Setup for Campus Nexus

## 📋 Prerequisites

### 1. **MySQL Server Installation**
Make sure MySQL is installed and running on your local machine:
- **MySQL Server 8.0+** recommended
- **Default port**: 3306
- **Root password**: 123456 (as specified)

### 2. **Verify MySQL Installation**
```bash
mysql --version
# Should show: mysql Ver 8.0.x
```

## 🚀 Database Setup

### **Step 1: Create Database**
Connect to MySQL and run the setup script:

```bash
# Connect to MySQL
mysql -u root -p123456

# Or run the setup script directly
mysql -u root -p123456 < mysql-setup.sql
```

### **Step 2: Verify Database Creation**
```sql
SHOW DATABASES;
USE campus_nexus;
SHOW TABLES;
```

## ⚙️ Spring Boot Configuration

### **Current Configuration (Automatic)**
Your `application.yml` is now configured for MySQL:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_nexus?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### **Alternative: Using MySQL Profile**
You can also use the dedicated MySQL profile:

```bash
# Run with MySQL profile
./mvnw spring-boot:run -Dspring.profiles.active=mysql
```

## 🏃‍♂️ Running the Application

### **Start the Application**
```bash
cd backendspringboot
./mvnw spring-boot:run
```

### **Verify Database Connection**
Check the console output for:
```
✅ HikariPool-1 - Start completed.
✅ Started CampusNexusApplication in X.XXX seconds
```

### **Test Health Endpoint**
```bash
curl http://localhost:5050/api/health
```

## 📊 Database Monitoring

### **View Tables Created by Hibernate**
```sql
USE campus_nexus;
SHOW TABLES;

-- Should show tables like:
-- users, clubs, jobs
```

### **Check Table Structure**
```sql
DESCRIBE users;
DESCRIBE clubs;
DESCRIBE jobs;
```

### **View Data**
```sql
SELECT * FROM users;
SELECT * FROM clubs;
SELECT * FROM jobs;
```

## 🔧 Configuration Details

### **Connection URL Parameters**
- `useSSL=false` - Disable SSL for local development
- `serverTimezone=UTC` - Set timezone to UTC
- `allowPublicKeyRetrieval=true` - Allow public key retrieval
- `createDatabaseIfNotExist=true` - Auto-create database (MySQL profile)

### **Hibernate Settings**
- `ddl-auto: update` - Auto-update table schema
- `MySQLDialect` - MySQL-specific SQL generation
- `show-sql: true` - Log all SQL queries

### **Connection Pool (HikariCP)**
- **Maximum pool size**: 20 connections
- **Minimum idle**: 5 connections
- **Connection timeout**: 20 seconds

## 🚨 Troubleshooting

### **Common Issues & Solutions**

#### **1. Connection Refused**
```
Error: Could not connect to MySQL server
```
**Solution**: Ensure MySQL server is running
```bash
# Start MySQL (varies by OS)
sudo systemctl start mysql  # Linux
brew services start mysql   # macOS
```

#### **2. Access Denied**
```
Error: Access denied for user 'root'@'localhost'
```
**Solution**: Verify password or reset it
```bash
mysql -u root -p
# Enter password: 123456
```

#### **3. Database Not Found**
```
Error: Unknown database 'campus_nexus'
```
**Solution**: Run the setup script to create database
```bash
mysql -u root -p123456 < mysql-setup.sql
```

#### **4. SSL Connection Error**
```
Error: SSL connection required
```
**Solution**: Connection URL already includes `useSSL=false`

## 🎯 Testing Your Setup

### **1. Register a Test User**
```bash
curl -X POST http://localhost:5050/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### **2. Check Database**
```sql
USE campus_nexus;
SELECT * FROM users;
-- Should show your test user
```

### **3. Login Test**
```bash
curl -X POST http://localhost:5050/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

## 📈 Production Considerations

### **For Production Deployment:**
1. **Create dedicated MySQL user** (not root)
2. **Enable SSL connections**
3. **Use environment variables** for sensitive data
4. **Configure connection pooling** for high load
5. **Set up database backups**

### **Environment Variables for Production:**
```bash
export DATABASE_URL="jdbc:mysql://your-mysql-host:3306/campus_nexus"
export DB_USERNAME="your_mysql_user"
export DB_PASSWORD="your_secure_password"
export HIBERNATE_DIALECT="org.hibernate.dialect.MySQLDialect"
```

## ✅ Configuration Complete!

Your Spring Boot application is now configured to use **MySQL** as the primary database:

- ✅ **MySQL 8.0** connector included
- ✅ **Database URL** configured for localhost:3306
- ✅ **Authentication** set up with root/123456
- ✅ **Hibernate** configured for MySQL dialect
- ✅ **Connection pooling** optimized
- ✅ **Auto table creation** enabled

**Your application will now use MySQL for data persistence!** 🎉 
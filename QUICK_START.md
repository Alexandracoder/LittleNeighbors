# Little Neighbors - Quick Start Guide

## Prerequisites
- Java 21
- Node.js 18+ and npm
- Maven (included via mvnw)

## 🚀 Start the Backend

```bash
# From project root
./mvnw spring-boot:run
```

The backend will start on **http://localhost:8080**

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:littleneighbors`
  - Username: `sa`
  - Password: (leave empty)

## 🎨 Start the Frontend

```bash
# From project root
cd frontend
npm install
npm run dev
```

The frontend will start on **http://localhost:5173**

## 🎯 Using the Application

### 1. Register a New Account
- Go to http://localhost:5173/register
- Fill in your details (email, name, password)
- Click "Register"

### 2. Login
- Go to http://localhost:5173/login
- Use your registered email and password
- Click "Login"

### 3. Browse Families
- After login, you'll be redirected to the families page
- Browse through neighborhood families
- View their profiles and children

### 4. Create Your Family Profile
- Click "Create Family Profile"
- Fill in family details
- Add children information

## 🎨 Color Palette

The application uses a warm, friendly color scheme:
- **Primary**: #ffcf53 (Sunshine Yellow)
- **Secondary**: #f6854d (Warm Orange)

## 📝 Default Admin Account

The database is pre-seeded with an admin account:
- Email: `admin@littleneighbors.com`
- Password: (check V3__init.sql migration - needs to be set properly)

## 🛠️ Development

### Backend Hot Reload
The Spring Boot dev tools are enabled, so most changes will auto-reload.

### Frontend Hot Reload
Vite provides instant hot module replacement (HMR) for all changes.

## 📚 API Documentation

Once the backend is running, visit:
**http://localhost:8080/swagger-ui.html**

You'll find complete API documentation for:
- Authentication
- User Management
- Family Management
- Children Management
- Interests
- Neighborhoods

## 🧪 Testing

```bash
# Backend tests
./mvnw test

# Frontend tests (when implemented)
cd frontend
npm test
```

## 🎉 You're Ready!

Start exploring the application and connecting families in your neighborhood!

# Little Neighbors - Project Structure

## Backend (Spring Boot)
```
src/main/java/com/alexandracoder/littleneighbors/
├── auth/          # Authentication endpoints
├── child/         # Children management
├── city/          # Cities
├── family/        # Family profiles
├── interest/      # Children interests
├── match/         # Family matching
├── neighborhood/  # Neighborhoods
├── security/      # JWT and security config
├── user/          # User management
└── swagger/       # API documentation
```

## Frontend (React + Vite)
```
frontend/
├── src/
│   ├── components/      # Reusable components
│   │   ├── Navbar.jsx
│   │   ├── Navbar.module.css
│   │   └── ProtectedRoute.jsx
│   ├── context/         # React context (Auth)
│   │   └── AuthContext.jsx
│   ├── pages/           # Page components
│   │   ├── Home.jsx
│   │   ├── Home.module.css
│   │   ├── Login.jsx
│   │   ├── Register.jsx
│   │   ├── Auth.module.css
│   │   ├── Families.jsx
│   │   ├── Families.module.css
│   │   ├── Profile.jsx
│   │   └── Profile.module.css
│   ├── services/        # API services
│   │   └── api.js
│   ├── App.jsx          # Main app component
│   ├── main.jsx         # Entry point
│   └── index.css        # Global styles
├── .env.local           # Environment variables
├── vite.config.js       # Vite configuration
└── package.json
```

## How to Run

### Backend
```bash
./mvnw spring-boot:run
```
API will be available at http://localhost:8080

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend will be available at http://localhost:5173

## Features Implemented

### Frontend
- ✅ User Registration
- ✅ User Login
- ✅ Protected Routes
- ✅ JWT Token Management
- ✅ Family Listing (Paginated)
- ✅ Family Profile View
- ✅ User Profile Page
- ✅ Responsive Design
- ✅ Custom Color Palette (#ffcf53, #f6854d)

### Backend
- ✅ JWT Authentication
- ✅ User Management
- ✅ Family CRUD
- ✅ Children Management
- ✅ Interests System
- ✅ Swagger Documentation
- ✅ Flyway Migrations
- ✅ H2 Database (Dev)

## Next Steps

1. Complete family profile creation form
2. Add children management interface
3. Implement family matching algorithm
4. Add messaging between families
5. Implement interest filtering
6. Add profile image upload
7. Create neighborhood-based search

# 🏘️ Little Neighbors - Complete Application

A full-stack application for connecting neighborhood families and organizing play dates for children.

## 🎨 Design

Built with a warm, welcoming color palette:
- **Primary**: #ffcf53 (Sunshine Yellow)
- **Secondary**: #f6854d (Warm Orange)

## 🏗️ Architecture

### Backend
- **Framework**: Spring Boot 3.5.6
- **Java**: 21
- **Database**: H2 (dev), MySQL (prod)
- **Security**: JWT Authentication
- **API Docs**: Swagger/OpenAPI

### Frontend
- **Framework**: React 19
- **Build Tool**: Vite 7
- **Routing**: React Router DOM 7
- **HTTP Client**: Axios
- **Styling**: CSS Modules

## 🚀 Quick Start

### 1. Start Backend (Terminal 1)
```bash
./mvnw spring-boot:run
```
Backend runs on **http://localhost:8080**

### 2. Start Frontend (Terminal 2)
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on **http://localhost:5173**

### 3. Access the Application
Open your browser and go to **http://localhost:5173**

## 📚 Documentation

- **QUICK_START.md** - Step-by-step getting started guide
- **PROJECT_STRUCTURE.md** - Complete project organization
- **FRONTEND_SUMMARY.md** - Frontend implementation details
- **frontend/COLOR_GUIDE.md** - Design system and colors
- **frontend/FRONTEND_README.md** - Frontend-specific docs

## 🔗 Important URLs

### Development
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

### Swagger Documentation
Access complete API documentation at:
http://localhost:8080/swagger-ui.html

## ✨ Features

### Implemented
- ✅ User registration and authentication
- ✅ JWT token management with refresh
- ✅ Family profile management
- ✅ Children information tracking
- ✅ Interest-based matching system
- ✅ Neighborhood organization
- ✅ Responsive design
- ✅ Protected routes
- ✅ API documentation

### UI/UX
- ✅ Modern, clean design
- ✅ Smooth animations
- ✅ Mobile-responsive
- ✅ Intuitive navigation
- ✅ Loading states
- ✅ Error handling
- ✅ Form validation

## 🎯 User Flow

1. **Register** - Create an account
2. **Login** - Authenticate with JWT
3. **Browse Families** - See families in your neighborhood
4. **Create Profile** - Set up your family profile
5. **Add Children** - Add children with interests
6. **Connect** - Find families with similar interests
7. **Organize** - Plan play dates and activities

## 🛠️ Development

### Backend Development
```bash
# Run tests
./mvnw test

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Build
./mvnw clean package
```

### Frontend Development
```bash
cd frontend

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

## 🗄️ Database

### H2 (Development)
- URL: jdbc:h2:mem:littleneighbors
- Username: sa
- Password: (empty)
- Console: http://localhost:8080/h2-console

### Flyway Migrations
Migrations are in `src/main/resources/db/migration/`
- V1__init.sql - Initial schema
- V2__init.sql - User roles table
- V3__init.sql - Initial data

## 🔐 Security

### Backend
- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control (ADMIN, FAMILY, USER)
- CORS configuration
- Security profiles (dev, test, prod)

### Frontend
- Token storage in localStorage
- Automatic token refresh
- Protected routes
- Request interceptors
- Secure API communication

## 📦 Tech Stack Details

### Backend Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- Spring Boot Starter Mail
- Flyway (database migrations)
- H2 Database (dev)
- MySQL Connector (prod)
- JWT (io.jsonwebtoken)
- Lombok
- Springdoc OpenAPI

### Frontend Dependencies
- react & react-dom: 19.2.0
- react-router-dom: 7.9.6
- axios: 1.13.2
- vite: 7.2.2

## 🎨 Design System

### Colors
```
Primary:   #ffcf53 (Sunshine Yellow)
Secondary: #f6854d (Warm Orange)
Dark:      #2c3e50
Gray:      #95a5a6
Light:     #ecf0f1
Success:   #27ae60
Danger:    #e74c3c
```

### Typography
- Font Stack: System fonts
- Line Height: 150% (body), 120% (headings)
- Max 3 font weights

### Spacing
- Base: 8px grid system
- Consistent padding and margins

## 🧪 Testing

### Backend Tests
```bash
./mvnw test
```

Tests include:
- ChildServiceImplTest
- FamilyServiceImplTest
- Context loading tests

## 📊 Project Stats

### Backend
- Java Files: ~30
- Lines of Code: ~3000
- API Endpoints: ~15
- Database Tables: 9

### Frontend
- React Components: 10
- Pages: 4
- CSS Modules: 5
- Build Size: ~280KB (gzipped)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests
5. Submit a pull request

## 📝 License

MIT License

## 🎉 Getting Help

- Check documentation in the `/docs` folder
- Review API documentation at /swagger-ui.html
- Check console logs for errors
- Ensure both backend and frontend are running

---

**Built with ❤️ for neighborhood families**

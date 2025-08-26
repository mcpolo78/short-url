# 🔗 LinkShortener - URL Shortener with Analytics

A modern, full-stack URL shortener application with advanced analytics, QR code generation, and real-time tracking capabilities.

## ✨ Features

### 🎯 Core Functionality
- **URL Shortening**: Transform long URLs into short, shareable links
- **Custom Aliases**: Create branded short links with custom aliases
- **QR Code Generation**: Automatic QR code creation for all short links
- **Password Protection**: Secure your links with password protection
- **Link Expiration**: Set expiration dates for temporary links

### 📊 Advanced Analytics
- **Real-time Tracking**: Monitor clicks as they happen
- **Geographic Analytics**: Track clicks by country and city
- **Device Analytics**: Browser, OS, and device type tracking
- **Referrer Tracking**: See where your traffic comes from
- **Time-based Analytics**: Daily and hourly click patterns
- **Bot Detection**: Filter out bot traffic for accurate metrics

### 🎨 User Experience
- **Modern UI**: Clean, responsive design with Tailwind CSS
- **Dashboard**: Comprehensive overview of all your links
- **Search & Filter**: Find your links quickly
- **Bulk Operations**: Create multiple links at once
- **Export Data**: Download analytics and QR codes

## 🏗️ Tech Stack

### Backend
- **Spring Boot 3.2** - Java framework
- **Spring Data JPA** - Database ORM
- **Spring Security** - Authentication & authorization
- **MySQL** - Primary database
- **Redis** - Caching layer
- **Maven** - Dependency management

### Frontend
- **React 18** - UI library
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Recharts** - Charts and analytics visualization
- **React Hook Form** - Form handling
- **Axios** - HTTP client

### Additional Libraries
- **ZXing** - QR code generation
- **MaxMind GeoIP2** - Geographic location tracking
- **UserAgentUtils** - User agent parsing
- **JWT** - JSON Web Tokens for auth

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Node.js 18 or higher
- MySQL 8.0 or higher
- Redis 6.0 or higher (optional, for caching)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/link-shortener.git
   cd link-shortener
   ```

2. **Configure the database**
   ```bash
   # Create MySQL database
   mysql -u root -p
   CREATE DATABASE linkshortener;
   ```

3. **Update application.properties**
   ```properties
   # Update database credentials in backend/src/main/resources/application.properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run the backend**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

### Frontend Setup

1. **Install dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Start the development server**
   ```bash
   npm run dev
   ```

3. **Open your browser**
   Navigate to `http://localhost:3000`

### Docker Setup (Optional)

```bash
# Run with Docker Compose
docker-compose up -d
```

## 📁 Project Structure

```
link-shortener/
├── backend/                          # Spring Boot application
│   ├── src/main/java/com/linkshortener/
│   │   ├── entity/                   # JPA entities
│   │   ├── repository/               # Data repositories
│   │   ├── service/                  # Business logic
│   │   ├── controller/               # REST controllers
│   │   ├── dto/                      # Data transfer objects
│   │   └── config/                   # Configuration classes
│   └── src/main/resources/
│       └── application.properties    # App configuration
├── frontend/                         # React application
│   ├── src/
│   │   ├── components/               # Reusable components
│   │   ├── pages/                    # Page components
│   │   ├── services/                 # API services
│   │   ├── hooks/                    # Custom React hooks
│   │   └── utils/                    # Utility functions
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml                # Docker configuration
└── README.md
```

## 🔧 Configuration

### Environment Variables

#### Backend (.env or application.properties)
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/linkshortener
spring.datasource.username=root
spring.datasource.password=password

# Redis (optional)
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# App
app.base-url=http://localhost:8080
app.short-url-length=8

# GeoIP (download from MaxMind)
geoip.database-path=src/main/resources/GeoLite2-City.mmdb
```

#### Frontend (.env)
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 📈 API Documentation

The API follows RESTful conventions. Key endpoints:

### Public Endpoints
- `POST /api/links` - Create a short link
- `GET /{code}` - Redirect to original URL
- `GET /{code}/info` - Get link information

### Protected Endpoints
- `GET /api/links` - Get user's links
- `GET /api/links/{id}/analytics` - Get link analytics
- `PUT /api/links/{id}` - Update a link
- `DELETE /api/links/{id}` - Delete a link

## 🚀 Deployment

### Railway (Backend)
1. Connect your GitHub repository
2. Set environment variables
3. Deploy automatically on push

### Vercel (Frontend)
1. Connect your GitHub repository
2. Set build command: `npm run build`
3. Set output directory: `dist`
4. Deploy automatically on push

### Environment Variables for Production
```bash
# Backend
DATABASE_URL=mysql://user:pass@host:port/db
REDIS_URL=redis://host:port
JWT_SECRET=your-production-secret

# Frontend
VITE_API_BASE_URL=https://your-api-domain.com/api
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Backend framework
- [React](https://reactjs.org/) - Frontend library
- [Tailwind CSS](https://tailwindcss.com/) - CSS framework
- [MaxMind](https://www.maxmind.com/) - GeoIP data
- [Lucide](https://lucide.dev/) - Icons

## 📊 Demo

🔗 **Live Demo**: [https://your-demo-url.com](https://your-demo-url.com)

### Sample Features
- Create a short link: `https://short.ly/abc123`
- View analytics dashboard
- Download QR codes
- Track geographic data

---

**Développé avec passion pour l'optimisation des liens** 🔗

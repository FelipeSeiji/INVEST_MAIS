# INVESTE- 🚀

[![Java Version](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Security-Hardened-red?style=for-the-badge&logo=springsecurity)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

**INVESTE-** is a robust, high-performance investment management backend built with **Spring Boot 3.5** and **Java 21**. Designed with a security-first mindset, it implements advanced authentication patterns, defensive infrastructure filters, and follows strict **SOLID** and **Domain-Driven Design (DDD)** principles to ensure scalability and maintainability.

---

## 🛠 Tech Stack

- **Core**: Java 21 LTS & Spring Boot 3.5.x
- **Persistence**: PostgreSQL (Production) / H2 (Development & Testing)
- **Security**: 
  - Spring Security with JWT (java-jwt)
  - OAuth2 Client integration
  - Argon2 Password Hashing
  - AES-256 DB Encryption for sensitive fields
- **Infrastructure**:
  - **Rate Limiting**: Implementation using Bucket4j & Caffeine
  - **Validation**: Bean Validation (Hibernate Validator)
  - **Mappers**: MapStruct for clean entity/DTO conversion
- **Documentation**: OpenAPI 3 / Swagger (SpringDoc UI)
- **Utilities**: Lombok, dotenv-java, BouncyCastle

---

## 🏗 Architecture & Principles

This project is built to transcend simple MVP standards, adhering to modern software engineering best practices:

- **Domain-Driven Design (DDD)**: Logic organized by domain boundaries (`auth`, `user`, `asset`, `admin`).
- **SOLID Principles**: Focused on decoupling, single responsibility, and interface-based design.
- **Defensive Programming**: Extensive input validation and standardized error handling.
- **Security Hardening**:
  - Custom filters to detect and block automated tools (Nmap/Gobuster).
  - Short-lived JWT tokens with server-side blacklisting (hashed).
  - Multi-layer 2FA (Two-Factor Authentication) implementation.

---

## ✨ Key Features

### 🔐 Secure Authentication
- **JWT flow**: Stateless authentication with automatic token validation.
- **2FA**: Two-factor authentication via e-mail for critical actions and login.
- **OAuth2**: Supports login via external providers.
- **Logout**: Secure token termination using a server-side blacklist.

### 🛡 Infrastructure Resilience
- **Rate Limiting**: DDoS and Brute-force protection implemented at the filter level.
- **Request Anomaly Detection**: Identification of common scanning patterns.
- **HTTPS/SSL**: Self-signed certificate support out-of-the-box for local dev.

### 📈 Investment Management
- **Asset Control**: Manage diverse investment portfolios across different domains.
- **Admin Dashboard**: Specialized endpoints for platform control and audits.

---

## 🚀 Getting Started

### Prerequisites
- JDK 21
- Maven 3.x
- PostgreSQL (Optional, H2 used by default in dev)

### Configuration
1. Clone the repository.
2. Create a `.env` file in the root directory based on the following template:

```env
# Mail Credentials (2FA)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Security
JWT_KEY=your-super-secret-key-32-chars
DB_ENCRYPTION_KEY=your-aes-key-32-chars

# Database
DB_URL=jdbc:h2:mem:testdb
DB_USERNAME=sa
DB_PASSWORD=
```

### Running the Application
```bash
./mvnw spring-boot:run
```

The application will be available at `https://localhost:8443` (Default HTTPS port).

---

## 📖 API Documentation

The interactive API documentation is automatically generated and can be accessed via:

- **Swagger UI**: `https://localhost:8443/swagger-ui.html`
- **OpenAPI Spec**: `https://localhost:8443/v3/api-docs`

---

## 🤝 Contributing

1. Fork the project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

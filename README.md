# INVESTE-

## Tecnologias Utilizadas
* Java 21
* Spring Boot 
* H2
* JWT

## Endpoints

### Gestão de Usuários
#### Cadastro
```request
POST /api/users
```
```json
{
    "name": "User Name",
    "email": "example@gmail.com",
    "password": "Password@123"
}
```
### Autenticação
#### Login
```request
POST /auth/login
```
```json
{
    "email": "example@gmail.com",
    "password": "Password@123"
}
```
#### Validar 2FA 
```request
POST /auth/verify-2fa
```
```json
{
    "email": "usuario@exemplo.com",
    "code": "123456"
}
```
#### Logout
```request
POST /auth/logout
Headers: Authorization: Bearer $token
```

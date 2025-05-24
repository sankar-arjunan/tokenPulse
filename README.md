# TokenPulse

TokenPulse is a lightweight Spring Boot-based token orchestration system for testing APIs locally. It issues master tokens with IP restrictions and manages service tokens with auto-refresh support.

## Features

- 🔐 **Master Token**: Issued per email with IP restrictions and expiration.
- 🧾 **Service Credentials**: Add service credentials under a master token.
- 🔁 **Token Refreshing**: Automatically refreshes service tokens when needed.
- 🧪 **For API Testing**: Built for safe local development and testing scenarios.

## Endpoints

### `GET /api/v1/token`
Generate a master token.
- `email`: User's email (or some token basically)
- `expiryMinutes`: Token lifespan
- `allowedIps`: List of allowed IPs

### `POST /api/v1/service`
Add a service with credentials.
- Header: `Token: <email>`
- Param: `url`
- Body: Raw JSON with credentials

### `GET /api/v1/services`
List all added services.
- Header: `Token: <email>`

### `GET /api/v1/service`
Get token for a specific service.
- Header: `Token: <email>`
- Param: `url`

### `POST /api/v1/service/refresh`
Manually refresh a service token.
- Header: `Token: <email>`
- Param: `url`

### `DELETE /api/v1/logout`
Invalidate the master token and delete all service tokens.
- Header: `Token: <email>`

## Tech Stack

- Spring Boot
- Java
- Jakarta Servlet API
- In-memory or persistent storage

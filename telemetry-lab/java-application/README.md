# URL Shortener - Java Spring Boot Application

Acortador de URLs básico implementado con Spring Boot 3.2, Gradle y persistencia en memoria.

## Características

- Acortamiento de URLs con códigos aleatorios de 6 caracteres
- Códigos personalizados opcionales
- Métricas con Prometheus y Micrometer
- Observabilidad completa con Spring Boot Actuator
- Logging estructurado para Promtail/Loki
- Persistencia en memoria con ConcurrentHashMap (thread-safe)

## Tecnologías

- **Java 21**
- **Spring Boot 3.2.0**
- **Gradle 8.7**
- **Micrometer + Prometheus**
- **Spring Boot Actuator**
- **Lombok**

## Endpoints

### API Endpoints

- `GET /api/` - Health check y información del servicio
- `POST /api/shorten` - Acortar una URL
  ```json
  {
    "url": "https://example.com",
    "customCode": "opcional"
  }
  ```
- `GET /api/{shortCode}` - Redirección a la URL original
- `GET /api/info/{shortCode}` - Información sobre una URL acortada
- `GET /api/urls` - Listar todas las URLs (debugging)
- `GET /api/stats` - Estadísticas del servicio

### Observabilidad Endpoints

- `GET /actuator/health` - Estado de salud de la aplicación
- `GET /actuator/metrics` - Métricas disponibles
- `GET /actuator/prometheus` - Métricas en formato Prometheus
- `GET /actuator/info` - Información de la aplicación

## Métricas Personalizadas

- `urls_shortened_total` - Total de URLs acortadas
- `urls_accessed_total` - Total de accesos a URLs acortadas
- Métricas HTTP estándar de Spring Boot (request count, duration, etc.)

## Construcción y Ejecución

### Local con Gradle
```bash
cd src
./gradlew bootRun
```

### Docker
```bash
docker build -t java-application -f Dockerfile .
docker run -p 8080:8080 java-application
```

### Docker Compose
```bash
cd ..
docker-compose up -d java-application
```

## Ejemplos de Uso

### Acortar una URL
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.google.com"}'
```

### Acortar con código personalizado
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.google.com", "customCode": "google"}'
```

### Obtener información de una URL
```bash
curl http://localhost:8080/api/info/google
```

### Ver estadísticas
```bash
curl http://localhost:8080/api/stats
```

### Ver métricas de Prometheus
```bash
curl http://localhost:8080/actuator/prometheus
```

## Integración con Stack de Observabilidad

La aplicación está diseñada para integrarse con:
- **Prometheus** - Scraping de métricas desde `/actuator/prometheus`
- **Loki/Promtail** - Recolección automática de logs desde stdout
- **Grafana** - Visualización de métricas y logs

## Estructura del Proyecto

```
java-application/
├── Dockerfile              # Multi-stage Dockerfile con Gradle
├── deployment.yaml         # Deployment y Service de Kubernetes
├── README.md               # Esta documentación
├── build.gradle            # Configuración de Gradle
├── settings.gradle         # Configuración del proyecto
└── src/
    └── main/
        ├── java/com/telemetry/urlshortener/
        │   ├── UrlShortenerApplication.java      # Clase principal
        │   ├── controller/UrlController.java     # Controlador REST
        │   ├── service/UrlShortenerService.java  # Lógica de negocio
        │   └── model/
        │       ├── UrlMapping.java               # Modelo de datos
        │       └── ShortenRequest.java           # DTO de request
        └── resources/
            └── application.properties            # Configuración
```

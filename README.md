# Telemetry Lab

Stack de observabilidad con Prometheus, Grafana y Loki para aplicaciones Java.

## Iniciar todo

```bash
docker-compose up -d
```

## Acceder a los servicios

- **Grafana**: http://localhost:3000
- **Prometheus**: http://localhost:9091
- **Java App**: http://localhost:8080

## Comandos útiles

### Reiniciar servicios (mantiene dashboards y datos)
```bash
docker-compose restart
```

### Detener servicios (mantiene dashboards y datos)
```bash
docker-compose down
```

### Detener y ELIMINAR TODO (dashboards, métricas, logs)
```bash
docker-compose down -v
```

### Ver logs
```bash
docker-compose logs -f
docker-compose logs -f grafana
```

## Persistencia de Dashboards

Los dashboards que crees en Grafana se guardan automáticamente en el volumen Docker `grafana-storage`.

**Esto significa:**
- Tus dashboards sobreviven a `docker-compose down` y `docker-compose up`
- Se pierden solo con `docker-compose down -v` (elimina volúmenes)

## Dashboard Base

El proyecto incluye un dashboard provisionado automáticamente:
- **Nombre**: "Application Telemetry (Prometheus)"
- **Secciones**: Java
- **Ubicación**: `./grafana-data/sample_dashboard.json`

Este dashboard **siempre se carga** al iniciar, incluso después de `docker-compose down -v`.

## Estructura

```
telemetry-lab/
├── grafana-data/               # Configuración de Grafana
│   ├── datasources.yaml        # Prometheus y Loki
│   ├── dashboards.yaml         # Config de provisioning
│   └── sample_dashboard.json   # Dashboard base
├── java-application/           # App Spring Boot con métricas
├── loki-data/                  # Config de loki para recoleccion de logs
└── docker-compose.yaml         # Orquestación de servicios
```

## 🎯 Workflow típico

```bash
# 1. Iniciar todo
docker-compose up -d

# 2. Trabajar en Grafana (crear dashboards, etc.)

# 3. Detener sin perder dashboards
docker-compose down

# 4. Reiniciar cuando quieras
docker-compose up -d

# 5. Reset completo (opcional)
docker-compose down -v
docker-compose up -d
```

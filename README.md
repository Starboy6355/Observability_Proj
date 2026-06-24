# Order Tracker — Observability Project

A simple Java Spring Boot REST API fully instrumented with:
- **Prometheus** — metrics collection
- **Grafana** — dashboards and visualization
- **Loki** — log aggregation
- **Jaeger** — distributed tracing
- **OpenTelemetry** — the standard instrumentation layer

---

## Architecture

```
                        ┌─────────────────────────────────────┐
                        │         Docker Network               │
                        │                                      │
  Browser/curl          │  ┌──────────────────────┐           │
      │                 │  │  order-tracker-app    │           │
      │ HTTP :8080      │  │  (Spring Boot)        │           │
      └────────────────►│  │                       │           │
                        │  │  Logs ──────────────────────────► Loki :3100
                        │  │  Metrics (pulled) ◄──────────── Prometheus :9090
                        │  │  Traces ────────────────────────► OTel Collector :4318
                        │  └──────────────────────┘           │     │
                        │                                      │     ▼
                        │                                      │  Jaeger :16686
                        │                                      │
                        │  Grafana :3000                       │
                        │  (connects to all three)             │
                        └─────────────────────────────────────┘
```

---

## Project Structure

```
order-tracker/
├── app/
│   ├── src/main/java/com/ordertracker/
│   │   ├── OrderTrackerApplication.java  # App entry point
│   │   ├── OrderController.java          # HTTP endpoints
│   │   ├── OrderService.java             # Business logic + logging
│   │   ├── Order.java                    # Order data model
│   │   └── OrderNotFoundException.java   # Custom exception → HTTP 404
│   ├── src/main/resources/
│   │   ├── application.properties        # App + OTel + Prometheus config
│   │   └── logback-spring.xml            # Log routing to Loki
│   ├── pom.xml                           # Java dependencies
│   └── Dockerfile                        # How to build the app container
├── observability/
│   ├── prometheus.yml                    # What to scrape and how often
│   ├── loki-config.yml                   # How Loki stores logs
│   └── otel-collector-config.yml         # Trace routing: app → Jaeger
├── docker-compose.yml                    # Starts all 5 services together
└── README.md
```

---

## Prerequisites

- Oracle Cloud VM (Ubuntu 22.04, 4 OCPU, 24GB RAM) or any Linux server
- Docker and Docker Compose installed
- Ports open: 8080, 9090, 3000, 16686, 3100

---

## Setup — Oracle Cloud VM

### Step 1 — Create the VM
- Shape: VM.Standard.A1.Flex
- OCPU: 4, RAM: 24GB
- Image: Ubuntu 22.04
- Download SSH key pair

### Step 2 — Open ports in Security List
```
Port 22    → SSH
Port 8080  → Spring Boot app
Port 9090  → Prometheus
Port 3000  → Grafana
Port 16686 → Jaeger UI
Port 3100  → Loki
```

### Step 3 — Connect internet gateway
In the instance page → Quick Actions → Connect public subnet to internet → Create

### Step 4 — SSH into VM
```bash
chmod 400 your-key.pem
ssh -i your-key.pem ubuntu@YOUR_VM_IP
```

### Step 5 — Install Docker
```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker ubuntu
newgrp docker
sudo apt install docker-compose-plugin -y
```

### Step 6 — Clone this repo
```bash
git clone https://github.com/Starboy6355/Observability_Proj.git
cd Observability_Proj
```

### Step 7 — Start everything
```bash
docker compose up --build -d
```

This single command:
1. Builds your Spring Boot app into a Docker image
2. Starts all 5 containers (app, Prometheus, Grafana, Loki, Jaeger)
3. Connects them all on the same Docker network

---

## Access the tools

Replace `YOUR_VM_IP` with your Oracle VM public IP.

| Tool | URL | Purpose |
|---|---|---|
| Spring Boot API | http://YOUR_VM_IP:8080/api/orders/health | Your app |
| Prometheus | http://YOUR_VM_IP:9090 | See raw metrics |
| Grafana | http://YOUR_VM_IP:3000 | Dashboards |
| Jaeger | http://YOUR_VM_IP:16686 | Traces |
| Loki | http://YOUR_VM_IP:3100 | Logs (via Grafana) |

---

## Test the API

```bash
# Health check
curl http://YOUR_VM_IP:8080/api/orders/health

# Get an existing order
curl http://YOUR_VM_IP:8080/api/orders/ORD001

# Get another order
curl http://YOUR_VM_IP:8080/api/orders/ORD002

# Trigger a 404 error (order doesn't exist)
curl http://YOUR_VM_IP:8080/api/orders/ORD999

# Create a new order
curl -X POST http://YOUR_VM_IP:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"product": "Keyboard", "amount": 2500}'
```

---

## Connect Grafana data sources

Open Grafana at http://YOUR_VM_IP:3000

### Add Prometheus
1. Go to Connections → Data Sources → Add
2. Select Prometheus
3. URL: `http://prometheus:9090`
4. Save & Test

### Add Loki
1. Go to Connections → Data Sources → Add
2. Select Loki
3. URL: `http://loki:3100`
4. Save & Test

### Add Jaeger
1. Go to Connections → Data Sources → Add
2. Select Jaeger
3. URL: `http://jaeger:16686`
4. Save & Test

---

## What each file does

| File | Why it exists |
|---|---|
| `pom.xml` | Lists all Java libraries the app needs — like package.json for Java |
| `OrderController.java` | Handles HTTP requests — defines the API endpoints |
| `OrderService.java` | Business logic — where log.info() and log.error() are written |
| `Order.java` | The data model — represents one order |
| `OrderNotFoundException.java` | Custom exception — converts to HTTP 404 automatically |
| `application.properties` | Config — sets ports, OTel endpoint, Prometheus settings |
| `logback-spring.xml` | Routes logs to both console and Loki |
| `Dockerfile` | Instructions to build the app into a container image |
| `prometheus.yml` | Tells Prometheus what URL to scrape for metrics |
| `loki-config.yml` | Loki storage and ingestion settings |
| `otel-collector-config.yml` | OTel router — receives traces from app, sends to Jaeger |
| `docker-compose.yml` | Starts all 5 services together on one network |

---

## Key concepts learned

**Why JAR?** Java compiles to a `.jar` file — a single package containing all code and dependencies. You run it with `java -jar app.jar`.

**Why Docker?** Packages the app + its environment together. Works the same on any machine.

**Why docker-compose?** Starts multiple containers (app + 5 tools) with one command and connects them on a shared network.

**Why Prometheus pulls instead of the app pushing?** Prometheus controls the scrape rate. If the app pushed, you'd have no control over the volume of data.

**Why OTel Collector in between?** Decouples your app from the APM tool. Change the collector config to switch tools — no app code changes needed.

**Why private IPs inside Docker?** Containers talk to each other by service name (e.g. `http://loki:3100`) over the internal Docker network. No need to go out to the internet and back.

---

## Stop everything
```bash
docker compose down
```

## View logs of a specific service
```bash
docker compose logs -f order-tracker-app
docker compose logs -f prometheus
```

# 🐳 Docker & Selenium Grid Guide

Complete guide for running tests in Docker containers with Selenium Grid.

---

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Docker Compose Setup](#docker-compose-setup)
- [Running Tests](#running-tests)
- [Selenium Grid Console](#selenium-grid-console)
- [Debugging with VNC](#debugging-with-vnc)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Advanced Usage](#advanced-usage)

---

## 📦 Prerequisites

### Required Software

1. **Docker** (20.10+)
   ```bash
   docker --version
   ```
   Install: https://docs.docker.com/get-docker/

2. **Docker Compose** (2.0+)
   ```bash
   docker-compose --version
   ```
   Included with Docker Desktop

3. **Google Cloud CLI** (for secret management)
   ```bash
   gcloud --version
   ```
   Install: https://cloud.google.com/sdk/docs/install

---

## 🚀 Quick Start

### 1. Start Selenium Grid

```bash
# Start Selenium Hub and all browser nodes
docker-compose up -d selenium-hub chrome-node-1 chrome-node-2 firefox-node edge-node

# Wait for Grid to be ready (check status)
curl http://localhost:4444/wd/hub/status
```

### 2. Run Tests

```bash
# Run ALL test frameworks (matching CI/CD pipeline)
./scripts/run-all-tests-docker.sh

# Run only Selenium/Java tests
docker-compose up tests

# Run specific test
docker-compose run --rm tests -Dtest=Scenarios#Google

# Run with specific browser
BROWSER=firefox docker-compose up tests

# Run Cypress tests
docker-compose run --rm -e BASE_URL=https://www.google.com tests bash -c 'cd /app/cypress && xvfb-run -a npm run cypress:run'

# Run Playwright tests
docker-compose run --rm -e BASE_URL=https://www.google.com tests bash -c 'cd /app/playwright && npm test'

# Run Robot Framework tests
docker-compose run --rm -e BASE_URL=https://www.google.com tests bash -c './mvnw test -Probot'
```

**Note**: Docker now matches the CI/CD pipeline environment exactly! If tests pass in Docker, they will also pass in the pipeline.

### 3. Stop Grid

```bash
# Stop all containers
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

## 🐳 Docker Compose Setup

### Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Selenium Hub                        │
│              (localhost:4444)                        │
└────────────────┬────────────────────────────────────┘
                 │
        ┌────────┼────────┬─────────┐
        │        │        │         │
    ┌───▼───┐┌───▼───┐┌───▼───┐ ┌───▼───┐
    │Chrome ││Chrome ││Firefox│ │ Edge  │
    │Node 1 ││Node 2 ││ Node  │ │ Node  │
    └───────┘└───────┘└───────┘ └───────┘
```

### Services

| Service | Port | Description |
|---------|------|-------------|
| **selenium-hub** | 4444 | Central hub for test distribution |
| **chrome-node-1** | 5900, 7900 | Chrome browser (instance 1) |
| **chrome-node-2** | 5901, 7901 | Chrome browser (instance 2) |
| **firefox-node** | 5902, 7902 | Firefox browser |
| **edge-node** | 5903, 7903 | Microsoft Edge browser |
| **tests** | - | Test execution container |

---

## 🧪 Running Tests

### Basic Commands

```bash
# 1. Run all tests
docker-compose up tests

# 2. Run specific test class
docker-compose run --rm tests -Dtest=Scenarios

# 3. Run specific test method
docker-compose run --rm tests -Dtest=Scenarios#Google

# 4. Run with specific browser
BROWSER=chrome docker-compose run --rm tests -Dtest=Scenarios#Microsoft

# 5. Run in parallel (5 threads)
PARALLEL_THREADS=5 docker-compose up tests
```

### With Environment Variables

```bash
# Set environment variables
export BROWSER=firefox
export PARALLEL_THREADS=10
export HEADLESS=true

# Run tests
docker-compose up tests
```

### Local Development (Without Docker)

```bash
# Start Grid only
docker-compose up -d selenium-hub chrome-node-1 firefox-node

# Run tests locally (pointing to Grid)
./mvnw test -DseleniumHub=http://localhost:4444/wd/hub -Dtest=Scenarios#Google
```

---

## 🖥️ Selenium Grid Console

### Web Interface

Open browser to: http://localhost:4444

**Features:**
- View available browsers
- Monitor active sessions
- Check node status
- View Grid configuration

### REST API

```bash
# Grid status
curl http://localhost:4444/wd/hub/status | jq .

# Get all sessions
curl http://localhost:4444/wd/hub/sessions | jq .

# Grid configuration
curl http://localhost:4444/wd/hub/status | jq '.value.nodes'
```

---

## 🔍 Debugging with VNC

### Option 1: VNC Client

**Install VNC Client:**
- **macOS**: Screen Sharing (built-in)
- **Windows**: RealVNC, TightVNC
- **Linux**: Vinagre, Remmina

**Connect to Browser Nodes:**

```bash
# Chrome Node 1
vnc://localhost:5900

# Chrome Node 2
vnc://localhost:5901

# Firefox Node
vnc://localhost:5902

# Edge Node
vnc://localhost:5903
```

**Password**: No password required (SE_VNC_NO_PASSWORD=1)

### Option 2: noVNC (Browser-based)

**Open in browser:**

- Chrome 1: http://localhost:7900
- Chrome 2: http://localhost:7901
- Firefox: http://localhost:7902
- Edge: http://localhost:7903

**Features:**
- View live browser session
- No VNC client installation needed
- Works in any web browser
- Real-time test execution monitoring

---

## ⚙️ Configuration

### Environment Variables

Edit `docker-compose.yml` or use `.env` file:

```bash
# .env file
BROWSER=chrome
HEADLESS=false
PARALLEL_THREADS=5
SELENIUM_REMOTE_URL=http://selenium-hub:4444/wd/hub
```

### Grid Configuration

**Selenium Hub:**
```yaml
environment:
  - GRID_MAX_SESSION=10              # Max concurrent sessions
  - GRID_BROWSER_TIMEOUT=300         # Browser timeout (seconds)
  - GRID_TIMEOUT=300                 # Grid timeout (seconds)
  - SE_SESSION_REQUEST_TIMEOUT=300   # Session request timeout
```

**Browser Nodes:**
```yaml
environment:
  - SE_NODE_MAX_SESSIONS=5           # Max sessions per node
  - SE_NODE_SESSION_TIMEOUT=300      # Session timeout
  - SE_SCREEN_WIDTH=1920             # Screen width
  - SE_SCREEN_HEIGHT=1080            # Screen height
```

### Resource Limits

```yaml
# Add to docker-compose.yml services
deploy:
  resources:
    limits:
      cpus: '2.0'
      memory: 4G
    reservations:
      cpus: '1.0'
      memory: 2G
```

---

## 🐛 Troubleshooting

### Grid Not Starting

**Problem:** Selenium Hub not accessible
```bash
# Check logs
docker-compose logs selenium-hub

# Check container status
docker ps

# Restart Grid
docker-compose restart selenium-hub
```

### Browser Node Connection Issues

**Problem:** Nodes not connecting to Hub
```bash
# Check node logs
docker-compose logs chrome-node-1

# Verify network
docker network ls
docker network inspect selenium_selenium-grid

# Restart node
docker-compose restart chrome-node-1
```

### Test Container Issues

**Problem:** Tests failing to connect
```bash
# Check test logs
docker-compose logs tests

# Verify environment variables
docker-compose config

# Run interactively for debugging
docker-compose run --rm tests bash
```

### Port Conflicts

**Problem:** Port already in use
```bash
# Find process using port 4444
lsof -i :4444  # macOS/Linux
netstat -ano | findstr :4444  # Windows

# Kill process or change port in docker-compose.yml
```

### Browser Node Crashes

**Problem:** Browser nodes crashing
```bash
# Increase shared memory
shm_size: 2gb  # In docker-compose.yml

# Reduce max sessions
SE_NODE_MAX_SESSIONS=2  # Instead of 5
```

### Timeout Issues

**Problem:** Tests timing out
```bash
# Increase timeouts in docker-compose.yml
SE_SESSION_REQUEST_TIMEOUT=600
SE_NODE_SESSION_TIMEOUT=600
```

---

## 🚀 Advanced Usage

### Scaling Browser Nodes

```bash
# Scale Chrome nodes to 5 instances
docker-compose up -d --scale chrome-node-1=5

# Scale dynamically
docker-compose up -d --scale firefox-node=3 --scale edge-node=2
```

### Custom Dockerfile

Build custom test image:

```dockerfile
FROM eclipse-temurin:21-jre-alpine

# Add custom tools
RUN apk add --no-cache jq curl bash

# Copy application
COPY target/*.jar /app/app.jar

# Run
CMD ["java", "-jar", "/app/app.jar"]
```

### CI/CD Integration

**GitHub Actions:**
```yaml
- name: Start Selenium Grid
  run: docker-compose up -d selenium-hub chrome-node-1

- name: Run Tests
  run: docker-compose run --rm tests -Dtest=Scenarios
```

**Jenkins Pipeline:**
```groovy
stage('Selenium Tests') {
    steps {
        sh 'docker-compose up -d selenium-hub chrome-node-1'
        sh 'docker-compose run --rm tests'
    }
    post {
        always {
            sh 'docker-compose down'
        }
    }
}
```

### Video Recording

Enable video recording in `docker-compose.yml`:

```yaml
chrome-node-1:
  image: selenium/node-chrome:4.39.0
  environment:
    - SE_VIDEO_RECORD=true
    - SE_VIDEO_FOLDER=/videos
  volumes:
    - ./videos:/videos
```

### Custom Browser Versions

```yaml
# Use specific browser version
chrome-node-1:
  image: selenium/node-chrome:chrome-120.0
```

### Grid Monitoring

```yaml
# Add monitoring service
prometheus:
  image: prom/prometheus
  ports:
    - "9090:9090"
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml

grafana:
  image: grafana/grafana
  ports:
    - "3000:3000"
```

---

## 📊 Performance Tips

### 1. Optimize Resource Usage

```bash
# Use headless mode (faster)
HEADLESS=true docker-compose up tests

# Reduce screen size
SE_SCREEN_WIDTH=1280
SE_SCREEN_HEIGHT=720
```

### 2. Parallel Execution

```bash
# Increase parallel threads
PARALLEL_THREADS=10 docker-compose up tests

# Scale nodes accordingly
docker-compose up -d --scale chrome-node-1=10
```

### 3. Network Optimization

```yaml
# Use host network for better performance
network_mode: host  # Linux only
```

### 4. Build Cache

```bash
# Use BuildKit for faster builds
DOCKER_BUILDKIT=1 docker-compose build

# Pre-pull images
docker-compose pull
```

---

## 📝 Cheat Sheet

### Essential Commands

```bash
# Start everything
docker-compose up -d

# Stop everything
docker-compose down

# View logs
docker-compose logs -f tests

# Restart service
docker-compose restart chrome-node-1

# Remove all
docker-compose down -v --remove-orphans

# Clean everything
docker system prune -a --volumes
```

### Useful Aliases

```bash
# Add to ~/.bashrc or ~/.zshrc
alias dc="docker-compose"
alias dcu="docker-compose up -d"
alias dcd="docker-compose down"
alias dcl="docker-compose logs -f"
alias dcr="docker-compose restart"
```

---

## 🔗 Related Documentation

- [Main README](../../README.md)
- [Installation Guide](../../README.md#installation)
- [Running Tests](../../README.md#running-tests)
- [Troubleshooting](../../README.md#troubleshooting)

---

## 📞 Support

### Resources

- [Selenium Grid Docs](https://www.selenium.dev/documentation/grid/)
- [Docker Compose Docs](https://docs.docker.com/compose/)
- [Selenium Docker Images](https://github.com/SeleniumHQ/docker-selenium)

### Getting Help

1. Check logs: `docker-compose logs`
2. Verify status: `curl http://localhost:4444/wd/hub/status`
3. Open issue on GitHub
4. Check [Troubleshooting Guide](../troubleshooting/CI_TROUBLESHOOTING.md)

---

<div align="center">

**Built with ❤️ by the CJS QA Team**

</div>

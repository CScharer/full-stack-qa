# CJS QA Automation Framework(s) - Dockerfile
# Multi-stage build for efficient test execution

# Stage 1: Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build

LABEL maintainer="CJS Consulting, L.L.C"
LABEL description="Selenium Test Automation Framework with Cucumber BDD"
LABEL version="1.0.0"

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (this layer will be cached)
# Note: dependency:go-offline may fail for some plugins, continue anyway
# Clear any cached failures first to ensure fresh downloads
RUN rm -rf ~/.m2/repository/org/apache/maven/plugins/maven-compiler-plugin || true
RUN ./mvnw -ntp dependency:go-offline -B || echo "Some dependencies could not be resolved offline"

# Copy source code
COPY src ./src
COPY Configurations ./Configurations
COPY Data ./Data
COPY XML ./XML

# Copy configuration files
COPY checkstyle-custom.xml checkstyle-suppressions.xml ./

# Build the project (skip tests in build stage)
RUN ./mvnw -ntp clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jdk

LABEL maintainer="CJS Consulting, L.L.C"

# Install required utilities and dependencies matching CI/CD environment
# This ensures Docker runs the same way as the pipeline
RUN apt-get update && apt-get install -y \
    curl \
    bash \
    tzdata \
    wget \
    gnupg \
    ca-certificates \
    # Node.js 20 (for Cypress and Playwright)
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    # Update npm to latest version (suppress notice about new version)
    && npm install -g npm@latest \
    # Python 3.13 (for Robot Framework) - use python3 if 3.13 not available
    && (apt-get install -y python3.13 python3.13-venv python3-pip || apt-get install -y python3 python3-venv python3-pip) \
    # System dependencies for Cypress and Playwright (matching CI/CD)
    && apt-get install -y \
    xvfb \
    libgtk2.0-0 \
    libgtk-3-0 \
    libgbm-dev \
    libnotify-dev \
    libnss3 \
    libxss1 \
    libasound2t64 || apt-get install -y libasound2 \
    # Additional dependencies
    && rm -rf /var/lib/apt/lists/*

# Set timezone
ENV TZ=America/Chicago
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create app user (don't run as root)
# Use existing group 1000 or create if needed
RUN groupadd -g 1001 appuser || true && \
    useradd -r -u 1001 -g 1001 -m -d /home/appuser appuser || true

# Set working directory
WORKDIR /app

# Copy built artifacts from build stage
COPY --from=build /app/target ./target
COPY --from=build /app/src ./src
COPY --from=build /app/Configurations ./Configurations
COPY --from=build /app/Data ./Data
COPY --from=build /app/XML ./XML
COPY --from=build /app/pom.xml ./

# Copy Maven wrapper for test execution
COPY --from=build /app/.mvn ./.mvn
COPY --from=build /app/mvnw ./

# Copy Cypress and Playwright directories (needed for test execution)
# Note: These are mounted as volumes in docker-compose.yml, but we copy them here
# for cases where the container is run standalone
COPY cypress ./cypress
COPY playwright ./playwright

# Install Cypress dependencies (if package.json exists)
WORKDIR /app/cypress
RUN if [ -f "package.json" ]; then \
        npm ci || echo "Cypress dependencies installation failed, will retry at runtime"; \
    else \
        echo "Cypress directory not found, skipping dependency installation"; \
    fi

# Install Playwright dependencies and browsers (if package.json exists)
WORKDIR /app/playwright
RUN if [ -f "package.json" ]; then \
        npm ci || echo "Playwright dependencies installation failed, will retry at runtime"; \
        npx playwright install --with-deps chromium || echo "Playwright browser installation failed, will retry at runtime"; \
    else \
        echo "Playwright directory not found, skipping dependency installation"; \
    fi

# Install Robot Framework dependencies
# Use --break-system-packages for Debian/Ubuntu systems with PEP 668 (externally-managed-environment)
RUN pip3 install --no-cache-dir --break-system-packages \
    robotframework \
    robotframework-seleniumlibrary \
    robotframework-requests 2>&1 || echo "Robot Framework dependencies installation failed, will retry at runtime"

# Return to app directory
WORKDIR /app

# Create directories for test results
RUN mkdir -p target/surefire-reports target/cucumber-reports target/robot-reports target/allure-results && \
    chown -R appuser:appuser /app

# Switch to app user
USER appuser

# Environment variables for test execution (matching CI/CD)
ENV SELENIUM_REMOTE_URL=http://selenium-hub:4444/wd/hub
ENV BROWSER=chrome
ENV HEADLESS=false
ENV PARALLEL_THREADS=5
ENV BASE_URL=https://www.google.com
ENV TEST_ENVIRONMENT=docker
ENV CI=true
ENV NODE_ENV=test

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:4444/wd/hub/status || exit 1

# Default command: Run all tests
ENTRYPOINT ["./mvnw", "test"]

# Override with specific test if needed
# docker run --rm cjs-qa-tests -Dtest=Scenarios#Google

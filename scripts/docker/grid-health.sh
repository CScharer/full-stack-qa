#!/bin/bash
# Selenium Grid Health Check Script

set -e

echo "🔍 Selenium Grid Health Check"
echo "=============================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Hub is running
echo "1. Checking Selenium Hub..."
if curl -s http://localhost:4444/wd/hub/status > /dev/null 2>&1; then
    STATUS=$(curl -s http://localhost:4444/wd/hub/status | jq -r '.value.ready')
    if [ "$STATUS" = "true" ]; then
        echo -e "${GREEN}✅ Hub is READY${NC}"
    else
        echo -e "${RED}❌ Hub is NOT READY${NC}"
        exit 1
    fi
else
    echo -e "${RED}❌ Hub is NOT ACCESSIBLE${NC}"
    exit 1
fi

echo ""
echo "2. Checking Grid Nodes..."
NODES=$(curl -s http://localhost:4444/wd/hub/status | jq -r '.value.nodes | length')
echo "   Total nodes: $NODES"

if [ "$NODES" -gt 0 ]; then
    echo -e "${GREEN}✅ Nodes are connected${NC}"

    # List node details
    curl -s http://localhost:4444/wd/hub/status | jq -r '.value.nodes[] | "   - \(.stereotype.browserName) (\(.availability))"'
else
    echo -e "${YELLOW}⚠️  No nodes connected${NC}"
fi

echo ""
echo "3. Checking Monitoring Services..."

# Check Prometheus
if curl -s http://localhost:9090/-/healthy > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Prometheus is running${NC}"
else
    echo -e "${YELLOW}⚠️  Prometheus is not accessible${NC}"
fi

# Check Grafana
if curl -s http://localhost:3000/api/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Grafana is running${NC}"
else
    echo -e "${YELLOW}⚠️  Grafana is not accessible${NC}"
fi

echo ""
echo "4. Container Status..."
docker ps --filter "name=selenium" --filter "name=chrome" --filter "name=firefox" --filter "name=edge" --format "table {{.Names}}\t{{.Status}}"

echo ""
echo "=============================="
echo "✅ Health check complete!"
echo ""
echo "🌐 Grid Console:     http://localhost:4444"
echo "📊 Prometheus:       http://localhost:9090"
echo "📈 Grafana:          http://localhost:3000"
echo "🎥 VNC (Chrome):     http://localhost:7900"
echo "=============================="

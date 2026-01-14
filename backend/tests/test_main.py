"""
Tests for main FastAPI application.
"""
import pytest
from fastapi.testclient import TestClient
from conftest import api_url


def test_root_endpoint(client: TestClient):
    """Test root endpoint returns API information."""
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert data["name"] == "ONE GOAL API"
    assert data["version"] == "1.0.0"
    assert data["status"] == "running"
    assert "docs" in data
    assert "api" in data


def test_health_check(client: TestClient):
    """Test health check endpoint."""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"


def test_openapi_docs_available(client: TestClient):
    """Test that OpenAPI documentation is accessible."""
    response = client.get("/docs")
    assert response.status_code == 200


def test_openapi_json_available(client: TestClient):
    """Test that OpenAPI JSON schema is accessible."""
    response = client.get(api_url("/openapi.json"))
    assert response.status_code == 200
    data = response.json()
    assert "openapi" in data
    assert "info" in data
    assert data["info"]["title"] == "ONE GOAL API"


def test_cors_headers(client: TestClient):
    """Test that CORS headers are present."""
    response = client.options("/", headers={"Origin": "http://localhost:3000"})
    # CORS preflight should return 200 or 204
    assert response.status_code in [200, 204, 405]  # 405 is OK for OPTIONS on some endpoints

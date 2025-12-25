"""
Tests for Companies API endpoints.
"""
import pytest
from fastapi.testclient import TestClient


def test_create_company(client: TestClient):
    """Test creating a company."""
    company_data = {
        "name": "Test Company",
        "city": "San Francisco",
        "country": "United States",
        "job_type": "Technology",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    
    response = client.post("/api/v1/companies", json=company_data)
    assert response.status_code == 201
    data = response.json()
    assert data["name"] == "Test Company"
    assert "id" in data


def test_get_company(client: TestClient):
    """Test getting a company by ID."""
    company_data = {
        "name": "Test Company",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/companies", json=company_data)
    company_id = create_response.json()["id"]
    
    response = client.get(f"/api/v1/companies/{company_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == company_id


def test_list_companies(client: TestClient):
    """Test listing companies."""
    for i in range(3):
        company_data = {
            "name": f"Company {i}",
            "created_by": "test@example.com",
            "modified_by": "test@example.com"
        }
        client.post("/api/v1/companies", json=company_data)
    
    response = client.get("/api/v1/companies")
    assert response.status_code == 200
    data = response.json()
    assert "data" in data
    assert len(data["data"]) >= 3


def test_update_company(client: TestClient):
    """Test updating a company."""
    company_data = {
        "name": "Original Name",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/companies", json=company_data)
    company_id = create_response.json()["id"]
    
    update_data = {
        "name": "Updated Name",
        "modified_by": "test@example.com"
    }
    response = client.put(f"/api/v1/companies/{company_id}", json=update_data)
    assert response.status_code == 200
    data = response.json()
    assert data["name"] == "Updated Name"


def test_delete_company(client: TestClient):
    """Test deleting a company."""
    company_data = {
        "name": "To Delete",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/companies", json=company_data)
    company_id = create_response.json()["id"]
    
    response = client.delete(f"/api/v1/companies/{company_id}")
    assert response.status_code == 204
    
    get_response = client.get(f"/api/v1/companies/{company_id}")
    assert get_response.status_code == 404

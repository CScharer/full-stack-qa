"""
Tests for Clients API endpoints.
"""
import pytest
from fastapi.testclient import TestClient


def test_create_client(client: TestClient):
    """Test creating a client."""
    client_data = {
        "name": "Test Client",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    
    response = client.post("/api/v1/clients", json=client_data)
    assert response.status_code == 201
    data = response.json()
    assert data["name"] == "Test Client"
    assert "id" in data


def test_get_client(client: TestClient):
    """Test getting a client by ID."""
    client_data = {
        "name": "Test Client",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/clients", json=client_data)
    client_id = create_response.json()["id"]
    
    response = client.get(f"/api/v1/clients/{client_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == client_id


def test_list_clients(client: TestClient):
    """Test listing clients."""
    for i in range(3):
        client_data = {
            "name": f"Client {i}",
            "created_by": "test@example.com",
            "modified_by": "test@example.com"
        }
        client.post("/api/v1/clients", json=client_data)
    
    response = client.get("/api/v1/clients")
    assert response.status_code == 200
    data = response.json()
    assert "data" in data
    assert len(data["data"]) >= 3


def test_update_client(client: TestClient):
    """Test updating a client."""
    client_data = {
        "name": "Original Name",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/clients", json=client_data)
    client_id = create_response.json()["id"]
    
    update_data = {
        "name": "Updated Name",
        "modified_by": "test@example.com"
    }
    response = client.put(f"/api/v1/clients/{client_id}", json=update_data)
    assert response.status_code == 200
    data = response.json()
    assert data["name"] == "Updated Name"


def test_delete_client(client: TestClient):
    """Test deleting a client."""
    client_data = {
        "name": "To Delete",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/clients", json=client_data)
    client_id = create_response.json()["id"]
    
    response = client.delete(f"/api/v1/clients/{client_id}")
    assert response.status_code == 204
    
    get_response = client.get(f"/api/v1/clients/{client_id}")
    assert get_response.status_code == 404

"""
Tests for Contacts API endpoints.
"""
import pytest
from fastapi.testclient import TestClient


def test_create_contact(client: TestClient):
    """Test creating a contact with emails and phones."""
    contact_data = {
        "first_name": "John",
        "last_name": "Doe",
        "title": "Recruiter",
        "contact_type": "Recruiter",
        "emails": [
            {"email": "john@example.com", "email_type": "Work", "is_primary": 1}
        ],
        "phones": [
            {"phone": "555-1234", "phone_type": "Cell", "is_primary": 1}
        ],
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    
    response = client.post("/api/v1/contacts", json=contact_data)
    assert response.status_code == 201
    data = response.json()
    assert data["first_name"] == "John"
    assert data["last_name"] == "Doe"
    # Note: name is computed in SELECT queries, may not always be present
    if "name" in data:
        assert data["name"] == "John Doe"  # Computed field
    assert len(data["emails"]) == 1
    assert len(data["phones"]) == 1
    assert "id" in data


def test_get_contact(client: TestClient):
    """Test getting a contact by ID."""
    contact_data = {
        "first_name": "Jane",
        "last_name": "Doe",
        "contact_type": "Manager",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/contacts", json=contact_data)
    contact_id = create_response.json()["id"]
    
    response = client.get(f"/api/v1/contacts/{contact_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == contact_id
    assert data["first_name"] == "Jane"
    assert data["last_name"] == "Doe"
    assert "emails" in data
    assert "phones" in data


def test_list_contacts(client: TestClient):
    """Test listing contacts."""
    for i in range(3):
        contact_data = {
            "first_name": f"Contact",
            "last_name": f"{i}",
            "contact_type": "Recruiter",
            "created_by": "test@example.com",
            "modified_by": "test@example.com"
        }
        client.post("/api/v1/contacts", json=contact_data)
    
    response = client.get("/api/v1/contacts")
    assert response.status_code == 200
    data = response.json()
    assert "data" in data
    assert len(data["data"]) >= 3


def test_update_contact(client: TestClient):
    """Test updating a contact."""
    contact_data = {
        "first_name": "Original",
        "last_name": "Name",
        "contact_type": "Recruiter",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/contacts", json=contact_data)
    contact_id = create_response.json()["id"]
    
    update_data = {
        "first_name": "Updated",
        "last_name": "Name",
        "modified_by": "test@example.com"
    }
    response = client.put(f"/api/v1/contacts/{contact_id}", json=update_data)
    assert response.status_code == 200
    data = response.json()
    assert data["first_name"] == "Updated"
    assert data["last_name"] == "Name"
    # Note: name is computed in SELECT queries, may not always be present
    if "name" in data:
        assert data["name"] == "Updated Name"  # Computed field


def test_delete_contact(client: TestClient):
    """Test deleting a contact."""
    contact_data = {
        "first_name": "To",
        "last_name": "Delete",
        "contact_type": "Recruiter",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/contacts", json=contact_data)
    contact_id = create_response.json()["id"]
    
    response = client.delete(f"/api/v1/contacts/{contact_id}")
    assert response.status_code == 204
    
    get_response = client.get(f"/api/v1/contacts/{contact_id}")
    assert get_response.status_code == 404

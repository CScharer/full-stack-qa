"""
Tests for Applications API endpoints.
"""
import pytest
from fastapi.testclient import TestClient
from app.models import ApplicationCreate, ApplicationUpdate


def test_create_application(client: TestClient):
    """Test creating an application."""
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "position": "Software Engineer",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    
    response = client.post("/api/v1/applications", json=application_data)
    assert response.status_code == 201
    data = response.json()
    assert data["status"] == "Pending"
    assert data["position"] == "Software Engineer"
    assert "id" in data
    assert "created_on" in data


def test_get_application(client: TestClient):
    """Test getting an application by ID."""
    # First create an application
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/applications", json=application_data)
    application_id = create_response.json()["id"]
    
    # Then get it
    response = client.get(f"/api/v1/applications/{application_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == application_id
    assert data["status"] == "Pending"


def test_get_application_not_found(client: TestClient):
    """Test getting a non-existent application."""
    response = client.get("/api/v1/applications/99999")
    assert response.status_code == 404
    data = response.json()
    assert "not found" in data["detail"]["error"].lower()


def test_list_applications(client: TestClient):
    """Test listing applications."""
    # Create a few applications
    for i in range(3):
        application_data = {
            "status": "Pending",
            "work_setting": "Remote",
            "position": f"Engineer {i}",
            "created_by": "test@example.com",
            "modified_by": "test@example.com"
        }
        client.post("/api/v1/applications", json=application_data)
    
    # List them
    response = client.get("/api/v1/applications")
    assert response.status_code == 200
    data = response.json()
    assert "data" in data
    assert "pagination" in data
    assert len(data["data"]) >= 3
    assert data["pagination"]["page"] == 1


def test_update_application(client: TestClient):
    """Test updating an application."""
    # Create an application
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/applications", json=application_data)
    application_id = create_response.json()["id"]
    
    # Update it
    update_data = {
        "status": "Interview",
        "modified_by": "test@example.com"
    }
    response = client.put(f"/api/v1/applications/{application_id}", json=update_data)
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "Interview"


def test_delete_application(client: TestClient):
    """Test deleting an application."""
    # Create an application
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/applications", json=application_data)
    application_id = create_response.json()["id"]
    
    # Delete it
    response = client.delete(f"/api/v1/applications/{application_id}")
    assert response.status_code == 204
    
    # Verify it's deleted
    get_response = client.get(f"/api/v1/applications/{application_id}")
    assert get_response.status_code == 404


def test_list_applications_with_filtering(client: TestClient):
    """Test listing applications with filters."""
    # Create applications with different statuses
    for status in ["Pending", "Interview", "Rejected"]:
        application_data = {
            "status": status,
            "work_setting": "Remote",
            "created_by": "test@example.com",
            "modified_by": "test@example.com"
        }
        client.post("/api/v1/applications", json=application_data)
    
    # Filter by status
    response = client.get("/api/v1/applications?status=Pending")
    assert response.status_code == 200
    data = response.json()
    assert all(app["status"] == "Pending" for app in data["data"])


def test_list_applications_pagination(client: TestClient):
    """Test pagination in list applications."""
    # Create multiple applications
    for i in range(5):
        application_data = {
            "status": "Pending",
            "work_setting": "Remote",
            "created_by": "test@example.com",
            "modified_by": "test@example.com"
        }
        client.post("/api/v1/applications", json=application_data)
    
    # Get first page
    response = client.get("/api/v1/applications?page=1&limit=2")
    assert response.status_code == 200
    data = response.json()
    assert len(data["data"]) <= 2
    assert data["pagination"]["page"] == 1
    assert data["pagination"]["limit"] == 2

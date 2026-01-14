"""
Tests for Notes API endpoints.
"""
import pytest
from fastapi.testclient import TestClient
from conftest import api_url

ENDPOINT: str = "/notes"
ENDPOINT_APPLICATIONS: str = "/applications"


def test_create_note(client: TestClient):
    """Test creating a note."""
    # First create an application
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    app_response = client.post(api_url(ENDPOINT_APPLICATIONS), json=application_data)
    application_id = app_response.json()["id"]
    
    # Then create a note
    note_data = {
        "application_id": application_id,
        "note": "This is a test note",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    
    response = client.post(api_url(ENDPOINT), json=note_data)
    assert response.status_code == 201
    data = response.json()
    assert data["note"] == "This is a test note"
    assert data["application_id"] == application_id
    assert "id" in data


def test_get_note(client: TestClient):
    """Test getting a note by ID."""
    # Create application and note
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    app_response = client.post(api_url(ENDPOINT_APPLICATIONS), json=application_data)
    application_id = app_response.json()["id"]
    
    note_data = {
        "application_id": application_id,
        "note": "Test note",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post(api_url(ENDPOINT), json=note_data)
    note_id = create_response.json()["id"]
    
    response = client.get(api_url(f"{ENDPOINT}/{note_id}"))
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == note_id


def test_list_notes(client: TestClient):
    """Test listing notes."""
    # Create application
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    app_response = client.post(api_url(ENDPOINT_APPLICATIONS), json=application_data)
    application_id = app_response.json()["id"]
    
    # Create multiple notes
    for i in range(3):
        note_data = {
            "application_id": application_id,
            "note": f"Note {i}",
            "created_by": "test@example.com",
            "modified_by": "test@example.com"
        }
        client.post(api_url(ENDPOINT), json=note_data)
    
    response = client.get(api_url(f"{ENDPOINT}?application_id={application_id}"))
    assert response.status_code == 200
    data = response.json()
    assert "data" in data
    assert len(data["data"]) >= 3


def test_update_note(client: TestClient):
    """Test updating a note."""
    # Create application and note
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    app_response = client.post(api_url(ENDPOINT_APPLICATIONS), json=application_data)
    application_id = app_response.json()["id"]
    
    note_data = {
        "application_id": application_id,
        "note": "Original note",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post(api_url(ENDPOINT), json=note_data)
    note_id = create_response.json()["id"]
    
    update_data = {
        "note": "Updated note",
        "modified_by": "test@example.com"
    }
    response = client.put(api_url(f"{ENDPOINT}/{note_id}"), json=update_data)
    assert response.status_code == 200
    data = response.json()
    assert data["note"] == "Updated note"


def test_delete_note(client: TestClient):
    """Test deleting a note."""
    # Create application and note
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    app_response = client.post(api_url(ENDPOINT_APPLICATIONS), json=application_data)
    application_id = app_response.json()["id"]
    
    note_data = {
        "application_id": application_id,
        "note": "To delete",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post(api_url(ENDPOINT), json=note_data)
    note_id = create_response.json()["id"]
    
    response = client.delete(api_url(f"{ENDPOINT}/{note_id}"))
    assert response.status_code == 204
    
    get_response = client.get(api_url(f"{ENDPOINT}/{note_id}"))
    assert get_response.status_code == 404

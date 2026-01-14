"""
Tests for Job Search Sites API endpoints.
"""
import pytest
from fastapi.testclient import TestClient
from conftest import api_url

ENDPOINT: str = "/job-search-sites"


def test_create_job_search_site(client: TestClient):
    """Test creating a job search site."""
    import uuid
    unique_name = f"LinkedIn_{uuid.uuid4().hex[:8]}"
    site_data = {
        "name": unique_name,
        "url": "https://linkedin.com/jobs",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    
    response = client.post(api_url(ENDPOINT), json=site_data)
    assert response.status_code == 201
    data = response.json()
    assert data["name"] == unique_name
    assert "id" in data


def test_get_job_search_site(client: TestClient):
    """Test getting a job search site by ID."""
    import uuid
    unique_name = f"Indeed_{uuid.uuid4().hex[:8]}"
    site_data = {
        "name": unique_name,
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post(api_url(ENDPOINT), json=site_data)
    site_id = create_response.json()["id"]
    
    response = client.get(api_url(f"{ENDPOINT}/{site_id}"))
    assert response.status_code == 200
    data = response.json()
    assert data["id"] == site_id


def test_list_job_search_sites(client: TestClient):
    """Test listing job search sites."""
    for i in range(3):
        site_data = {
            "name": f"Site {i}",
            "created_by": "test@example.com",
            "modified_by": "test@example.com"
        }
        client.post(api_url(ENDPOINT), json=site_data)
    
    response = client.get(api_url(ENDPOINT))
    assert response.status_code == 200
    data = response.json()
    assert "data" in data
    assert len(data["data"]) >= 3


def test_update_job_search_site(client: TestClient):
    """Test updating a job search site."""
    site_data = {
        "name": "Original Name",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post(api_url(ENDPOINT), json=site_data)
    site_id = create_response.json()["id"]
    
    update_data = {
        "name": "Updated Name",
        "modified_by": "test@example.com"
    }
    response = client.put(api_url(f"{ENDPOINT}/{site_id}"), json=update_data)
    assert response.status_code == 200
    data = response.json()
    assert data["name"] == "Updated Name"


def test_delete_job_search_site(client: TestClient):
    """Test deleting a job search site."""
    site_data = {
        "name": "To Delete",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post(api_url(ENDPOINT), json=site_data)
    site_id = create_response.json()["id"]
    
    response = client.delete(api_url(f"{ENDPOINT}/{site_id}"))
    assert response.status_code == 204
    
    get_response = client.get(api_url(f"{ENDPOINT}/{site_id}"))
    assert get_response.status_code == 404


def test_create_duplicate_job_search_site(client: TestClient):
    """Test creating a duplicate job search site (should fail)."""
    site_data = {
        "name": "Unique Site",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    client.post(api_url(ENDPOINT), json=site_data)
    
    # Try to create duplicate
    response = client.post(api_url(ENDPOINT), json=site_data)
    assert response.status_code == 409

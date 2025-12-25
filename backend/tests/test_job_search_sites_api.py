"""
Tests for Job Search Sites API endpoints.
"""
import pytest
from fastapi.testclient import TestClient


def test_create_job_search_site(client: TestClient):
    """Test creating a job search site."""
    site_data = {
        "name": "LinkedIn",
        "url": "https://linkedin.com/jobs",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    
    response = client.post("/api/v1/job-search-sites", json=site_data)
    assert response.status_code == 201
    data = response.json()
    assert data["name"] == "LinkedIn"
    assert "id" in data


def test_get_job_search_site(client: TestClient):
    """Test getting a job search site by ID."""
    site_data = {
        "name": "Indeed",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    create_response = client.post("/api/v1/job-search-sites", json=site_data)
    site_id = create_response.json()["id"]
    
    response = client.get(f"/api/v1/job-search-sites/{site_id}")
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
        client.post("/api/v1/job-search-sites", json=site_data)
    
    response = client.get("/api/v1/job-search-sites")
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
    create_response = client.post("/api/v1/job-search-sites", json=site_data)
    site_id = create_response.json()["id"]
    
    update_data = {
        "name": "Updated Name",
        "modified_by": "test@example.com"
    }
    response = client.put(f"/api/v1/job-search-sites/{site_id}", json=update_data)
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
    create_response = client.post("/api/v1/job-search-sites", json=site_data)
    site_id = create_response.json()["id"]
    
    response = client.delete(f"/api/v1/job-search-sites/{site_id}")
    assert response.status_code == 204
    
    get_response = client.get(f"/api/v1/job-search-sites/{site_id}")
    assert get_response.status_code == 404


def test_create_duplicate_job_search_site(client: TestClient):
    """Test creating a duplicate job search site (should fail)."""
    site_data = {
        "name": "Unique Site",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    client.post("/api/v1/job-search-sites", json=site_data)
    
    # Try to create duplicate
    response = client.post("/api/v1/job-search-sites", json=site_data)
    assert response.status_code == 409

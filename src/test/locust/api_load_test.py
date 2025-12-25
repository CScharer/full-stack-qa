"""
Locust API Load Test

Tests internal API performance under concurrent user load:
- Simulates real user interaction with the application API
- Measures response times for core entities (applications, companies)
- Validates health and reliability of the backend service
"""

from locust import HttpUser, task, between, events
import json
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class ApiUser(HttpUser):
    """
    Simulates a user interacting with the internal REST API
    """

    # Wait 1-3 seconds between tasks
    wait_time = between(1, 3)

    # Note: host will be provided via command line --host argument
    # or defaults to local dev port if run manually
    host = "http://127.0.0.1:8003"

    def on_start(self):
        """Called when a user starts"""
        logger.info(f"User started - API Load Test targeting {self.host}")

    @task(10)
    def get_applications(self):
        """GET /api/v1/applications - Retrieve all job applications"""
        with self.client.get(
            "/api/v1/applications",
            catch_response=True,
            name="GET /applications"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status code: {response.status_code}")

    @task(8)
    def get_companies(self):
        """GET /api/v1/companies - Retrieve all companies"""
        with self.client.get(
            "/api/v1/companies",
            catch_response=True,
            name="GET /companies"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status code: {response.status_code}")

    @task(5)
    def get_health(self):
        """GET /health - Check API health"""
        with self.client.get(
            "/health",
            catch_response=True,
            name="GET /health"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status code: {response.status_code}")

    @task(3)
    def get_contacts(self):
        """GET /api/v1/contacts - Retrieve all contacts"""
        with self.client.get(
            "/api/v1/contacts",
            catch_response=True,
            name="GET /contacts"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status code: {response.status_code}")


@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """Called when test starts"""
    logger.info("=" * 60)
    logger.info("🔥 LOCUST INTERNAL API PERFORMANCE TEST STARTING")
    logger.info(f"Target: {environment.host}")
    logger.info("=" * 60)


@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """Called when test stops"""
    logger.info("=" * 60)
    logger.info("✅ LOCUST INTERNAL API PERFORMANCE TEST COMPLETED")
    logger.info("=" * 60)

"""
Locust Web Load Test

Tests frontend performance for the internal application:
- Simulates users browsing the web application
- Measures page load times for core application routes
- Validates frontend reliability under load
"""

from locust import HttpUser, task, between, events
import logging
import os

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class WebsiteUser(HttpUser):
    """
    Simulates a user browsing the ONE GOAL web application
    """

    # Wait 2-5 seconds between tasks
    wait_time = between(2, 5)

    # Note: host will be provided via command line --host argument
    # or defaults to local dev port if run manually
    host = "http://127.0.0.1:3003"

    def on_start(self):
        """Called when a user starts"""
        logger.info(f"User started - Web Load Test targeting {self.host}")

    @task(5)
    def browse_homepage(self):
        """Visit application homepage"""
        with self.client.get(
            "/",
            catch_response=True,
            name="Page: Homepage"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")

    @task(3)
    def browse_applications(self):
        """Visit Applications page"""
        with self.client.get(
            "/applications",
            catch_response=True,
            name="Page: Applications"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")

    @task(2)
    def browse_companies(self):
        """Visit Companies page"""
        with self.client.get(
            "/companies",
            catch_response=True,
            name="Page: Companies"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")

    @task(2)
    def browse_contacts(self):
        """Visit Contacts page"""
        with self.client.get(
            "/contacts",
            catch_response=True,
            name="Page: Contacts"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")


@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """Called when test starts"""
    logger.info("=" * 60)
    logger.info("🌐 LOCUST INTERNAL WEB LOAD TEST STARTING")
    logger.info(f"Target Frontend: {environment.host}")
    logger.info("=" * 60)


@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """Called when test stops"""
    logger.info("=" * 60)
    logger.info("✅ LOCUST INTERNAL WEB LOAD TEST COMPLETED")
    logger.info("=" * 60)

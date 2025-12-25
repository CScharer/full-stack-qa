"""
Locust Comprehensive Load Test

Advanced performance testing scenarios for internal application:
- Sequential user journeys
- Multi-endpoint interaction
- Entity relationship browsing
"""

from locust import HttpUser, task, between, SequentialTaskSet, events
import json
import random
import logging
import os

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class UserJourney(SequentialTaskSet):
    """
    Sequential user journey through the application
    """

    @task
    def step1_check_health(self):
        """Step 1: System health check"""
        self.client.get("/health", name="1. Health Check")

    @task
    def step2_browse_applications(self):
        """Step 2: Browse all applications"""
        self.client.get("/api/v1/applications", name="2. Browse Applications")

    @task
    def step3_browse_companies(self):
        """Step 3: Browse all companies"""
        self.client.get("/api/v1/companies", name="3. Browse Companies")

    @task
    def step4_browse_contacts(self):
        """Step 4: Browse all contacts"""
        self.client.get("/api/v1/contacts", name="4. Browse Contacts")

    @task
    def step5_browse_notes(self):
        """Step 5: Browse all notes"""
        self.client.get("/api/v1/notes", name="5. Browse Notes")


class ComprehensiveUser(HttpUser):
    """
    Comprehensive load testing with realistic patterns
    """

    tasks = [UserJourney]
    wait_time = between(1, 5)
    
    # Note: host will be provided via command line --host argument
    # or defaults to local dev port if run manually
    host = "http://127.0.0.1:8003"

    @task(10)
    def fast_health_check(self):
        """Frequent health checks"""
        self.client.get("/health", name="Pulse: Health Check")

    @task(5)
    def check_version(self):
        """Check API root for version info"""
        self.client.get("/", name="Root: Version Info")


@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """Test start event"""
    logger.info("=" * 80)
    logger.info("🚀 COMPREHENSIVE INTERNAL LOAD TEST STARTING")
    logger.info(f"Target: {environment.host}")
    logger.info("=" * 80)


@events.test_stop.add_listener
def on_test_stop(environment, **kwargs):
    """Test stop event"""
    stats = environment.stats
    logger.info("=" * 80)
    logger.info("📊 LOAD TEST RESULTS:")
    logger.info(f"  Total requests: {stats.total.num_requests}")
    logger.info(f"  Failures: {stats.total.num_failures}")
    logger.info(f"  RPS: {stats.total.total_rps:.2f}")
    logger.info(f"  Avg response time: {stats.total.avg_response_time:.2f}ms")
    logger.info("=" * 80)
    logger.info("✅ Internal reports saved to target/locust/")
    logger.info("=" * 80)

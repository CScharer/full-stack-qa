package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling Load Test Simulation
 *
 * Tests API performance under load:
 * - 30% of total performance testing
 * - Simulates realistic user load
 * - Measures response times and throughput
 * - Generates detailed HTML reports
 *
 * Two modes (see env vars below):
 * - Internal: When `BACKEND_URL` or `GATLING_API_BASE_URL` is set (e.g. CI via `env-be.yml`),
 *   exercises this repo's backend: `/health` and list endpoints under `GATLING_API_BASE_PATH`
 *   (default `/api/v1`). Matches Locust/JMeter targets.
 * - Demo: When neither is set, uses jsonplaceholder.typicode.com so you can run Gatling without
 *   a local API.
 *
 * Load profile:
 * - Ramp up from 1 to 50 users over 30 seconds
 * - Sustain load at 5 users/sec for 60 seconds
 *
 * Run: mvn gatling:test -Pgatling
 */
class ApiLoadSimulation extends Simulation {

  /** Set in CI to the service URL (e.g. http://localhost:8003). Optional override: GATLING_API_BASE_URL. */
  private val internalApiBase: Option[String] =
    Option(System.getenv("GATLING_API_BASE_URL"))
      .orElse(Option(System.getenv("BACKEND_URL")))
      .map(_.trim)
      .filter(_.nonEmpty)

  /** Prefix for REST routes in internal mode; default matches `config` / Locust. */
  private val apiBasePath: String =
    Option(System.getenv("GATLING_API_BASE_PATH")).map(_.trim).filter(_.nonEmpty).getOrElse("/api/v1")

  private val httpProtocol = internalApiBase match {
    case Some(base) =>
      http
        .baseUrl(base)
        .acceptHeader("application/json")
        .acceptEncodingHeader("gzip, deflate")
        .userAgentHeader("Gatling Performance Test")
    case None =>
      http
        .baseUrl("https://jsonplaceholder.typicode.com")
        .acceptHeader("application/json")
        .acceptEncodingHeader("gzip, deflate")
        .userAgentHeader("Gatling Performance Test")
  }

  private val browseApi = internalApiBase match {
    case Some(_) =>
      // Internal stack: health + core read-only list endpoints
      scenario("API Load Test (internal)")
        .exec(
          http("Health Check")
            .get("/health")
            .check(status.is(200))
        )
        .pause(1)
        .exec(
          http("Get Applications")
            .get(s"$apiBasePath/applications")
            .check(status.is(200))
        )
        .pause(1)
        .exec(
          http("Get Companies")
            .get(s"$apiBasePath/companies")
            .check(status.is(200))
        )
    case None =>
      // Public demo API: posts, users, and a create-post mutation
      scenario("API Load Test (demo)")
        .exec(
          http("Get All Posts")
            .get("/posts")
            .check(status.is(200))
            .check(jsonPath("$[0].id").exists)
        )
        .pause(1)
        .exec(
          http("Get Single Post")
            .get("/posts/1")
            .check(status.is(200))
            .check(jsonPath("$.userId").is("1"))
            .check(jsonPath("$.title").exists)
        )
        .pause(1)
        .exec(
          http("Get Post Comments")
            .get("/posts/1/comments")
            .check(status.is(200))
            .check(jsonPath("$[0].postId").is("1"))
        )
        .pause(1)
        .exec(
          http("Get User")
            .get("/users/1")
            .check(status.is(200))
            .check(jsonPath("$.email").exists)
        )
        .pause(1)
        .exec(
          http("Create Post")
            .post("/posts")
            .header("Content-Type", "application/json")
            .body(StringBody("""{"title": "Test Post", "body": "Test Body", "userId": 1}"""))
            .check(status.is(201))
        )
  }

  setUp(
    browseApi.inject(
      rampUsers(50).during(30.seconds),
      constantUsersPerSec(5).during(60.seconds)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(5000), // Max response time < 5s
      global.responseTime.mean.lt(1000), // Mean response time < 1s
      global.successfulRequests.percent.gt(95) // Success rate > 95%
    )
}

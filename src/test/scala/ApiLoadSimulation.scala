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
 * Scenario:
 * - Ramp up from 1 to 50 users over 30 seconds
 * - Sustain 50 users for 60 seconds
 * - Test multiple API endpoints
 *
 * Run: mvn gatling:test
 */
class ApiLoadSimulation extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling Performance Test")

  // Scenario: Browse and interact with API
  val browseApi = scenario("API Load Test")
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

  // Load Profile: Ramp up to 50 users over 30s, sustain for 60s
  setUp(
    browseApi.inject(
      rampUsers(50).during(30.seconds),
      constantUsersPerSec(5).during(60.seconds)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(5000),        // Max response time < 5s
      global.responseTime.mean.lt(1000),       // Mean response time < 1s
      global.successfulRequests.percent.gt(95) // Success rate > 95%
    )
}

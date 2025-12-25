package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling Web Load Test
 *
 * Tests web page performance under load:
 * - Simulates multiple users browsing websites
 * - Measures page load times
 * - Tests concurrent access patterns
 *
 * Scenario:
 * - Ramp up from 1 to 30 users over 20 seconds
 * - Each user navigates through multiple pages
 *
 * Run: mvn gatling:test -Dgatling.simulationClass=simulations.WebLoadSimulation
 */
class WebLoadSimulation extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .acceptHeader("text/html,application/xhtml+xml,application/xml")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Gatling Performance Test)")

  // Scenario: Browse multiple websites
  val browseWebsites = scenario("Web Browsing Load Test")
    .exec(
      http("Load Google Homepage")
        .get("https://www.google.com")
        .check(status.is(200))
    )
    .pause(2)
    .exec(
      http("Load GitHub Homepage")
        .get("https://github.com")
        .check(status.is(200))
    )
    .pause(2)
    .exec(
      http("Load Wikipedia Homepage")
        .get("https://www.wikipedia.org")
        .check(status.is(200))
    )
    .pause(2)
    .exec(
      http("Load W3C Homepage")
        .get("https://www.w3.org")
        .check(status.is(200))
    )

  // Load Profile: Ramp up to 30 concurrent users
  setUp(
    browseWebsites.inject(
      rampUsers(30).during(20.seconds)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(10000),       // Max response time < 10s
      global.responseTime.mean.lt(3000),       // Mean response time < 3s
      global.successfulRequests.percent.gt(90) // Success rate > 90%
    )
}

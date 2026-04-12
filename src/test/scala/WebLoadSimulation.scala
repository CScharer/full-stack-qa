package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling Web Load Test
 *
 * Tests web page performance under load:
 * - Simulates multiple users requesting pages
 * - Measures response times and concurrent access patterns
 * - Generates detailed HTML reports
 *
 * Two modes:
 * - Internal: When `GATLING_WEB_BASE_URL`, `FRONTEND_URL`, or `BASE_URL` is set (CI sets
 *   `BASE_URL` / `FRONTEND_URL` to the local app), only hits that origin (repeated `GET /`).
 *   Avoids third-party sites on runners where bots often get non-200 responses.
 * - External demo: When none of those env vars are set, loads several public homepages
 *   (Google, GitHub, Wikipedia, W3C) for local exploration without the frontend running.
 *
 * Scenario shape:
 * - Each virtual user walks through a short sequence of HTTP requests (several pages or repeats).
 *
 * Load profile:
 * - Ramp up from 1 to 30 users over 20 seconds
 *
 * Run: mvn gatling:test -Pgatling
 * Run this simulation only:
 *   mvn gatling:test -Pgatling -Dgatling.simulationClass=simulations.WebLoadSimulation
 */
class WebLoadSimulation extends Simulation {

  /** First match wins: explicit web base, then frontend, then generic BASE_URL (CI). */
  private val internalWebBase: Option[String] =
    Option(System.getenv("GATLING_WEB_BASE_URL"))
      .orElse(Option(System.getenv("FRONTEND_URL")))
      .orElse(Option(System.getenv("BASE_URL")))
      .map(_.trim)
      .filter(_.nonEmpty)

  private val httpProtocol = internalWebBase match {
    case Some(base) =>
      http
        .baseUrl(base)
        .acceptHeader("text/html,application/xhtml+xml,application/xml")
        .acceptEncodingHeader("gzip, deflate")
        .userAgentHeader("Mozilla/5.0 (Gatling Performance Test)")
    case None =>
      // Absolute URLs in requests; no single baseUrl
      http
        .acceptHeader("text/html,application/xhtml+xml,application/xml")
        .acceptEncodingHeader("gzip, deflate")
        .userAgentHeader("Mozilla/5.0 (Gatling Performance Test)")
  }

  private val browseWebsites = internalWebBase match {
    case Some(_) =>
      // Same route multiple times to mimic multi-tab / repeat visits under load
      scenario("Web Load Test (internal)")
        .exec(
          http("Load app root")
            .get("/")
            .check(status.in(200, 204, 304))
        )
        .pause(2)
        .exec(
          http("Load app root again")
            .get("/")
            .check(status.in(200, 204, 304))
        )
        .pause(2)
        .exec(
          http("Load app root (concurrent pattern)")
            .get("/")
            .check(status.in(200, 204, 304))
        )
    case None =>
      scenario("Web Browsing Load Test (external demo)")
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
  }

  setUp(
    browseWebsites.inject(
      rampUsers(30).during(20.seconds)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(10000), // Max response time < 10s
      global.responseTime.mean.lt(3000), // Mean response time < 3s
      global.successfulRequests.percent.gt(90) // Success rate > 90%
    )
}

package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.probate.performance.scenarios._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils._

import scala.concurrent.duration._

class Caveat extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources(BlackList("https://www.payments.service.gov.uk/.*"), WhiteList())
    .silentResources

  val ProbateNewCaveat = scenario( "ProbateNewCaveat").repeat(2) {

    exec(
      ProbateCaveat.ProbateCaveat
    )
  }

  setUp(
    ProbateNewCaveat.inject(rampUsers(1) during (1 minutes))
  )
    .protocols(httpProtocol)

}
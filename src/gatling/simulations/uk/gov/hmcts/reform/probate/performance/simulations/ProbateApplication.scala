package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.probate.performance.scenarios._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils._

import scala.concurrent.duration._

class ProbateApplication extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  val ProbateNewApplication = scenario( "ProbateNewApplication").repeat(1) {
    exec(
      Homepage.ProbateHomepage,
      Login.ProbateLogin,
      NewProbateApp_ExecOne_Apply.ProbateEligibility,
      NewProbateApp_ExecOne_Apply.ProbateApplication,
      Logout.ProbateLogout
    )
  }

  setUp(
    ProbateNewApplication.inject(rampUsers(1) during (1 minutes))
  )
    .protocols(httpProtocol)

}
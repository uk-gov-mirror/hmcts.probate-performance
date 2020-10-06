package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.probate.performance.scenarios._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils._

import scala.concurrent.duration._

class ProbateIntestacy extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  val ProbateNewIntestacyApplication = scenario( "ProbateNewIntestacyApplication").repeat(1) {

    exec(
      CreateUser.CreateCitizen,
      Homepage.ProbateHomepage,
      Login.ProbateLogin,
      ProbateApp_Intestacy.IntestacyEligibility,
      ProbateApp_Intestacy.IntestacyApplication,
      Logout.ProbateLogout
    )
  }

  setUp(
    ProbateNewIntestacyApplication.inject(rampUsers(1) during (1 minutes))
  )
    .protocols(httpProtocol)

}
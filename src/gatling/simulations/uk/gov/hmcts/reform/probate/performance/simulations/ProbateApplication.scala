package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.probate.performance.scenarios._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils._

import scala.concurrent.duration._

class ProbateApplication extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources(BlackList("https://www.payments.service.gov.uk/.*"), WhiteList())
    .silentResources

  val ProbateNewApplication = scenario( "ProbateNewApplication").repeat(1) {
    exitBlockOnFail {
      exec(
        CreateUser.CreateCitizen,
        Homepage.ProbateHomepage,
        Login.ProbateLogin,
        ProbateApp_ExecOne_Apply.ProbateEligibility,
        ProbateApp_ExecOne_Apply.ProbateApplication,
        Logout.ProbateLogout)
      .exec(flushHttpCache)
      .exec(
        ProbateApp_ExecTwo_Declaration.ProbateDeclaration)
      .exec(flushHttpCache)
      .exec(
        Homepage.ProbateHomepage,
        Login.ProbateLogin,
        ProbateApp_ExecOne_Submit.ProbateSubmit,
        Logout.ProbateLogout)
    }
      //.exec(DeleteUser.DeleteCitizen)
  }

  setUp(
    ProbateNewApplication.inject(atOnceUsers(1))
  ).protocols(httpProtocol)


}
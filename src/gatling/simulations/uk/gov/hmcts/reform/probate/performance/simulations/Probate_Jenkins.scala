package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.probate.performance.scenarios._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.{Environment, Common}
import scala.concurrent.duration._

class Probate_Jenkins extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources(BlackList("https://www.payments.service.gov.uk/.*"), WhiteList())
    .silentResources

  val AllApplications = scenario( "AllApplications")
    .exitBlockOnFail {
      exec(Common.ClearSessionVariables)
      .exec(
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
    .exec(DeleteUser.DeleteCitizen)
    .exitBlockOnFail {
      exec(Common.ClearSessionVariables)
      .exec(
        CreateUser.CreateCitizen,
        Homepage.ProbateHomepage,
        Login.ProbateLogin,
        ProbateApp_Intestacy.IntestacyEligibility,
        ProbateApp_Intestacy.IntestacyApplication,
        Logout.ProbateLogout
      )
    }
    .exec(DeleteUser.DeleteCitizen)
    .exitBlockOnFail {
      exec(Common.ClearSessionVariables)
      .exec(
        ProbateCaveat.ProbateCaveat
      )
    }

  setUp(
    //.inject(rampUsers(10).over(10 seconds))
    AllApplications.inject(rampUsers(10) during (2 minutes))
  ).protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(95))

}
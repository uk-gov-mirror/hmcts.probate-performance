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

  val numberOfPipelineUsers:Double = 10

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
    AllApplications.inject(rampUsers(numberOfPipelineUsers.toInt) during (2 minutes))
  ).protocols(httpProtocol)
    .assertions(
      //ensure at least 95% of attempted transactions have passed
      global.successfulRequests.percent.gte(95),
      //ensure that at least 80% of the users complete each journey end to end
      details("Probate_590_DownloadDeclarationPDF").successfulRequests.count.gte((numberOfPipelineUsers * 0.8).ceil.toInt),
      details("Intestacy_420_DownloadDeclarationPDF").successfulRequests.count.gte((numberOfPipelineUsers * 0.8).ceil.toInt),
      details("Caveat_170_CardDetailsConfirmSubmit").successfulRequests.count.gte((numberOfPipelineUsers * 0.8).ceil.toInt)
    )

}

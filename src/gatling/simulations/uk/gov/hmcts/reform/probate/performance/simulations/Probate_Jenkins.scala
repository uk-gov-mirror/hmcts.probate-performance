package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.probate.performance.scenarios._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

class Probate_Jenkins extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  val AllApplications = scenario( "AllApplications")
    .exitBlockOnFail {
      exec(flushHttpCache)
      .exec(flushCookieJar)
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
    //.exec(DeleteUser.DeleteCitizen)
    .exitBlockOnFail {
      exec(flushHttpCache)
      .exec(flushCookieJar)
      .exec { session =>
        println(session)
        session
      }
      .exec(
        CreateUser.CreateCitizen,
        Homepage.ProbateHomepage,
        Login.ProbateLogin,
        ProbateApp_Intestacy.IntestacyEligibility,
        ProbateApp_Intestacy.IntestacyApplication,
        Logout.ProbateLogout
      )
    }
    //.exec(DeleteUser.DeleteCitizen)
    .exitBlockOnFail {
      exec(flushHttpCache)
      .exec(flushCookieJar)
      .exec(
        ProbateCaveat.ProbateCaveat
      )
    }

  setUp(
    AllApplications.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
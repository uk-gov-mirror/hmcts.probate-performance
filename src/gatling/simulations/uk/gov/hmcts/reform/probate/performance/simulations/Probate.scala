package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.probate.performance.scenarios._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

class Probate extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  val probateAppUsers = 3
  val probateIntestacyUsers = 3
  val probateCaveatUsers = 3
  val rampDurationMins = 1
  val testDurationMins = 5

  before{
    println(s"Probate Application Users: ${probateAppUsers}")
    println(s"Probate Intestacy Users: ${probateIntestacyUsers}")
    println(s"Probate Caveat Users: ${probateCaveatUsers}")
    println(s"Total Test Duration: ${testDurationMins} minutes")
  }

  val ProbateNewApplication = scenario( "ProbateNewApplication")
    .forever() {
      exec(
        CreateUser.CreateCitizen,
        Homepage.ProbateHomepage,
        Login.ProbateLogin,
        ProbateApp_ExecOne_Apply.ProbateEligibility,
        ProbateApp_ExecOne_Apply.ProbateApplication,
        Logout.ProbateLogout,
        ProbateApp_ExecTwo_Declaration.ProbateDeclaration,
        Homepage.ProbateHomepage,
        Login.ProbateLogin,
        ProbateApp_ExecOne_Submit.ProbateSubmit,
        Logout.ProbateLogout
      )
    }

  val ProbateNewIntestacyApplication = scenario( "ProbateNewIntestacyApplication")
    .forever() {
      exec(
        CreateUser.CreateCitizen,
        Homepage.ProbateHomepage,
        Login.ProbateLogin,
        ProbateApp_Intestacy.IntestacyEligibility,
        ProbateApp_Intestacy.IntestacyApplication,
        Logout.ProbateLogout
      )
    }

  val ProbateNewCaveat = scenario( "ProbateNewCaveat")
    .forever() {
      exec(
        ProbateCaveat.ProbateCaveat
      )
    }

  setUp(
    ProbateNewApplication.inject(
      rampUsers(probateAppUsers) during (rampDurationMins minutes)
    ),
    ProbateNewIntestacyApplication.inject(
      nothingFor(10 seconds),
      rampUsers(probateIntestacyUsers) during (rampDurationMins minutes)
    ),
    ProbateNewCaveat.inject(
      nothingFor(20 seconds),
      rampUsers(probateCaveatUsers) during (rampDurationMins minutes)
    ),
  )
    .protocols(httpProtocol)
    .maxDuration(testDurationMins minutes)

}
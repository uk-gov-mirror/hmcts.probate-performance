package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.reform.probate.performance.scenarios._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._
import scala.util.Random

class Probate extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  val rampUpDurationMins = 2
  val rampDownDurationMins = 2
  val testDurationMins = 60

  //Must be doubles to ensure the calculations result in doubles not rounded integers
  val probateHourlyTarget:Double = 88
  val intestacyHourlyTarget:Double = 12
  val caveatHourlyTarget:Double = 53

  val continueAfterEligibilityPercentage = 58

  val probateRatePerSec = probateHourlyTarget / 3600
  val intestacyRatePerSec = intestacyHourlyTarget / 3600
  val caveatRatePerSec = caveatHourlyTarget / 3600

  val randomFeeder = Iterator.continually( Map( "perc" -> Random.nextInt(100)))

  before{
    println(s"Total Test Duration: ${testDurationMins} minutes")
  }

  val ProbateNewApplication = scenario( "ProbateNewApplication")
    .feed(randomFeeder)
      .exitBlockOnFail {
        exec(
          CreateUser.CreateCitizen,
          Homepage.ProbateHomepage,
          Login.ProbateLogin,
          ProbateApp_ExecOne_Apply.ProbateEligibility
        )
        .doIf(session => session("perc").as[Int] < continueAfterEligibilityPercentage) {
          exec(
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
      }
      .exec(DeleteUser.DeleteCitizen)

  val ProbateNewIntestacyApplication = scenario( "ProbateNewIntestacyApplication")
    .feed(randomFeeder)
      .exitBlockOnFail {
        exec(
          CreateUser.CreateCitizen,
          Homepage.ProbateHomepage,
          Login.ProbateLogin,
          ProbateApp_Intestacy.IntestacyEligibility
        )
        .doIf(session => session("perc").as[Int] < continueAfterEligibilityPercentage) {
          exec(
            ProbateApp_Intestacy.IntestacyApplication,
            Logout.ProbateLogout
          )
        }
      }
      .exec(DeleteUser.DeleteCitizen)

  val ProbateNewCaveat = scenario( "ProbateNewCaveat")
    .exitBlockOnFail {
      exec(
        ProbateCaveat.ProbateCaveat
      )
    }

  setUp(
    ProbateNewApplication.inject(
      rampUsersPerSec(0.00) to (probateRatePerSec) during (rampUpDurationMins minutes),
      constantUsersPerSec(probateRatePerSec) during (testDurationMins minutes),
      rampUsersPerSec(probateRatePerSec) to (0.00) during (rampDownDurationMins minutes)
    ),
    ProbateNewIntestacyApplication.inject(
      nothingFor(20 seconds),
      rampUsersPerSec(0.00) to (intestacyRatePerSec) during (rampUpDurationMins minutes),
      constantUsersPerSec(intestacyRatePerSec) during (testDurationMins minutes),
      rampUsersPerSec(intestacyRatePerSec) to (0.00) during (rampDownDurationMins minutes)
    ),
    ProbateNewCaveat.inject(
      nothingFor(40 seconds),
      rampUsersPerSec(0.00) to (caveatRatePerSec) during (rampUpDurationMins minutes),
      constantUsersPerSec(caveatRatePerSec) during (testDurationMins minutes),
      rampUsersPerSec(caveatRatePerSec) to (0.00) during (rampDownDurationMins minutes)
    ),
  )
    .protocols(httpProtocol)

}
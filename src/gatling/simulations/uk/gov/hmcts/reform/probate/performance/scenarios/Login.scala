package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object Login {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val ConstantThinkTime = Environment.constantThinkTime


  val HomepageHeader = Environment.homepageHeader
  val IdamHeader = Environment.idamHeader

  val CitizenUsername = "perftest004@perftest12345.com"
  val CitizenPassword = "Pa55word11"


  val ProbateLogin = group("Probate_Login") {

    exec(http("Probate_020_005_Login")
      .post(IdamURL + "/login?ui_locales=en&response_type=code&state=${state}&client_id=probate&redirect_uri=" + BaseURL + "/oauth2/callback")
      .disableFollowRedirect
      .headers(IdamHeader)
      .formParam("username", CitizenUsername)
      .formParam("password", CitizenPassword)
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "true")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&state").saveAs("authCode"))
      .check(status.in(200, 302)))

  }

}

package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object Login {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader


  val ProbateLogin =

    exec(http("Probate_000_005_Login")
      .post(IdamURL + "/login?ui_locales=en&response_type=code&state=${state}&client_id=probate&redirect_uri=" + BaseURL + "/oauth2/callback")
      .disableFollowRedirect
      .headers(CommonHeader)
      .headers(Map("accept-language" -> "en-GB,en;q=0.9",
        "content-type" -> "application/x-www-form-urlencoded",
        "sec-fetch-site" -> "same-origin"))
      .formParam("username", "${emailAddress}")
      .formParam("password", "${password}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "true")
      .formParam("_csrf", "${csrf}")
      .check(headerRegex("Location", "(?<=code=)(.*)&state").saveAs("authCode"))
      .check(status.in(200, 302)))

    .exec(http("Probate_000_010_Login")
      .get(BaseURL + "/oauth2/callback?code=${authCode}&state=${state}&client_id=probate&iss=" + IdamURL + "/o")
      .headers(CommonHeader)
      .headers(Map("accept-language" -> "en-GB,en;q=0.9",
        "sec-fetch-site" -> "same-site")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)


}

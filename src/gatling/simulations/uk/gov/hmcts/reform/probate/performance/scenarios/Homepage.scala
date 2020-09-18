package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object Homepage {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader

  val ProbateHomepage =

    exec(http("Probate_001_005_HomePage")
      .get(BaseURL + "/")
      .disableFollowRedirect
      .headers(CommonHeader)
      .header("sec-fetch-site", "none")
      .check(headerRegex("Location", "(?<=state=)(.*)&client").saveAs("state"))
      .check(status.in(200, 302)))

    .exec(http("Probate_001_010_HomePage")
      .get(IdamURL + "/login?ui_locales=en&response_type=code&state=${state}&client_id=probate&redirect_uri=" + BaseURL + "/oauth2/callback")
      .headers(CommonHeader)
      .header("sec-fetch-site", "none")
      .check(CurrentPageUrl.save)
      .check(CsrfCheck.save))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

}
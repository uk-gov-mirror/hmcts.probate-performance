package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object ProbateCaveat {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val ProbateCaveat = group("Probate_Caveat") {

    exec(http("ProbateCaveat_010_005_Homepage")
      .get(BaseURL + "/caveats/start-apply")
      .headers(CommonHeader)
      .header("sec-fetch-site", "none")
      .header("accept-language", "en-GB,en;q=0.9")
      .check(regex("Stop an application for probate")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("ProbateCaveat_010_010_ApplicantName")
        .get(BaseURL + "/caveats/applicant-name")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(CsrfCheck.save)
        .check(regex("What is your full name")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("ProbateCaveat_010_015_ApplicantNameSubmit")
        .post(BaseURL + "/caveats/applicant-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("pin", "${pin}")
        .check(regex("")))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)


  }

}
package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object Logout {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader

  val ProbateLogout = group("Probate_Logout") {

    exec(http("Probate_050_005_Logout")
      .get(BaseURL + "/sign-out")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("signed out")))

  }

}
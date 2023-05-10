package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.CsrfCheck
import utils.Environment

import scala.concurrent.duration._

object Homepage {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader

  val ProbateHomepage =

    exec(flushHttpCache)
    .exec(flushCookieJar)

    .group("Probate_001_HomePage") {

      exec(http("Probate_001_005_HomePage")
        .get(BaseURL + "/")
        .headers(CommonHeader)
        .header("sec-fetch-site", "none")
        .check(regex("state=([a-z0-9-]+)&client").saveAs("state"))
        .check(CsrfCheck.save)
        .check(substring("Sign in or create an account")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

}
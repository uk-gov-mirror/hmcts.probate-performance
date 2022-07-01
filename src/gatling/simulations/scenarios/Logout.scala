package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object Logout {

  val BaseURL = Environment.baseURL

  val CommonHeader = Environment.commonHeader

  val ProbateLogout =

    group("Probate_999_Logout") {

      exec(http("Logout")
        .get(BaseURL + "/sign-out")
        .headers(CommonHeader)
        .check(regex("signed out")))

    }

}
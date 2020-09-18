package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

object DeleteUser {

  val IdamAPIURL = Environment.idamAPIURL

  val DeleteCitizen =
    exec(http("Probate_000_DeleteCitizen")
      .delete(IdamAPIURL + "/testing-support/accounts/${emailAddress}")
      .check(status.is(204)))

}

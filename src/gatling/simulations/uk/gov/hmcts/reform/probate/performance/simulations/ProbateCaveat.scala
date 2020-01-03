package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import uk.gov.hmcts.reform.cmc.performance.utils.Environment
import uk.gov.hmcts.reform.probate.performance.caveat.CaveatJourney

class ProbateCaveat extends Simulation {

 // val userFeeder1 = csv("probate_executors1.csv").queue

  val httpProtocol: HttpProtocolBuilder = http.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    //.proxy(Proxy("proxyout.reform.hmcts.net", 8080))
    .baseUrl(Environment.caveatURL)
    .headers(Environment.commonHeader)



  val CaveatScn = scenario("Probate_Caveat").repeat(1)
  {
    exec(
      CaveatJourney.homePage,
      CaveatJourney.applicantName_get,
      CaveatJourney.applicantName_Post,
      CaveatJourney.emailAddress,
      CaveatJourney.addressLookup,
      CaveatJourney.address,
      CaveatJourney.deceasedName,
      CaveatJourney.deceasedDOD,
      CaveatJourney.deceasedDOBKnown,
      CaveatJourney.deceasedDOB,
      CaveatJourney.deceasedAlias,
      CaveatJourney.caveatFindAddress,
      CaveatJourney.caveatAddress,
      CaveatJourney.paymentBreakdown_get,
      CaveatJourney.paymentBreakdown_post,
      CaveatJourney.checkCardDetails,
      CaveatJourney.cardConfirmation

    )
  }

  setUp(CaveatScn.inject(atOnceUsers(1))).protocols(httpProtocol)

}

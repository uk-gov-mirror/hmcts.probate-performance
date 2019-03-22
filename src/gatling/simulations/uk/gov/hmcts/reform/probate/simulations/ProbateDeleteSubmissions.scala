package simulations.uk.gov.hmcts.reform.probate.simulations

import uk.gov.hmcts.reform.probate.performance.simulations.checks.CsrfCheck.{csrfParameter, csrfTemplate}
import uk.gov.hmcts.reform.probate.performance.simulations.checks.CsrfCheckForPayment.{csrfParameterForPayment, csrfTemplateForPayment}
import uk.gov.hmcts.reform.probate.performance.simulations.checks.CurrentPageUrl.currentPageTemplate
import uk.gov.hmcts.reform.probate.performance.simulations.checks.PaymentSessionToken.{chargeIdParameter, chargeIdTemplate}
import uk.gov.hmcts.reform.probate.performance.simulations.checks.CsrfCheck
import uk.gov.hmcts.reform.probate.performance.simulations.checks
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.http.check.HttpCheck
import jodd.lagarto.dom.NodeSelector
import java.util.concurrent._

class ProbateDeleteSubmissions extends Simulation {

  val userFeeder1 = csv("probate_executors1.csv").queue

  val httpProtocol = http
    .baseURL("https://probate-persistence-service-sprod.service.core-compute-sprod.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")

  val probUserFeed1 = feed(userFeeder1)

  val deleteSubmission =

    feed(userFeeder1)

    .exec(http("Delete_Submissions")
      .delete("/formdata/${email}")
      )

  val scn = scenario("DeleteProbateSubmissions")
    .repeat(1)(
    exec(deleteSubmission)
    )

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

}

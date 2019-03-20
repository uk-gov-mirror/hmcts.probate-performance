package uk.gov.hmcts.reform.probate.simulations

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
import io.gatling.http.check.HttpCheck
import jodd.lagarto.dom.NodeSelector

class ProbateJourney_ExecutorTwo extends Simulation {

  val userFeeder = csv("probate_executors2.csv").queue

  val httpProtocol = http
    .baseURL("https://probate-frontend-sprod.service.core-compute-sprod.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")

  val uri1 = "https://idam-test.dev.ccidam.reform.hmcts.net"

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_4 = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

  val headers_5 = Map(
    "Accept-Encoding" -> "gzip, deflate",
    "Pragma" -> "no-cache")

  val headers_16 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Cache-Control" -> "max-age=0",
    "Connection" -> "keep-alive",
    "Origin" -> "https://idam.preprod.ccidam.reform.hmcts.net",
    "Upgrade-Insecure-Requests" -> "1")

  val ProbHome =

    exec(http("PROBATETwo_010_Home")
      .get("/")
      .check(CsrfCheck.save)
      .check(css(".form-group>input[name='client_id']", "value").saveAs("clientId"))
      .check(css(".form-group>input[name='state']", "value").saveAs("state"))
      .check(css(".form-group>input[name='redirect_uri']", "value").saveAs("redirectUri"))
      .check(css(".form-group>input[name='continue']", "value").saveAs("continue"))
      .check(regex("Email address")))

      .pause(1)

  val ProbLogin =

    feed(userFeeder)

    .exec(http("PROBATETwo_020_Login")
      .post(uri1 + "/login?response_type=code&state=${state}&client_id=probate&redirect_uri=https%3A%2F%2Fprobate-frontend-sprod.service.core-compute-sprod.internal%2Foauth2%2Fcallback")
      .headers(headers_16)
      .formParam("username", "${email}") //pt.probate0151@perftest.uk.gov
      .formParam("password", "${password}") //Pass19word
      .formParam("continue", "${continue}")
      .formParam("state", "${state}")
      .formParam("upliftToken", "")
      .formParam("response_type", "code")
      .formParam("_csrf", "${csrf}")
      .formParam("scope", "")
      .formParam("redirect_uri", "${redirectUri}")
      .formParam("selfRegistrationEnabled","true")
      .formParam("client_id", "probate")
      .check(regex("Apply for probate")))

      .pause(1)

  val ExecutorTwo =

    exec(http("PROBATETwo_030_Tasklist")
      .get("/tasklist")
      .headers(headers_0))

    .pause(1)

    .exec(http("PROBATETwo_040_GetInvitedId")
      .get("/inviteIdList")
      .headers(headers_0)
      .check(regex("\\\"ids\\\":\\[\\\"(.+?)\\\"").saveAs("iD")))

    .pause(1)

    .exec(http("PROBATETwo_050_InvitationLink")
      .get("/executors/invitation/${iD}") //one-test-b23f3e1f-471c-469a-a4b5-05ab5801ea19
      .check(CsrfCheck.save)
      .headers(headers_0))

    .pause(1)

    .exec(http("PROBATETwo_060_EnterMobilePin")
      .get("/pin")
      .headers(headers_0)
      .check(jsonPath("$.pin").saveAs("pin")))

    .pause(1)

    .exec(http("PROBATETwo_070_SignIn")
      .post("/sign-in")
      .headers(headers_0)
      .formParam("_csrf", "${csrf}") //tAlRNbDK-QwOsgffstWlHe9SU8H75oaYR-Ts
      .formParam("pin", "${pin}"))

    .pause(1)

    .exec(http("PROBATETwo_080_DeclarationPage")
      .get("/co-applicant-declaration")
      .check(CsrfCheck.save)
      .headers(headers_0))

    .pause(1)

    .exec(http("PROBATETwo_090_ConfirmDeclaration")
      .post("/co-applicant-declaration")
      .headers(headers_0)
      .formParam("_csrf", "${csrf}") //1NKs6RDC-iHexWw7GUdIBgjli0G-1MBNJ17A
      .formParam("agreement", "Yes, I agree that the information is correct"))

    .pause(1)

    .exec(http("PROBATETwo_140_SignOut")
      .get("/sign-out")
      .headers(headers_0)
      .check(regex("signed out")))

  val scn = scenario("ProbateExecutorTwo")
    .repeat(50)(
      exec(
        ProbHome,
        ProbLogin,
        ExecutorTwo
      )
  )

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
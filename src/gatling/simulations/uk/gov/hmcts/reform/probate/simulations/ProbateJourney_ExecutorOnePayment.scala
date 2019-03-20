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

class ProbateJourney_ExecutorOnePayment extends Simulation {

  val userFeeder = csv("probate_executors3.csv").queue

  val httpProtocol = http
    .baseURL("https://probate-frontend-sprod.service.core-compute-sprod.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")

  val uri1 = "https://idam-test.dev.ccidam.reform.hmcts.net"
  val uri2 = "https://www.payments.service.gov.uk"

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_1 = Map(
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

  val probHome =

    exec(http("PROBATEThree_010_Home")
      .get("/")
      .check(CsrfCheck.save)
      .check(css(".form-group>input[name='client_id']", "value").saveAs("clientId"))
      .check(css(".form-group>input[name='state']", "value").saveAs("state"))
      .check(css(".form-group>input[name='redirect_uri']", "value").saveAs("redirectUri"))
      .check(css(".form-group>input[name='continue']", "value").saveAs("continue"))
      .check(regex("Email address")))

      .pause(1)

  val probUserFeed = feed(userFeeder)

  val probLogin =

    feed(userFeeder)

    .exec(http("PROBATEThree_020_Login")
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

  val probDeclaration =

    exec(http("PROBATEThree_030_DeclarationSummary")
      .get("/summary/declaration")
      //.check(CsrfCheck.save)
      .headers(headers_1)
      .check(regex("Check the information below carefully")))

    .pause(1)

    .exec(http("PROBATEThree_040_Declaration")
      .get("/declaration")
      .check(CsrfCheck.save)
      .headers(headers_1))

    .exec(http("PROBATEThree_050_DeclarationConfirmAndNotify")
      .post("/declaration")
      .headers(headers_1)
      .formParam("_csrf", "${csrf}") //eVpbq0IQ-L0dRAe--WuW-BDXWPnF4YuTCz30
      .formParam("declarationCheckbox", "true")
      .check(regex("Task list - Apply for probate")))

    .pause(1)

    .exec(http("PROBATEThree_060_TasklistPage")
      .get("/tasklist")
      .headers(headers_1)
      .check(regex("Complete these steps to get the legal right to deal with the property and belongings")))

    .pause(1)

  val probCopies =

    exec(http("PROBATEThree_070_CopiesPage")
      .get("/copies-uk")
      .check(CsrfCheck.save)
      .headers(headers_0))

    .pause(1)

    .exec(http("PROBATEThree_080_RequestNoCopies")
      .post("/copies-uk")
      .check(CsrfCheck.save)
      .headers(headers_0)
      .formParam("_csrf", "${csrf}") //A5z8lOKd-McRFFdw2eANPDiF9H2al8ZzDYCA
      .formParam("uk", "0"))

    .pause(1)

    .exec(http("PROBATEThree_090_NoOverseasAssets")
      .post("/assets-overseas")
      .headers(headers_0)
      .formParam("_csrf", "${csrf}") //pSuQi8tQ-rzdZDRarEXqArBkUFsp7VmwqLJc
      .formParam("assetsoverseas", "No"))

    .pause(1)

    /*.exec(http("request_18")
      .get("/tasklist")
      .headers(headers_0))

    .pause(1)*/

  val probPayment =

    exec(http("PROBATEThree_100_PaymentBreakdown")
      .get("/payment-breakdown")
      .check(CsrfCheck.save)
      .headers(headers_1)
      .check(regex("Application fee")))

      .pause(1)

      .exec(http("PROBATEThree_110_ConfirmPayment")
        .post("/payment-breakdown")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //w7Ngjz5W-OyH5db3nnydgOhAlKj6RP8-8qDw
        .check(regex("Before your application can be processed, you need to send your documents by post")))

      .pause(1)

      .exec(http("PROBATEThree_120_ConfirmationPage")
        .post("/payment-status")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //cFMKpcKi-K8sSIGFR9hibg7P_NpD1veVidGs
        .check(regex("I understand that I need to sign the will")))

      .pause(1)

      .exec(http("PROBATEThree_130_ApplicationSubmitted")
        .post("/documents")
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //AiOSAfFT-dxn99IGzxOglfadx3dBsmd07cPc
        .formParam("sentDocuments", "true")
        .check(regex("Application complete"))
        .check(regex("Your reference number is")))

      .pause(1)

      .exec(http("PROBATEThree_140_SignOut")
        .get("/sign-out")
        .headers(headers_1)
        .check(regex("signed out")))

  val scn = scenario("ProbateJourney_ExecutorOnePayment").exec(
    probHome,
    probLogin,
    probDeclaration,
    probCopies,
    probPayment
  )

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

}
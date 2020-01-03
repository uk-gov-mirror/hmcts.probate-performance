package uk.gov.hmcts.reform.probate.performance.simulations

import uk.gov.hmcts.reform.probate.performance.simulations.checks.CsrfCheck.{csrfParameter, csrfTemplate}
import uk.gov.hmcts.reform.probate.performance.simulations.checks.CsrfCheckForPayment.{csrfParameterForPayment, csrfTemplateForPayment}
import uk.gov.hmcts.reform.probate.performance.simulations.checks.CurrentPageUrl.currentPageTemplate
import uk.gov.hmcts.reform.probate.performance.simulations.checks.PaymentSessionToken.{chargeIdParameter, chargeIdTemplate}
import uk.gov.hmcts.reform.probate.performance.simulations.checks.{CsrfCheck, CsrfCheckForPayment, CurrentPageUrl, PaymentSessionToken}
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class probateJourneyNewFebruary extends Simulation{

  val userFeeder = csv("probate_executors.csv").queue

  val httpProtocol = http
    .baseUrl("https://probate-frontend-sprod.service.core-compute-sprod.internal")
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_1 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive")

  val headers_4 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive")

  val headers_5 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive")

  val headers_16 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Cache-Control" -> "max-age=0",
    "Connection" -> "keep-alive",
    "Origin" -> "https://idam.preprod.ccidam.reform.hmcts.net",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_38 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Wed, 27 Sep 2017 11:53:08 GMT",
    "If-None-Match" -> """W/"419-15ec32ef4a0"""")

  val headers_39 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Wed, 27 Sep 2017 11:53:08 GMT",
    "If-None-Match" -> """W/"cdc-15ec32ef4a0"""")

  val headers_40 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Thu, 31 May 2018 13:29:48 GMT",
    "If-None-Match" -> """W/"47f6f-163b663e05c"""")

  val headers_41 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Tue, 03 Oct 2017 09:54:56 GMT",
    "If-None-Match" -> """W/"4a4-15ee1a8e580"""")

  val headers_42 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Tue, 03 Oct 2017 09:54:56 GMT",
    "If-None-Match" -> """W/"211b-15ee1a8e580"""")

  val headers_43 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Tue, 03 Oct 2017 09:54:56 GMT",
    "If-None-Match" -> """W/"41e-15ee1a8e580"""")

  val headers_44 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Tue, 03 Oct 2017 09:54:56 GMT",
    "If-None-Match" -> """W/"1475-15ee1a8e580"""")

  val headers_45 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Thu, 31 May 2018 13:29:48 GMT",
    "If-None-Match" -> """W/"271-163b663e02e"""")

  val headers_46 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Thu, 31 May 2018 13:29:48 GMT",
    "If-None-Match" -> """W/"1a2-163b663e03d"""")

  val headers_48 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Wed, 27 Sep 2017 11:53:08 GMT",
    "If-None-Match" -> """W/"22b4-15ec32ef4a0"""")

  val headers_49 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Wed, 27 Sep 2017 11:53:08 GMT",
    "If-None-Match" -> """W/"1f8-15ec32ef4a0"""")

  val headers_50 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Tue, 03 Oct 2017 09:54:56 GMT",
    "If-None-Match" -> """W/"12d-15ee1a8e580"""")

  val headers_51 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Wed, 27 Sep 2017 11:53:08 GMT",
    "If-None-Match" -> """W/"587-15ec32ef4a0"""")

  val headers_69 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Cache-Control" -> "max-age=0",
    "Connection" -> "keep-alive",
    "Origin" -> "https://probate-frontend-saat.service.core-compute-saat.internal",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_87 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Wed, 27 Sep 2017 11:53:08 GMT",
    "If-None-Match" -> """W/"4ffd-15ec32ef4a0"""")

  val headers_88 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Wed, 27 Sep 2017 11:53:08 GMT",
    "If-None-Match" -> """W/"42b96-15ec32ef4a0"""")

  val headers_89 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Thu, 31 May 2018 13:29:55 GMT",
    "If-None-Match" -> """W/"bb9f-163b663fa55"""")

  val headers_99 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "If-Modified-Since" -> "Wed, 27 Sep 2017 11:53:08 GMT",
    "If-None-Match" -> """W/"64f-15ec32ef4a0"""")

  val headers_1033 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Connection" -> "keep-alive",
    "Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8",
    "Origin" -> "https://www.payments.service.gov.uk",
    "X-Requested-With" -> "XMLHttpRequest")

  val headers_1035 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en,en-GB;q=0.9",
    "Cache-Control" -> "max-age=0",
    "Connection" -> "keep-alive",
    "Origin" -> "https://www.payments.service.gov.uk",
    "Upgrade-Insecure-Requests" -> "1")

  val uri1 = "https://idam-test.dev.ccidam.reform.hmcts.net"
  val uri2 = "https://www.payments.service.gov.uk"

  val probHome = exec(http("PROBATE_010_HOME")
    .get("/")
    // .check(CurrentPageCheck.save)
    .check(CsrfCheck.save)
    .check(css(".form-group>input[name='client_id']", "value").saveAs("clientId"))
    .check(css(".form-group>input[name='state']", "value").saveAs("state"))
    .check(css(".form-group>input[name='redirect_uri']", "value").saveAs("redirectUri"))
    .check(css(".form-group>input[name='continue']", "value").saveAs("continue"))
    .check(regex("Email address")))

  val probUserFeed = feed(userFeeder)

  val probLogin =

    pause(20)

      .exec(http("PROBATE_020_LOGIN")
        .post(uri1 + "/login?response_type=code&state=${state}&client_id=probate&redirect_uri=https%3A%2F%2Fprobate-frontend-saat.service.core-compute-saat.internal%2Foauth2%2Fcallback")
        .headers(headers_16)
        .formParam("username", "${email}")
        .formParam("password", "${password}")
        .formParam("continue", "${continue}")
        .formParam("state", "${state}")
        .formParam("upliftToken", "")
        .formParam("response_type", "code")
        .formParam("_csrf", "${csrf}")
        .formParam("scope", "")
        .formParam("redirect_uri", "${redirectUri}")
        .formParam("selfRegistrationEnabled","true")
        .formParam("client_id", "probate")
        //    .check(regex("Apply for Probate"))
      )


      //Updating step order here 18/02/2019

      .exec(http("PROBATE_300_DECEASED_01")
        .post("/deceased-name")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("firstName", "Abangonda")
        .formParam("lastName", "Head")
        .check(status.is(200)))

      .exec(http("PROBATE_340_DECEASED_05")
        .post("/deceased-dob")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("dob_day", "06")
        .formParam("dob_month", "06")
        .formParam("dob_year", "1960")
        .check(status.is(200)))

      .exec(http("PROBATE_330_DECEASED_04")
        .post("/deceased-dod")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("dod_day", "05")
        .formParam("dod_month", "05")
        .formParam("dod_year", "2018")
        .check(status.is(200)))

      .exec(http("PROBATE_370_DECEASED_08")
        .post("/deceased-address")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("postcode", "KT2 5BU")
        .formParam("referrer", "DeceasedAddress")
        .formParam("addressFound", "true")
        .formParam("postcodeAddress", "Flat 11 Bramber House Royal Quarter Seven Kings Way Kingston Upon Thames KT2 5BU")
        .formParam("freeTextAddress", "")
        .check(status.is(200)))

      .exec(http("PROBATE_120_IHT_02")
        .post("/iht-method")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("method", "By post")
        .check(status.is(200)))

      .exec(http("PROBATE_130_IHT_03")
        .post("/iht-paper")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("form", "205")
        .formParam("gross205", "4999")
        .formParam("net205", "4900")
        .formParam("gross207", "")
        .formParam("net207", "")
        .formParam("gross400", "")
        .formParam("net400", "")
        .check(status.is(200)))

      .exec(http("PROBATE_310_DECEASED_02")
        .post("/deceased-alias")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("alias", "No")
        .check(status.is(200)))

      .exec(http("PROBATE_320_DECEASED_03")
        .post("/deceased-married")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("married", "No")
        .check(status.is(200)))

      .exec(http("PROBATE_080_WILL_05")
        .post("/will-codicils")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("codicils", "Yes")
        .check(status.is(200)))

      .exec(http("PROBATE_090_WILL_06")
        .post("/codicils-number")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("codicilsNumber", "1")
        .check(status.is(200)))

      .exec(http("PROBATE_TASKLIST")
        .get("/tasklist"))

      .exec(http("PROBATE_150_APPLICANT_02")
        .get("/applicant-name")
        .headers(headers_0)
        .check(status.is(200)))

      .exec(http("PROBATE_160_APPLICANT_03")
        .post("/applicant-name")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("firstName", "Peter")
        .formParam("lastName", "Posh")
        .check(status.is(200)))

      .exec(http("PROBATE_170_APPLICANT_04")
        .post("/applicant-name-as-on-will")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("nameAsOnTheWill", "Yes")
        .check(status.is(200)))

      .exec(http("PROBATE_180_APPLICANT_05")
        .post("/applicant-phone")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("phoneNumber", "07000000000")
        .check(status.is(200)))

      .exec(http("PROBATE_200_APPLICANT_07")
        .post("/applicant-address")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("postcode", "KT2 5BU")
        .formParam("referrer", "ApplicantAddress")
        .formParam("addressFound", "true")
        .formParam("postcodeAddress", "Flat 1 Bramber House Royal Quarter Seven Kings Way Kingston Upon Thames KT2 5BU")
        .formParam("freeTextAddress", "")
        .check(status.is(200)))

      .exec(http("PROBATE_210_EXECUTORS_01")
        .post("/executors-number")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("executorsNumber", "2")
        .check(status.is(200)))

      .exec(http("PROBATE_220_EXECUTORS_02")
        .post("/executors-names")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("executorName[0]", "Paul Poor")
        .check(status.is(200)))

      .exec(http("PROBATE_230_EXECUTORS_03")
        .post("/executors-all-alive")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("allalive", "Yes")
        .check(status.is(200)))

      .exec(http("PROBATE_240_EXECUTORS_04")
        .post("/other-executors-applying")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("otherExecutorsApplying", "Yes")
        .check(status.is(200)))

      .exec(http("PROBATE_250_EXECUTORS_05")
        .post("/executors-dealing-with-estate")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("executorsApplying[]", "Paul Poor")
        .check(status.is(200)))

      .exec(http("PROBATE_260_EXECUTORS_06")
        .post("/executors-alias")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("alias", "No")
        .check(status.is(200)))

      .exec(http("PROBATE_270_EXECUTORS_07")
        .post("/executor-contact-details/*")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("email", "perftest-jm@outlook.com")
        .formParam("mobile", "07000000000")
        .check(status.is(200)))

      .exec(http("PROBATE_290_EXECUTORS_02")
        .post("/executor-address/1")
        .headers(headers_69)
        .formParam("_csrf", "")
        .formParam("postcode", "KT2 5BU")
        .formParam("referrer", "ExecutorAddress")
        .formParam("addressFound", "true")
        .formParam("postcodeAddress", "Flat 10 Bramber House Royal Quarter Seven Kings Way Kingston Upon Thames KT2 5BU")
        .formParam("freeTextAddress", "")
        .check(status.is(200)))

      .exec(http("PROBATE_TASKLIST")
        .get("/tasklist"))

      .exec(http("PROBATE_390_DECLARATION_01")
        .get("/summary/declaration")
        .headers(headers_0)
        .check(status.is(200)))

      .exec(http("PROBATE_390_DECLARATION_01")
        .get("/summary/declaration")
        .headers(headers_0)
        .check(status.is(200)))

      .exec(http("PROBATE_430_INVITE_01")
        .post("/executors-invite")
        .headers(headers_69)
        .formParam("_csrf", "")
        .check(status.is(200)))

      .exec(http("PROBATE_430_INVITE_02")
        .post("/executors-invites-sent")
        .headers(headers_69)
        .formParam("_csrf", "")
        .check(status.is(200)))

      .exec(http("PROBATE_TASKLIST")
        .get("/tasklist"))


  /////////////////////////


  //	end second part of the probate process - 2nd executor

  //	CLEAR CACHE TO LOG IN AGAIN

  val probClearCache = exec(flushHttpCache)

  val probAPIinvited =

  //  THIS IS AN ADDITIONAL API NOT USED IN THE REAL USER JOURNEY
  //  THE API is used to capture the ID that is sent to the 2nd Exec in email

    exec(http("PROBATE_450_API_INVITEIDLIST")
      .get("/inviteIdList")
      .headers(headers_0)
      //	    .check(jsonPath("$.ids").saveAs("ids"))
      .check(regex("\\\"ids\\\":\\[\\\"(.+?)\\\"").saveAs("ids"))
      .check(status.is(200)))

  //	end first part of the probate process

  //  START SECOND PART OF THE PROBATE PROCESS - EXEC 2

  val probAPIExecTwoPin = pause(3)

    // CLEAR CACHE - SIMULATING ANOTHER USER

    .exec(flushHttpCache)

    //  THIS IS AN ADDITIONAL API NOT USED IN THE REAL USER JOURNEY
    //  THE API is used to capture the PIN that is sent to the 2nd Exec in a text message
    //  THIS USES THE 'ids' PARAM CAPTURED AT TEH END OF THE FIRST EXEC PART ONE

    .exec(http("PROBATE_470_GENERATE_PIN")
    .get("/executors/invitation/${ids}")
    .headers(headers_0)
    .check(status.is(200)))

    .pause(3)

    .exec(http("PROBATE_480_API_PIN")
      .get("/pin")
      .headers(headers_0)
      .check(jsonPath("$.pin").saveAs("pin"))
      .check(status.is(200)))

    /*.exec(http("PROBATE_490_SIGN_IN_01")
      .get("/sign-in")
      .headers(headers_0)
      .check(status.is(304)))

    .exec(http("PROBATE_500_SIGN_IN_02")
      .post("/sign-in")
      .headers(headers_69)
      .formParam("_csrf", "")
      .formParam("pin", "${pin}")
      .check(status.is(302)))*/

    .exec(http("PROBATE_510_APPLICATION_DECLARATION_01")
      .post("/co-applicant-start-page")
      .headers(headers_0)
      .check(status.is(200)))

    .exec(http("PROBATE_510_APPLICATION_DECLARATION_01")
      .get("/co-applicant-declaration")
      .headers(headers_0)
      .check(status.is(200)))

    .exec(http("PROBATE_510_APPLICATION_DECLARATION_01")
      .get("/co-applicant-agree-page")
      .headers(headers_0)
      .check(status.is(200)))

    .exec(flushHttpCache)

    .exec(http("PROBATE_TASKLIST")
      .get("/tasklist"))

    .exec(http("PROBATE_390_DECLARATION_01")
      .get("/summary/declaration")
      .headers(headers_0)
      .check(status.is(200)))

    .exec(http("PROBATE_390_DECLARATION_01")
      .get("/summary/declaration")
      .headers(headers_0)
      .check(status.is(200)))

    .exec(http("PROBATE_590_COPIES_01")
      .get("/copies-uk")
      .headers(headers_0)
      .check(status.is(200)))

    .exec(http("PROBATE_600_COPIES_02")
      .post("/copies-uk")
      .headers(headers_69)
      .formParam("_csrf", "")
      .formParam("uk", "0")
      .check(status.is(200)))

    .exec(http("PROBATE_610_ASSETS_OVERSEAS")
      .post("/assets-overseas")
      .headers(headers_69)
      .formParam("_csrf", "")
      .formParam("assetsoverseas", "No")
      .check(status.is(200)))

    .exec(http("PROBATE_600_COPIES_02")
      .post("/copies-summary")
      .headers(headers_0)
      .check(status.is(200)))

    .exec(http("PROBATE_TASKLIST")
      .get("/tasklist"))

    .exec(http("request_818")
      .get("/payment-breakdown")
      .headers(headers_0))





  val scn = scenario("probateJourneyNewFebruary").exec(
    probUserFeed,
    probHome,
    probLogin,
    probAPIinvited,
    probClearCache,
    probAPIExecTwoPin,
   /* probExecTwo,
    probSignOut,
    probClearCache,
    probHome,
    probLogin,
    probDeclaration,
    probCopies,
    probPayments,
    probSignOut*/)

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

  //  setUp(scn.inject(rampUsers(100).over(2700 seconds))).protocols(httpProtocol).maxDuration(70 minutes)

}

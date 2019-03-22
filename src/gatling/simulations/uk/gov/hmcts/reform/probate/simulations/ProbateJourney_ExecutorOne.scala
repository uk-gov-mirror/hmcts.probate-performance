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

class ProbateJourney_ExecutorOne extends Simulation {

  val userFeeder = csv("probate_executors2.csv").queue

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

  val ProbateEligibility =

    exec(http("PROBATEEligibility_010_StartEligibility")
      .get("/start-eligibility")
      .check(status.is(200)))

      .pause(1)

      .exec(http("PROBATEEligibility_020_DeathCertificate")
        .get("/death-certificate")
        .check(regex("csrfToken: \"(.*)\"").find.saveAs("csrf")))

      .pause(1)

      .exec(http("PROBATEEligibility_030_ConfirmDeathCertificate")
        .post("/death-certificate")
        .headers(headers_0)
        .formParam("_csrf", "${csrf}")
        .formParam("deathCertificate", "Yes"))

      .pause(1)

      .exec(http("PROBATEEligibility_040_DeceasedDomicile")
        .post("/deceased-domicile")
        .headers(headers_0)
        .formParam("_csrf", "${csrf}")
        .formParam("domicile", "Yes"))

      .pause(1)

      .exec(http("PROBATEEligibility_050_IHTCompleted")
        .post("/iht-completed")
        .headers(headers_0)
        .formParam("_csrf", "${csrf}")
        .formParam("completed", "Yes"))

      .pause(1)

      .exec(http("PROBATEEligibility_060_WillLeft")
        .post("/will-left")
        .headers(headers_0)
        .formParam("_csrf", "${csrf}")
        .formParam("left", "Yes"))

      .pause(1)

      .exec(http("PROBATEEligibility_070_OriginalWill")
        .post("/will-original")
        .headers(headers_0)
        .formParam("_csrf", "${csrf}")
        .formParam("original", "Yes"))

      .pause(1)

      .exec(http("PROBATEEligibility_080_ApplicantIsExecutor")
        .post("/applicant-executor")
        .headers(headers_0)
        .formParam("_csrf", "${csrf}")
        .formParam("executor", "Yes"))

      .pause(1)

      .exec(http("PROBATEEligibility_090_MentalCapacity")
        .post("/mental-capacity")
        .headers(headers_0)
        .formParam("_csrf", "${csrf}")
        .formParam("mentalCapacity", "Yes"))

      .pause(1)

      .exec(http("PROBATEEligibility_100_Tasklist")
        .get("/tasklist")
        .headers(headers_0))

      .pause(1)

  val probHome =

    exec(http("PROBATEOne_010_Home")
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

      .exec(http("PROBATEOne_020_Login")
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
        .formParam("selfRegistrationEnabled", "true")
        .formParam("client_id", "probate")
        .check(regex("Apply for probate"))
      )

      .pause(1)

  val probExecutorOneJourney =

    exec(http("PROBATEOne_030_DeceasedName")
      .get("/deceased-name")
      .check(CsrfCheck.save)
      .check(regex("First name and any middle names"))
      .headers(headers_1))

      .pause(1)

      .exec(http("PROBATEOne_040_ConfirmName")
        .post("/deceased-name")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //8fYkUhcv-w0VGzFINcVoMpLGjG_NjEn_4H6g
        .formParam("firstName", "One")
        .formParam("lastName", "Test")
        .check(regex("Please enter date of birth")))

      .pause(1)

      .exec(http("PROBATEOne_050_DeceasedDOB")
        .post("/deceased-dob")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //fIP4TCsd-Bid3KTknu63Onnbqey8FCkqTB40
        .formParam("dob_day", "01")
        .formParam("dob_month", "02")
        .formParam("dob_year", "1970")
        .check(regex("What was the date that they died?")))

      .pause(1)

      .exec(http("PROBATEOne_060_DeceasedDOD")
        .post("/deceased-dod")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //NO983ka5-DRcrvO_YW9XA8pz9HZKzqyMGc68
        .formParam("dod_day", "05")
        .formParam("dod_month", "06")
        .formParam("dod_year", "2018")
        .check(regex("What was the permanent address at the time of their death?")))

      .pause(1)

      .exec(http("PROBATEOne_070_FindAddress")
        .post("/find-address")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //zqGgY5bT-8MtajVPawbU04Qj22de28Lw9iow
        .formParam("postcode", "KT25BU")
        .formParam("referrer", "DeceasedAddress")
        .formParam("findaddress", "Find UK address")
        .formParam("addressFound", "none")
        .formParam("freeTextAddress", "")
        .check(regex("Find UK address")))

      .pause(1)

      .exec(http("PROBATEOne_080_ConfirmAddress")
        .post("/deceased-address")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //IagigGcN-IRkKoP-O7h9_JMVKJHRuxscrUCg
        .formParam("postcode", "KT25BU")
        .formParam("referrer", "DeceasedAddress")
        .formParam("addressFound", "true")
        .formParam("postcodeAddress", "Flat 1 Bramber House Royal Quarter Seven Kings Way Kingston Upon Thames KT2 5BU")
        .formParam("freeTextAddress", "")
        .check(regex("How was the Inheritance Tax")))

      .pause(1)

      .exec(http("PROBATEOne_090_IHTMethod")
        .post("/iht-method")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //gQ5J2aSA-g_ClevqfiU6JB7GvzC5qhoL91Hk
        .formParam("method", "By post")
        .check(regex("IHT 205 - there was no inheritance tax to pay")))

      .pause(1)

      .exec(http("PROBATEOne_100_ConfirmIHT")
        .post("/iht-paper")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //AYUXU1rZ-4Ql2K2tkVe_0ObkerOlEeeGV4L4
        .formParam("form", "IHT205")
        .formParam("grossIHT205", "4999")
        .formParam("netIHT205", "4999")
        .formParam("grossIHT207", "")
        .formParam("netIHT207", "")
        .formParam("grossIHT400421", "")
        .formParam("netIHT400421", "")
        .check(regex("have assets in another name")))

      .pause(1)

      .exec(http("PROBATEOne_110_DeceasedAlias")
        .post("/deceased-alias")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //DiGYttQZ--zFiq2o76Z_vbPoDaK3t_74vZlw
        .formParam("alias", "No")
        .check(regex("get married or enter into a civil partnership")))

      .pause(1)

      .exec(http("PROBATEOne_120_DeceasedMarried")
        .post("/deceased-married")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //t1FbGisu-qIYLEnB0wEPYMtSRI784HFXouSA
        .formParam("married", "No")
        .check(regex("made to the will")))

      .pause(1)

      .exec(http("PROBATEOne_130_WillCodicils")
        .post("/will-codicils")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //limrP2mM-vhZLWi9GCU2oxyUdgfcuBxLnP6Q
        .formParam("codicils", "Yes")
        .check(regex("How many updates")))

      .pause(1)

      .exec(http("PROBATEOne_140_NumberOfCodicils")
        .post("/codicils-number")
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //Gd8Rxlyf-qnBeZAwEQDDQRAlsYhOmrtOVkIY
        .formParam("codicilsNumber", "1")
        .check(regex("About the person who died")))

      .pause(1)

      .exec(http("PROBATEOne_150_ApplicantName")
        .get("/applicant-name")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .check(regex("What is your full name")))

      .pause(1)

      .exec(http("PROBATEOne_160_ConfirmApplicantName")
        .post("/applicant-name")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //VLcvnDoo--Pw2zBA_YXJOjt9q049Y1dHd1YY
        .formParam("firstName", "Peter")
        .formParam("lastName", "Test")
        .check(regex("exactly what appears on the will or codicil")))

      .pause(1)

      .exec(http("PROBATEOne_170_NameOnWill")
        .post("/applicant-name-as-on-will")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //rt257kT3-5xSgFPo8sHaHRW4fxw0Im3s9jKM
        .formParam("nameAsOnTheWill", "Yes")
        .check(regex("What is your phone number")))

      .pause(1)

      .exec(http("PROBATEOne_180_ApplicantPhone")
        .post("/applicant-phone")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //Oni6qGih-Ndkqtjr3R8pweGazKZdK0iftuw8
        .formParam("phoneNumber", "07000111222")
        .check(regex("What is your address")))

      .pause(1)

      .exec(http("PROBATEOne_190_ApplicantAddress")
        .post("/find-address")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //IGamgxwS-ApGUgJ2Fl3i_HdHILtDcWhuTBgA
        .formParam("postcode", "KT25BU")
        .formParam("referrer", "ApplicantAddress")
        .formParam("findaddress", "Find UK address")
        .formParam("addressFound", "none")
        .formParam("freeTextAddress", "")
        .check(regex("Select address")))

      .pause(1)

      .exec(http("PROBATEOne_200_ConfirmApplicantAddress")
        .post("/applicant-address")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //Gd9NTRh1-XrNub9m7RDGRTmXrLOPi5h080bw
        .formParam("postcode", "KT25BU")
        .formParam("referrer", "ApplicantAddress")
        .formParam("addressFound", "true")
        .formParam("postcodeAddress", "Flat 11 Bramber House Royal Quarter Seven Kings Way Kingston Upon Thames KT2 5BU")
        .formParam("freeTextAddress", "")
        .check(regex("How many past and present executors")))

      .pause(1)

      .exec(http("PROBATEOne_210_NumberOfExecutors")
        .post("/executors-number")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //EvUmEM1w-_0IhhXS856J01z8thYAlPxcTV2A
        .formParam("executorsNumber", "2")
        .check(regex("What are the executors")))

      .pause(1)

      .exec(http("PROBATEOne_220_OtherExecutorName")
        .post("/executors-names")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //Evznbvxm-KY4-VsG4yjweOdCLYG9FaXgOYtM
        .formParam("executorName[0]", "Steve Test")
        .check(regex("Are all the executors alive")))

      .pause(1)

      .exec(http("PROBATEOne_230_OtherExecutorAlive")
        .post("/executors-all-alive")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //8UAMZSj2-nS5gPjIyMJkeBYoP6EZFdkFf__k
        .formParam("allalive", "Yes")
        .check(regex("Will any of the other executors be dealing with the estate")))

      .pause(1)

      .exec(http("PROBATEOne_240_OtherExecutorApplying")
        .post("/other-executors-applying")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //uOwI6UTZ-4Mr2BeWZnu8Zqz5CSYKAUk4eLLc
        .formParam("otherExecutorsApplying", "Yes")
        .check(regex("Which executors will be dealing with the estate")))

      .pause(1)

      .exec(http("PROBATEOne_250_ExecutorsDealingWithEstate")
        .post("/executors-dealing-with-estate")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //ydoSqxIg-6eI2gv_oSrKGj7a_ao1VhXM-VjI
        .formParam("executorsApplying[]", "Steve Test")
        .check(regex("Do any of these executors now have a different name to that on the will")))

      .pause(1)

      .exec(http("PROBATEOne_260_OtherExecutorAlias")
        .post("/executors-alias")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //fCBW0hSV-gCUbZVLfhlIRmG_kIjIB3kCmWrc
        .formParam("alias", "No")
        .check(regex("email address and mobile number")))

      .pause(1)

      .exec(http("PROBATEOne_270_OtherExecutorContactDetails")
        .post("/executor-contact-details/*")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //T1ltPndT-KEqQj1CUw_8DYHjLgrIiLaLd1IU
        .formParam("email", "spam@tes8ungftujnfft.com")
        .formParam("mobile", "07000333444")
        .check(regex("permanent address")))

      .pause(1)

      .exec(http("PROBATEOne_280_OtherExecutorAddress")
        .post("/find-address")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //xoy8OzEk-kq4hHKRGAKhhzgJAtEHvYF9QxGc
        .formParam("postcode", "KT25BU")
        .formParam("referrer", "ExecutorAddress")
        .formParam("findaddress", "Find UK address")
        .formParam("addressFound", "none")
        .formParam("freeTextAddress", "")
        .check(regex("Select address")))

      .pause(1)

      .exec(http("PROBATEOne_290_OtherExecutorAddressConfirm")
        .post("/executor-address/*")
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //tckMarjv-u5vWfIctyrHQwGB6W2DRa52_ZBc
        .formParam("postcode", "KT25BU")
        .formParam("referrer", "ExecutorAddress")
        .formParam("addressFound", "true")
        .formParam("postcodeAddress", "Flat 12 Bramber House Royal Quarter Seven Kings Way Kingston Upon Thames KT2 5BU")
        .formParam("freeTextAddress", "")
        .check(regex("Complete these steps to get the legal right to deal with the property and belongings of someone who has died")))

      .pause(1)

      .exec(http("PROBATEOne_300_DeclarationSummary")
        .get("/summary/declaration")
        .headers(headers_1)
        .check(regex("Check the information below carefully")))

      .pause(1)

      .exec(http("PROBATEOne_310_DeclarationCheck")
        .get("/declaration")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .check(regex("Check the legal statement and make your declaration")))

      .pause(1)

      .exec(http("PROBATEOne_320_DeclarationConfirmAndNotify")
        .post("/declaration")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //eVpbq0IQ-L0dRAe--WuW-BDXWPnF4YuTCz30
        .formParam("declarationCheckbox", "true")
        .check(regex("Notify the other executors who are applying for probate")))

      .pause(1)

      .exec(http("PROBATEOne_330_NotifyOtherExecutors")
        .post("/executors-invite")
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //TRSeI3Xc-RKOSRomKvp6PkQszOnjKm19k-Z8
        .check(regex("notified the other executors who are applying for probate")))

      .pause(1)

      .exec(http("PROBATEOne_340_TasklistPage")
        .get("/tasklist")
        .headers(headers_1)
        .check(regex("Complete these steps to get the legal right to deal with the property and belongings")))

      .pause(1)

      .exec(http("PROBATEOne_140_SignOut")
        .get("/sign-out")
        .headers(headers_1)
        .check(regex("signed out")))

  val scn = scenario("ProbateJourney_ExecutorOne")
    .repeat(25)(
      exec(
        //ProbateEligibility,
        probHome,
        probLogin,
        probExecutorOneJourney
      )
    )

  setUp(scn.inject(atOnceUsers(2))).protocols(httpProtocol)
}
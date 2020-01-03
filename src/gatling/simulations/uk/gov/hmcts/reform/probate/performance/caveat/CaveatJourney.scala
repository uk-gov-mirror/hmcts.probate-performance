package uk.gov.hmcts.reform.probate.performance.caveat

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.simulations.checks.CsrfCheck
//import uk.gov.hmcts.reform.idam.User

import uk.gov.hmcts.reform.cmc.performance.utils._

object CaveatJourney {
  val paymentURL = Environment.PaymentURL

  val thinktime = Environment.thinkTime
 val dataFeeder= csv("caveat_details.csv").circular
 val headers_544 = Map(
  "Accept" -> "*/*",
  "Accept-Encoding" -> "gzip, deflate, br",
  "Accept-Language" -> "en-US,en;q=0.9",
  "Content-type" -> "application/json",
  "Origin" -> "https://www.payments.service.gov.uk",
  "Sec-Fetch-Mode" -> "cors",
  "Sec-Fetch-Site" -> "same-origin",
  "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")
  



   
  //def logIn(user: User)(implicit postHeaders: Map[String, String]): ChainBuilder = {
 val homePage =feed(dataFeeder).
  exec(http("TX01_PA_Caveat_StartApply")
    .get("/caveats/start-apply"))
    //.headers(headers_0))
      .pause(thinktime)


  val applicantName_get =
  exec(http("TX02_PA_Caveat_GetName")
    .get("/caveats/applicant-name")
    .check(CsrfCheck.save))
  //  .headers(headers_37))
    .pause(thinktime)



  val applicantName_Post =
  exec(http("TX03_PA_Caveat_PostName")
    .post("/caveats/applicant-name")
  //  .headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("firstName", "${firstname}")
    .formParam("lastName", "${lastname}")
    .check(CsrfCheck.save))
      .pause(thinktime)
  val emailAddress =
  exec(http("TX04_PA_Caveat_Email")
    .post("/caveats/applicant-email")
   // .headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("email", "${email}"+"@mailinator.com")
    .check(CsrfCheck.save))
      .pause(thinktime)
  val addressLookup =
  exec(http("TX05_PA_Caveat_AddressLookup")
    .post("/caveats/find-address")
   // .headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("postcode", "TW3 3SD")
    .formParam("referrer", "ApplicantAddress")
    .formParam("addressFound", "none")
    .check(CsrfCheck.save))
      .pause(thinktime)
  val address =
  exec(http("TX06_PA_Caveat_Address")
    .post("/caveats/applicant-address")
    //.headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("addressLine1", "26 Hibernia Gardens")
    .formParam("addressLine2", "")
    .formParam("addressLine3", "")
    .formParam("postTown", "Hounslow")
    .formParam("newPostCode", "TW3 3SD")
    .formParam("country", "United Kingdom")
    .check(CsrfCheck.save))
    .pause(thinktime)

  val deceasedName =
  exec(http("TX07_PA_Caveat_DeceasedName")
    .post("/caveats/deceased-name")
    //.headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("firstName", "${deceasedfname}")
    .formParam("lastName", "${deceasedlastname}")
    .check(CsrfCheck.save))
      .pause(thinktime)
  val deceasedDOD =
  exec(http("TX08_PA_Caveat_DOD")
    .post("/caveats/deceased-dod")
   // .headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("dod-day", "01")
    .formParam("dod-month", "01")
    .formParam("dod-year", "2017")
    .check(CsrfCheck.save))
      .pause(thinktime)
  val deceasedDOBKnown =

  exec(http("TX09_PA_Caveat_DeceasedDOBKnown")
    .post("/caveats/deceased-dob-known")
   // .headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("dobknown", "Yes")
    .check(CsrfCheck.save)
  )
      .pause(thinktime)
  val deceasedDOB =
  exec(http("TX10_PA_Caveat_DeceasedDOB")
    .post("/caveats/deceased-dob")
  //  .headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("dob-day", "01")
    .formParam("dob-month", "01")
    .formParam("dob-year", "1947")
    .check(CsrfCheck.save)
  )
      .pause(thinktime)
  val deceasedAlias =
  exec(http("TX11_PA_Caveat_DeceasedAlias")
    .post("/caveats/deceased-alias")
    //.headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("alias", "No")
    .check(CsrfCheck.save)
  )
      .pause(thinktime)
  val caveatFindAddress =
  exec(http("TX12_PA_Caveat_DeceasedFAddress")
    .post("/caveats/find-address")
    //.headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("postcode", "TW3 3SD")
    .formParam("referrer", "DeceasedAddress")
    .formParam("addressFound", "none")
    .check(CsrfCheck.save)
  )
      .pause(thinktime)
  val caveatAddress =
  exec(http("TX13_PA_Caveat_DeceasedAddress")
    .post("/caveats/deceased-address")
   // .headers(headers_75)
    .formParam("_csrf", "${csrf}")
    .formParam("addressLine1", "18 Hibernia Gardens")
    .formParam("addressLine2", "")
    .formParam("addressLine3", "")
    .formParam("postTown", "Hounslow")
    .formParam("newPostCode", "TW3 3SD")
    .formParam("country", "United Kingdom"))
      .pause(thinktime)
  val paymentBreakdown_get =
  exec(http("TX14_PA_Caveat_paymentBreakdown")
    .get("/caveats/payment-breakdown")
   // .headers(headers_37)
    //.check(CsrfCheck.save)
    .check(css("input[name='_csrf']", "value").saveAs("_csrfCardDetailPage"))
  )
      .pause(thinktime)
  val paymentBreakdown_post =
  exec(http("TX15_PA_Caveat_paymentBreakdownPost")
    .post("/caveats/payment-breakdown")
    //.headers(headers_75)
    .formParam("_csrf", "${_csrfCardDetailPage}")
    .check(css("input[name='csrfToken']", "value").saveAs("_csrfTokenCardDetailPage"))
    .check(css("input[name='chargeId']", "value").saveAs("_csrfCardDetailPageChargeId"))
  )
      .pause(thinktime)
  val checkCardDetails =
  exec(http("TX16_PA_Caveat_paymentcheckCardDetails")
    .post(paymentURL + "/check_card/${_csrfCardDetailPageChargeId}")
    .headers(headers_544)
    .body(RawFileBody("RecordedSimulationProbate_0544_request.txt"))
   // .check(css("input[name='csrfToken']", "value").saveAs("_csrfTokenCardDetailPage"))
  )
    .pause(37)
    .exec(http("TX17_PA_Caveat_payment_Details")
      .post(paymentURL + "/card_details/${_csrfCardDetailPageChargeId}")
     // .headers(headers_545)
      .formParam("chargeId", "${_csrfCardDetailPageChargeId}")
      .formParam("csrfToken", "${_csrfTokenCardDetailPage}")
      .formParam("cardNo", "4444333322221111")
      .formParam("expiryMonth", "08")
      .formParam("expiryYear", "20")
      .formParam("cardholderName", "vfvfvfvff")
      .formParam("cvc", "123")
      .formParam("addressCountry", "GB")
      .formParam("addressLine1", "4")
      .formParam("addressLine2", "Hibernia Gardens")
      .formParam("addressCity", "Hounslow")
      .formParam("addressPostcode", "TW3 3SD")
      .formParam("email", "${paymentemail}"+"@mailinator.com")
      .check(css("input[name='csrfToken']", "value").saveAs("_csrfTokenCardDetailConfirm"))
  )
      .pause(thinktime)

  val cardConfirmation=
  exec(http("TX16_PA_Caveat_paymentBreakdown_PaymentConfirmation")
    .post(paymentURL + "/card_details/${_csrfCardDetailPageChargeId}/confirm")
    //.headers(headers_545)
    .formParam("csrfToken", "${_csrfTokenCardDetailConfirm}")
    .formParam("chargeId", "${_csrfCardDetailPageChargeId}")
    .check(regex("Application complete")))

    .pause(thinktime)






}

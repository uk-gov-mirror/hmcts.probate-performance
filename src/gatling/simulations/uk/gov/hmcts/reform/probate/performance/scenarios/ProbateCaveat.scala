package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object ProbateCaveat {

  val BaseURL = Environment.baseURL
  val PaymentURL = Environment.paymentURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val ProbateCaveat = group("Probate_Caveat") {

    exec(http("ProbateCaveat_010_005_Homepage")
      .get(BaseURL + "/caveats/start-apply")
      .headers(CommonHeader)
      .header("sec-fetch-site", "none")
      .header("accept-language", "en-GB,en;q=0.9")
      .check(regex("Stop an application for probate")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_010_ApplicantName")
      .get(BaseURL + "/caveats/applicant-name")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("What is your full name")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_015_ApplicantNameSubmit")
      .post(BaseURL + "/caveats/applicant-name")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("firstName", "Perf")
      .formParam("lastName", "Test")
      .check(CsrfCheck.save)
      .check(regex("What is your email address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_020_EmailAddressSubmit")
      .post(BaseURL + "/caveats/applicant-email")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("email", "perftest123@perftest12345.com")
      .check(CsrfCheck.save)
      .check(regex("What is your address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_025_AddressSubmit")
      .post(BaseURL + "/caveats/applicant-address")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("addressLine1", "1 Perf Test Road")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "")
      .formParam("postTown", "Perf Test Town")
      .formParam("newPostCode", "PR1 1RF")
      .formParam("country", "")
      .check(CsrfCheck.save)
      .check(regex("full name of the person")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_030_DeceasedNameSubmit")
      .post(BaseURL + "/caveats/deceased-name")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("firstName", "Perf")
      .formParam("lastName", "Tester")
      .check(CsrfCheck.save)
      .check(regex("What was the date that they died")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_035_DeceasedDodSubmit")
      .post(BaseURL + "/caveats/deceased-dod")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("dod-day", "12")
      .formParam("dod-month", "12")
      .formParam("dod-year", "2012")
      .check(CsrfCheck.save)
      .check(regex("date of birth")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_040_DeceasedDobKnownSubmit")
      .post(BaseURL + "/caveats/deceased-dob-known")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("dobknown", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("What was their date of birth")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_045_DeceasedDobSubmit")
      .post(BaseURL + "/caveats/deceased-dob")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("dob-day", "12")
      .formParam("dob-month", "12")
      .formParam("dob-year", "1912")
      .check(CsrfCheck.save)
      .check(regex("known by any other names")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_050_DeceasedAliasSubmit")
      .post(BaseURL + "/caveats/deceased-alias")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("alias", "optionNo")
      .check(CsrfCheck.save)
      .check(regex("What was the permanent address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_055_DeceasedAddressSubmit")
      .post(BaseURL + "/caveats/deceased-address")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("addressLine1", "1 Perf Test Road")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "")
      .formParam("postTown", "Perf Test Town")
      .formParam("newPostCode", "PR1 1RF")
      .formParam("country", "")
      .check(CsrfCheck.save)
      .check(regex("English and Welsh")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_060_BilingualSubmit")
      .post(BaseURL + "/caveats/bilingual-gop")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("bilingual", "optionNo")
      .check(regex("Check your answers|Equality and diversity questions")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_065_PaymentBreakdown")
      .get(BaseURL + "/caveats/payment-breakdown")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Application fee")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_070_PaymentBreakdownSubmit")
      .post(BaseURL + "/caveats/payment-breakdown")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(regex("Enter card details"))
      .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
      .check(css("input[name='chargeId']", "value").saveAs("ChargeId")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_075_CheckCard")
      .post(PaymentURL + "/check_card/${ChargeId}")
      .headers(PostHeader)
      .formParam("cardNo", "4444333322221111")
      .check(jsonPath("$.accepted").is("true"))
      .check(bodyString.saveAs("responseBody")))
    .exec { session => println(session("responseBody").as[String]); session}

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_080_CardDetailsSubmit")
      .post(PaymentURL + "/card_details/${ChargeId}")
      .headers(PostHeader)
      .formParam("chargeId", "${ChargeId}")
      .formParam("csrfToken", "${csrf}")
      .formParam("cardNo", "4444333322221111")
      .formParam("expiryMonth", "08")
      .formParam("expiryYear", "21")
      .formParam("cardholderName", "Perf Tester")
      .formParam("cvc", "123")
      .formParam("addressCountry", "")
      .formParam("addressLine1", "1 Perf Test Road")
      .formParam("addressLine2", "")
      .formParam("addressCity", "Perf Test Town")
      .formParam("addressPostcode", "PR1 1RF")
      .formParam("email", "perftest12345@perftest12345.com")
      .check(regex("Confirm your payment"))
      .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
      .check(bodyString.saveAs("responseBody")))
      .exec { session => println(session("responseBody").as[String]); session}

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_085_CardDetailsConfirmSubmit")
      .post(PaymentURL + "/card_details/${ChargeId}/confirm")
      .headers(PostHeader)
      .formParam("chargeId", "${ChargeId}")
      .formParam("csrfToken", "${csrf}")
      .check(regex("Application complete")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  }

}
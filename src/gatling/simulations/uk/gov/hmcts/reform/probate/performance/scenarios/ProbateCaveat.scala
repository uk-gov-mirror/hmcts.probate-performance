package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.CsrfCheck
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.{Environment, Common}

import scala.concurrent.duration._
import scala.util.Random

object ProbateCaveat {

  val BaseURL = Environment.baseURL
  val PaymentURL = Environment.paymentURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val rnd = new Random()

  val ProbateCaveat =

    group("Caveat_010_Homepage") {

      exec(http("Homepage")
        .get(BaseURL + "/caveats/start-apply")
        .headers(CommonHeader)
        .header("sec-fetch-site", "none")
        .header("accept-language", "en-GB,en;q=0.9")
        .check(regex("Stop an application for probate")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_020_ApplicantName") {

      exec(http("ApplicantName")
        .get(BaseURL + "/caveats/applicant-name")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(CsrfCheck.save)
        .check(regex("What is your full name")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_030_ApplicantNameSubmit") {

      exec(http("ApplicantNameSubmit")
        .post(BaseURL + "/caveats/applicant-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("firstName", "Perf" + Common.randomString(5))
        .formParam("lastName", "Test" + Common.randomString(5))
        .check(CsrfCheck.save)
        .check(regex("What is your email address")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_040_EmailAddressSubmit") {

      exec(http("EmailAddressSubmit")
        .post(BaseURL + "/caveats/applicant-email")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("email", "caveat@perftest" + Common.randomString(8) + ".com")
        .check(CsrfCheck.save)
        .check(regex("What is your address")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_050_AddressSubmit") {

      exec(http("AddressSubmit")
        .post(BaseURL + "/caveats/applicant-address")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("addressLine1", rnd.nextInt(1000).toString + " Perf" + Common.randomString(5) + " Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf " + Common.randomString(5) + " Town")
        .formParam("newPostCode", Common.getPostcode())
        .formParam("country", "")
        .check(CsrfCheck.save)
        .check(regex("full name of the person")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_060_DeceasedNameSubmit") {

      exec(http("DeceasedNameSubmit")
        .post(BaseURL + "/caveats/deceased-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("firstName", "Perf" + Common.randomString(5))
        .formParam("lastName", "Tester" + Common.randomString(5))
        .check(CsrfCheck.save)
        .check(regex("What was the date that they died")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_070_DeceasedDodSubmit") {

      exec(http("DeceasedDodSubmit")
        .post(BaseURL + "/caveats/deceased-dod")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("dod-day", Common.getDay())
        .formParam("dod-month", Common.getMonth())
        .formParam("dod-year", Common.getDodYear())
        .check(CsrfCheck.save)
        .check(regex("date of birth")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_080_DeceasedDobKnownSubmit") {

      exec(http("DeceasedDobKnownSubmit")
        .post(BaseURL + "/caveats/deceased-dob-known")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("dobknown", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("What was their date of birth")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_090_DeceasedDobSubmit") {

      exec(http("DeceasedDobSubmit")
        .post(BaseURL + "/caveats/deceased-dob")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("dob-day", Common.getDay())
        .formParam("dob-month", Common.getMonth())
        .formParam("dob-year", Common.getDobYear())
        .check(CsrfCheck.save)
        .check(regex("known by any other names")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_100_DeceasedAliasSubmit") {

      exec(http("DeceasedAliasSubmit")
        .post(BaseURL + "/caveats/deceased-alias")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("alias", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("What was the permanent address")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_110_DeceasedAddressSubmit") {

      exec(http("DeceasedAddressSubmit")
        .post(BaseURL + "/caveats/deceased-address")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("addressLine1", rnd.nextInt(1000).toString + " Perf" + Common.randomString(5) + " Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf " + Common.randomString(5) + " Town")
        .formParam("newPostCode", Common.getPostcode())
        .formParam("country", "")
        .check(CsrfCheck.save)
        .check(regex("English and Welsh")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_120_BilingualSubmit") {

      exec(http("BilingualSubmit")
        .post(BaseURL + "/caveats/bilingual-gop")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("bilingual", "optionNo")
        .check(regex("Check your answers|Equality and diversity questions")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_130_PaymentBreakdown") {

      exec(http("PaymentBreakdown")
        .get(BaseURL + "/caveats/payment-breakdown")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(CsrfCheck.save)
        .check(regex("Application fee")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_140_PaymentBreakdownSubmit") {

      exec(http("PaymentBreakdownSubmit")
        .post(BaseURL + "/caveats/payment-breakdown")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .check(regex("Enter card details"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
        .check(css("input[name='chargeId']", "value").saveAs("ChargeId")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_150_CheckCard") {

      exec(http("CheckCard")
        .post(PaymentURL + "/check_card/${ChargeId}")
        .headers(PostHeader)
        .formParam("cardNo", "4444333322221111")
        .check(jsonPath("$.accepted").is("true")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // Gov Pay does strict postcode validation, so won't accept all postcodes in the format XXN NXX
    // Therefore, not using the postcode random function as payments with an invalid postcode fail

    .group("Caveat_160_CardDetailsSubmit") {

      exec(http("CardDetailsSubmit")
        .post(PaymentURL + "/card_details/${ChargeId}")
        .headers(PostHeader)
        .formParam("chargeId", "${ChargeId}")
        .formParam("csrfToken", "${csrf}")
        .formParam("cardNo", "4444333322221111")
        .formParam("expiryMonth", Common.getMonth())
        .formParam("expiryYear", "23")
        .formParam("cardholderName", "Perf Tester" + Common.randomString(5))
        .formParam("cvc", (100 + rnd.nextInt(900)).toString())
        .formParam("addressCountry", "GB")
        .formParam("addressLine1", rnd.nextInt(1000).toString + " Perf" + Common.randomString(5) + " Road")
        .formParam("addressLine2", "")
        .formParam("addressCity", "Perf " + Common.randomString(5) + " Town")
        .formParam("addressPostcode", "PR1 1RF") //Common.getPostcode()
        .formParam("email", "caveat@perftest" + Common.randomString(8) + ".com")
        .check(regex("Confirm your payment"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Caveat_170_CardDetailsConfirmSubmit") {

      exec(http("CardDetailsConfirmSubmit")
        .post(PaymentURL + "/card_details/${ChargeId}/confirm")
        .headers(PostHeader)
        .formParam("chargeId", "${ChargeId}")
        .formParam("csrfToken", "${csrf}")
        .check(regex("Application complete"))
        .check(regex("Your reference number is</span><br>(?s).*?<strong class=.govuk-!-font-weight-bold. aria-label=..>([0-9|-]*)<").saveAs("referenceNo"))
        .check(status.saveAs("statusValue")))

    }

      /*
    .exec {
      session =>
        println("CAVEAT REFERENCE NO: " + session("referenceNo").as[String])
        session
    }
       */

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

}
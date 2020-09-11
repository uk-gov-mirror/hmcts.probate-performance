package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.{Environment, Common}
import java.io.{BufferedWriter, FileWriter}

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
      .formParam("firstName", "Perf" + Common.randomString(5))
      .formParam("lastName", "Test" + Common.randomString(5))
      .check(CsrfCheck.save)
      .check(regex("What is your email address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_020_EmailAddressSubmit")
      .post(BaseURL + "/caveats/applicant-email")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("email", "caveat@perftest" + Common.randomString(8) + ".com")
      .check(CsrfCheck.save)
      .check(regex("What is your address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_025_AddressSubmit")
      .post(BaseURL + "/caveats/applicant-address")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("addressLine1", rnd.nextInt(1000).toString + " Perf" + Common.randomString(5)  + " Road")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "")
      .formParam("postTown", "Perf " + Common.randomString(5) + " Town")
      .formParam("newPostCode", Common.getPostcode())
      .formParam("country", "")
      .check(CsrfCheck.save)
      .check(regex("full name of the person")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_030_DeceasedNameSubmit")
      .post(BaseURL + "/caveats/deceased-name")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("firstName", "Perf" + Common.randomString(5))
      .formParam("lastName", "Tester" + Common.randomString(5))
      .check(CsrfCheck.save)
      .check(regex("What was the date that they died")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_035_DeceasedDodSubmit")
      .post(BaseURL + "/caveats/deceased-dod")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("dod-day", Common.getDay())
      .formParam("dod-month", Common.getMonth())
      .formParam("dod-year", Common.getDodYear())
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
      .formParam("dob-day", Common.getDay())
      .formParam("dob-month", Common.getMonth())
      .formParam("dob-year", Common.getDobYear())
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
      .formParam("addressLine1", rnd.nextInt(1000).toString + " Perf" + Common.randomString(5)  + " Road")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "")
      .formParam("postTown", "Perf " + Common.randomString(5) + " Town")
      .formParam("newPostCode", Common.getPostcode())
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
      .check(jsonPath("$.accepted").is("true")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_080_CardDetailsSubmit")
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
      .formParam("addressLine1", rnd.nextInt(1000).toString + " Perf" + Common.randomString(5)  + " Road")
      .formParam("addressLine2", "")
      .formParam("addressCity", "Perf " + Common.randomString(5) + " Town")
      .formParam("addressPostcode", "PR1 1RF") //Common.getPostcode()
      .formParam("email", "caveat@perftest" + Common.randomString(8) + ".com")
      .check(regex("Confirm your payment"))
      .check(css("input[name='csrfToken']", "value").saveAs("csrf")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("ProbateCaveat_010_085_CardDetailsConfirmSubmit")
      .post(PaymentURL + "/card_details/${ChargeId}/confirm")
      .headers(PostHeader)
      .formParam("chargeId", "${ChargeId}")
      .formParam("csrfToken", "${csrf}")
      .check(regex("Application complete"))
      .check(regex("Your reference number is</span><br>(?s).*?<strong class=.govuk-!-font-weight-bold. aria-label=..>([0-9|-]*)<").saveAs("referenceNo"))
      .check(status.saveAs("statusValue")))
      //.check(bodyString.saveAs("responseBody")))
      //.exec { session => println(session("responseBody").as[String]); session}
      //Write out the caveat reference number to a csv file
      .doIf(session=>session("statusValue").as[String].contains("200")) {
        exec {
          session =>
            val fw = new BufferedWriter(new FileWriter("SubmittedCaveats.csv", true))
            try {
              fw.write(session("referenceNo").as[String] + "\r\n")
            }
            finally fw.close()
            session
        }
      }

    .exec {
      session =>
        println("CAVEAT REFERENCE NO: " + session("referenceNo").as[String])
        session
    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  }

}
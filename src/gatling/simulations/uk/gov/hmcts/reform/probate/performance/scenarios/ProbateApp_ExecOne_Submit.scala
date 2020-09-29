package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.CsrfCheck
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.{Environment, Common}

import scala.concurrent.duration._
import scala.util.Random

object ProbateApp_ExecOne_Submit {

  val BaseURL = Environment.baseURL
  val PaymentURL = Environment.paymentURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val rnd = new Random()

  val randomNumber = Iterator.continually( Map( "rand" -> Random.nextInt(100)))

  val ProbateSubmit =

    feed(randomNumber)

    .exec(http("Probate_450_GetCase")
      .get(BaseURL + "/get-case/${appId}?probateType=PA")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("Complete these steps"))
      .check(regex("""3.</span> Check your answers and make your legal declaration\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_460_SectionFourStart")
      .get(BaseURL + "/copies-uk")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("How many extra copies")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_470_ExtraCopiesSubmit")
      .post(BaseURL + "/copies-uk")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("uk", "0")
      .check(CsrfCheck.save)
      .check(regex("have assets outside the UK")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_480_AssetsOverseasSubmit")
      .post(BaseURL + "/assets-overseas")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("assetsoverseas", "optionNo")
      .check(regex("Check your answers")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_490_TaskList")
      .get(BaseURL + "/task-list")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("Complete these steps"))
      .check(regex("""4.</span> Order extra copies of the grant of probate\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_500_SectionFiveStart")
      .get(BaseURL + "/payment-breakdown")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Application fee")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_510_PaymentBreakdownSubmit")
      .post(BaseURL + "/payment-breakdown")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(regex("Enter card details"))
      .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
      .check(css("input[name='chargeId']", "value").saveAs("ChargeId")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_520_CheckCard")
      .post(PaymentURL + "/check_card/${ChargeId}")
      .headers(PostHeader)
      .formParam("cardNo", "4444333322221111")
      .check(jsonPath("$.accepted").is("true")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // Gov Pay does strict postcode validation, so won't accept all postcodes in the format XXN NXX
    // Therefore, not using the postcode random function as payments with an invalid postcode fail

    .exec(http("Probate_530_CardDetailsSubmit")
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
      .formParam("email", "probate@perftest" + Common.randomString(8) + ".com")
      .check(regex("Confirm your payment"))
      .check(css("input[name='csrfToken']", "value").saveAs("csrf")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_540_CardDetailsConfirmSubmit")
      .post(PaymentURL + "/card_details/${ChargeId}/confirm")
      .headers(PostHeader)
      .formParam("chargeId", "${ChargeId}")
      .formParam("csrfToken", "${csrf}")
      .check(CsrfCheck.save)
      .check(regex("Before your application can be processed")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_550_PaymentStatusSubmit")
      .post(BaseURL + "/payment-status")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save)
      .check(regex("Prepare to send your documents")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_560_DocumentsSubmit")
      .post(BaseURL + "/documents")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("sentDocuments", "true")
      .check(regex("Application complete")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_570_DownloadCoverSheetPDF")
      .get(BaseURL + "/cover-sheet-pdf")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(bodyString.transform(_.size > 10000).is(true)))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // 50% of the time, download the additional two PDFs
    .doIf(session => session("rand").as[Int] < 50) {

      exec(http("Probate_580_DownloadCheckAnswersPDF")
        .get(BaseURL + "/check-answers-pdf")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(bodyString.transform(_.size > 3000).is(true)))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("Probate_590_DownloadDeclarationPDF")
        .get(BaseURL + "/declaration-pdf")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(bodyString.transform(_.size > 15000).is(true)))

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    }

}
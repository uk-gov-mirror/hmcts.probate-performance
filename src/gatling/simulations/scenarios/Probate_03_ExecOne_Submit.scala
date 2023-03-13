package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, CsrfCheck, Environment}

import scala.concurrent.duration._
import scala.util.Random

object Probate_03_ExecOne_Submit {

  val BaseURL = Environment.baseURL
  val PaymentURL = Environment.paymentURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val ProbateSubmit =

    group("Probate_450_GetCase") {

      exec(http("GetCase")
        .get(BaseURL + "/get-case/${caseId}?probateType=PA")
        .headers(CommonHeader)
        .check(regex("Complete these steps"))
        .check(regex("""3.</span> Check your answers and make your legal declaration\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_460_SectionFourStart") {

      exec(http("SectionFourStart")
        .get(BaseURL + "/copies-uk")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(regex("How many extra official copies")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_470_ExtraCopiesSubmit") {

      exec(http("ExtraCopiesSubmit")
        .post(BaseURL + "/copies-uk")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("uk", "0")
        .check(CsrfCheck.save)
        .check(regex("have assets outside the UK")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_480_AssetsOverseasSubmit") {

      exec(http("AssetsOverseasSubmit")
        .post(BaseURL + "/assets-overseas")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("assetsoverseas", "optionNo")
        .check(regex("Check your answers")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_490_TaskList") {

      exec(http("TaskList")
        .get(BaseURL + "/task-list")
        .headers(CommonHeader)
        .check(regex("Complete these steps"))
        .check(regex("""4.</span> Order extra copies of the grant of probate\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_500_SectionFiveStart") {

      exec(http("SectionFiveStart")
        .get(BaseURL + "/payment-breakdown")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(regex("Application fee")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_510_PaymentBreakdownSubmit") {

      exec(http("PaymentBreakdownSubmit")
        .post(BaseURL + "/payment-breakdown")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .check(regex("Enter card details"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
        .check(css("input[name='chargeId']", "value").saveAs("ChargeId")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_520_CheckCard") {

      exec(http("CheckCard")
        .post(PaymentURL + "/check_card/${ChargeId}")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("cardNo", "4444333322221111")
        .check(jsonPath("$.accepted").is("true")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // Gov Pay does strict postcode validation, so won't accept all postcodes in the format XXN NXX
    // Therefore, not using the postcode random function as payments with an invalid postcode fail

    .group("Probate_530_CardDetailsSubmit") {

      exec(http("CardDetailsSubmit")
        .post(PaymentURL + "/card_details/${ChargeId}")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("chargeId", "${ChargeId}")
        .formParam("csrfToken", "${csrf}")
        .formParam("cardNo", "4444333322221111")
        .formParam("expiryMonth", "01")
        .formParam("expiryYear", "25")
        .formParam("cardholderName", "Perf Tester ${randomString}")
        .formParam("cvc", "123")
        .formParam("addressCountry", "GB")
        .formParam("addressLine1", "1 Perf${randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressCity", "Perf ${randomString} Town")
        .formParam("addressPostcode", "TS1 1ST") //Common.getPostcode()
        .formParam("email", "probate@perftest${randomString}.com")
        .check(regex("Confirm your payment"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_540_CardDetailsConfirmSubmit") {

      exec(http("CardDetailsConfirmSubmit")
        .post(PaymentURL + "/card_details/${ChargeId}/confirm")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("chargeId", "${ChargeId}")
        .formParam("csrfToken", "${csrf}")
        .check(CsrfCheck.save)
        .check(regex("Before your application can be processed")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_550_PaymentStatusSubmit") {

      exec(http("PaymentStatusSubmit")
        .post(BaseURL + "/payment-status")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .check(CsrfCheck.save)
        .check(regex("Prepare to send your documents")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_560_DocumentsSubmit") {

      exec(http("DocumentsSubmit")
        .post(BaseURL + "/documents")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("sentDocuments", "true")
        .check(regex("Application complete")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_570_DownloadCoverSheetPDF") {

      exec(http("DownloadCoverSheetPDF")
        .get(BaseURL + "/cover-sheet-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 10000).is(true)))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_580_DownloadCheckAnswersPDF") {

      exec(http("DownloadCheckAnswersPDF")
        .get(BaseURL + "/check-answers-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 3000).is(true)))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Probate_590_DownloadDeclarationPDF") {

      exec(http("DownloadDeclarationPDF")
        .get(BaseURL + "/declaration-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 15000).is(true)))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

}
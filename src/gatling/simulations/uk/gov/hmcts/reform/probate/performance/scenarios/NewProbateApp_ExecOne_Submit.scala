package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object NewProbateApp_ExecOne_Submit {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val ProbateSubmit = group("Probate_NewApp_ExecOne_Submit") {

    exec(http("Probate_060_005_GetCase")
      .get(BaseURL + "/get-case/${appId}?probateType=PA")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("Complete these steps"))
      .check(regex("""3.</span> Check your answers and make your legal declaration\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_060_010_SectionFourStart")
      .get(BaseURL + "/copies-uk")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("How many extra copies")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_060_015_ExtraCopiesSubmit")
      .post(BaseURL + "/copies-uk")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("uk", "0")
      .check(CsrfCheck.save)
      .check(regex("have assets outside the UK")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_060_020_AssetsOverseasSubmit")
      .post(BaseURL + "/assets-overseas")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("assetsoverseas", "optionNo")
      .check(regex("Check your answers")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_060_025_TaskList")
      .get(BaseURL + "/task-list")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("Complete these steps"))
      .check(regex("""4.</span> Order extra copies of the grant of probate\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_060_030_SectionFiveStart")
      .get(BaseURL + "/payment-breakdown")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Application fee")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_060_035_PaymentsBreakdownSubmit")
      .post(BaseURL + "/payment-breakdown")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save)
      .check(regex("received your application")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_060_040_PaymentStatusSubmit")
      .post(BaseURL + "/payment-status")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save)
      .check(regex("Prepare to send your documents")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_060_045_DocumentsSubmit")
      .post(BaseURL + "/documents")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("sentDocuments", "true")
      .check(regex("Application complete")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  }

}
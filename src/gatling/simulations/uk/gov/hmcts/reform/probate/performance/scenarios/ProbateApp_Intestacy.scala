package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.{Environment, Common}
import java.io.{BufferedWriter, FileWriter}

import scala.concurrent.duration._
import scala.util.Random

object ProbateApp_Intestacy {

  val BaseURL = Environment.baseURL
  val PaymentURL = Environment.paymentURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val rnd = new Random()

  val IntestacyEligibility = group("Intestacy_Eligibility") {

    exec(http("Intestacy_030_005_DeathCertificate")
      .get(BaseURL + "/death-certificate")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Do you have a death certificate")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_030_010_DeathCertificateSubmit")
      .post(BaseURL + "/death-certificate")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("deathCertificate", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Did the person who died live permanently")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_030_015_DomicileSubmit")
      .post(BaseURL + "/deceased-domicile")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("domicile", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Has an Inheritance Tax")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_030_020_IHTSubmit")
      .post(BaseURL + "/iht-completed")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("completed", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Did the person who died leave a will")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_030_025_WillLeftSubmit")
      .post(BaseURL + "/will-left")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("left", "optionNo")
      .check(CsrfCheck.save)
      .check(regex("Did the person die on or after 1 October 2014")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_030_030_DiedAfterSubmit")
      .post(BaseURL + "/died-after-october-2014")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("diedAfter", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Are you the spouse")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_030_035_RelatedSubmit")
      .post(BaseURL + "/related-to-deceased")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("related", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Are you planning to make a joint application")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_030_040_OtherApplicantsSubmit")
      .post(BaseURL + "/other-applicants")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("otherApplicants", "optionNo")
      .check(regex("a href=./get-case/([0-9]+).probateType=INTESTACY").find.saveAs("appId"))
      .check(regex("In progress"))
      .check(status.saveAs("statusValue")))
    //Write out the email address, case id and type to a csv file
    .doIf(session => session("statusValue").as[String].contains("200")) {
      exec {
        session =>
          val fw = new BufferedWriter(new FileWriter("EmailAndCaseID.csv", true))
          try {
            fw.write(session("email").as[String] + "," + session("appId").as[String] + ",INTESTACY\r\n")
          }
          finally fw.close()
          session
      }
    }

    .exec {
      session =>
        println("APPLICANT EMAIL: " + session("email").as[String])
        println("CASE ID: " + session("appId").as[String])
        println("APPLICATION TYPE: INTESTACY")
        session
    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //At this point, the user will be redirected to their dashboard, listing the new application as 'In progress'

  }

  val IntestacyApplication = group("Intestacy_Apply") {

    exec(http("Intestacy_040_005_ContinueApplication")
      .get(BaseURL + "/get-case/${appId}?probateType=INTESTACY")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("Complete these steps")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_010_SectionOneStart")
      .get(BaseURL + "/bilingual-gop")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Do you require a bilingual grant")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_011_BilingualGrantSubmit")
      .post(BaseURL + "/bilingual-gop")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("bilingual", "optionNo")
      .check(CsrfCheck.save)
      .check(regex("What are the details of the person")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_015_DeceasedDetailsSubmit")
      .post(BaseURL + "/deceased-details")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("firstName", "Perf" + Common.randomString(5))
      .formParam("lastName", "Test" + Common.randomString(5))
      .formParam("dob-day", Common.getDay())
      .formParam("dob-month", Common.getMonth())
      .formParam("dob-year", Common.getDobYear())
      .formParam("dod-day", Common.getDay())
      .formParam("dod-month", Common.getMonth())
      .formParam("dod-year", "2019") //MUST BE > 2014
      .check(CsrfCheck.save)
      .check(regex("What was the permanent address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_020_DeceasedAddressSubmit")
      .post(BaseURL + "/deceased-address")
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
      .check(regex("Upload the death certificate")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_025_DocumentUpload")
      .post(BaseURL + "/document-upload")
      .header("Accept", "application/json")
      .header("Accept-Encoding", "gzip, deflate, br")
      .header("Accept-Language", "en-GB,en;q=0.5")
      .header("Content-Type", "multipart/form-data")
      .header("TE", "Trailers")
      .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
      .header("x-csrf-token", "${csrf}")
      .header("X-Requested-With", "XMLHttpRequest")
      .formParam("isUploadingDocument", "true")
      .bodyPart(RawFileBodyPart("file", "2MB.pdf")
        .fileName("2MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .check(status.is(200))
      .check(currentLocation.is(BaseURL + "/document-upload"))
      .check(regex("/document-upload/remove/0.>Remove</a>"))
      .check(regex("Upload the death certificate")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_030_DocumentUploadSubmit")
      .post(BaseURL + "/document-upload")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save)
      .check(regex("How was the Inheritance Tax")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_035_IHTMethodSubmit")
      .post(BaseURL + "/iht-method")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("method", "optionPaper")
      .check(CsrfCheck.save)
      .check(regex("Which paper form was filled in")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_040_IHTPaperSubmit")
      .post(BaseURL + "/iht-paper")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("form", "optionIHT205")
      .formParam("grossValueFieldIHT205", "8000")
      .formParam("netValueFieldIHT205", "8000")
      .formParam("grossValueFieldIHT207", "")
      .formParam("netValueFieldIHT207", "")
      .formParam("grossValueFieldIHT400421", "")
      .formParam("netValueFieldIHT400421", "")
      .check(CsrfCheck.save)
      .check(regex("any assets outside of England")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_045_AssetsOutsideEnglandWalesSubmit")
      .post(BaseURL + "/assets-outside-england-wales")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("assetsOutside", "optionNo")
      .check(CsrfCheck.save)
      .check(regex("have assets in another name")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_050_DeceasedAliasSubmit")
      .post(BaseURL + "/deceased-alias")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("alias", "optionNo")
      .check(CsrfCheck.save)
      .check(regex("marital status")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_055_MaritalStatusSubmit")
      .post(BaseURL + "/deceased-marital-status")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("maritalStatus", "optionMarried")
      .check(regex("Complete these steps"))
      .check(regex("""1.</span> Tell us about the person who has died\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_060_SectionTwoStart")
      .get(BaseURL + "/relationship-to-deceased")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("What was your relationship")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_065_RelationshipSubmit")
      .post(BaseURL + "/relationship-to-deceased")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("relationshipToDeceased", "optionSpousePartner")
      .check(CsrfCheck.save)
      .check(regex("What is your full name")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_070_ApplicantNameSubmit")
      .post(BaseURL + "/applicant-name")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("firstName", "Perf" + Common.randomString(5))
      .formParam("lastName", "ExecOne" + Common.randomString(5))
      .check(CsrfCheck.save)
      .check(regex("What is your phone number")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_075_ApplicantPhoneSubmit")
      .post(BaseURL + "/applicant-phone")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("phoneNumber", "07000000000")
      .check(CsrfCheck.save)
      .check(regex("What is your address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_080_ApplicantAddressSubmit")
      .post(BaseURL + "/applicant-address")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("addressLine1", rnd.nextInt(1000).toString + " Perf" + Common.randomString(5)  + " Road")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "")
      .formParam("postTown", "Perf " + Common.randomString(5) + " Town")
      .formParam("newPostCode", Common.getPostcode())
      .formParam("country", "")
      .check(regex("2.</span> Give details about the people applying(?s).*?<span class=.govuk-tag task-completed.>Completed</span>|Equality and diversity questions")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_085_SectionThreeStart")
      .get(BaseURL + "/summary/declaration")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("Check your answers")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_090_Declaration")
      .get(BaseURL + "/declaration")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Check the legal statement and make your declaration")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_095_DeclarationSubmit")
      .post(BaseURL + "/declaration")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("declarationCheckbox", "true")
      .check(regex("Complete these steps"))
      .check(regex("3.</span> Check your answers and make your legal declaration(?s).*?<span class=.govuk-tag task-completed.>Completed</span>")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_100_SectionFourStart")
      .get(BaseURL + "/copies-uk")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("How many extra copies")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_105_ExtraCopiesSubmit")
      .post(BaseURL + "/copies-uk")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("uk", "0")
      .check(CsrfCheck.save)
      .check(regex("have assets outside the UK")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_110_AssetsOverseasSubmit")
      .post(BaseURL + "/assets-overseas")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("assetsoverseas", "optionNo")
      .check(regex("Check your answers")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_115_TaskList")
      .get(BaseURL + "/task-list")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("Complete these steps"))
      .check(regex("""4.</span> Order copies of the letters of administration\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_120_SectionFiveStart")
      .get(BaseURL + "/payment-breakdown")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Application fee")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_125_PaymentBreakdownSubmit")
      .post(BaseURL + "/payment-breakdown")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(regex("Enter card details"))
      .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
      .check(css("input[name='chargeId']", "value").saveAs("ChargeId")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_130_CheckCard")
      .post(PaymentURL + "/check_card/${ChargeId}")
      .headers(PostHeader)
      .formParam("cardNo", "4444333322221111")
      .check(jsonPath("$.accepted").is("true")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // Gov Pay does strict postcode validation, so won't accept all postcodes in the format XXN NXX
    // Therefore, not using the postcode random function as payments with an invalid postcode fail

    .exec(http("Intestacy_040_135_CardDetailsSubmit")
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
      .formParam("email", "intestacy@perftest" + Common.randomString(8) + ".com")
      .check(regex("Confirm your payment"))
      .check(css("input[name='csrfToken']", "value").saveAs("csrf")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_140_CardDetailsConfirmSubmit")
      .post(PaymentURL + "/card_details/${ChargeId}/confirm")
      .headers(PostHeader)
      .formParam("chargeId", "${ChargeId}")
      .formParam("csrfToken", "${csrf}")
      .check(CsrfCheck.save)
      .check(regex("Before your application can be processed")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_145_PaymentStatusSubmit")
      .post(BaseURL + "/payment-status")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save)
      .check(regex("Prepare to send your documents")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Intestacy_040_150_DocumentsSubmit")
      .post(BaseURL + "/documents")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("sentDocuments", "true")
      .check(regex("Application complete")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  }

}
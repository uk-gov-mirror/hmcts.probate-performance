package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.CsrfCheck
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.{Environment, Common}

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

  val randomNumber = Iterator.continually( Map( "rand" -> Random.nextInt(100)))

  val IntestacyEligibility =

    group("Intestacy_010_StartEligibility") {

      exec(http("StartEligibility")
        .get(BaseURL + "/death-certificate")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(CsrfCheck.save)
        .check(regex("Do you have a death certificate")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_020_DeathCertificateSubmit") {

      exec(http("DeathCertificateSubmit")
        .post(BaseURL + "/death-certificate")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("deathCertificate", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Did the person who died live permanently")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_030_DomicileSubmit") {

      exec(http("DomicileSubmit")
        .post(BaseURL + "/deceased-domicile")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("domicile", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Has an Inheritance Tax")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_040_IHTSubmit") {

      exec(http("IHTSubmit")
        .post(BaseURL + "/iht-completed")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("completed", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Did the person who died leave a will")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_050_WillLeftSubmit") {

      exec(http("WillLeftSubmit")
        .post(BaseURL + "/will-left")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("left", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("Did the person die on or after 1 October 2014")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_060_DiedAfterSubmit") {

      exec(http("DiedAfterSubmit")
        .post(BaseURL + "/died-after-october-2014")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("diedAfter", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Are you the spouse")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_070_RelatedSubmit") {

      exec(http("RelatedSubmit")
        .post(BaseURL + "/related-to-deceased")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("related", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Are you planning to make a joint application")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_080_OtherApplicantsSubmit") {

      exec(http("OtherApplicantsSubmit")
        .post(BaseURL + "/other-applicants")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("otherApplicants", "optionNo")
        .check(regex("a href=./get-case/([0-9]+).probateType=INTESTACY").find.saveAs("appId"))
        .check(regex("In progress"))
        .check(status.saveAs("statusValue")))

    }

      /*
    .exec {
      session =>
        println("APPLICANT EMAIL: " + session("emailAddress").as[String])
        println("CASE ID: " + session("appId").as[String])
        println("APPLICATION TYPE: INTESTACY")
        session
    }
       */

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //At this point, the user will be redirected to their dashboard, listing the new application as 'In progress'

  val IntestacyApplication =

    feed(randomNumber)

    .group("Intestacy_090_ContinueApplication") {

      exec(http("ContinueApplication")
        .get(BaseURL + "/get-case/${appId}?probateType=INTESTACY")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(regex("Complete these steps")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_100_SectionOneStart") {

      exec(http("SectionOneStart")
        .get(BaseURL + "/bilingual-gop")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(CsrfCheck.save)
        .check(regex("Do you require a bilingual grant")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_110_BilingualGrantSubmit") {

      exec(http("BilingualGrantSubmit")
        .post(BaseURL + "/bilingual-gop")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("bilingual", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("What are the details of the person")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_120_DeceasedDetailsSubmit") {

      exec(http("DeceasedDetailsSubmit")
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

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_130_DeceasedAddressSubmit") {

      exec(http("DeceasedAddressSubmit")
        .post(BaseURL + "/deceased-address")
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
        .check(regex("Upload the death certificate")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_140_DocumentUpload") {

      exec(http("DocumentUpload")
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

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_150_DocumentUploadSubmit") {

      exec(http("DocumentUploadSubmit")
        .post(BaseURL + "/document-upload")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .check(CsrfCheck.save)
        .check(regex("How was the Inheritance Tax")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_160_IHTMethodSubmit") {

      exec(http("IHTMethodSubmit")
        .post(BaseURL + "/iht-method")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("method", "optionPaper")
        .check(CsrfCheck.save)
        .check(regex("Which paper form was filled in")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_170_IHTPaperSubmit") {

      exec(http("IHTPaperSubmit")
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

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_180_AssetsOutsideEngSubmit") {

      exec(http("AssetsOutsideEngSubmit")
        .post(BaseURL + "/assets-outside-england-wales")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("assetsOutside", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("have assets in another name")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_190_DeceasedAliasSubmit") {

      exec(http("DeceasedAliasSubmit")
        .post(BaseURL + "/deceased-alias")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("alias", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("marital status")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_200_MaritalStatusSubmit") {

      exec(http("MaritalStatusSubmit")
        .post(BaseURL + "/deceased-marital-status")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("maritalStatus", "optionMarried")
        .check(regex("Complete these steps"))
        .check(regex("""1.</span> Tell us about the person who has died\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_210_SectionTwoStart") {

      exec(http("SectionTwoStart")
        .get(BaseURL + "/relationship-to-deceased")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(CsrfCheck.save)
        .check(regex("What was your relationship")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_220_RelationshipSubmit") {

      exec(http("RelationshipSubmit")
        .post(BaseURL + "/relationship-to-deceased")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("relationshipToDeceased", "optionSpousePartner")
        .check(CsrfCheck.save)
        .check(regex("What is your full name")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_230_ApplicantNameSubmit") {

      exec(http("ApplicantNameSubmit")
        .post(BaseURL + "/applicant-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("firstName", "Perf" + Common.randomString(5))
        .formParam("lastName", "ExecOne" + Common.randomString(5))
        .check(CsrfCheck.save)
        .check(regex("What is your phone number")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_240_ApplicantPhoneSubmit") {

      exec(http("ApplicantPhoneSubmit")
        .post(BaseURL + "/applicant-phone")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("phoneNumber", "07000000000")
        .check(CsrfCheck.save)
        .check(regex("What is your address")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_250_ApplicantAddressSubmit") {

      exec(http("ApplicantAddressSubmit")
        .post(BaseURL + "/applicant-address")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("addressLine1", rnd.nextInt(1000).toString + " Perf" + Common.randomString(5) + " Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf " + Common.randomString(5) + " Town")
        .formParam("newPostCode", Common.getPostcode())
        .formParam("country", "")
        .check(regex("2.</span> Give details about the people applying(?s).*?<span class=.govuk-tag task-completed.>Completed</span>|Equality and diversity questions")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_260_SectionThreeStart") {

      exec(http("SectionThreeStart")
        .get(BaseURL + "/summary/declaration")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(regex("Check your answers")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_270_Declaration") {

      exec(http("Declaration")
        .get(BaseURL + "/declaration")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(CsrfCheck.save)
        .check(regex("Check the legal statement and make your declaration")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_280_DeclarationSubmit") {

      exec(http("DeclarationSubmit")
        .post(BaseURL + "/declaration")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("declarationCheckbox", "true")
        .check(regex("Complete these steps"))
        .check(regex("3.</span> Check your answers and make your legal declaration(?s).*?<span class=.govuk-tag task-completed.>Completed</span>")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_290_SectionFourStart") {

      exec(http("SectionFourStart")
        .get(BaseURL + "/copies-uk")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(CsrfCheck.save)
        .check(regex("How many extra official copies")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_300_ExtraCopiesSubmit") {

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

    .group("Intestacy_310_AssetsOverseasSubmit") {

      exec(http("AssetsOverseasSubmit")
        .post(BaseURL + "/assets-overseas")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("assetsoverseas", "optionNo")
        .check(regex("Check your answers")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_320_TaskList") {

      exec(http("TaskList")
        .get(BaseURL + "/task-list")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(regex("Complete these steps"))
        .check(regex("""4.</span> Order copies of the letters of administration\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_330_SectionFiveStart") {

      exec(http("SectionFiveStart")
        .get(BaseURL + "/payment-breakdown")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(CsrfCheck.save)
        .check(regex("Application fee")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_340_PaymentBreakdownSubmit") {

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

    .group("Intestacy_350_CheckCard") {

      exec(http("CheckCard")
        .post(PaymentURL + "/check_card/${ChargeId}")
        .headers(PostHeader)
        .formParam("cardNo", "4444333322221111")
        .check(jsonPath("$.accepted").is("true")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // Gov Pay does strict postcode validation, so won't accept all postcodes in the format XXN NXX
    // Therefore, not using the postcode random function as payments with an invalid postcode fail

    .group("Intestacy_360_CardDetailsSubmit") {

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
        .formParam("email", "intestacy@perftest" + Common.randomString(8) + ".com")
        .check(regex("Confirm your payment"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_370_CardDetailsConfirmSubmit") {

      exec(http("CardDetailsConfirmSubmit")
        .post(PaymentURL + "/card_details/${ChargeId}/confirm")
        .headers(PostHeader)
        .formParam("chargeId", "${ChargeId}")
        .formParam("csrfToken", "${csrf}")
        .check(CsrfCheck.save)
        .check(regex("Before your application can be processed")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_380_PaymentStatusSubmit") {

      exec(http("PaymentStatusSubmit")
        .post(BaseURL + "/payment-status")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .check(CsrfCheck.save)
        .check(regex("Prepare to send your documents")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_390_DocumentsSubmit") {

      exec(http("DocumentsSubmit")
        .post(BaseURL + "/documents")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("sentDocuments", "true")
        .check(regex("Application complete")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_400_DownloadCoverSheetPDF") {

      exec(http("DownloadCoverSheetPDF")
        .get(BaseURL + "/cover-sheet-pdf")
        .headers(CommonHeader)
        .headers(GetHeader)
        .check(bodyString.transform(_.size > 10000).is(true)))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    // 50% of the time, download the additional two PDFs
    .doIf(session => session("rand").as[Int] < 50) {

      group("Intestacy_410_DownloadCheckAnswersPDF") {

        exec(http("DownloadCheckAnswersPDF")
          .get(BaseURL + "/check-answers-pdf")
          .headers(CommonHeader)
          .headers(GetHeader)
          .check(bodyString.transform(_.size > 3000).is(true)))

      }

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .group("Intestacy_420_DownloadDeclarationPDF") {

        exec(http("DownloadDeclarationPDF")
          .get(BaseURL + "/declaration-pdf")
          .headers(CommonHeader)
          .headers(GetHeader)
          .check(bodyString.transform(_.size > 15000).is(true)))

      }

      .pause(MinThinkTime seconds, MaxThinkTime seconds)

    }

}
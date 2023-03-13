package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, CsrfCheck, Environment}

import scala.concurrent.duration._

object Probate_Intestacy {

  val BaseURL = Environment.baseURL
  val PaymentURL = Environment.paymentURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val IntestacyEligibility =

    exec(_.setAll("randomString" -> Common.randomString(5),
      "dobDay" -> Common.getDay(),
      "dobMonth" -> Common.getMonth(),
      "dobYear" -> Common.getDobYear(),
      "dodDay" -> Common.getDay(),
      "dodMonth" -> "03", //Removing random DOD to test Excepted Estates (requires DOD after 01/01/2022)
      "dodYear" -> "2022",
      "randomPostcode" -> Common.getPostcode()))

    .group("Intestacy_010_StartEligibility") {

      exec(http("StartEligibility")
        .get(BaseURL + "/death-certificate")
        .headers(CommonHeader)
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
        .check(regex("Is the original death certificate in English")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_025_DeathCertEnglishSubmit") {

      exec(http("DeathCertEnglishSubmit")
        .post(BaseURL + "/death-certificate-english")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("deathCertificateInEnglish", "optionYes")
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
        .check(regex("1 January 2022")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_035_ExceptedEstatesDodSubmit") {

      exec(http("EEDodSubmit")
        .post(BaseURL + "/ee-deceased-dod")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("eeDeceasedDod", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Have you worked out")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_040_ExceptedEstatesValuedSubmit") {

      exec(http("ExceptedEstatesValuedSubmit")
        .post(BaseURL + "/ee-estate-valued")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("eeEstateValued", "optionYes")
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
        .check(regex("a href=./get-case/([0-9]+).probateType=INTESTACY").find.optional.saveAs("caseId"))
        .check(status.saveAs("statusValue")))

    }

    //WORKAROUND: Sometimes ElasticSearch isn't indexed quick enough with the new case, so the case will not be listed
    //on the dashboard. If this is the case, wait 5 seconds and refresh the dashboard
    .doIf("${caseId.isUndefined()}") {

      pause(5)

      .group("Intestacy_085_RefreshDashboard") {

        exec(http("RefreshDashboard")
          .get(BaseURL + "/dashboard")
          .headers(CommonHeader)
          .header("sec-fetch-site", "none")
          .check(regex("a href=./get-case/([0-9]+).probateType=INTESTACY").find.saveAs("caseId"))
          .check(regex("In progress")))

        }

    }

      /*
    .exec {
      session =>
        println("APPLICANT EMAIL: " + session("emailAddress").as[String])
        println("CASE ID: " + session("caseId").as[String])
        println("APPLICATION TYPE: INTESTACY")
        session
    }
       */

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //At this point, the user will be redirected to their dashboard, listing the new application as 'In progress'

  val IntestacyApplicationSection1 =

    group("Intestacy_090_ContinueApplication") {

      exec(http("ContinueApplication")
        .get(BaseURL + "/get-case/${caseId}?probateType=INTESTACY")
        .headers(CommonHeader)
        .check(regex("Complete these steps")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_100_SectionOneStart") {

      exec(http("SectionOneStart")
        .get(BaseURL + "/bilingual-gop")
        .headers(CommonHeader)
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
        .formParam("firstName", "Perf${randomString}")
        .formParam("lastName", "Test${randomString}")
        .formParam("dob-day", "${dobDay}")
        .formParam("dob-month", "${dobMonth}")
        .formParam("dob-year", "${dobYear}")
        .formParam("dod-day", "${dodDay}")
        .formParam("dod-month", "${dodMonth}")
        .formParam("dod-year", "${dodYear}")
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
        .formParam("addressLine1", "1 Perf${randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf ${randomString} Town")
        .formParam("newPostCode", "${randomPostcode}")
        .formParam("country", "")
        .check(CsrfCheck.save)
        .check(regex("die in England or Wales")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_140_DiedEngOrWalesSubmit") {

      exec(http("DiedEngOrWalesSubmit")
        .post(BaseURL + "/died-eng-or-wales")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("diedEngOrWales", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Do you have a death certificate")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_150_CertificateInterimSubmit") {

      exec(http("CertificateInterimSubmit")
        .post(BaseURL + "/certificate-interim")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("deathCertificate", "optionDeathCertificate")
        .check(CsrfCheck.save)
        .check(regex("Did you complete IHT forms")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_160_EstateValuedSubmit") {

      exec(http("EstateValuedSubmit")
        .post(BaseURL + "/estate-valued")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("estateValueCompleted", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Which IHT forms")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_170_EstateFormSubmit") {

      exec(http("EstateFormSubmit")
        .post(BaseURL + "/estate-form")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("ihtFormEstateId", "optionIHT400421")
        .check(CsrfCheck.save)
        .check(regex("What are the values of the estate")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_180_EstateValuesSubmit") {

      exec(http("EstateValuesSubmit")
        .post(BaseURL + "/probate-estate-values")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("grossValueField", "900000")
        .formParam("netValueField", "800000")
        .check(CsrfCheck.save)
        .check(regex("any assets outside of England")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_185_AssetsOutsideUKSubmit") {

      exec(http("AssetsOutsideUKSubmit")
        .post(BaseURL + "/assets-outside-england-wales")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("assetsOutside", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("assets in another name")))

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

  val IntestacyApplicationSection2 =

    group("Intestacy_210_SectionTwoStart") {

      exec(http("SectionTwoStart")
        .get(BaseURL + "/relationship-to-deceased")
        .headers(CommonHeader)
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
        .check(regex("have any children")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_225_AnyChildrenSubmit") {

      exec(http("AnyChildrenSubmit")
        .post(BaseURL + "/any-children")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .formParam("anyChildren", "optionNo")
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
        .formParam("firstName", "Perf${randomString}")
        .formParam("lastName", "ExecOne${randomString}")
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
        .formParam("addressLine1", "2 Perf${randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf ${randomString} Town")
        .formParam("newPostCode", "${randomPostcode}")
        .formParam("country", "")
        .check(regex("2.</span> Give details about the people applying(?s).*?<span class=.govuk-tag task-completed.>Completed</span>|Equality and diversity questions")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val IntestacyApplicationSection3 =

    group("Intestacy_260_SectionThreeStart") {

      exec(http("SectionThreeStart")
        .get(BaseURL + "/summary/declaration")
        .headers(CommonHeader)
        .check(regex("Check your answers")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_270_Declaration") {

      exec(http("Declaration")
        .get(BaseURL + "/declaration")
        .headers(CommonHeader)
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

  val IntestacyApplicationSection4 =

    group("Intestacy_290_SectionFourStart") {

      exec(http("SectionFourStart")
        .get(BaseURL + "/copies-uk")
        .headers(CommonHeader)
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
        .check(regex("Complete these steps"))
        .check(regex("""4.</span> Order copies of the letters of administration\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  val IntestacyApplicationSection5 =

    group("Intestacy_330_SectionFiveStart") {

      exec(http("SectionFiveStart")
        .get(BaseURL + "/payment-breakdown")
        .headers(CommonHeader)
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
        .formParam("expiryMonth", "01")
        .formParam("expiryYear", "25")
        .formParam("cardholderName", "Perf Tester ${randomString}")
        .formParam("cvc", "123")
        .formParam("addressCountry", "GB")
        .formParam("addressLine1", "1 Perf${randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressCity", "Perf ${randomString} Town")
        .formParam("addressPostcode", "TS1 1ST") //Common.getPostcode()
        .formParam("email", "intestacy@perftest${randomString}.com")
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
        .check(regex("received your payment")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_380_PaymentStatusSubmit") {

      exec(http("PaymentStatusSubmit")
        .post(BaseURL + "/payment-status")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "${csrf}")
        .check(regex("Application complete")))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_400_DownloadCoverSheetPDF") {

      exec(http("DownloadCoverSheetPDF")
        .get(BaseURL + "/cover-sheet-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 10000).is(true)))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_410_DownloadCheckAnswersPDF") {

      exec(http("DownloadCheckAnswersPDF")
        .get(BaseURL + "/check-answers-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 3000).is(true)))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .group("Intestacy_420_DownloadDeclarationPDF") {

      exec(http("DownloadDeclarationPDF")
        .get(BaseURL + "/declaration-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 15000).is(true)))

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

}
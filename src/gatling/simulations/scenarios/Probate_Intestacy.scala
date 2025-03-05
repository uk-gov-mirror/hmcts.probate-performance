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
      "cardExpiryYear" -> Common.getCardExpiryYear(),
      "randomPostcode" -> Common.getPostcode()))

    .group("Intestacy_010_StartEligibility") {

      exec(http("StartEligibility")
        .get(BaseURL + "/death-certificate")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Do you have the death certificate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_020_DeathCertificateSubmit") {

      exec(http("DeathCertificateSubmit")
        .post(BaseURL + "/death-certificate")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deathCertificate", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Is the original death certificate in English")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_025_DeathCertEnglishSubmit") {

      exec(http("DeathCertEnglishSubmit")
        .post(BaseURL + "/death-certificate-english")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deathCertificateInEnglish", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Did the person who died live permanently")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_030_DomicileSubmit") {

      exec(http("DomicileSubmit")
        .post(BaseURL + "/deceased-domicile")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("domicile", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("1 January 2022")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_035_ExceptedEstatesDodSubmit") {

      exec(http("EEDodSubmit")
        .post(BaseURL + "/ee-deceased-dod")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("eeDeceasedDod", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Have you worked out")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_040_ExceptedEstatesValuedSubmit") {

      exec(http("ExceptedEstatesValuedSubmit")
        .post(BaseURL + "/ee-estate-valued")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("eeEstateValued", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Did the person who died leave a will")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_050_WillLeftSubmit") {

      exec(http("WillLeftSubmit")
        .post(BaseURL + "/will-left")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("left", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("Did the person die on or after 1 October 2014")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_060_DiedAfterSubmit") {

      exec(http("DiedAfterSubmit")
        .post(BaseURL + "/died-after-october-2014")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("diedAfter", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Are you the spouse")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_070_RelatedSubmit") {

      exec(http("RelatedSubmit")
        .post(BaseURL + "/related-to-deceased")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("related", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Are you planning to make a joint application")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_080_OtherApplicantsSubmit") {

      exec(http("OtherApplicantsSubmit")
        .post(BaseURL + "/other-applicants")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("otherApplicants", "optionNo")
        .check(substring("Complete these steps")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  //Update May 2024: The user is no longer shown the dashboard, but will now be taken directly to the task-list

  val IntestacyApplicationSection1 =

    group("Intestacy_100_SectionOneStart") {

      exec(http("SectionOneStart")
        .get(BaseURL + "/bilingual-gop")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Do you require a bilingual grant")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_110_BilingualGrantSubmit") {

      exec(http("BilingualGrantSubmit")
        .post(BaseURL + "/bilingual-gop")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("bilingual", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("What are the details of the person")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_120_DeceasedDetailsSubmit") {

      exec(http("DeceasedDetailsSubmit")
        .post(BaseURL + "/deceased-details")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("firstName", "Perf#{randomString}")
        .formParam("lastName", "Test#{randomString}")
        .formParam("dob-day", "#{dobDay}")
        .formParam("dob-month", "#{dobMonth}")
        .formParam("dob-year", "#{dobYear}")
        .formParam("dod-day", "#{dodDay}")
        .formParam("dod-month", "#{dodMonth}")
        .formParam("dod-year", "#{dodYear}")
        .check(CsrfCheck.save)
        .check(substring("permanent address at the time of their death")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_130_DeceasedAddressSubmit") {

      exec(http("DeceasedAddressSubmit")
        .post(BaseURL + "/deceased-address")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("addressLine1", "1 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf #{randomString} Town")
        .formParam("newPostCode", "#{randomPostcode}")
        .formParam("country", "")
        .check(CsrfCheck.save)
        .check(substring("die in England or Wales")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_140_DiedEngOrWalesSubmit") {

      exec(http("DiedEngOrWalesSubmit")
        .post(BaseURL + "/died-eng-or-wales")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("diedEngOrWales", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Do you have a death certificate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_150_CertificateInterimSubmit") {

      exec(http("CertificateInterimSubmit")
        .post(BaseURL + "/certificate-interim")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deathCertificate", "optionDeathCertificate")
        .check(CsrfCheck.save)
        .check(substring("report the estate value to HMRC")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_155_CalcCheckSubmit") {

      exec(http("CalcCheckSubmit")
        .post(BaseURL + "/calc-check")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("calcCheckCompleted", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Which forms did you submit to HMRC")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_160_HMRCFormsSubmit") {

      exec(http("HMRCFormsSubmit")
        .post(BaseURL + "/new-submitted-to-hmrc")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("ihtFormEstateId", "optionIHT400")
        .check(CsrfCheck.save)
        .check(substring("Have you received a letter from HMRC")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_165_HMRCLetterSubmit") {

      exec(http("HMRCLetterSubmit")
        .post(BaseURL + "/hmrc-letter")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("hmrcLetterId", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Enter the unique probate code")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_170_HMRCCodeSubmit") {

      exec(http("HMRCCodeSubmit")
        .post(BaseURL + "/unique-probate-code")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("uniqueProbateCodeId", "CTS 040523 1104 3tpp s8e9")
        .check(CsrfCheck.save)
        .check(substring("What are the values of assets")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_180_EstateValuesSubmit") {

      exec(http("EstateValuesSubmit")
        .post(BaseURL + "/probate-estate-values")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("grossValueField", "900000")
        .formParam("netValueField", "800000")
        .check(CsrfCheck.save)
        .check(substring("any assets outside of England")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_185_AssetsOutsideUKSubmit") {

      exec(http("AssetsOutsideUKSubmit")
        .post(BaseURL + "/assets-outside-england-wales")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("assetsOutside", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("assets in another name")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_190_DeceasedAliasSubmit") {

      exec(http("DeceasedAliasSubmit")
        .post(BaseURL + "/deceased-alias")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("alias", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("marital status")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_200_MaritalStatusSubmit") {

      exec(http("MaritalStatusSubmit")
        .post(BaseURL + "/deceased-marital-status")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("maritalStatus", "optionMarried")
        .check(substring("Complete these steps"))
        .check(regex("Tell us about the person who has died(?s).*?govuk-task-list__status\">(.+?)</div>").is("Completed")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  val IntestacyApplicationSection2 =

    group("Intestacy_210_SectionTwoStart") {

      exec(http("SectionTwoStart")
        .get(BaseURL + "/relationship-to-deceased")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("What was your relationship")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_220_RelationshipSubmit") {

      exec(http("RelationshipSubmit")
        .post(BaseURL + "/relationship-to-deceased")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("relationshipToDeceased", "optionSpousePartner")
        .check(CsrfCheck.save)
        .check(substring("have any children")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_225_AnyChildrenSubmit") {

      exec(http("AnyChildrenSubmit")
        .post(BaseURL + "/any-children")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("anyChildren", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("What is your full name")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_230_ApplicantNameSubmit") {

      exec(http("ApplicantNameSubmit")
        .post(BaseURL + "/applicant-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("firstName", "Perf#{randomString}")
        .formParam("lastName", "ExecOne#{randomString}")
        .check(CsrfCheck.save)
        .check(substring("What is your phone number")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_240_ApplicantPhoneSubmit") {

      exec(http("ApplicantPhoneSubmit")
        .post(BaseURL + "/applicant-phone")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("phoneNumber", "07000000000")
        .check(CsrfCheck.save)
        .check(substring("What is your address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_250_ApplicantAddressSubmit") {

      exec(http("ApplicantAddressSubmit")
        .post(BaseURL + "/applicant-address")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("addressLine1", "2 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf #{randomString} Town")
        .formParam("newPostCode", "#{randomPostcode}")
        .formParam("country", "")
        .check(regex("Give details about the people applying(?s).*?<span class=.govuk-tag task-completed.>Completed</span>|Equality and diversity questions")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  val IntestacyApplicationSection3 =

    group("Intestacy_260_SectionThreeStart") {

      exec(http("SectionThreeStart")
        .get(BaseURL + "/summary/declaration")
        .headers(CommonHeader)
        .check(substring("Check your answers")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_270_Declaration") {

      exec(http("Declaration")
        .get(BaseURL + "/declaration")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Check the legal statement and make your declaration")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_280_DeclarationSubmit") {

      exec(http("DeclarationSubmit")
        .post(BaseURL + "/declaration")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("declarationCheckbox", "true")
        .check(substring("Complete these steps"))
        .check(regex("Check your answers and make your legal declaration(?s).*?govuk-task-list__status\">(.+?)</div>").is("Completed")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  val IntestacyApplicationSection4 =

    group("Intestacy_290_SectionFourStart") {

      exec(http("SectionFourStart")
        .get(BaseURL + "/copies-uk")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("How many extra official copies")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_300_ExtraCopiesSubmit") {

      exec(http("ExtraCopiesSubmit")
        .post(BaseURL + "/copies-uk")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("uk", "0")
        .check(CsrfCheck.save)
        .check(substring("have assets outside the UK")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_310_AssetsOverseasSubmit") {

      exec(http("AssetsOverseasSubmit")
        .post(BaseURL + "/assets-overseas")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("assetsoverseas", "optionNo")
        .check(substring("Check your answers")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  val IntestacyPayment =

    group("Intestacy_330_PaymentBreakdown") {

      exec(http("PaymentBreakdown")
        .get(BaseURL + "/payment-breakdown")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Application fee")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_340_PaymentBreakdownSubmit") {

      exec(http("PaymentBreakdownSubmit")
        .post(BaseURL + "/payment-breakdown")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .check(substring("Enter card details"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
        .check(css("input[name='chargeId']", "value").saveAs("ChargeId")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_350_CheckCard") {

      exec(http("CheckCard")
        .post(PaymentURL + "/check_card/#{ChargeId}")
        .headers(PostHeader)
        .formParam("cardNo", "4444333322221111")
        .check(jsonPath("$.accepted").is("true")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    // Gov Pay does strict postcode validation, so won't accept all postcodes in the format XXN NXX
    // Therefore, not using the postcode random function as payments with an invalid postcode fail

    .group("Intestacy_360_CardDetailsSubmit") {

      exec(http("CardDetailsSubmit")
        .post(PaymentURL + "/card_details/#{ChargeId}")
        .headers(PostHeader)
        .formParam("chargeId", "#{ChargeId}")
        .formParam("csrfToken", "#{csrf}")
        .formParam("cardNo", "4444333322221111")
        .formParam("expiryMonth", "01")
        .formParam("expiryYear", "#{cardExpiryYear}")
        .formParam("cardholderName", "Perf Tester #{randomString}")
        .formParam("cvc", "123")
        .formParam("addressCountry", "GB")
        .formParam("addressLine1", "1 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressCity", "Perf #{randomString} Town")
        .formParam("addressPostcode", "TS1 1ST") //Common.getPostcode()
        .formParam("email", "intestacy@perftest#{randomString}.com")
        .check(substring("Confirm your payment"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_370_CardDetailsConfirmSubmit") {

      exec(http("CardDetailsConfirmSubmit")
        .post(PaymentURL + "/card_details/#{ChargeId}/confirm")
        .headers(PostHeader)
        .formParam("chargeId", "#{ChargeId}")
        .formParam("csrfToken", "#{csrf}")
        .check(CsrfCheck.save)
        .check(substring("Payment received")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_380_PaymentStatusSubmit") {

      exec(http("PaymentStatusSubmit")
        .post(BaseURL + "/payment-status")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .check(substring("Application submitted")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_400_DownloadCoverSheetPDF") {

      exec(http("DownloadCoverSheetPDF")
        .get(BaseURL + "/cover-sheet-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 10000).is(true)))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_410_DownloadCheckAnswersPDF") {

      exec(http("DownloadCheckAnswersPDF")
        .get(BaseURL + "/check-answers-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 3000).is(true)))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_420_DownloadDeclarationPDF") {

      exec(http("DownloadDeclarationPDF")
        .get(BaseURL + "/declaration-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 15000).is(true)))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

}

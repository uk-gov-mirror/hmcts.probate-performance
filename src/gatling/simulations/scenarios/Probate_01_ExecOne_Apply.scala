package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.CsrfCheck
import utils.{Common, Environment}

import scala.concurrent.duration._

object Probate_01_ExecOne_Apply {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val ProbateEligibility =

    exec(_.setAll("randomString" -> Common.randomString(5),
      "dobDay" -> Common.getDay(),
      "dobMonth" -> Common.getMonth(),
      "dobYear" -> Common.getDobYear(),
      "dodDay" -> Common.getDay(),
      "dodMonth" -> "03", //Removing random DOD to test Excepted Estates (requires DOD after 01/01/2022)
      "dodYear" -> "2022",
      "randomPostcode" -> Common.getPostcode()))

    .group("Probate_010_StartEligibility") {

      exec(http("StartEligibility")
        .get(BaseURL + "/death-certificate")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(regex("Do you have the death certificate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_020_DeathCertificateSubmit") {

      exec(http("DeathCertificateSubmit")
        .post(BaseURL + "/death-certificate")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deathCertificate", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Is the original death certificate in English")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_025_DeathCertEnglishSubmit") {

      exec(http("DeathCertEnglishSubmit")
        .post(BaseURL + "/death-certificate-english")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deathCertificateInEnglish", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Did the person who died live permanently")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_030_DomicileSubmit") {

      exec(http("DomicileSubmit")
        .post(BaseURL + "/deceased-domicile")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("domicile", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("1 January 2022")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_035_ExceptedEstatesDodSubmit") {

      exec(http("EEDodSubmit")
        .post(BaseURL + "/ee-deceased-dod")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("eeDeceasedDod", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Have you worked out")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_040_ExceptedEstatesValuedSubmit") {

      exec(http("ExceptedEstatesValuedSubmit")
        .post(BaseURL + "/ee-estate-valued")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("eeEstateValued", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Did the person who died leave a will")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_050_WillLeftSubmit") {

      exec(http("WillLeftSubmit")
        .post(BaseURL + "/will-left")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("left", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Do you have the original will")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_060_WillOriginalSubmit") {

      exec(http("WillOriginalSubmit")
        .post(BaseURL + "/will-original")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("original", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Are you named as an executor")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_070_NamedExecutorSubmit") {

      exec(http("NamedExecutorSubmit")
        .post(BaseURL + "/applicant-executor")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("executor", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Are all the executors able to make their own decisions")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_080_MentalCapacitySubmit") {

      exec(http("MentalCapacitySubmit")
        .post(BaseURL + "/mental-capacity")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("mentalCapacity", "optionYes")
        .check(regex("Complete these steps")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  //Update May 2024: The user is no longer shown the dashboard, but will now be taken directly to the task-list

  val ProbateApplicationSection1 =

    group("Probate_100_SectionOneStart") {

      exec(http("SectionOneStart")
        .get(BaseURL + "/bilingual-gop")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(regex("Do you require a bilingual grant")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_110_BilingualGrantSubmit") {

      exec(http("BilingualGrantSubmit")
        .post(BaseURL + "/bilingual-gop")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("bilingual", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("full name of the person who died")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_120_DeceasedNameSubmit") {

      exec(http("DeceasedNameSubmit")
        .post(BaseURL + "/deceased-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("firstName", "Perf#{randomString}")
        .formParam("lastName", "Test#{randomString}")
        .check(CsrfCheck.save)
        .check(regex("exactly how the name is written on the will")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_125_NameAsOnWillSubmit") {

      exec(http("NameAsOnWillSubmit")
        .post(BaseURL + "/deceased-name-as-on-will")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("nameAsOnTheWill", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("What was their date of birth")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_130_DeceasedDOBSubmit") {

      exec(http("DeceasedDOBSubmit")
        .post(BaseURL + "/deceased-dob")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("dob-day", "#{dobDay}")
        .formParam("dob-month", "#{dobMonth}")
        .formParam("dob-year", "#{dobYear}")
        .check(CsrfCheck.save)
        .check(regex("What was the date that they died")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_140_DeceasedDODSubmit") {

      exec(http("DeceasedDODSubmit")
        .post(BaseURL + "/deceased-dod")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("dod-day", "#{dodDay}")
        .formParam("dod-month", "#{dodMonth}")
        .formParam("dod-year", "#{dodYear}")
        .check(CsrfCheck.save)
        .check(regex("What was the permanent address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_150_DeceasedAddressSubmit") {

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
        .check(regex("die in England or Wales")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_160_DiedEngOrWalesSubmit") {

      exec(http("DiedEngOrWalesSubmit")
        .post(BaseURL + "/died-eng-or-wales")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("diedEngOrWales", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Do you have a death certificate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_170_CertificateInterimSubmit") {

      exec(http("CertificateInterimSubmit")
        .post(BaseURL + "/certificate-interim")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deathCertificate", "optionDeathCertificate")
        .check(CsrfCheck.save)
        .check(regex("report the estate value to HMRC")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_172_CalcCheckSubmit") {

      exec(http("CalcCheckSubmit")
        .post(BaseURL + "/calc-check")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("calcCheckCompleted", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Which forms did you submit to HMRC")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_175_HMRCFormsSubmit") {

      exec(http("HMRCFormsSubmit")
        .post(BaseURL + "/new-submitted-to-hmrc")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("ihtFormEstateId", "optionIHT400")
        .check(CsrfCheck.save)
        .check(regex("Have you received a letter from HMRC")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_180_HMRCLetterSubmit") {

      exec(http("HMRCLetterSubmit")
        .post(BaseURL + "/hmrc-letter")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("hmrcLetterId", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Enter the unique probate code")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_185_HMRCCodeSubmit") {

      exec(http("HMRCCodeSubmit")
        .post(BaseURL + "/unique-probate-code")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("uniqueProbateCodeId", "CTS 040523 1104 3tpp s8e9")
        .check(CsrfCheck.save)
        .check(regex("What are the values of assets")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_190_EstateValuesSubmit") {

      exec(http("EstateValuesSubmit")
        .post(BaseURL + "/probate-estate-values")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("grossValueField", "900000")
        .formParam("netValueField", "800000")
        .check(CsrfCheck.save)
        .check(regex("have assets in another name")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_200_DeceasedAliasSubmit") {

      exec(http("DeceasedAliasSubmit")
        .post(BaseURL + "/deceased-alias")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("alias", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("get married or enter into a civil partnership")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_210_DeceasedMarriedSubmit") {

      exec(http("DeceasedMarriedSubmit")
        .post(BaseURL + "/deceased-married")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("married", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("Does the will have any damage or marks")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_215_WillDamagesSubmit") {

      exec(http("WillDamagesSubmit")
        .post(BaseURL + "/will-has-damage")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("otherDamageDescription", "")
        .formParam("willHasVisibleDamage", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("Were any codicils made to the will")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_220_WillCodicilsSubmit") {

      exec(http("WillCodicilsSubmit")
        .post(BaseURL + "/will-codicils")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("codicils", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("How many codicils were made to the will")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_230_WillNumberSubmit") {

      exec(http("WillNumberSubmit")
        .post(BaseURL + "/codicils-number")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("codicilsNumber", "1")
        .check(regex("Do the codicils have any damage or marks")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_235_CodicilsDamageSubmit") {

      exec(http("CodicilsDamageSubmit")
        .post(BaseURL + "/codicils-have-damage")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("otherDamageDescription", "")
        .formParam("codicilsHasVisibleDamage", "optionNo")
        .check(regex("Did the person who died leave any other written wishes")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_238_WrittenWishesSubmit") {

      exec(http("WrittenWishesSubmit")
        .post(BaseURL + "/deceased-written-wishes")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deceasedWrittenWishes", "optionNo")
        .check(regex("Complete these steps"))
        .check(regex("""1.</span> Tell us about the person who has died\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  val ProbateApplicationSection2 =

    group("Probate_240_SectionTwoStart") {

      exec(http("SectionTwoStart")
        .get(BaseURL + "/applicant-name")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(regex("What is your full name")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_250_ApplicantNameSubmit") {

      exec(http("ApplicantNameSubmit")
        .post(BaseURL + "/applicant-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("firstName", "Perf#{randomString}")
        .formParam("lastName", "ExecOne#{randomString}")
        .check(CsrfCheck.save)
        .check(regex("exactly what appears on the will")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_260_ApplicantNameAsOnWillSubmit") {

      exec(http("ApplicantNameAsOnWillSubmit")
        .post(BaseURL + "/applicant-name-as-on-will")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("nameAsOnTheWill", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("What is your phone number")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_270_ApplicantPhoneSubmit") {

      exec(http("ApplicantPhoneSubmit")
        .post(BaseURL + "/applicant-phone")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("phoneNumber", "07000000000")
        .check(CsrfCheck.save)
        .check(regex("What is your address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_280_ApplicantAddressSubmit") {

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
        .check(CsrfCheck.save)
        .check(regex("How many executors are named")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_290_ExecutorsNumberSubmit") {

      exec(http("ExecutorsNumberSubmit")
        .post(BaseURL + "/executors-number")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("executorsNumber", "2")
        .check(CsrfCheck.save)
        .check(regex("What are the executors")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_300_ExecutorsNamesSubmit") {

      exec(http("ExecutorsNamesSubmit")
        .post(BaseURL + "/executors-names")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("executorName[0]", "Perf Exec Two")
        .check(CsrfCheck.save)
        .check(regex("Are all the executors alive")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_310_ExecutorsAllAliveSubmit") {

      exec(http("ExecutorsAllAliveSubmit")
        .post(BaseURL + "/executors-all-alive")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("allalive", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Will any of the other executors be dealing with the estate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_320_OtherExecutorsSubmit") {

      exec(http("OtherExecutorsSubmit")
        .post(BaseURL + "/other-executors-applying")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("otherExecutorsApplying", "optionYes")
        .check(CsrfCheck.save)
        .check(regex("Which executors will be dealing with the estate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_330_ExecutorsDealingSubmit") {

      exec(http("ExecutorsDealingSubmit")
        .post(BaseURL + "/executors-dealing-with-estate")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("executorsApplying[]", "Perf Exec Two")
        .check(CsrfCheck.save)
        .check(regex("Do any of these executors now have a different name")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_340_ExecutorsAliasSubmit") {

      exec(http("ExecutorsAliasSubmit")
        .post(BaseURL + "/executors-alias")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("alias", "optionNo")
        .check(CsrfCheck.save)
        .check(regex("email address and mobile number")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_350_ExecTwoContactDetailsSubmit") {

      exec(http("ExecTwoContactDetailsSubmit")
        .post(BaseURL + "/executor-contact-details/1")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("email", "exec-two@perftest#{randomString}.com")
        .formParam("mobile", "07000000001")
        .check(CsrfCheck.save)
        .check(regex("permanent address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_360_ExecTwoAddressSubmit") {

      exec(http("ExecTwoAddressSubmit")
        .post(BaseURL + "/executor-address/1")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("addressLine1", "3 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf #{randomString} Town")
        .formParam("newPostCode", "#{randomPostcode}")
        .formParam("country", "")
        //PCQ (Equality/diversity survey) might pop up at this point, so cater for either outcome in the text check
        .check(regex("2.</span> Give details about the executors(?s).*?<span class=.govuk-tag task-completed.>Completed</span>|Equality and diversity questions")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  val ProbateApplicationSection3 =

    group("Probate_370_SectionThreeStart") {

      exec(http("SectionThreeStart")
        .get(BaseURL + "/summary/declaration")
        .headers(CommonHeader)
        .check(regex("Check your answers")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_380_Declaration") {

      exec(http("Declaration")
        .get(BaseURL + "/declaration")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(regex("Check the legal statement and make your declaration")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_390_DeclarationSubmit") {

      exec(http("DeclarationSubmit")
        .post(BaseURL + "/declaration")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("declarationCheckbox", "true")
        .check(CsrfCheck.save)
        .check(regex("Notify the other executors")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_400_ExecutorsInviteSubmit") {

      exec(http("ExecutorsInviteSubmit")
        .post(BaseURL + "/executors-invite")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .check(regex("Complete these steps"))
        .check(regex("Not declared")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    //Get the invite ID associated with the second executor

    .group("Probate_Util_InviteIdList") {

      exec(http("InviteIdList")
        .get(BaseURL + "/inviteIdList")
        .headers(CommonHeader)
        .check(regex("\\\"ids\\\":\\[\\\"(.+?)\\\"").saveAs("inviteId")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

}

package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object NewProbateApp_ExecOne_Apply {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val ProbateEligibility = group("Probate_NewApp_ExecOne_Eligibility") {

    exec(http("Probate_030_005_DeathCertificate")
      .get(BaseURL + "/death-certificate")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Do you have a death certificate")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

      .exec(http("Probate_030_010_DeathCertificateSubmit")
      .post(BaseURL + "/death-certificate")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("deathCertificate", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Did the person who died live permanently")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_030_015_DomicileSubmit")
      .post(BaseURL + "/deceased-domicile")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("domicile", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Has an Inheritance Tax")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_030_020_IHTSubmit")
      .post(BaseURL + "/iht-completed")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("completed", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Did the person who died leave a will")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_030_025_WillLeftSubmit")
      .post(BaseURL + "/will-left")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("left", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Do you have the original will")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_030_030_WillOriginalSubmit")
      .post(BaseURL + "/will-original")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("original", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Are you named as an executor")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_030_035_NamedExecutorSubmit")
      .post(BaseURL + "/applicant-executor")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("executor", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Are all the executors able to make their own decisions")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_030_040_MentalCapacitySubmit")
      .post(BaseURL + "/mental-capacity")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("mentalCapacity", "optionYes")
      .check(regex("a href=./get-case/([0-9]+).probateType=PA").find.saveAs("appId"))
      .check(regex("In progress")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //At this point, the user will be redirected to their dashboard, listing the new application as 'In progress'

  }

  val ProbateApplication = group("Probate_NewApp_ExecOne_Apply") {

    exec(http("Probate_040_005_ContinueApplication")
      .get(BaseURL + "/get-case/${appId}?probateType=PA")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("Complete these steps")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_010_SectionOneStart")
      .get(BaseURL + "/bilingual-gop")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Do you require a bilingual grant")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_015_BilingualGrantSubmit")
      .post(BaseURL + "/bilingual-gop")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("bilingual", "optionNo")
      .check(CsrfCheck.save)
      .check(regex("full name of the person who died")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_020_DeceasedNameSubmit")
      .post(BaseURL + "/deceased-name")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("firstName", "Perf")
      .formParam("lastName", "Tester")
      .check(CsrfCheck.save)
      .check(regex("What was their date of birth")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_025_DeceasedDOBSubmit")
      .post(BaseURL + "/deceased-dob")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("dob-day", "12")
      .formParam("dob-month", "12")
      .formParam("dob-year", "1912")
      .check(CsrfCheck.save)
      .check(regex("What was the date that they died")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_030_DeceasedDODSubmit")
      .post(BaseURL + "/deceased-dod")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("dod-day", "12")
      .formParam("dod-month", "12")
      .formParam("dod-year", "2012")
      .check(CsrfCheck.save)
      .check(regex("What was the permanent address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_035_DeceasedAddressSubmit")
      .post(BaseURL + "/deceased-address")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("addressLine1", "1 Perf Test Road")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "")
      .formParam("postTown", "Perf Test Town")
      .formParam("newPostCode", "PR1 1RF")
      .formParam("country", "")
      .check(CsrfCheck.save)
      .check(regex("Upload the death certificate")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_040_DocumentUploadSubmit")
      .post(BaseURL + "/document-upload")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save)
      .check(regex("How was the Inheritance Tax")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_045_IHTMethodSubmit")
      .post(BaseURL + "/iht-method")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("method", "optionPaper")
      .check(CsrfCheck.save)
      .check(regex("Which paper form was filled in")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_050_IHTPaperSubmit")
      .post(BaseURL + "/iht-paper")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("form", "optionIHT205")
      .formParam("grossValueFieldIHT205", "4999")
      .formParam("netValueFieldIHT205", "4999")
      .formParam("grossValueFieldIHT207", "")
      .formParam("netValueFieldIHT207", "")
      .formParam("grossValueFieldIHT400421", "")
      .formParam("netValueFieldIHT400421", "")
      .check(CsrfCheck.save)
      .check(regex("have assets in another name")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_055_DeceasedAliasSubmit")
      .post(BaseURL + "/deceased-alias")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("alias", "optionNo")
      .check(CsrfCheck.save)
      .check(regex("get married or enter into a civil partnership")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_060_DeceasedMarriedSubmit")
      .post(BaseURL + "/deceased-married")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("married", "optionNo")
      .check(CsrfCheck.save)
      .check(regex("Were any updates")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_065_WillCodicilsSubmit")
      .post(BaseURL + "/will-codicils")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("codicils", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("How many updates")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_070_WillNumberSubmit")
      .post(BaseURL + "/codicils-number")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("codicilsNumber", "1")
      .check(regex("Complete these steps"))
      .check(regex("""1.</span> Tell us about the person who has died\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_075_SectionTwoStart")
      .get(BaseURL + "/applicant-name")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("What is your full name")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_080_ApplicantNameSubmit")
      .post(BaseURL + "/applicant-name")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("firstName", "Perf")
      .formParam("lastName", "Applicant")
      .check(CsrfCheck.save)
      .check(regex("exactly what appears on the will")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_085_ApplicantNameAsOnWillSubmit")
      .post(BaseURL + "/applicant-name-as-on-will")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("nameAsOnTheWill", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("What is your phone number")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_090_ApplicantPhoneSubmit")
      .post(BaseURL + "/applicant-phone")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("phoneNumber", "07000000000")
      .check(CsrfCheck.save)
      .check(regex("What is your address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_095_ApplicantAddressSubmit")
      .post(BaseURL + "/applicant-address")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("addressLine1", "1 Perf Test Road")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "")
      .formParam("postTown", "Perf Test Town")
      .formParam("newPostCode", "PR1 1RF")
      .formParam("country", "")
      .check(CsrfCheck.save)
      .check(regex("How many past and present executors")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_100_ExecutorsNumberSubmit")
      .post(BaseURL + "/executors-number")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("executorsNumber", "2")
      .check(CsrfCheck.save)
      .check(regex("What are the executors")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_105_ExecutorsNamesSubmit")
      .post(BaseURL + "/executors-names")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("executorName[0]", "Perf Exec Two")
      .check(CsrfCheck.save)
      .check(regex("Are all the executors alive")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_110_ExecutorsAllAliveSubmit")
      .post(BaseURL + "/executors-all-alive")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("allalive", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Will any of the other executors be dealing with the estate")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_115_OtherExecutorsApplyingSubmit")
      .post(BaseURL + "/other-executors-applying")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("otherExecutorsApplying", "optionYes")
      .check(CsrfCheck.save)
      .check(regex("Which executors will be dealing with the estate")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_120_ExecutorsDealingWithEstateSubmit")
      .post(BaseURL + "/executors-dealing-with-estate")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("executorsApplying[]", "Perf Exec Two")
      .check(CsrfCheck.save)
      .check(regex("Do any of these executors now have a different name")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_125_ExecutorsAliasSubmit")
      .post(BaseURL + "/executors-alias")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("alias", "optionNo")
      .check(CsrfCheck.save)
      .check(regex("email address and mobile number")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_130_ExecutorTwoContactDetailsSubmit")
      .post(BaseURL + "/executor-contact-details/1")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("email", "exec-two@perftest12345.com")
      .formParam("mobile", "07000000001")
      .check(CsrfCheck.save)
      .check(regex("permanent address")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_135_ExecutorTwoAddressSubmit")
      .post(BaseURL + "/executor-address/1")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("addressLine1", "1 Perf Test Road")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "")
      .formParam("postTown", "Perf Test Town")
      .formParam("newPostCode", "PR1 1RF")
      .formParam("country", "")
      .check(regex("Complete these steps"))
      .check(regex("""2.</span> Give details about the executors\n    </h2>\n    \n        <span class="govuk-tag task-completed">Completed</span>""")))
      //.check(status.not(500)))
      //.exitHereIfFailed

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_140_SectionThreeStart")
      .get(BaseURL + "/summary/declaration")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("Check your answers")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_145_Declaration")
      .get(BaseURL + "/declaration")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Check the legal statement and make your declaration")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_150_DeclarationSubmit")
      .post(BaseURL + "/declaration")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("declarationCheckbox", "true")
      .check(CsrfCheck.save)
      .check(regex("Notify the other executors")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_040_155_ExecutorsInviteSubmit")
      .post(BaseURL + "/executors-invite")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .check(regex("Complete these steps"))
      .check(regex("Not declared")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //Get the invite ID associated with the second executor

    .exec(http("Probate_040_160_InviteIdList")
      .get(BaseURL + "/inviteIdList")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(regex("\\\"ids\\\":\\[\\\"(.+?)\\\"").saveAs("inviteId")))
      //.check(jsonPath("$.ids[0]").saveAs("inviteId")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  }

}
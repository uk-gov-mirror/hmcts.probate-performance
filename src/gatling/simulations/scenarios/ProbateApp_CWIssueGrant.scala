package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object ProbateApp_CWIssueGrant {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val IssueGrant = {

    val CaseDocUrl = Environment.caseDocUrl

    //Upload Document (Will)

    exec(CCDAPI.Auth("CaseworkerDocUpload"))

    .exec(http("Probate_000_DocumentUpload")
      .post(CaseDocUrl + "/cases/documents")
      .header("Authorization", "Bearer ${bearerToken}")
      .header("ServiceAuthorization", "${authToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "5MB.pdf")
        .fileName("5MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURL"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .pause(1)

    //Link uploaded document to case
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boUploadDocumentsForCaseCreated", "bodies/UploadDocumentsForCaseCreated.json"))

    .pause(1)

    //Print the case
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boPrintCase", "bodies/CCD_PrintCase.json"))

    .pause(1)

    //Mark as ready for examination
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boMarkAsReadyForExamination", "bodies/CCD_MarkAsReadyForExamination.json"))

    .pause(1)

    //Find matches (Examining)
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boFindMatchesForReadyForExamining", "bodies/CCD_FindMatchesForReadyForExamining.json"))

    .pause(1)

    //Examine case
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boExamineCase", "bodies/CCD_ExamineCase.json"))

    .pause(1)

    //Mark as ready to issue
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boMarkAsReadyToIssue", "bodies/CCD_MarkAsReadyToIssue.json"))

    .pause(1)

    //Find matches (Issue grant)
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boFindMatchesNoQA", "bodies/CCD_FindMatchesNoQA.json"))

    .pause(1)

    //Issue grant
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boIssueGrantForCaseMatching", "bodies/CCD_IssueGrantForCaseMatching.json"))

    .pause(1)
  }

}
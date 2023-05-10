package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

object Probate_04_CW_IssueGrant {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val IssueGrant = {

    val CaseDocUrl = Environment.caseDocUrl

    //Upload Document (Will)

    exec(CCDAPI.Auth("CaseworkerDocUpload"))

    .exec(http("Probate_000_DocumentUpload")
      .post(CaseDocUrl + "/cases/documents")
      .header("Authorization", "Bearer #{bearerToken}")
      .header("ServiceAuthorization", "#{authToken}")
      .header("accept", "application/json")
      .header("Content-Type", "multipart/form-data")
      .formParam("classification", "PUBLIC")
      .formParam("caseTypeId", "GrantOfRepresentation")
      .formParam("jurisdictionId", "PROBATE")
      .bodyPart(RawFileBodyPart("files", "2MB.pdf")
        .fileName("2MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURL"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    .pause(1)

    //Link uploaded document to case
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boUploadDocumentsAwaitingDoc", "bodies/CCD_boUploadDocumentsAwaitingDoc.json"))

    .pause(1)

    //Print the case
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boGenerateGrantPreviewForExamining", "bodies/CCD_boGenerateGrantPreviewForExamining.json"))

    .pause(1)

    //Find matches (Issue grant)
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boFindMatchesNoQA", "bodies/CCD_FindMatchesNoQA.json"))

    .pause(1)

    //Issue grant
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boIssueGrantForCaseMatching", "bodies/CCD_IssueGrantForCaseMatching.json"))

    .pause(1)
  }

}
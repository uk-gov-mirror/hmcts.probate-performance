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
      .bodyPart(RawFileBodyPart("files", "2MB.pdf")
        .fileName("2MB.pdf")
        .transferEncoding("binary"))
      .asMultipartForm
      .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURL"))
      .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken")))

    //Link uploaded document to case
    .exec(CCDAPI.CreateEvent("Caseworker", "PROBATE", "GrantOfRepresentation", "boUploadDocumentsForCaseCreated", "bodies/CCD_Probate_DocumentUpload.json"))

    .pause(1)
  }

}
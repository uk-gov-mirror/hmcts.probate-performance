package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

import scala.concurrent.duration._

object Probate_05_ExecOne_CitizenHub {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader


  val ProbateCitizenHub =

    group("Probate_600_LoadCitizenHub") {

      exec(http("LoadCitizenHub")
        .get(BaseURL + "/get-case/${caseId}?probateType=PA")
        .headers(CommonHeader)
        .check(regex("Submitted|Issued"))) //TODO: update this with text checks for the citizen hub (should be either submitted or issued)

    }

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

}
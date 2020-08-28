package uk.gov.hmcts.reform.probate.performance.scenarios.checks

import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.status.HttpStatusCheckType

object StatusCheck {
  def save: CheckBuilder[HttpStatusCheckType,String,Int] = status.saveAs("httpStatus")

  //doesn't like the above - required string,int; found response,int
  //something maybe to go here to evaluate the httpStatus


  //https://github.com/gatling/gatling/blob/2.0.0-M3a/gatling-http/src/main/scala/io/gatling/http/check/status/HttpStatusCheckBuilder.scala


  def check: String = "${httpStatus}"
}
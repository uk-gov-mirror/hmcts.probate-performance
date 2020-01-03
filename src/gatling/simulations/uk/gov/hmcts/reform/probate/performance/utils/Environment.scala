package uk.gov.hmcts.reform.cmc.performance.utils

object Environment {
  val httpConfig = scala.util.Properties.envOrElse("httpConfig", "http")


  val users = scala.util.Properties.envOrElse("numberOfUser", "10")
  val maxResponseTime = scala.util.Properties.envOrElse("maxResponseTime", "500")
  val idamCookieName="SESSION_ID"

  val commonHeader = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1",
    "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")

  val caveatURL = scala.util.Properties.envOrElse("caveatURL", "https://probate.aat.platform.hmcts.net")
  val intestacyURL = scala.util.Properties.envOrElse("intestacyURL", "https://probate-frontend-aat.service.core-compute-aat.internal")
  //val cmcBashURL = scala.util.Properties.envOrElse("baseUrl", "https://cmc-citizen-frontend-aat.service.core-compute-aat.internal")
  //val cmcBashURL = scala.util.Properties.envOrElse("baseUrl", "https://www.moneyclaims.demo.platform.hmcts.net")
  // val cmcBashURL = scala.util.Properties.envOrElse("baseUrl", "https://moneyclaim.nonprod.platform.hmcts.net")
  //val cmcBashURL = scala.util.Properties.envOrElse("baseUrl", "https://www-demo.moneyclaim.reform.hmcts.net")
  //val cmcBashURL = scala.util.Properties.envOrElse("baseUrl", "https://moneyclaim.nonprod.platform.hmcts.net")
  //val iadmURL = "https://idam-test.dev.ccidam.reform.hmcts.net"
  val PaymentURL = scala.util.Properties.envOrElse("paymentURL", "https://www.payments.service.gov.uk")
  //val idamBaseURL=scala.util.Properties.envOrElse("idamBaseURL", "https://idam-api-idam-perftest.service.core-compute-idam-perftest.internal")
  val idamBaseURL=scala.util.Properties.envOrElse("idamBaseURL", "https://idam-api.perftest.platform.hmcts.net")

    val thinkTime = 3
    val waitForNextIteration = 80
}
package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class MeldingerSokSimulation extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:8083")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("nb-no,nb;q=0.9,no-no;q=0.8,no;q=0.6,nn-no;q=0.5,nn;q=0.4,en-us;q=0.3,en;q=0.1")
    .connection("keep-alive")
    .contentTypeHeader("application/x-www-form-urlencoded")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")

  val headers_0 = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

  val headers_4 = Map(
    "Accept" -> "application/xml, text/xml, */*; q=0.01",
    "Wicket-Ajax" -> "true",
    "Wicket-Ajax-BaseURL" -> "person/***REMOVED***?0",
    "X-Requested-With" -> "XMLHttpRequest")

  val headers_5 = Map("X-Requested-With" -> "XMLHttpRequest")

  val headers_9 = Map(
    "Accept" -> "application/xml, text/xml, */*; q=0.01",
    "Wicket-Ajax" -> "true",
    "Wicket-Ajax-BaseURL" -> "person/***REMOVED***?0",
    "Wicket-FocusedElementId" -> "henvendelseSokToggle7e",
    "X-Requested-With" -> "XMLHttpRequest")

  val headers_14 = Map("Accept" -> "image/png,image/*;q=0.8,*/*;q=0.5")

  val uri1 = "http://localhost:8083"

  val scn = scenario("RecordedSimulation")
    .exec(http("request_0")
    .get("/modiabrukerdialog/person/***REMOVED***")
    .headers(headers_0)
    .resources(http("request_1")
    .get(uri1 + "/favicon.ico")
    .headers(headers_0)
    .check(status.is(404)),
      http("request_2")
        .get(uri1 + "/favicon.ico")
        .headers(headers_0)
        .check(status.is(404)))
    .check(status.is(304)))
    .pause(6)
    .exec(http("request_3")
    .post("/modiabrukerdialog/j_security_check")
    .headers(headers_0)
    .formParam("j_username", "Z000001")
    .formParam("j_password", "***REMOVED***"))
    .pause(1)
    .exec(http("request_4")
    .get("/modiabrukerdialog/person/***REMOVED***?0-2.IBehaviorListener.1-lameller&_=1427111665307&TOKEN=")
    .headers(headers_4)
    .resources(http("request_5")
    .get(uri1 + "/modiabrukerdialog/rest/skrivestotte/sok?fritekst=")
    .headers(headers_5)))
    .pause(1)
    .exec(http("request_6")
    .get("/modiabrukerdialog/person/***REMOVED***?0-2.IBehaviorListener.0-lameller-lameller-oversikt-lerretWrapper-lerret-meldinger-header&_=1427111665308")
    .headers(headers_4)
    .resources(http("request_7")
    .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/indekser")
    .headers(headers_5),
      http("request_8")
        .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/sok/")
        .headers(headers_5)))
    .pause(1)
    .exec(http("request_9")
    .get("/modiabrukerdialog/person/***REMOVED***?0-2.IBehaviorListener.0-lameller-lameller-meldinger-lerretWrapper-lerret-henvendelseSokToggle&_=1427111665309")
    .headers(headers_9)
    .resources(http("request_10")
    .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/indekser")
    .headers(headers_5),
      http("request_11")
        .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/sok/")
        .headers(headers_5)))
    .pause(5)
    .exec(http("request_12")
    .get("/modiabrukerdialog/rest/meldinger/***REMOVED***/sok/f")
    .headers(headers_5)
    .resources(http("request_13")
    .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/sok/fa")
    .headers(headers_5),
      http("request_14")
        .get(uri1 + "/modiabrukerdialog/img/personikon.svg")
        .headers(headers_14)
        .check(status.is(304)),
      http("request_15")
        .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/sok/fam")
        .headers(headers_5),
      http("request_16")
        .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/sok/fami")
        .headers(headers_5),
      http("request_17")
        .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/sok/famil")
        .headers(headers_5),
      http("request_18")
        .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/sok/famili")
        .headers(headers_5),
      http("request_19")
        .get(uri1 + "/modiabrukerdialog/rest/meldinger/***REMOVED***/sok/familie")
        .headers(headers_5)))
    .pause(3)
    .exec(http("request_20")
    .get("/modiabrukerdialog/person/***REMOVED***?0-2.IBehaviorListener.0-lameller-lameller-meldinger-lerretWrapper-lerret-meldinger-nyesteMeldingerITraad-1&_=1427111665310")
    .headers(headers_4)
    .resources(http("request_21")
    .get(uri1 + "/modiabrukerdialog/img/ny_oppgave_inaktiv.svg")
    .headers(headers_14)
    .check(status.is(304))))

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
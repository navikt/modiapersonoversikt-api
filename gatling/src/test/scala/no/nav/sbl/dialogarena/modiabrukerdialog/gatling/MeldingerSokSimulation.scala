package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import io.gatling.http.request.builder.{Http, HttpRequestBuilder}

import scala.concurrent.duration._
import io.gatling.core.session

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

  val headers = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

  def sokChain(ord: String) = {
    def split(ord: String): List[String] = {
      if (ord.isEmpty) {
        List(ord)
      } else {
        ord :: split(ord.init)
      }
    }

    split(ord).sortBy(_.length).map(s =>
      exec(
        http("søk")
          .get("/modiabrukerdialog/rest/meldinger/${fnr}/sok/" + s)
          .headers(headers))
        .pause(50 millis))
  }

  val scn = scenario("Søk i meldinger")
    .feed(csv("fnr.csv").random)
    .feed(csv("navIdenter.csv").random)
    .exec(
      http("login")
        .post("/modiabrukerdialog/j_security_check")
        .headers(headers)
        .formParam("j_username", session => session("navIdent").as[String])
        .formParam("j_password", session => session("passord").as[String]))
    .pause(1)
    .exitHereIfFailed
    .exec(
      http("indekser")
        .get("/modiabrukerdialog/rest/meldinger/${fnr}/indekser")
        .headers(headers))
    .pause(100 millis)
    .exec(sokChain("Arbeid"))

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
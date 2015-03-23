package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import java.lang.Double._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class MeldingerSokSimulation extends Simulation {

  val baseUrl = System.getProperty("baseUrl")
  val users = Integer.getInteger("users")
  val duration: Double = valueOf(System.getProperty("duration"))

  val httpProtocol = http
    .baseURL(baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("nb-no,nb;q=0.9,no-no;q=0.8,no;q=0.6,nn-no;q=0.5,nn;q=0.4,en-us;q=0.3,en;q=0.1")
    .connection("keep-alive")
    .contentTypeHeader("application/x-www-form-urlencoded")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")

  val headers = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")


  def query: String = {
    val queries = Array("Arbeid", "Familie", "Arbeidsavklaringspenger", "Sykepenger", "Pensjon", "Samtalereferat telefon")
    val index = new Random(System.currentTimeMillis()).nextInt(queries.length)
    queries(index)
  }

  def sokChain(query: String) = {
    def split(s: String): List[String] = {
      if (s.isEmpty) {
        List(s)
      } else {
        s :: split(s.init)
      }
    }

    split(query).sortBy(_.length).map(_.replace(" ", "%20")).map(s =>
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
    .exec(sokChain(query))

  setUp(scn.inject(rampUsers(users) over(duration minutes))).protocols(httpProtocol)
}
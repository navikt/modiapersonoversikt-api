package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class MeldingerSokSimulation extends Simulation {

  val env = "q4"
  val meldingerSokUsers = 1
  val skrivestotteSokUsers = 1
  val duration: Double = 1
  val baseUrl = "https://modapp-" + env + ".adeo.no"

  val httpProtocol = http
    .baseURL(baseUrl)
    .disableWarmUp
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
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

  def sokChain(name: String, url: String, query: String) = {
    def split(s: String): List[String] = {
      if (s.isEmpty) {
        List(s)
      } else {
        s :: split(s.init)
      }
    }

    split(query).sortBy(_.length).map(_.replace(" ", "%20")).map(s => {
      exec(
        http(name)
          .get(url + s)
          .headers(headers))
        .pause(50 millis)
    })
  }

  def login(scenarioName: String) = {
    scenario(scenarioName)
      .feed(csv("fnr_" + env + ".csv").random)
      .feed(csv("navIdenter_" + env + ".csv").random)
      .exec(
        http("start")
          .get("/modiabrukerdialog/")
          .check(status.is(401)))
      .pause(5)
      .exec(
        http("login")
          .post("/modiabrukerdialog/j_security_check")
          .headers(headers)
          .formParam("j_username", "${navIdent}")
          .formParam("j_password", "${passord}"))
      .exitHereIfFailed
  }

  val meldingerSokScenario = login("Søk i meldinger scenario")
    .pause(1)
    .exec(
      http("indekser meldinger")
        .get("/modiabrukerdialog/rest/meldinger/${fnr}/indekser")
        .headers(headers))
    .exitHereIfFailed
    .pause(100 millis)
    .exec(sokChain("Søk i meldinger", "/modiabrukerdialog/rest/meldinger/${fnr}/sok/", query))

  val skrivestotteSokScenario = login("Søk i hjelpetekster scenario")
    .pause(1)
    .exec(sokChain("Søk i hjelpetekster", "/modiabrukerdialog/rest/skrivestotte/sok?fritekst=", query))

  setUp(
    meldingerSokScenario.inject(rampUsers(meldingerSokUsers) over (duration minutes)),
    skrivestotteSokScenario.inject(rampUsers(skrivestotteSokUsers) over (duration minutes)))
    .protocols(httpProtocol)
}
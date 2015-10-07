package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import java.lang.Double._

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.SkrivestotteSokScenario.skrivestotteSokScenario

import scala.concurrent.duration.DurationDouble

class SkrivestotteSokSimulation extends Simulation {

  val duration: Double = valueOf(System.getProperty("duration.minutes"))
  var users: Int = Integer.getInteger("skrivestottesok.users")
  setUp(
    skrivestotteSokScenario.inject(rampUsers(users) over (duration minutes))
  ).protocols(Utils.httpProtocol)

}
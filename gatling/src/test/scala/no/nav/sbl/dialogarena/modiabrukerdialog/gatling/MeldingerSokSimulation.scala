package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import java.lang.Double._
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.MeldingerSokScenario.meldingerSokScenario
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.Utils._
import scala.concurrent.duration.DurationDouble


class MeldingerSokSimulation extends Simulation {

  // System properties
  val duration: Double = valueOf(System.getProperty("duration.minutes"))
  val users: Int = Integer.getInteger("meldingersok.users")

  setUp(meldingerSokScenario.inject(rampUsers(users) over (duration minutes))).protocols(httpProtocol)
}
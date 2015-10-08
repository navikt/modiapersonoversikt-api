package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import java.lang.Double._

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.JournalforingScenario.journalforingsScenario
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.Utils._

import scala.concurrent.duration.DurationDouble


class JournalforingSimulation extends Simulation {

  // System properties
  val duration: Double = valueOf(System.getProperty("duration.minutes"))
  val users: Int = Integer.getInteger("journalforing.users")

  setUp(journalforingsScenario.inject(rampUsers(users) over (duration minutes))).protocols(httpProtocol)
}
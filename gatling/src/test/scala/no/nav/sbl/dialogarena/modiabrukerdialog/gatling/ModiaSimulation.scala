package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import java.lang.Double._

import io.gatling.core.Predef._
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.JournalforingScenario.journalforingsScenario
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.MeldingerSokScenario.meldingerSokScenario
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.SkrivestotteSokScenario.skrivestotteSokScenario

import scala.concurrent.duration.DurationDouble

class ModiaSimulation extends Simulation {

  val duration: Double = valueOf(System.getProperty("duration.minutes"))
  val meldingerSokUsers: Int = Integer.getInteger("meldingersok.users")
  val skrivestotteSokUsers: Int = Integer.getInteger("skrivestottesok.users")
  val journalforingUsers: Int = Integer.getInteger("journalforing.users")



  setUp(
    meldingerSokScenario.inject(rampUsers(meldingerSokUsers) over (duration minutes)),
    skrivestotteSokScenario.inject(rampUsers(skrivestotteSokUsers) over (duration minutes)),
    journalforingsScenario.inject(rampUsers(journalforingUsers) over (duration minutes))
  ).protocols(Utils.httpProtocol)
}

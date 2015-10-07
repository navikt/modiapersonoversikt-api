package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import io.gatling.core.Predef._
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.Utils._

object SkrivestotteSokScenario {
  val skrivestotteSokScenario = login("Søk i hjelpetekster scenario")
    .pause(1)
    .exec(sokChain("Søk i hjelpetekster", "/modiabrukerdialog/rest/skrivestotte/sok?fritekst=", query))
}

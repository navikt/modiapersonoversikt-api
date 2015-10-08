package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.Utils.{headers, login}

object MeldingerSokScenario {
  val meldingerSokScenario = login("Søk i meldinger scenario")
    .pause(1)
    .exec(
      http("indekser meldinger")
        .get("/modiabrukerdialog/rest/meldinger/${fnr}/indekser")
        .headers(headers))
    .exitHereIfFailed
    .exec(Utils.sokChain("Søk i meldinger", "/modiabrukerdialog/rest/meldinger/${fnr}/sok/", Utils.query))
}

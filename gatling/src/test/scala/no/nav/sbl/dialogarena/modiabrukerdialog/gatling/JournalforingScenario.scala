package no.nav.sbl.dialogarena.modiabrukerdialog.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import no.nav.sbl.dialogarena.modiabrukerdialog.gatling.Utils.{headers, login}

object JournalforingScenario {
  var journalforingsScenario = login("Hent saker")
    .pause(1)
    .exec(
      http("Hent sammensatte saker")
        .get("/modiabrukerdialog/rest/journalforing/${fnr}/saker/sammensatte")
        .headers(headers)
        .check(status.is(200))
    )
    .exec(
      http("Hent pensjon saker")
        .get("/modiabrukerdialog/rest/journalforing/${fnr}/saker/pensjon")
        .headers(headers)
        .check(status.is(200))
    )
}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.model

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.Publikumsmottak

class Publikumsmottak {
    var besoksadresse: Gateadresse
    var apningstider: List<Apningstid>

    constructor(publikumsmottak: Publikumsmottak) {
        this.besoksadresse = Gateadresse(
            publikumsmottak.besoeksadresse?.gatenavn,
            publikumsmottak.besoeksadresse?.husnummer,
            publikumsmottak.besoeksadresse?.husbokstav,
            publikumsmottak.besoeksadresse?.postnummer,
            publikumsmottak.besoeksadresse?.poststed
        )
        this.apningstider = publikumsmottak.apningstider.apningstider.map {
            Apningstid(
                it.ukedag.name,
                Klokkeslett(it.apentFra?.time, it.apentFra?.minutt, it.apentFra?.sekund),
                Klokkeslett(it.apentTil?.time, it.apentTil?.minutt, it.apentTil?.sekund)
            )
        }
    }
}

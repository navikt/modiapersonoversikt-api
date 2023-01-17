package no.nav.modiapersonoversikt.service.kontonummer

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.kontoregister.generated.apis.KontoregisterV1Api
import no.nav.modiapersonoversikt.consumer.kontoregister.generated.models.HentAktivKontoDTO

class KontonummerRegisterService(
    private val kontoregister: KontoregisterV1Api
) : KontonummerService {
    override fun hentKontonummer(fnr: Fnr): KontonummerService.Konto {
        val konto = kontoregister.hentAktivKonto(
            HentAktivKontoDTO(
                kontohaver = fnr.get()
            )
        )
        return konto.let {
            KontonummerService.Konto(
                kontonummer = it.kontonummer,
                banknavn = it.utenlandskKontoInfo?.banknavn,
                sistEndret = null, // Informasjon finnes ikke i API
                swift = it.utenlandskKontoInfo?.swiftBicKode,
                adresse = KontonummerService.Adresse(
                    linje1 = it.utenlandskKontoInfo?.bankadresse1 ?: "Ukjent adresse",
                    linje2 = it.utenlandskKontoInfo?.bankadresse2,
                    linje3 = it.utenlandskKontoInfo?.bankadresse3,
                ),
                bankkode = it.utenlandskKontoInfo?.bankkode,
                landkode = it.utenlandskKontoInfo?.bankLandkode,
                valutakode = it.utenlandskKontoInfo?.valutakode,
            )
        }
    }
}

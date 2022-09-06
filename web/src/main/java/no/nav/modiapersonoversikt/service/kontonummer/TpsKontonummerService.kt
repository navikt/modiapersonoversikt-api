package no.nav.modiapersonoversikt.service.kontonummer

import no.nav.common.types.identer.Fnr
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoNorge
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse

class TpsKontonummerService(
    private val tps: PersonV3
) : KontonummerService {
    override fun hentKontonummer(fnr: Fnr): KontonummerService.Konto? {
        val wsResponse: HentPersonResponse = tps.hentPerson(
            HentPersonRequest()
                .withAktoer(PersonIdent().withIdent(NorskIdent().withIdent(fnr.get())))
                .withInformasjonsbehov(
                    Informasjonsbehov.BANKKONTO,
                    Informasjonsbehov.SPORINGSINFORMASJON,
                )
        )

        val person = wsResponse.person
        val bruker: Bruker? = if (person is Bruker) person else null

        return bruker?.let {
            when (val bankkonto = bruker.bankkonto) {
                is BankkontoNorge -> KontonummerService.Konto(
                    kontonummer = bankkonto.bankkonto.bankkontonummer,
                    banknavn = bankkonto.bankkonto.banknavn,
                    sistEndret = sistEndret(bankkonto),
                )
                is BankkontoUtland -> KontonummerService.Konto(
                    kontonummer = bankkonto.bankkontoUtland.bankkontonummer,
                    banknavn = bankkonto.bankkontoUtland.banknavn,
                    sistEndret = sistEndret(bankkonto),
                    bankkode = bankkonto.bankkontoUtland.bankkode,
                    swift = bankkonto.bankkontoUtland.swift,
                    landkode = bankkonto.bankkontoUtland.landkode.value,
                    adresse = KontonummerService.Adresse(
                        linje1 = bankkonto.bankkontoUtland.bankadresse.adresselinje1 ?: "Ukjent adresse",
                        linje2 = bankkonto.bankkontoUtland.bankadresse.adresselinje2,
                        linje3 = bankkonto.bankkontoUtland.bankadresse.adresselinje3,
                    ),
                    valutakode = bankkonto.bankkontoUtland.valuta.value,
                )
                else -> null
            }
        }
    }

    private fun sistEndret(bankkonto: Bankkonto): KontonummerService.SistEndret? {
        return if (bankkonto.endretAv != null && bankkonto.endringstidspunkt != null) {
            KontonummerService.SistEndret(
                ident = bankkonto.endretAv,
                tidspunkt = bankkonto.endringstidspunkt
                    .toGregorianCalendar()
                    .toZonedDateTime()
                    .toLocalDateTime()
            )
        } else {
            null
        }
    }
}

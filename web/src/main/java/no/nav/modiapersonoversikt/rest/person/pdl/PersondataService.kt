package no.nav.modiapersonoversikt.rest.person.pdl

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollContext
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondataLite
import no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.modiapersonoversikt.service.dkif.Dkif
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse

interface PersondataService {
    fun hentPerson(fnr: String): Persondata.Data
}

class PersondataServiceImpl(
    private val pdl: PdlOppslagService,
    private val dkif: Dkif.Service,
    private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service,
    private val personV3: PersonV3,
    private val egenAnsattService: EgenAnsattService,
    private val tilgangskontroll: TilgangskontrollContext
) : PersondataService {
    val persondateFletter: PersondataFletter = TODO()
    override fun hentPerson(fnr: String): Persondata.Data {
        val persondata = requireNotNull(pdl.hentPersondata(fnr)) {
            "Fant ikke person med fnr $fnr"
        }
        val geografiskeTilknytning = Persondata.runCatching("PDL-GT") { pdl.hentGeografiskTilknyttning(fnr) }
        val navEnhet = hentNavEnhet(persondata, geografiskeTilknytning)
        val erEgenAnsatt = Persondata.runCatching("TPS-EGEN-ANSATT") { egenAnsattService.erEgenAnsatt(fnr) }

        val harTilgangTilKode6 = tilgangskontroll.harSaksbehandlerRolle("0000-GA-GOSYS_KODE6")
        val harTilgangTilKode7 = tilgangskontroll.harSaksbehandlerRolle("0000-GA-GOSYS_KODE7")
        val tredjepartsPerson = Persondata.runCatching("PDL") {
            persondata
                .findTredjepartsPersoner()
                .let { pdl.hentPersondataLite(it) }
                .map { it.saksbehandlerHarTilgangTilBruker(harTilgangTilKode6, harTilgangTilKode7) }
        }

        val dkifData = Persondata.runCatching("DKIF") { dkif.hentDigitalKontaktinformasjon(fnr) }
        val bankkonto = Persondata.runCatching("TPS") { hentBankkonto(fnr) }

        return persondateFletter.flettSammenData(
            PersondataFletter.Data(
                persondata,
                geografiskeTilknytning,
                erEgenAnsatt,
                navEnhet,
                dkifData,
                bankkonto,
                tredjepartsPerson
            )
        )
    }

    private fun hentBankkonto(fnr: String): HentPersonResponse {
        return personV3.hentPerson(
            HentPersonRequest()
                .withAktoer(PersonIdent().withIdent(NorskIdent().withIdent(fnr)))
                .withInformasjonsbehov(Informasjonsbehov.BANKKONTO)
        )
    }

    private fun hentNavEnhet(
        persondata: HentPersondata.Person,
        geografiskeTilknytning: Persondata.Result<String?>
    ): Persondata.Result<AnsattEnhet> {
        var diskresjonskode = ""
        val adressebeskyttelse = persondata.adressebeskyttelse
        for (beskyttelse in adressebeskyttelse) {
            if (beskyttelse.gradering == HentPersondata.AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND) {
                diskresjonskode = "SPSF"
                break
            } else if (beskyttelse.gradering == HentPersondata.AdressebeskyttelseGradering.STRENGT_FORTROLIG) {
                diskresjonskode = "SPSF"
                break
            } else if (beskyttelse.gradering == HentPersondata.AdressebeskyttelseGradering.FORTROLIG) {
                diskresjonskode = "SPFO"
                break
            }
        }
        return geografiskeTilknytning
            .map("NORG") {
                organisasjonEnhetV2Service
                    .finnNAVKontor(it, diskresjonskode)
                    .orElseThrow()
            }
    }

    private fun HentPersondata.Person.findTredjepartsPersoner(): List<String> {
        return setOf(
            *this.fullmakt.map { it.motpartsPersonident }.toTypedArray(),
            *this.vergemaalEllerFremtidsfullmakt.mapNotNull {
                it.vergeEllerFullmektig.motpartsPersonident
            }.toTypedArray(),
            *this.foreldreansvar.mapNotNull { it.ansvarlig }.toTypedArray(),
            *this.foreldreansvar.mapNotNull { it.ansvarssubjekt }.toTypedArray()
        ).toList()
    }

    private fun HentPersondataLite.HentPersonBolkResult.saksbehandlerHarTilgangTilBruker(
        harTilgangTilKode6: Boolean,
        harTilgangTilKode7: Boolean
    ): HentPersondataLite.HentPersonBolkResult {
        val person = this.person ?: return this
        var kode = 0
        val adressebeskyttelse = person.adressebeskyttelse
        for (beskyttelse in adressebeskyttelse) {
            if (beskyttelse.gradering == HentPersondataLite.AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND) {
                kode = 6
                break
            } else if (beskyttelse.gradering == HentPersondataLite.AdressebeskyttelseGradering.STRENGT_FORTROLIG) {
                kode = 6
                break
            } else if (beskyttelse.gradering == HentPersondataLite.AdressebeskyttelseGradering.FORTROLIG) {
                kode = 7
                break
            }
        }

        return when(kode) {
            6 -> if (harTilgangTilKode6) this else this.fjernAdresseInformasjon()
            7 -> if (harTilgangTilKode7) this else this.fjernAdresseInformasjon()
            else -> this
        }
    }

    private fun HentPersondataLite.HentPersonBolkResult.fjernAdresseInformasjon() = HentPersondataLite.HentPersonBolkResult(
        ident = this.ident,
        person = this.person?.let {
            HentPersondataLite.Person(
                navn = it.navn,
                adressebeskyttelse = it.adressebeskyttelse,
                bostedsadresse = emptyList()
            )
        }
    )
}



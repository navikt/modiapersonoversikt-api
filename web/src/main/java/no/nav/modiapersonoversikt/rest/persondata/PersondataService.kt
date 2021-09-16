package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.modiapersonoversikt.service.dkif.Dkif
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse

interface PersondataService {
    fun hentPerson(fnr: String): Persondata.Data

    data class Tilganger(
        val kode6: Boolean,
        val kode7: Boolean
    )
}

class PersondataServiceImpl(
    private val pdl: PdlOppslagService,
    private val dkif: Dkif.Service,
    private val organisasjonEnhetV2Service: OrganisasjonEnhetV2Service,
    private val personV3: PersonV3,
    private val egenAnsattService: EgenAnsattService,
    private val tilgangskontroll: Tilgangskontroll,
    kodeverk: EnhetligKodeverk.Service
) : PersondataService {
    val persondataFletter = PersondataFletter(kodeverk)
    val tredjepartspersonMapper = TredjepartspersonMapper(kodeverk)

    override fun hentPerson(fnr: String): Persondata.Data {
        val persondata = requireNotNull(pdl.hentPersondata(fnr)) {
            "Fant ikke person med fnr $fnr"
        }
        val geografiskeTilknytning = PersondataResult.runCatching("PDL-GT") { pdl.hentGeografiskTilknyttning(fnr) }
        val navEnhet = hentNavEnhet(persondata, geografiskeTilknytning)
        val erEgenAnsatt = PersondataResult.runCatching("TPS-EGEN-ANSATT") { egenAnsattService.erEgenAnsatt(fnr) }
        val tilganger = PersondataResult
            .runCatching("TILGANGSKONTROLL") { hentTilganger() }
            .getOrElse(PersondataService.Tilganger(kode6 = false, kode7 = false))
        val tredjepartsPerson = PersondataResult.runCatching("PDL") {
            persondata
                .findTredjepartsPersoner()
                .let { pdl.hentPersondataLite(it) }
                .map { tredjepartspersonMapper.lagTredjepartsperson(it, tilganger) }
                .associateBy { it.fnr }
        }

        val dkifData = PersondataResult.runCatching("DKIF") { dkif.hentDigitalKontaktinformasjon(fnr) }
        val bankkonto = PersondataResult.runCatching("TPS") { hentBankkonto(fnr) }

        return persondataFletter.flettSammenData(
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
        geografiskeTilknytning: PersondataResult<String?>
    ): PersondataResult<AnsattEnhet> {
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

    private fun hentTilganger() = PersondataService.Tilganger(
        kode6 = tilgangskontroll.context().harSaksbehandlerRolle("0000-GA-GOSYS_KODE6"),
        kode7 = tilgangskontroll.context().harSaksbehandlerRolle("0000-GA-GOSYS_KODE7")
    )
}

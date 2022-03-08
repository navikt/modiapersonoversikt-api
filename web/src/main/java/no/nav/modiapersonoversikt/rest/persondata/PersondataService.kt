package no.nav.modiapersonoversikt.rest.persondata

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata
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
    fun hentPerson(personIdent: String): Persondata.Data
    fun hentGeografiskTilknytning(personIdent: String): String?
    fun hentNavEnhet(personIdent: String): Persondata.Enhet?
    fun hentAdressebeskyttelse(personIdent: String): List<Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse>>

    data class Tilganger(
        val kode6: Boolean,
        val kode7: Boolean
    )
}

class PersondataServiceImpl(
    private val pdl: PdlOppslagService,
    private val dkif: Dkif.Service,
    private val norgApi: NorgApi,
    private val personV3: PersonV3,
    private val egenAnsattService: EgenAnsattService,
    private val skjermedePersonerApi: SkjermedePersonerApi,
    private val tilgangskontroll: Tilgangskontroll,
    kodeverk: EnhetligKodeverk.Service
) : PersondataService {
    val persondataFletter = PersondataFletter(kodeverk)
    val tredjepartspersonMapper = TredjepartspersonMapper(kodeverk)

    override fun hentPerson(personIdent: String): Persondata.Data {
        val persondata = requireNotNull(pdl.hentPersondata(personIdent)) {
            "Fant ikke person med personIdent $personIdent"
        }
        val geografiskeTilknytning = PersondataResult.runCatching("PDL-GT") { pdl.hentGeografiskTilknyttning(personIdent) }
        val adressebeskyttelse = persondataFletter.hentAdressebeskyttelse(persondata.adressebeskyttelse)
        val navEnhet = hentNavEnhetFraNorg(adressebeskyttelse, geografiskeTilknytning)
        val erEgenAnsatt = PersondataResult.runCatching("TPS-EGEN-ANSATT") { egenAnsattService.erEgenAnsatt(personIdent) }
        val erSkjermetPerson = PersondataResult.runCatching("SKJERMEDE-PERSONER") { skjermedePersonerApi.erSkjermetPerson(personIdent) }
        val tilganger = PersondataResult
            .runCatching("TILGANGSKONTROLL") { hentTilganger() }
            .getOrElse(PersondataService.Tilganger(kode6 = false, kode7 = false))
        val tredjepartsPerson = PersondataResult.runCatching("PDL") {
            persondata
                .findTredjepartsPersoner()
                .let { pdl.hentTredjepartspersondata(it) }
                .mapNotNull { tredjepartspersonMapper.lagTredjepartsperson(it.ident, it.person, tilganger) }
                .associateBy { it.fnr }
        }

        val dkifData = PersondataResult.runCatching("DKIF") { dkif.hentDigitalKontaktinformasjon(personIdent) }
        val bankkonto = PersondataResult.runCatching("TPS") { hentBankkonto(personIdent) }

        return persondataFletter.flettSammenData(
            PersondataFletter.Data(
                personIdent,
                persondata,
                geografiskeTilknytning,
                erEgenAnsatt,
                erSkjermetPerson,
                navEnhet,
                dkifData,
                bankkonto,
                tredjepartsPerson
            )
        )
    }

    override fun hentGeografiskTilknytning(personIdent: String): String? {
        return PersondataResult.runCatching("PDL-GT") { pdl.hentGeografiskTilknyttning(personIdent) }.getOrNull()
    }

    override fun hentNavEnhet(personIdent: String): Persondata.Enhet? {
        val geografiskeTilknytning = PersondataResult.runCatching("PDL-GT") { pdl.hentGeografiskTilknyttning(personIdent) }
        val adressebeskyttelse = hentAdressebeskyttelse(personIdent)
        return hentNavEnhetFraNorg(adressebeskyttelse, geografiskeTilknytning).let { persondataFletter.hentNavEnhet(it) }
    }

    override fun hentAdressebeskyttelse(personIdent: String): List<Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse>> {
        return pdl.hentTredjepartspersondata(listOf(personIdent)).mapNotNull {
            tredjepartspersonMapper.lagTredjepartsperson(
                ident = it.ident,
                person = it.person,
                tilganger = PersondataService.Tilganger(false, false)
            )
        }.firstOrNull()?.adressebeskyttelse ?: emptyList()
    }

    private fun hentBankkonto(fnr: String): HentPersonResponse {
        return personV3.hentPerson(
            HentPersonRequest()
                .withAktoer(PersonIdent().withIdent(NorskIdent().withIdent(fnr)))
                .withInformasjonsbehov(
                    Informasjonsbehov.BANKKONTO,
                    Informasjonsbehov.SPORINGSINFORMASJON
                )
        )
    }

    private fun hentNavEnhetFraNorg(
        adressebeskyttelse: List<Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse>>,
        geografiskeTilknytning: PersondataResult<String?>
    ): PersondataResult<NorgDomain.EnhetKontaktinformasjon?> {
        val gt: String = geografiskeTilknytning
            .map { it ?: "" }
            .getOrElse("")

        var diskresjonskode: NorgDomain.DiskresjonsKode? = null
        for (beskyttelse in adressebeskyttelse) {
            if (beskyttelse.kode == Persondata.AdresseBeskyttelse.KODE6) {
                diskresjonskode = NorgDomain.DiskresjonsKode.SPSF
                break
            } else if (beskyttelse.kode == Persondata.AdresseBeskyttelse.KODE6_UTLAND) {
                diskresjonskode = NorgDomain.DiskresjonsKode.SPSF
                break
            } else if (beskyttelse.kode == Persondata.AdresseBeskyttelse.KODE7) {
                diskresjonskode = NorgDomain.DiskresjonsKode.SPFO
                break
            }
        }
        return PersondataResult.runCatching("NORG") {
            norgApi
                .finnNavKontor(gt, diskresjonskode)
                ?.enhetId
        }
            .map("NORG Kontaktinformasjon") {
                it?.let { enhetId -> norgApi.hentKontaktinfo(EnhetId(enhetId)) }
            }
    }

    private fun HentPersondata.Person.findTredjepartsPersoner(): List<String> {
        return setOf(
            *this.fullmakt.map { it.motpartsPersonident }.toTypedArray(),
            *this.vergemaalEllerFremtidsfullmakt.mapNotNull {
                it.vergeEllerFullmektig.motpartsPersonident
            }.toTypedArray(),
            *this.foreldreansvar.mapNotNull { it.ansvarlig }.toTypedArray(),
            *this.foreldreansvar.mapNotNull { it.ansvarssubjekt }.toTypedArray(),
            *this.sivilstand.mapNotNull { it.relatertVedSivilstand }.toTypedArray(),
            *this.forelderBarnRelasjon.mapNotNull { it.relatertPersonsIdent }.toTypedArray(),
            *this.kontaktinformasjonForDoedsbo.mapNotNull { it.personSomKontakt?.identifikasjonsnummer }.toTypedArray()
        ).toList()
    }

    private fun hentTilganger() = PersondataService.Tilganger(
        kode6 = tilgangskontroll.context().harSaksbehandlerRolle("0000-GA-Strengt_Fortrolig_Adresse"),
        kode7 = tilgangskontroll.context().harSaksbehandlerRolle("0000-GA-Fortrolig_Adresse")
    )
}

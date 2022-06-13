package no.nav.modiapersonoversikt.rest.persondata

import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.dkif.Dkif
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilBrukerMedKode6Policy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilBrukerMedKode7Policy
import no.nav.modiapersonoversikt.rest.persondata.PersondataResult.InformasjonElement
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse

interface PersondataService {
    fun hentPerson(personIdent: String): Persondata.Data

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
    private val skjermedePersonerApi: SkjermedePersonerApi,
    private val policyEnforcementPoint: Kabac.PolicyEnforcementPoint,
    kodeverk: EnhetligKodeverk.Service
) : PersondataService {
    val persondataFletter = PersondataFletter(kodeverk)
    val tredjepartspersonMapper = TredjepartspersonMapper(kodeverk)

    override fun hentPerson(personIdent: String): Persondata.Data {
        val persondata = requireNotNull(pdl.hentPersondata(personIdent)) {
            "Fant ikke person med personIdent $personIdent"
        }

        val geografiskeTilknytning = PersondataResult.runCatching(InformasjonElement.PDL_GT) { pdl.hentGeografiskTilknyttning(personIdent) }
        val adressebeskyttelse = persondataFletter.hentAdressebeskyttelse(persondata.adressebeskyttelse)
        val navEnhet = hentNavEnhetFraNorg(adressebeskyttelse, geografiskeTilknytning)
        val erEgenAnsatt = PersondataResult.runCatching(InformasjonElement.EGEN_ANSATT) {
            skjermedePersonerApi.erSkjermetPerson(
                Fnr(personIdent)
            )
        }
        val tilganger = PersondataResult
            .runCatching(InformasjonElement.VEILEDER_ROLLER) { hentTilganger() }
            .getOrElse(PersondataService.Tilganger(kode6 = false, kode7 = false))
        val kontaktinformasjonTredjepartsperson = PersondataResult.runCatching(InformasjonElement.DKIF_TREDJEPARTSPERSONER) {
            persondata
                .findKontaktinformasjonTredjepartspersoner()
                .associateWith { dkif.hentDigitalKontaktinformasjon(it) }
                .mapValues { tredjepartspersonMapper.tilKontaktinformasjonTredjepartsperson(it.value) }
        }
        val kontaktinformasjonTredjepartspersonMap = kontaktinformasjonTredjepartsperson.getOrElse(emptyMap())
        val tredjepartsPerson = PersondataResult.runCatching(InformasjonElement.PDL_TREDJEPARTSPERSONER) {
            persondata
                .findTredjepartsPersoner()
                .let { pdl.hentTredjepartspersondata(it) }
                .mapNotNull { tredjepartspersonMapper.lagTredjepartsperson(it.ident, it.person, tilganger, kontaktinformasjonTredjepartspersonMap[it.ident]) }
                .associateBy { it.fnr }
        }

        val dkifData = PersondataResult.runCatching(InformasjonElement.DKIF) { dkif.hentDigitalKontaktinformasjon(personIdent) }
        val bankkonto = PersondataResult.runCatching(InformasjonElement.BANKKONTO) { hentBankkonto(personIdent) }

        return persondataFletter.flettSammenData(
            PersondataFletter.Data(
                personIdent,
                persondata,
                geografiskeTilknytning,
                erEgenAnsatt,
                navEnhet,
                dkifData,
                bankkonto,
                tredjepartsPerson,
                kontaktinformasjonTredjepartsperson
            )
        )
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
        return PersondataResult.runCatching(InformasjonElement.NORG_NAVKONTOR) {
            norgApi
                .finnNavKontor(gt, diskresjonskode)
                ?.enhetId
        }
            .map(InformasjonElement.NORG_KONTAKTINFORMASJON) {
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

    private fun HentPersondata.Person.findKontaktinformasjonTredjepartspersoner(): List<String> {
        return setOf(
            *this.fullmakt.map { it.motpartsPersonident }.toTypedArray(),
        ).toList()
    }

    private fun hentTilganger(): PersondataService.Tilganger {
        val ctx = policyEnforcementPoint.createEvaluationContext()
        val tilgangKode6 = policyEnforcementPoint.evaluatePolicyWithContext(
            ctx = ctx,
            policy = TilgangTilBrukerMedKode6Policy
        )
        val tilgangKode7 = policyEnforcementPoint.evaluatePolicyWithContext(
            ctx = ctx,
            policy = TilgangTilBrukerMedKode7Policy
        )
        return PersondataService.Tilganger(
            kode6 = tilgangKode6.type == Decision.Type.PERMIT,
            kode7 = tilgangKode7.type == Decision.Type.PERMIT
        )
    }
}

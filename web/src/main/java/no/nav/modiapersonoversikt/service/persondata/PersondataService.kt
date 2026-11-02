package no.nav.modiapersonoversikt.service.persondata

import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Person
import no.nav.modiapersonoversikt.consumer.representasjon.RepresentasjonApi
import no.nav.modiapersonoversikt.consumer.representasjon.generated.models.FullmaktDto
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.Tilgangsmaskinen
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.TredjepersonTilgang
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilBrukerMedSkjermingPolicy
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.kontonummer.KontonummerService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.persondata.PersondataResult.InformasjonElement
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.logging.TjenestekallLogger

interface PersondataService {
    fun hentPerson(personIdent: String): Persondata.Data
}

class PersondataServiceImpl(
    private val pdl: PdlOppslagService,
    private val representasjonApi: RepresentasjonApi,
    private val krrService: Krr.Service,
    private val norgApi: NorgApi,
    private val skjermedePersonerApi: SkjermedePersonerApi,
    private val kontonummerService: KontonummerService,
    private val oppfolgingService: ArbeidsrettetOppfolging.Service,
    private val policyEnforcementPoint: Kabac.PolicyEnforcementPoint,
    kodeverk: EnhetligKodeverk.Service,
    tjenestekallLogger: TjenestekallLogger,
    private val tilgangsmaskinen: Tilgangsmaskinen,
) : PersondataService {
    private val persondataFletter = PersondataFletter(kodeverk, tjenestekallLogger)
    private val tredjepartspersonMapper = TredjepartspersonMapper(kodeverk)

    override fun hentPerson(personIdent: String): Persondata.Data {
        val persondataResult = pdl.hentPersondata(personIdent)
        val persondata =
            requireNotNull(persondataResult?.hentPerson) {
                "Fant ikke person med personIdent $personIdent"
            }

        val geografiskeTilknytning = hentGeografiskTilknyttning(persondataResult)
        val adressebeskyttelse = persondataFletter.hentAdressebeskyttelse(persondata.adressebeskyttelse)
        val navEnhet = hentNavEnhetFraNorg(adressebeskyttelse, geografiskeTilknytning)
        val erEgenAnsatt =
            PersondataResult.runCatching(InformasjonElement.EGEN_ANSATT) {
                skjermedePersonerApi.erSkjermetPerson(
                    Fnr(personIdent),
                )
            }
        val harTilgangTilSkjermetPerson =
            PersondataResult
                .runCatching(InformasjonElement.VEILEDER_ROLLER) { harTilgangTilSkjermetPerson() }
                .getOrElse(false)
        val fullmektige =
            PersondataResult.runCatching(InformasjonElement.REPR_API) {
                representasjonApi.hentFullmakterForFullmaktsgiver(Fnr(personIdent))
            }
        val fullmektigIdenter = finnFullmektigIdenter(fullmektige)
        val kandidatIdenter = persondata.findTredjepartsPersoner(fullmektigIdenter)
        val tredjepersonIdenter = kandidatIdenter.filter { it.isNotBlank() }
        val tilganger = tilgangsmaskinen.sjekkTilganger(AuthContextUtils.requireNavIdent(), tredjepersonIdenter)
        val godkjente = tredjepersonIdenter.filter { tilganger[it] == TredjepersonTilgang.GODKJENT }
        val pdlTredjepersoner =
            PersondataResult.runCatching(InformasjonElement.PDL_TREDJEPARTSPERSONER) {
                if (godkjente.isEmpty()) emptyList() else pdl.hentTredjepartspersondata(godkjente)
            }
        val funn = pdlTredjepersoner.getOrElse(emptyList()).groupBy { it.ident }
        val tillatte = godkjente.filter { ident -> funn[ident]?.singleOrNull()?.person != null }.toSet()
        val kontaktinformasjonTredjepartsperson =
            PersondataResult.runCatching(InformasjonElement.DKIF_TREDJEPARTSPERSONER) {
                fullmektigIdenter
                    .filter { it in tillatte }
                    .associateWith { krrService.hentDigitalKontaktinformasjon(it) }
                    .mapValues { tredjepartspersonMapper.tilKontaktinformasjonTredjepartsperson(it.value) }
            }
        val kontaktinformasjonTredjepartspersonMap = kontaktinformasjonTredjepartsperson.getOrElse(emptyMap())
        val tredjepartsPerson =
            pdlTredjepersoner.map { personer ->
                personer
                    .filter { it.ident in tillatte }
                    .mapNotNull {
                        tredjepartspersonMapper.lagTredjepartsperson(
                            it.ident,
                            it.person,
                            kontaktinformasjonTredjepartspersonMap[it.ident],
                        )
                    }.associateBy { it.fnr }
            }

        val dkifData =
            PersondataResult.runCatching(InformasjonElement.DKIF) { krrService.hentDigitalKontaktinformasjon(personIdent) }
        val bankkonto =
            PersondataResult.runCatching(InformasjonElement.BANKKONTO) {
                kontonummerService.hentKontonummer(Fnr(personIdent))
            }
        val oppfolging =
            PersondataResult.runCatching(InformasjonElement.OPPFOLGING) {
                oppfolgingService.hentOppfolgingsinfo(Fnr(personIdent))
            }

        return persondataFletter.flettSammenData(
            PersondataFletter.Data(
                personIdent,
                persondata,
                fullmektige,
                geografiskeTilknytning,
                erEgenAnsatt,
                navEnhet,
                dkifData,
                bankkonto,
                oppfolging,
                tredjepartsPerson,
                kontaktinformasjonTredjepartsperson,
                harTilgangTilSkjermetPerson,
                skjulingsaarsaker =
                    tredjepersonIdenter
                        .filterNot { it in tillatte }
                        .associateWith {
                            if (tilganger[it] == TredjepersonTilgang.AVVIST) {
                                Persondata.Skjulingsaarsak.MANGLER_TILGANG
                            } else {
                                Persondata.Skjulingsaarsak.UAVKLART
                            }
                        },
                tilgangsfeil = tredjepersonIdenter.any { tilganger[it] == null || tilganger[it] == TredjepersonTilgang.UAVKLART },
            ),
        )
    }

    private fun hentGeografiskTilknyttning(persondata: HentPersondata.Result?): PersondataResult<String?> {
        val personDodsfall = persondata?.hentPerson?.doedsfall ?: emptyList()
        if (personDodsfall.isNotEmpty()) {
            return PersondataResult.NotRelevant()
        }

        val geografiskeTilknytning =
            persondata
                ?.hentGeografiskTilknytning
                ?.run { gtBydel ?: gtKommune ?: gtLand }

        return PersondataResult.of(geografiskeTilknytning)
    }

    private val gtSomBurdeHaByDel = listOf("0301", "4601", "5001", "1103")

    private fun erGyldigGT(gt: String?): Boolean =
        when {
            gt == null -> false
            gtSomBurdeHaByDel.contains(gt) -> false
            else -> true
        }

    fun hentNavEnhetFraNorg(
        adressebeskyttelse: List<Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse>>,
        geografiskeTilknytning: PersondataResult<String?>,
    ): PersondataResult<NorgDomain.EnhetKontaktinformasjon?> {
        val gt: String =
            geografiskeTilknytning
                .flatMap {
                    if (erGyldigGT(it.getOrNull())) {
                        it
                    } else {
                        PersondataResult.NotRelevant()
                    }
                }.fold(
                    onSuccess = { it ?: "" },
                    onNotRelevant = { null },
                    onFailure = { _, _ -> "" },
                ) ?: return PersondataResult.NotRelevant()

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
        return PersondataResult
            .runCatching(InformasjonElement.NORG_NAVKONTOR) {
                norgApi
                    .finnNavKontor(gt, diskresjonskode)
                    ?.enhetId
            }.map(InformasjonElement.NORG_KONTAKTINFORMASJON) {
                it?.let { enhetId -> norgApi.hentKontaktinfo(EnhetId(enhetId)) }
            }
    }

    private fun Person.findTredjepartsPersoner(andrePersoner: List<String>): List<String> =
        setOf(
            *this.vergemaalEllerFremtidsfullmakt
                .mapNotNull {
                    it.vergeEllerFullmektig.motpartsPersonident
                }.toTypedArray(),
            *this.foreldreansvar.mapNotNull { it.ansvarlig }.toTypedArray(),
            *this.foreldreansvar.mapNotNull { it.ansvarssubjekt }.toTypedArray(),
            *this.sivilstand.mapNotNull { it.relatertVedSivilstand }.toTypedArray(),
            *this.forelderBarnRelasjon.mapNotNull { it.relatertPersonsIdent }.toTypedArray(),
            *this.kontaktinformasjonForDoedsbo.mapNotNull { it.personSomKontakt?.identifikasjonsnummer }.toTypedArray(),
            *andrePersoner.toTypedArray(),
        ).toList()

    internal fun finnFullmektigIdenter(fullmektige: PersondataResult<List<FullmaktDto>>): List<String> =
        fullmektige.getOrElse(emptyList()).map { it.fullmektig }.distinct()

    private fun harTilgangTilSkjermetPerson(): Boolean {
        val ctx = policyEnforcementPoint.createEvaluationContext()
        val tilgangSkjermetPerson =
            policyEnforcementPoint.evaluatePolicyWithContext(
                ctx = ctx,
                policy = TilgangTilBrukerMedSkjermingPolicy,
            )
        return tilgangSkjermetPerson.type == Decision.Type.PERMIT
    }
}

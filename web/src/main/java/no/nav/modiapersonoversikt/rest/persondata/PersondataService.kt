package no.nav.modiapersonoversikt.rest.persondata

import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilBrukerMedKode6Policy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilBrukerMedKode7Policy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilBrukerMedSkjermingPolicy
import no.nav.modiapersonoversikt.rest.persondata.PersondataResult.InformasjonElement
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.kontonummer.KontonummerService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac

interface PersondataService {
    fun hentPerson(personIdent: String): Persondata.Data

    data class Tilganger(
        val kode6: Boolean,
        val kode7: Boolean,
    )
}

class PersondataServiceImpl(
    private val pdl: PdlOppslagService,
    private val krrService: Krr.Service,
    private val norgApi: NorgApi,
    private val skjermedePersonerApi: SkjermedePersonerApi,
    private val kontonummerService: KontonummerService,
    private val oppfolgingService: ArbeidsrettetOppfolging.Service,
    private val policyEnforcementPoint: Kabac.PolicyEnforcementPoint,
    kodeverk: EnhetligKodeverk.Service,
) : PersondataService {
    private val persondataFletter = PersondataFletter(kodeverk)
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
        val tilganger =
            PersondataResult
                .runCatching(InformasjonElement.VEILEDER_ROLLER) { hentTilganger() }
                .getOrElse(PersondataService.Tilganger(kode6 = false, kode7 = false))
        val kontaktinformasjonTredjepartsperson =
            PersondataResult.runCatching(InformasjonElement.DKIF_TREDJEPARTSPERSONER) {
                persondata
                    .findKontaktinformasjonTredjepartspersoner()
                    .associateWith { krrService.hentDigitalKontaktinformasjon(it) }
                    .mapValues { tredjepartspersonMapper.tilKontaktinformasjonTredjepartsperson(it.value) }
            }
        val kontaktinformasjonTredjepartspersonMap = kontaktinformasjonTredjepartsperson.getOrElse(emptyMap())
        val tredjepartsPerson =
            PersondataResult.runCatching(InformasjonElement.PDL_TREDJEPARTSPERSONER) {
                persondata
                    .findTredjepartsPersoner()
                    .let { pdl.hentTredjepartspersondata(it) }
                    .mapNotNull {
                        tredjepartspersonMapper.lagTredjepartsperson(
                            it.ident,
                            it.person,
                            tilganger,
                            kontaktinformasjonTredjepartspersonMap[it.ident],
                        )
                    }
                    .associateBy { it.fnr }
            }

        val dkifData =
            PersondataResult.runCatching(InformasjonElement.DKIF) { krrService.hentDigitalKontaktinformasjon(personIdent) }
        val bankkonto =
            PersondataResult.runCatching(InformasjonElement.BANKKONTO) {
                kontonummerService.hentKontonummer(Fnr(personIdent))
            }
        val oppfolging =
            PersondataResult.runCatching(InformasjonElement.OPPFOLGING) {
                oppfolgingService.hentOppfolgingStatus(Fnr(personIdent))
            }

        return persondataFletter.flettSammenData(
            PersondataFletter.Data(
                personIdent,
                persondata,
                geografiskeTilknytning,
                erEgenAnsatt,
                navEnhet,
                dkifData,
                bankkonto,
                oppfolging,
                tredjepartsPerson,
                kontaktinformasjonTredjepartsperson,
                harTilgangTilSkjermetPerson,
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

    private fun erGyldigGT(gt: String?): Boolean {
        return when {
            gt == null -> false
            gtSomBurdeHaByDel.contains(gt) -> false
            else -> true
        }
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
                }
                .fold(
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
            *this.kontaktinformasjonForDoedsbo.mapNotNull { it.personSomKontakt?.identifikasjonsnummer }.toTypedArray(),
        ).toList()
    }

    private fun HentPersondata.Person.findKontaktinformasjonTredjepartspersoner(): List<String> {
        return setOf(
            *this.fullmakt.map { it.motpartsPersonident }.toTypedArray(),
        ).toList()
    }

    private fun hentTilganger(): PersondataService.Tilganger {
        val ctx = policyEnforcementPoint.createEvaluationContext()
        val tilgangKode6 =
            policyEnforcementPoint.evaluatePolicyWithContext(
                ctx = ctx,
                policy = TilgangTilBrukerMedKode6Policy,
            )
        val tilgangKode7 =
            policyEnforcementPoint.evaluatePolicyWithContext(
                ctx = ctx,
                policy = TilgangTilBrukerMedKode7Policy,
            )
        return PersondataService.Tilganger(
            kode6 = tilgangKode6.type == Decision.Type.PERMIT,
            kode7 = tilgangKode7.type == Decision.Type.PERMIT,
        )
    }

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

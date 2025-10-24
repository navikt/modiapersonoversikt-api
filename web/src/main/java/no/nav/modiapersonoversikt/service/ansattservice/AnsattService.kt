package no.nav.modiapersonoversikt.service.ansattservice

import no.nav.common.client.nom.NomClient
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.service.ansattservice.domain.AnsattEnhet
import no.nav.modiapersonoversikt.service.azure.AzureADService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.Exception

interface AnsattService {
    fun hentEnhetsliste(): List<AnsattEnhet>

    fun hentEnhetsliste(ident: NavIdent): List<AnsattEnhet>

    fun hentEnhetIds(ident: NavIdent): List<EnhetId>

    fun hentVeileder(ident: NavIdent): Veileder

    fun hentVeiledere(identer: List<NavIdent>): Map<NavIdent, Veileder>

    fun hentVeilederRoller(ident: NavIdent): RolleListe

    fun hentAnsattFagomrader(ident: String): Set<String>

    fun ansatteForEnhet(enhet: AnsattEnhet): List<Ansatt>
}

class AnsattServiceImpl
    @Autowired
    constructor(
        private val norgApi: NorgApi,
        private val nomClient: NomClient,
        private val azureADService: AzureADService,
    ) : AnsattService {
        private val log = LoggerFactory.getLogger(AnsattServiceImpl::class.java)

        override fun hentEnhetsliste(): List<AnsattEnhet> =
            AuthContextUtils
                .getIdent()
                .map { hentEnhetsliste(NavIdent(it)) }
                .orElse(emptyList())

        override fun hentEnhetsliste(ident: NavIdent): List<AnsattEnhet> {
            val enheter = azureADService.hentEnheterForVeileder(ident)
            return enheter
                .map {
                    val enhet = norgApi.hentEnheter().get(it)
                    AnsattEnhet(it.get(), enhet?.enhetNavn ?: "UKJENT")
                }
        }

        override fun hentEnhetIds(ident: NavIdent): List<EnhetId> = azureADService.hentEnheterForVeileder(ident)

        override fun hentVeileder(ident: NavIdent): Veileder =
            hentVeiledere(listOf(ident))
                .getOrDefault(ident, Veileder("", "", ident.get()))

        override fun hentVeiledere(identer: List<NavIdent>): Map<NavIdent, Veileder> =
            nomClient
                .runCatching { finnNavn(identer) }
                .getOrDefault(emptyList())
                .associateBy { it.navIdent }
                .mapValues { (_, value) ->
                    Veileder(
                        ident = value.navIdent.get(),
                        fornavn = value.fornavn,
                        etternavn = value.etternavn,
                    )
                }

        override fun hentVeilederRoller(ident: NavIdent): RolleListe = RolleListe(azureADService.hentRollerForVeileder(ident).toSet())

        override fun hentAnsattFagomrader(ident: String): Set<String> = azureADService.hentTemaerForVeileder(NavIdent(ident)).toSet()

        override fun ansatteForEnhet(enhet: AnsattEnhet): List<Ansatt> =
            try {
                azureADService.hentAnsatteForEnhet(EnhetId(enhet.enhetId))
            } catch (e: Exception) {
                log.error("Får ikke hentet ansatte for enhet", e)
                emptyList()
            }
    }

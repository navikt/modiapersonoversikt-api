package no.nav.modiapersonoversikt.service.ansattservice

import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.nom.NomClient
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.service.ansattservice.domain.AnsattEnhet
import no.nav.modiapersonoversikt.service.azure.AzureADService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.Exception

interface AnsattService {
    fun hentEnhetsliste(ident: NavIdent): List<AnsattEnhet>

    fun hentVeileder(ident: NavIdent): Veileder

    fun hentVeiledere(identer: List<NavIdent>): Map<NavIdent, Veileder>

    fun hentVeilederRoller(ident: NavIdent): RolleListe

    fun hentAnsattFagomrader(
        ident: String,
        enhet: String,
    ): Set<String>

    fun ansatteForEnhet(enhet: AnsattEnhet): List<Ansatt>
}

class AnsattServiceImpl
    @Autowired
    constructor(
        private val axsys: AxsysClient,
        private val nomClient: NomClient,
        private val azureADService: AzureADService,
    ) : AnsattService {
        private val log = LoggerFactory.getLogger(AnsattServiceImpl::class.java)

        // TODO: Hent enheter fra MS graph
        override fun hentEnhetsliste(ident: NavIdent): List<AnsattEnhet> =
            (axsys.hentTilganger(ident) ?: emptyList())
                .map { AnsattEnhet(it.enhetId.get(), it.navn ?: "UKJENT") }

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

        override fun hentVeilederRoller(ident: NavIdent): RolleListe = RolleListe(azureADService.hentRollerForVeileder(ident))

        override fun hentAnsattFagomrader(
            ident: String,
            enhet: String,
        ): Set<String> =
            axsys
                .runCatching {
                    hentTilganger(NavIdent(ident))
                        .find {
                            it.enhetId.get() == enhet
                        }?.temaer
                        ?.toSet()
                        ?: emptySet()
                }.getOrElse {
                    log.error("Klarte ikke å hente ansatt fagområder for $ident $enhet", it)
                    emptySet()
                }

        override fun ansatteForEnhet(enhet: AnsattEnhet): List<Ansatt> {
            return try {
                val gruppe = azureADService.hentEnhetGruppe(enhet.enhetId)
                if (gruppe == null) {
                    log.error("Finner ikke gruppe for enhet ${enhet.enhetId}")
                    return emptyList()
                }
                azureADService.hentAnsatteForEnhet(enhet.enhetId, gruppe.gruppeId)
            } catch (e: Exception) {
                log.error("Får ikke hentet ansatte for enhet ${enhet.enhetId}", e)
                emptyList()
            }
        }
    }

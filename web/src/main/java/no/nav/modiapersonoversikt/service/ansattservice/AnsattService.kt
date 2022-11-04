package no.nav.modiapersonoversikt.service.ansattservice

import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.nom.NomClient
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.service.ansattservice.domain.AnsattEnhet
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.Exception

interface AnsattService {
    fun hentEnhetsliste(): List<AnsattEnhet>
    fun hentEnhetsliste(ident: NavIdent): List<AnsattEnhet>
    fun hentVeileder(ident: NavIdent): Veileder
    fun hentVeiledere(identer: List<NavIdent>): Map<NavIdent, Veileder>
    fun hentVeilederRoller(ident: NavIdent): RolleListe
    fun hentAnsattFagomrader(ident: String, enhet: String): Set<String>
    fun ansatteForEnhet(enhet: AnsattEnhet): List<Ansatt>
}

class AnsattServiceImpl @Autowired constructor(
    private val axsys: AxsysClient,
    private val nomClient: NomClient,
    private val ldap: LDAPService,
) : AnsattService {
    private val log = LoggerFactory.getLogger(AnsattServiceImpl::class.java)

    override fun hentEnhetsliste(): List<AnsattEnhet> {
        return AuthContextUtils
            .getIdent()
            .map { hentEnhetsliste(NavIdent(it)) }
            .orElse(emptyList())
    }

    override fun hentEnhetsliste(ident: NavIdent): List<AnsattEnhet> {
        return (axsys.hentTilganger(ident) ?: emptyList())
            .map { AnsattEnhet(it.enhetId.get(), it.navn) }
    }

    override fun hentVeileder(ident: NavIdent): Veileder {
        return hentVeiledere(listOf(ident))
            .getOrDefault(ident, Veileder("", "", ident.get()))
    }

    override fun hentVeiledere(identer: List<NavIdent>): Map<NavIdent, Veileder> {
        return nomClient
            .runCatching { finnNavn(identer) }
            .getOrDefault(emptyList())
            .associateBy { it.navIdent }
            .mapValues { (_, value) ->
                Veileder(
                    ident = value.navIdent.get(),
                    fornavn = value.fornavn,
                    etternavn = value.etternavn
                )
            }
    }

    override fun hentVeilederRoller(ident: NavIdent): RolleListe {
        return RolleListe(ldap.hentRollerForVeileder(ident))
    }

    override fun hentAnsattFagomrader(ident: String, enhet: String): Set<String> {
        return axsys
            .runCatching {
                hentTilganger(NavIdent(ident))
                    .find {
                        it.enhetId.get() == enhet
                    }
                    ?.temaer
                    ?.toSet()
                    ?: emptySet()
            }
            .getOrElse {
                log.error("Klarte ikke å hente ansatt fagområder for $ident $enhet", it)
                emptySet()
            }
    }

    override fun ansatteForEnhet(enhet: AnsattEnhet): List<Ansatt> {
        return try {
            val hentAnsatte = axsys.hentAnsatte(EnhetId(enhet.enhetId))
            val ansatteNavn = nomClient.finnNavn(hentAnsatte)
            ansatteNavn.map {
                Ansatt(
                    it.fornavn,
                    it.etternavn,
                    it.navIdent.get()
                )
            }
        } catch (e: Exception) {
            log.error("Får ikke hentet ansatte for enhet", e)
            emptyList()
        }
    }
}

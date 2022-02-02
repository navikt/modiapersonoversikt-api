package no.nav.modiapersonoversikt.service.ansattservice

import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.nom.NomClient
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.legacy.api.domain.norg.Ansatt
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.Exception

interface AnsattService {
    fun hentEnhetsliste(): List<AnsattEnhet>
    fun hentAnsattNavn(ident: String): String
    fun hentAnsattFagomrader(ident: String, enhet: String): Set<String>
    fun ansatteForEnhet(enhet: AnsattEnhet): List<Ansatt>
}

class AnsattServiceImpl @Autowired constructor(
    private val axsys: AxsysClient,
    private val nomClient: NomClient
) : AnsattService {
    private val log = LoggerFactory.getLogger(AnsattServiceImpl::class.java)

    override fun hentEnhetsliste(): List<AnsattEnhet> {
        return AuthContextUtils
            .getIdent()
            .map {
                axsys.hentTilganger(NavIdent(it))
            }
            .orElse(emptyList())
            .map {
                AnsattEnhet(
                    it.enhetId.get(),
                    it.navn
                )
            }
    }

    override fun hentAnsattNavn(ident: String): String {
        return nomClient.finnNavn(NavIdent(ident)).visningsNavn
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

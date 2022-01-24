package no.nav.modiapersonoversikt.service.ansattservice

import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.nom.NomClient
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.legacy.api.domain.norg.Ansatt
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet
import org.springframework.beans.factory.annotation.Autowired

interface AnsattService {
    fun hentEnhetsliste(): List<AnsattEnhet>
    fun hentAnsattNavn(ident: String): String
    fun hentAnsattFagomrader(ident: String, enhet: String): Set<String>
    fun ansatteForEnhet(enhet: AnsattEnhet): List<Ansatt>
}

class AnsattServerImpl @Autowired constructor(
    private val axsys: AxsysClient,
    private val nomClient: NomClient
) : AnsattService {

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
        TODO("Not yet implemented")
    }

    override fun hentAnsattFagomrader(ident: String, enhet: String): Set<String> {
        TODO("Not yet implemented")
    }

    override fun ansatteForEnhet(enhet: AnsattEnhet): List<Ansatt> {
        TODO("Not yet implemented")
    }
}

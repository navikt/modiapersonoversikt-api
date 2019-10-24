package no.nav.sbl.dialogarena.modiabrukerdialog.web.config

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSHentNAVEnhetListeRequest
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet
import no.nav.common.auth.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext
import org.slf4j.LoggerFactory
import java.lang.RuntimeException
import java.util.*

class TilgangskontrollContextImpl(
        private val ldap: LDAPService,
        private val grunninfo: GrunninfoService,
        private val ansattService: GOSYSNAVansatt,
        private val enhetService: GOSYSNAVOrgEnhet,
        private val henvendelseLesService: HenvendelseLesService
): TilgangskontrollContext {
    private val logger = LoggerFactory.getLogger(TilgangskontrollContext::class.java)

    override fun hentSaksbehandlerId(): Optional<String> = SubjectHandler.getIdent().map(String::toUpperCase)
    override fun hentSaksbehandlerRoller(): List<String> =
            hentSaksbehandlerId()
                    .map(ldap::hentRollerForVeileder)
                    .orElse(emptyList())
                    .map { it.toLowerCase() }

    override fun harSaksbehandlerRolle(rolle: String) = hentSaksbehandlerRoller().contains(rolle.toLowerCase())
    override fun hentDiskresjonkode(fnr: String): String? = grunninfo.hentBrukerInfo(fnr).diskresjonskode
    override fun hentBrukersEnhet(fnr: String): String? = grunninfo.hentBrukerInfo(fnr).navkontor
    override fun hentTemagrupperForSaksbehandler(valgtEnhet: String): Set<String> = try {
        val ansattFagomraderRequest = ASBOGOSYSHentNAVAnsattFagomradeListeRequest()
        ansattFagomraderRequest.ansattId = hentSaksbehandlerId().orElseThrow { RuntimeException("Fant ikke saksbehandlerIdent") }
        ansattFagomraderRequest.enhetsId = valgtEnhet

        ansattService
                .hentNAVAnsattFagomradeListe(ansattFagomraderRequest)
                .fagomrader
                .map { it.fagomradeKode }
                .toSet()
    } catch (e: Exception) {
        when (e) {
            is HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg ->
                logger.warn("Fant ikke ansatt med ident {}.", hentSaksbehandlerId(), e)
            is HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg ->
                logger.warn("Feil oppsto under henting av ansatt fagområdeliste for enhet med enhetsId {}.", valgtEnhet, e)
            else ->
                logger.warn("Ukjent feil ved henting av fagområdelsite for ident {} enhet {}.", hentSaksbehandlerId(), valgtEnhet, e)
        }

        emptySet()
    }

    override fun hentSaksbehandlerLokalEnheter(): Set<String> = hentSaksbehandlerLokalEnheterData().map { it.enhetsId }.toSet()

    override fun hentSaksbehandlersFylkesEnheter(): Set<String> = try {
        hentSaksbehandlerLokalEnheterData()
                .flatMap(::hentUnderenheterForLokalEnhet)
                .toSet()
    } catch (e: Exception) {
        emptySet()
    }

    override fun hentSaksbehandlereMedTilgangTilHastekassering(): List<String> {
        return System.getProperty("hastekassering.tilgang", "")
                .let {
                    it.split(",")
                            .map(String::trim)
                            .map(String::toUpperCase)
                }
    }

    override fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        return henvendelseLesService.alleBehandlingsIderTilhorerBruker(fnr, behandlingsIder)
    }

    override fun alleHenvendelseIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        return henvendelseLesService.alleHenvendelseIderTilhorerBruker(fnr, behandlingsIder)
    }

    private fun hentSaksbehandlerLokalEnheterData(): List<ASBOGOSYSNavEnhet> = try {
        val ansatt = ASBOGOSYSNAVAnsatt()
        ansatt.ansattId = hentSaksbehandlerId().orElseThrow { RuntimeException("Fant ikke saksbehandlerIdent") }
        ansattService.hentNAVAnsattEnhetListe(ansatt).navEnheter
    } catch (e: Exception) {
        when (e) {
            is HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg ->
                logger.warn("Fant ikke ansatt med ident {}", hentSaksbehandlerId(), e)
            is HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg ->
                logger.warn("Feil ved henting av saksbehandlers ({}) enheter", hentSaksbehandlerId(), e)
            else ->
                logger.warn("Feil ved henting av saksbehandlers ({}) enheter", hentSaksbehandlerId(), e)
        }

        emptyList()
    }

    private fun hentUnderenheterForLokalEnhet(enhet: ASBOGOSYSNavEnhet): Set<String> {
        // Spesial enhet
        return if ("SPESEN".equals(enhet.orgNivaKode, true))
            hentUnderenheter(enhet)
        else
            hentFylkesEnheter(enhet)
    }

    private fun hentUnderenheter(enhet: ASBOGOSYSNavEnhet): Set<String> {
        val request = ASBOGOSYSNavEnhet()
        request.enhetsId = enhet.enhetsId
        return enhetService
                .hentNAVEnhetGruppeListe(request)
                .navEnheter
                .map { it.enhetsId }
                .toSet()
    }

    private fun hentFylkesEnheter(enhet: ASBOGOSYSNavEnhet): Set<String> {
        val request = ASBOGOSYSHentNAVEnhetListeRequest()
        request.navEnhet = enhet
        request.typeOrganisertUnder = "FYLKE"
        val fylkesEnheters = enhetService.hentNAVEnhetListe(request).navEnheter

        return fylkesEnheters
                .flatMap { hentUnderenheter(it).plus(it.enhetsId) }
                .let {
                    if ("FYLKE".equals(enhet.orgNivaKode, true)) {
                        it.plus(hentUnderenheter(enhet).plus(enhet.enhetsId))
                    } else {
                        it
                    }
                }
                .toSet()
    }
}
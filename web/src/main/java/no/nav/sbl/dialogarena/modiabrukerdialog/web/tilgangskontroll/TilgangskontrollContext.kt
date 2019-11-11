package no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSHentNAVEnhetListeRequest
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet
import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoService
import org.slf4j.LoggerFactory

fun hentSaksbehandlereMedTilgangTilHastekassering(): List<String> {
    return System.getProperty("hastekassering.tilgang", "")
            .let {
                it.split(",")
                        .map(String::trim)
                        .map(String::toUpperCase)
            }
}

class TilgangskontrollContext(
        private val ldap: LDAPService,
        private val grunninfo: GrunninfoService,
        private val ansattService: GOSYSNAVansatt,
        private val enhetService: GOSYSNAVOrgEnhet,
        private val henvendelseLesService: HenvendelseLesService
) {
    private val logger = LoggerFactory.getLogger(TilgangskontrollContext::class.java)

    fun hentSaksbehandlerId(): String = SubjectHandler.getSubjectHandler().uid.toUpperCase()
    fun hentSaksbehandlerRoller(): List<String> = ldap.hentRollerForVeileder(hentSaksbehandlerId()).map { it.toLowerCase() }
    fun harSaksbehandlerRolle(rolle: String) = hentSaksbehandlerRoller().contains(rolle.toLowerCase())
    fun hentDiskresjonkode(fnr: String): String? = grunninfo.hentBrukerInfo(fnr).diskresjonskode
    fun hentBrukersEnhet(fnr: String): String? = grunninfo.hentBrukerInfo(fnr).navkontorId
    fun hentTemagrupperForSaksbehandler(valgtEnhet: String): Set<String> = try {
        val ansattFagomraderRequest = ASBOGOSYSHentNAVAnsattFagomradeListeRequest()
        ansattFagomraderRequest.ansattId = hentSaksbehandlerId()
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

    fun hentSaksbehandlerLokalEnheter(): Set<String> = hentSaksbehandlerLokalEnheterData().map { it.enhetsId }.toSet()

    private fun hentSaksbehandlerLokalEnheterData(): List<ASBOGOSYSNavEnhet> = try {
        val ansatt = ASBOGOSYSNAVAnsatt()
        ansatt.ansattId = hentSaksbehandlerId()
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

    fun hentSaksbehandlersFylkesEnheter(): Set<String> = try {
        hentSaksbehandlerLokalEnheterData()
                .flatMap(::hentUnderenheterForLokalEnhet)
                .toSet()
    } catch (e: Exception) {
        emptySet()
    }

    fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        return henvendelseLesService.alleBehandlingsIderTilhorerBruker(fnr, behandlingsIder)
    }

    fun alleHenvendelseIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        return henvendelseLesService.alleHenvendelseIderTilhorerBruker(fnr, behandlingsIder)
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
package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgave

import com.google.gson.GsonBuilder
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OpprettOppgaveRest
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.sts.StsServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants
import no.nav.sbl.rest.RestUtils
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgave
import org.slf4j.MDC
import javax.inject.Inject
import javax.ws.rs.client.Client
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION


open class OppgaveOpprettelseClient : OpprettOppgaveRest {
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    @Inject
    private lateinit var stsService: StsServiceImpl

    open fun opprettOppgave(oppgave: WSOpprettOppgave): String  {
        val url =getEnviroment()
        val oppgaveskjermetRequest = OppgaveSkjermetRequest

          return gjorSporring(url, oppgaveskjermetRequest, response)





    }

    private  fun <T> gjorSporring(url: String, request: OppgaveSkjermetRequest, targetClass: Class<T>): T {
        val ssoToken = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { RuntimeException("Fant ikke OIDC-token") }
        return RestUtils.withClient { client: Client ->
            client
                    .target(url)
                    .request()
                    .header(RestConstants.NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                    .header(AUTHORIZATION, "Bearer $ssoToken")
                    .post(request)[targetClass]

        }





    }
private fun getAktørId(fnr : String): String {
    // TODO implemente hente aktørId
    return "hei"
}
    private fun getEnviroment() {

    }
}


data class OppgaveResponse {
     val status : String,
    val error? : String

}
data class  OppgaveSkjermetRequest {
    // bygge requesteobjektet
}

/**
TODO viser opprettelsen av gammel oppgave
int valgtEnhetId;
try {
    valgtEnhetId = Integer.parseInt(enhetId);
} catch (NumberFormatException e) {
    logger.error(String.format("EnhetId %s kunne ikke gjøres om til Integer", enhetId));
    valgtEnhetId = DEFAULT_OPPRETTET_AV_ENHET_ID;
}

String beskrivelse = "Fra Modia:\n" + nyOppgave.beskrivelse;

GsakKodeTema.Underkategori underkategori = nyOppgave.underkategori != null ? nyOppgave.underkategori : new GsakKodeTema.Underkategori(null, null);
GsakKodeTema.Prioritet prioritet = nyOppgave.prioritet != null ? nyOppgave.prioritet : new GsakKodeTema.Prioritet("NORM_" + nyOppgave.tema.kode, "Normal");

oppgavebehandlingWS.opprettOppgave(
new WSOpprettOppgaveRequest()
.withOpprettetAvEnhetId(valgtEnhetId)
.withHenvendelsetypeKode(HENVENDELSESTYPE_KODE)
.withOpprettOppgave(
new WSOpprettOppgave()
.withHenvendelseId(nyOppgave.henvendelseId)
.withAktivFra(LocalDate.now())
.withAktivTil(arbeidsdagerFraDato(nyOppgave.type.dagerFrist, LocalDate.now()))
.withAnsvarligEnhetId(nyOppgave.enhet.enhetId)
.withAnsvarligId(nyOppgave.valgtAnsatt != null ? nyOppgave.valgtAnsatt.ident : null)
.withBeskrivelse(leggTilBeskrivelse(beskrivelse, enhetId))
.withFagomradeKode(nyOppgave.tema.kode)
.withUnderkategoriKode(underkategori.kode)
.withBrukerId(nyOppgave.brukerId)
.withOppgavetypeKode(nyOppgave.type.kode)
.withPrioritetKode(prioritet.kode)
.withLest(false)
)
);
        **/
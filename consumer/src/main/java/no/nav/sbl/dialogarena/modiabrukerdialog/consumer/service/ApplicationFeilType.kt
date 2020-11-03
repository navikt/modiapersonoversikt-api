package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service

import no.nav.common.types.feil.Feil
import javax.ws.rs.core.Response

enum class ApplikasjonsFeilType(private val status: Response.Status) : Feil.Type {
    JOURNALFORING_FEILET(Response.Status.INTERNAL_SERVER_ERROR),
    INGEN_MELDINGER(Response.Status.INTERNAL_SERVER_ERROR),
    OPPGAVE_ER_FERDIGSTILT(Response.Status.INTERNAL_SERVER_ERROR);

    override fun getName(): String {
        return name
    }

    override fun getStatus(): Response.Status {
        return status
    }

}

internal class JournalforingFeiletException(t: Throwable) : Feil(ApplikasjonsFeilType.JOURNALFORING_FEILET, t)
internal class OppgaveErFerdigstiltException : Feil(ApplikasjonsFeilType.OPPGAVE_ER_FERDIGSTILT)
internal class IngenMeldingerException(fnr: String?, traadId: String?) : Feil(
        ApplikasjonsFeilType.INGEN_MELDINGER,
        "Fant ingen meldinger for fnr: $fnr med traadId: $traadId"
)

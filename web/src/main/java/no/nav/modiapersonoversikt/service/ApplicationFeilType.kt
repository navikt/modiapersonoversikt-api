package no.nav.modiapersonoversikt.service

import javax.ws.rs.core.Response

open class Feil(val type: Type, cause: Throwable? = null) : RuntimeException(type.getName(), cause) {
    constructor(type: Type, cause: String) : this(type, IllegalStateException(cause))

    interface Type {
        fun getName(): String
        fun getStatus(): Response.Status
    }
}
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

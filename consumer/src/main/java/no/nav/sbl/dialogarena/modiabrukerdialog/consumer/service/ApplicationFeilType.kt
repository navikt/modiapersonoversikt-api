package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service

import no.nav.apiapp.feil.Feil
import javax.ws.rs.core.Response

enum class ApplikasjonsFeilType(private val status: Response.Status) : Feil.Type {
    JOURNALFORING_FEILET(Response.Status.INTERNAL_SERVER_ERROR);

    override fun getName(): String {
        return name
    }

    override fun getStatus(): Response.Status {
        return status
    }

}

internal class JournalforingFeiletException : Feil(ApplikasjonsFeilType.JOURNALFORING_FEILET)
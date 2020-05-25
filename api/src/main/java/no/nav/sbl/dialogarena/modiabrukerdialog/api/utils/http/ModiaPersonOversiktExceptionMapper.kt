package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http

import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
class ModiaPersonOversiktExceptionMapper : ExceptionMapper<Exception> {
    override fun toResponse(ex: Exception): Response? {
        return when (ex) {
            is InternalServerErrorException -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(ex.message)
                    .build()
            else -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ex.message)
                    .build()
        }

    }
}
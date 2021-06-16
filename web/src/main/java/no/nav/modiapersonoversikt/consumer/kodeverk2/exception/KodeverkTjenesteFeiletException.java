package no.nav.modiapersonoversikt.consumer.kodeverk2.exception;

import no.nav.modiapersonoversikt.infrastructure.core.exception.SystemException;

public class KodeverkTjenesteFeiletException extends SystemException {

    public KodeverkTjenesteFeiletException(Throwable cause) {
        this("Kodeverktjenesten feilet.", cause);
    }

    public KodeverkTjenesteFeiletException(String message, Throwable cause) {
        super(message, cause);
    }
}

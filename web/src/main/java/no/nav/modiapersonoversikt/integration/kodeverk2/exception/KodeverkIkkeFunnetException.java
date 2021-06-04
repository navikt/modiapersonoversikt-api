package no.nav.modiapersonoversikt.integration.kodeverk2.exception;

import no.nav.modiapersonoversikt.infrastructure.core.exception.SystemException;

public class KodeverkIkkeFunnetException extends SystemException {

    public KodeverkIkkeFunnetException(String navn, Throwable cause) {
        super("Kodeverk med navn " + navn + " ikke funnet.", cause);
    }
}

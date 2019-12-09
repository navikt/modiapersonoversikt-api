package no.nav.sbl.dialogarena.common.kodeverk.exception;

import no.nav.modig.core.exception.SystemException;

public class KodeverkIkkeFunnetException extends SystemException {

    public KodeverkIkkeFunnetException(String navn, Throwable cause) {
        super("Kodeverk med navn " + navn + " ikke funnet.", cause);
    }
}

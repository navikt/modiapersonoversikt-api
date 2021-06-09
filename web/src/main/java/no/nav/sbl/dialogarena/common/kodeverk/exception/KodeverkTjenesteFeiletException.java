package no.nav.sbl.dialogarena.common.kodeverk.exception;

import no.nav.modig.core.exception.SystemException;

public class KodeverkTjenesteFeiletException extends SystemException {

    public KodeverkTjenesteFeiletException(Throwable cause) {
        this("Kodeverktjenesten feilet.", cause);
    }

    public KodeverkTjenesteFeiletException(String message, Throwable cause) {
        super(message, cause);
    }
}

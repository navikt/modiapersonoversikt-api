package no.nav.modig.core.exception;

/**
 * SystemException skal brukes i tilfeller der en systemfeil forårsaker en exception F.eks. at en database er nede eller at man
 * ikke får svar fra web-tjeneste etc.
 */

public class SystemException extends ModigException {
    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param id      i18n in wicket. Else general id for exception
     */
    public SystemException(String message, Throwable cause, String id) {
        super(message, cause, id);
    }
}
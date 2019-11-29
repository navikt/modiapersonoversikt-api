package no.nav.modig.core.exception;

public class AuthorizationException extends ModigException {
    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
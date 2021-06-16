package no.nav.modiapersonoversikt.infrastructure.core.exception;

public class AuthorizationException extends ModigException {
    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
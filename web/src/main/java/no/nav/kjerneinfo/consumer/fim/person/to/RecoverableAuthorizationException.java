package no.nav.kjerneinfo.consumer.fim.person.to;

import no.nav.modig.core.exception.AuthorizationException;

/**
 * Authorization exception that is possible to recover from.
 *
 * For instance, when having a role that allows expansion.
 */
public class RecoverableAuthorizationException extends AuthorizationException {

    public RecoverableAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
	public RecoverableAuthorizationException(String message) {
		super(message);
	}
}

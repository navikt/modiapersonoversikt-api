package no.nav.modiapersonoversikt.legacy.kjerneinfo.common.exceptions;

import no.nav.modiapersonoversikt.infrastructure.core.exception.SystemException;

public class TjenesteFeilException extends SystemException {

	public TjenesteFeilException(String message, Throwable cause) {
		super(message, cause);
	}
}

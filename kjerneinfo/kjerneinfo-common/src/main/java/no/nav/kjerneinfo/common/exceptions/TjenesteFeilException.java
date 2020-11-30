package no.nav.kjerneinfo.common.exceptions;

import no.nav.modig.core.exception.SystemException;

public class TjenesteFeilException extends SystemException {

	public TjenesteFeilException(String message, Throwable cause) {
		super(message, cause);
	}
}

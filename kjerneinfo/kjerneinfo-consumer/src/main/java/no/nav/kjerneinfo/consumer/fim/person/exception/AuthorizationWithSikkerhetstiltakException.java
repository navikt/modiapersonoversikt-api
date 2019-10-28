package no.nav.kjerneinfo.consumer.fim.person.exception;

import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.modig.core.exception.AuthorizationException;

public class AuthorizationWithSikkerhetstiltakException extends AuthorizationException {
	private Sikkerhetstiltak sikkerhetstiltak;

	public AuthorizationWithSikkerhetstiltakException(String message) {
		super(message);
	}

	public AuthorizationWithSikkerhetstiltakException(String message, Throwable cause) {
		super(message, cause);
	}

	public Sikkerhetstiltak getSikkerhetstiltak() {
		return sikkerhetstiltak;
	}

	public void setSikkerhetstiltak(Sikkerhetstiltak sikkerhetstiltak) {
		this.sikkerhetstiltak = sikkerhetstiltak;
	}
}

package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.exception;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.modiapersonoversikt.infrastructure.core.exception.AuthorizationException;

public class AuthorizationWithSikkerhetstiltakException extends AuthorizationException {
	private Sikkerhetstiltak sikkerhetstiltak;

	public AuthorizationWithSikkerhetstiltakException(String message) {
		super(message);
	}

	public Sikkerhetstiltak getSikkerhetstiltak() {
		return sikkerhetstiltak;
	}

	public void setSikkerhetstiltak(Sikkerhetstiltak sikkerhetstiltak) {
		this.sikkerhetstiltak = sikkerhetstiltak;
	}
}

package no.nav.kjerneinfo.consumer.fim.person.exception;

import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AuthorizationWithSikkerhetstiltakExceptionTest {
	@Test
	public void verifySetterAndGetter() {
		AuthorizationWithSikkerhetstiltakException exception = new AuthorizationWithSikkerhetstiltakException("message") ;
		exception.setSikkerhetstiltak(new Sikkerhetstiltak());
		assertNotNull(exception.getSikkerhetstiltak());
	}
}

package no.nav.sykmeldingsperioder.consumer.foreldrepenger;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.sykmeldingsperioder.domain.foreldrepenger.Foedsel;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.FimAdopsjon;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.informasjon.FimForeldrepengeperiode;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.meldinger.FimHentForeldrepengerettighetResponse;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;

public class ForeldrepengerMockFactoryTest {

	@Test
	public void testCreateFimForeldrepengerListeResponse() {
		FimHentForeldrepengerettighetResponse fimHentForeldrepengerListeResponse = ForeldrepengerMockFactory.createFimHentForeldrepengerListeResponse();
		assertNotNull(fimHentForeldrepengerListeResponse.getForeldrepengerettighet());
	}

	@Test
	public void foreldrepengerlisteFoedsel() {
		ForeldrepengerMockFactory factory = new ForeldrepengerMockFactory();

		ForeldrepengerListeResponse response = factory.createForeldrepengerListeResponse();

		assertNotNull(response);
		assertNotNull(response.getForeldrepengerettighet());
		assertNotNull(response.getForeldrepengerettighet().getBarnetsFoedselsdato());
		assertThat(response.getForeldrepengerettighet().getAntallBarn() != null, equalTo(response.getForeldrepengerettighet().getBarnetsFoedselsdato() != null));
		assertNotNull(response.getForeldrepengerettighet().getForelder());
		assertNotNull(response.getForeldrepengerettighet().getPeriode());
		assertThat(response.getForeldrepengerettighet(),  instanceOf(Foedsel.class));
	}

	@Test
	public void createForeldrepengeperioder2() {
		List<FimForeldrepengeperiode> list = ForeldrepengerMockFactory.createForeldrepengeperioder2();
		assertThat(list.size(), greaterThan(0));
		assertNotNull(list.get(1).getForeldrepengerFom());
	}

	@Test
	public void createFimHentForeldrepengerListeResponseAdopsjon() {
		FimHentForeldrepengerettighetResponse response = ForeldrepengerMockFactory.createFimHentForeldrepengerListeResponseAdopsjon();

		assertNotNull(response);
		assertNotNull(response.getForeldrepengerettighet());
		assertThat(response.getForeldrepengerettighet(), instanceOf(FimAdopsjon.class));
	}
}

package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.brukerprofil.consumer.messages.BrukerprofilResponse;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Adresse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.Personnavn;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjonstype;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.MockContext.FODSELSNUMMER;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class KjerneinfoMock {

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        PersonKjerneinfoServiceBi serviceMock = mock(PersonKjerneinfoServiceBi.class);
        HentKjerneinformasjonResponse mockReturnValue = createPersonResponse();

        when(serviceMock.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(mockReturnValue);
        return serviceMock;
    }

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        BrukerprofilServiceBi serviceMock = mock(BrukerprofilServiceBi.class);
        BrukerprofilResponse mockReturnValue = createBrukerprofilResponse();

        when(serviceMock.hentKontaktinformasjonOgPreferanser(any(BrukerprofilRequest.class))).thenReturn(mockReturnValue);
        return serviceMock;
    }

    @Bean
    public BehandleBrukerprofilServiceBi behandleBrukerprofilServiceBi() {
        return mock(BehandleBrukerprofilServiceBi.class);
    }

    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        return mock(SykepengerWidgetService.class);
    }

    @Bean
    public KodeverkmanagerBi kodeverkmanagerBi() {
        return mock(KodeverkmanagerBi.class);
    }

    private BrukerprofilResponse createBrukerprofilResponse() {
        BrukerprofilResponse mockReturnValue = new BrukerprofilResponse();
        mockReturnValue.setBruker(new Bruker());
        return mockReturnValue;
    }

    private HentKjerneinformasjonResponse createPersonResponse() {
        HentKjerneinformasjonResponse mockReturnValue = new HentKjerneinformasjonResponse();
        mockReturnValue.setPerson(createPerson());
        return mockReturnValue;
    }

    private Person createPerson() {
	    Person barn = new Person.With()
			    .fodselsnummer("01019912345")
			    .personfakta(new Personfakta.With()
					    .sivilstand(new Kodeverdi.With()
							    .value("SINGEL")
							    .done())
					    .navn(new Personnavn.With()
							    .fornavn("Barn")
							    .etternavn("Testesen")
							    .done())
					    .adresse(new Adresse.With()
							    .gatenavn("Testgata")
							    .postnummer("1337")
							    .poststed("Test").done())
					    .done())
			    .done();

	    Familierelasjon familierelasjon = new Familierelasjon();
	    	    familierelasjon.setHarSammeBosted(true);
	    	    familierelasjon.setTilRolle(Familierelasjonstype.BARN.toString().toUpperCase());
	    	    familierelasjon.setTilPerson(barn);

	    return new Person.With()
	    			    .fodselsnummer(FODSELSNUMMER)
	    			    .personfakta(new Personfakta.With()
						        .sivilstand(new Kodeverdi.With()
								        .value("SINGEL")
								        .done())
						        .navn(new Personnavn.With()
								        .fornavn("Test")
								        .etternavn("Testesen")
								        .done())
						        .adresse(new Adresse.With()
								        .gatenavn("Testgata")
								        .postnummer("1337")
								        .poststed("Test").done())
						        .familierelasjoner(Arrays.asList(familierelasjon))
						        .done())
	    			    .done();
    }

}

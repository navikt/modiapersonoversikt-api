package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts;

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
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class Kjerneinfo {

    public static final String FODSELSNUMMER = "23067911223";

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        PersonKjerneinfoServiceBi mock = mock(PersonKjerneinfoServiceBi.class);
        HentKjerneinformasjonResponse value = new HentKjerneinformasjonResponse();

        value.setPerson(
                new Person.With()
                        .fodselsnummer(FODSELSNUMMER)
                        .personfakta(new Personfakta.With()
                                .sivilstand(new Kodeverdi.With()
                                        .value("SINGEL")
                                        .done())
                                .navn(new Personnavn.With()
                                        .fornavn("Testern")
                                        .etternavn("Testesen")
                                        .done())
                                .adresse(new Adresse.With()
                                        .gatenavn("Testgata")
                                        .postnummer("1337")
                                        .poststed("Test").done())
                                .done())
                        .done());
        when(mock.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(value);
        return mock;
    }

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        BrukerprofilServiceBi mock = mock(BrukerprofilServiceBi.class);
        BrukerprofilResponse value = new BrukerprofilResponse();

        value.setBruker(new Bruker());

        when(mock.hentKontaktinformasjonOgPreferanser(any(BrukerprofilRequest.class))).thenReturn(value);
        return mock;
    }

}

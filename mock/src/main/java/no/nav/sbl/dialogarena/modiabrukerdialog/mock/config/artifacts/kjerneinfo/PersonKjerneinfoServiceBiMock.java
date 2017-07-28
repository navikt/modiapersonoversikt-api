package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Adresse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.Personnavn;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;

import java.util.Arrays;

import static no.nav.kjerneinfo.domain.person.fakta.Familierelasjonstype.BARN;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonKjerneinfoServiceBiMock {

    public static final String FODSELSNUMMER = "***REMOVED***";

    public static PersonKjerneinfoServiceBi getPersonKjerneinfoServiceBiMock() {
        PersonKjerneinfoServiceBi serviceMock = mock(PersonKjerneinfoServiceBi.class);
        HentKjerneinformasjonResponse mockReturnValue = createPersonResponse();

        when(serviceMock.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(mockReturnValue);
        return serviceMock;
    }

    public static HentKjerneinformasjonResponse createPersonResponse() {
        HentKjerneinformasjonResponse mockReturnValue = new HentKjerneinformasjonResponse();
        mockReturnValue.setPerson(createPerson());
        return mockReturnValue;
    }

    private static Person createPerson() {
        Person barn = new Person.With()
                .fodselsnummer("***REMOVED***")
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
        familierelasjon.setTilRolle(BARN.toString().toUpperCase());
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
                        .harAnsvarligEnhet(new AnsvarligEnhet.With()
                                .organisasjonsenhet(new Organisasjonsenhet.With()
                                        .organisasjonselementId("1234")
                                        .done())
                                .done())
                        .sikkerhetsTiltak(new Sikkerhetstiltak.With()
                            .sikkerhetstiltaksbeskrivelse("Egen ansatt")
                            .done())
                        .familierelasjoner(Arrays.asList(familierelasjon))
                        .done())
                .done();
    }

}

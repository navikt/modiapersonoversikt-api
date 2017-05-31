package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.*;
import no.nav.kjerneinfo.domain.person.fakta.*;

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
        Person barn = lagBarn();
        Familierelasjon familierelasjon = new Familierelasjon();
        familierelasjon.setHarSammeBosted(true);
        familierelasjon.setTilRolle(BARN.toString().toUpperCase());
        familierelasjon.setTilPerson(barn);

        Personfakta personfakta = new Personfakta();
        personfakta.setSivilstand(new Kodeverdi.With().value("SINGEL").done());
        personfakta.setPersonnavn(lagPersonnavn("Test", "Testesen"));
        personfakta.setAdresse(lagMockAdresse());
        personfakta.setAnsvarligEnhet(new AnsvarligEnhet.With()
                .organisasjonsenhet(new Organisasjonsenhet.With()
                        .organisasjonselementId("1234")
                        .organisasjonselementNavn("NAV Mockenhet")
                        .done())
                .done());
        personfakta.setHarFraRolleIList(Arrays.asList(familierelasjon));

        return new Person.With()
                .fodselsnummer(FODSELSNUMMER)
                .personfakta(personfakta).done();
    }

    private static Person lagBarn() {
        Personfakta personfakta = new Personfakta();
        personfakta.setSivilstand(new Kodeverdi.With().value("SINGEL").done());
        personfakta.setPersonnavn(lagPersonnavn("Barn", "Testesen"));
        personfakta.setAdresse(lagMockAdresse());
        Person barn = new Person.With()
                .fodselsnummer("***REMOVED***")
                .personfakta(personfakta)
                .done();

        return barn;
    }

    private static Adresse lagMockAdresse() {
        Adresse adresse = new Adresse();
        adresse.setGatenavn("Testgata");
        adresse.setPostnummer("1337");
        adresse.setPoststednavn("Test");
        return adresse;
    }

    private static Personnavn lagPersonnavn(String fornavn, String etternavn) {
        Personnavn personnavn = new Personnavn();
        personnavn.setFornavn(fornavn);
        personnavn.setEtternavn(etternavn);
        return  personnavn;
    }

}

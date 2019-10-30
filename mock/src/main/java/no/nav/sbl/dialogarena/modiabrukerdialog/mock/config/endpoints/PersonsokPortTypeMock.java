package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonForMangeForekomster;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonUgyldigInput;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonRequest;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class PersonsokPortTypeMock {

    @Bean
    public PersonsokPortType personsokPortType() { return createPersonsokMock(); }

    public static PersonsokPortType createPersonsokMock() {
        return new PersonsokPortType() {
            @Override
            public FimFinnPersonResponse finnPerson(FimFinnPersonRequest fimFinnPersonRequest) throws FinnPersonUgyldigInput, FinnPersonForMangeForekomster {
                return new FimFinnPersonResponse().withTotaltAntallTreff(0).withPersonListe(new ArrayList<>());
            }

            @Override
            public void ping() {

            }
        };
    }
}

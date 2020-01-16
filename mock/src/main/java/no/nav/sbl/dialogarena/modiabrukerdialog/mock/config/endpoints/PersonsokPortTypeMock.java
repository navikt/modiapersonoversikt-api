package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault1;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Collections.emptyList;

@Configuration
public class PersonsokPortTypeMock {

    @Bean
    public PersonsokPortType personsokPortType() {
        return createPersonsokMock();
    }

    public static PersonsokPortType createPersonsokMock() {
        return new PersonsokPortType() {
            @Override
            public FinnPersonResponse finnPerson(FinnPersonRequest fimFinnPersonRequest) throws FinnPersonFault, FinnPersonFault1 {
                FinnPersonResponse response = new FinnPersonResponse();
                response.setTotaltAntallTreff(0);
                response.getPersonListe().addAll(emptyList());
                return response;
            }

            @Override
            public void ping() {

            }
        };
    }
}

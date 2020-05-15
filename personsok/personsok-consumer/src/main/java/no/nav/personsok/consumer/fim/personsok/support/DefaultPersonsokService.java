package no.nav.personsok.consumer.fim.personsok.support;

import kotlin.Pair;
import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonResponse;
import no.nav.sbl.dialogarena.naudit.Audit;
import no.nav.sbl.dialogarena.naudit.AuditIdentifier;
import no.nav.sbl.dialogarena.naudit.AuditResources;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault1;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten.
 */
public class DefaultPersonsokService implements PersonsokServiceBi {
    private static Audit.AuditDescriptor<Person> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Personalia,
            (person) -> singletonList(new Pair<>(AuditIdentifier.FNR, person.getIdent().getIdent()))
    );

    private PersonsokPortType personsokService;

    private FIMMapper mapper;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public FinnPersonResponse finnPerson(FinnPersonRequest finnPersonRequest) throws FinnPersonFault, FinnPersonFault1 {

        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest rawRequest = mapper.map(finnPersonRequest, no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest.class);

        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse rawResponse = personsokService.finnPerson(rawRequest);

        for (Person fimPerson : rawResponse.getPersonListe()) {
            auditLogger.log(fimPerson);
        }
        logger.info("finnPersonReturnerte " + rawResponse.getPersonListe().size() + " treff.");

        FinnPersonResponse response = mapper.map(rawResponse, FinnPersonResponse.class);
        return response;
    }

    public void setPersonsokService(PersonsokPortType personsokService) {
        this.personsokService = personsokService;
    }

    public void setMapper(FIMMapper mapper) {
        this.mapper = mapper;
    }
}

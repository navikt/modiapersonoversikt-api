package no.nav.personsok.consumer.fim.personsok.support;

import kotlin.Pair;
import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonResponse;
import no.nav.sbl.dialogarena.naudit.Audit;
import no.nav.sbl.dialogarena.naudit.AuditResources;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonForMangeForekomster;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonUgyldigInput;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimPerson;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonRequest;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.Collections.singletonList;

/**
 * Vår standardimplementasjonen av den eksterne tjenesten.
 */
public class DefaultPersonsokService implements PersonsokServiceBi {
    private static Audit.AuditDescriptor<FimPerson> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Personalia,
            (person) -> singletonList(new Pair<>("fnr", person.getIdent().getIdent()))
    );

    private PersonsokPortType personsokService;

    private FIMMapper mapper;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public FinnPersonResponse finnPerson(FinnPersonRequest finnPersonRequest) throws FinnPersonForMangeForekomster, FinnPersonUgyldigInput {

        FimFinnPersonRequest rawRequest = mapper.map(finnPersonRequest, FimFinnPersonRequest.class);

        FimFinnPersonResponse rawResponse = personsokService.finnPerson(rawRequest);

        for (FimPerson fimPerson : rawResponse.getPersonListe()) {
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

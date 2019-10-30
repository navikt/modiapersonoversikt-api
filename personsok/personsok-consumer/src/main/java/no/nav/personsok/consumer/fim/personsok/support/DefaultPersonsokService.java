package no.nav.personsok.consumer.fim.personsok.support;

import no.nav.modig.common.SporingsAksjon;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.common.SporingsLoggerFactory;
import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonResponse;
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

/**
 * Vår standardimplementasjonen av den eksterne tjenesten.
 */
public class DefaultPersonsokService implements PersonsokServiceBi {

    private PersonsokPortType personsokService;

    private FIMMapper mapper;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public FinnPersonResponse finnPerson(FinnPersonRequest finnPersonRequest) throws FinnPersonForMangeForekomster, FinnPersonUgyldigInput {

        FimFinnPersonRequest rawRequest = mapper.map(finnPersonRequest, FimFinnPersonRequest.class);

        FimFinnPersonResponse rawResponse = personsokService.finnPerson(rawRequest);
        for (FimPerson fimPerson : rawResponse.getPersonListe()) {
            logSporingsInformasjon(fimPerson);
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

    private void logSporingsInformasjon(FimPerson fimPerson) {
        SporingsLogger sporingsLogger;
        try {
            sporingsLogger = SporingsLoggerFactory.sporingsLogger(configFileAsBufferedReader("personsok-sporing-config.txt"));
            sporingsLogger.logg(fimPerson, SporingsAksjon.Les);
        } catch (Exception e) {
            logger.error("hentSykmeldingsperioder:SporingsLogger ble ikke opprettet.", e);
        }
    }

    private BufferedReader configFileAsBufferedReader(String filepath) {
        BufferedReader br = null;
        try {
            InputStream is = getClass().getClassLoader().getResource(filepath).openStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
        } catch (IOException e) {
            logger.warn("Feil i oppsett av sporingslogg" + filepath, e);
        }
        return br;
    }
}

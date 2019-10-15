package no.nav.personsok.consumer.fim.personsok.mock;

import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.mapping.PersonMockMapper;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonResponse;
import no.nav.personsok.consumer.mdc.MDCUtils;
import no.nav.personsok.domain.UtvidetPersonsok;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonForMangeForekomster;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonUgyldigInput;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.FimPerson;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonResponse;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PersonsokServiceMock implements PersonsokServiceBi {

    public static final String MAX = "MAX";
    private FIMMapper mapper;
    private List<FimPerson> personer;
    private FinnPersonRequest currentFinnPersonRequest;

    public PersonsokServiceMock() {
        personer = new ArrayList<>();
        List<PersonMock> personMockList = PersonMockFactory.createPersonMockList();
        for (PersonMock personMock : personMockList) {
            personer.add(PersonMockMapper.map(personMock));
        }
    }

    @Override
    public FinnPersonResponse finnPerson(FinnPersonRequest finnPersonRequest) throws FinnPersonForMangeForekomster, FinnPersonUgyldigInput {
        MDCUtils.putMDCInfo("finnPersonMock()", "Etternavn: " + finnPersonRequest.getUtvidetPersonsok().getEtternavn()
                + " Fornavn: " + finnPersonRequest.getUtvidetPersonsok().getEtternavn());

        currentFinnPersonRequest = finnPersonRequest;
        FimFinnPersonResponse rawResponse = new FimFinnPersonResponse();

        populatePersonListe(finnPersonRequest.getUtvidetPersonsok(), rawResponse);

        FinnPersonResponse mappedfinnPersonResponse = mapper.map(rawResponse, FinnPersonResponse.class);

        if (MAX.equalsIgnoreCase(finnPersonRequest.getUtvidetPersonsok().getEtternavn())) {
            throw new FinnPersonForMangeForekomster();
        }
        MDCUtils.clearMDCInfo();
        return mappedfinnPersonResponse;
    }

    public FinnPersonRequest getCurrentFinnPersonRequest() {
        return currentFinnPersonRequest;
    }

    public void setMapper(FIMMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Legger til en person i person-lista.
     *
     * @param person
     */
    public void addPerson(FimPerson person) {
        personer.add(person);
    }

    public void resetPersoner() {
        personer.clear();
    }

    /**
     * If no search criteria is set, all objects are returned.
     */
    private void populatePersonListe(UtvidetPersonsok utvidetPersonsok, FimFinnPersonResponse rawResponse) {

        List<FimPerson> personList = rawResponse.getPersonListe();

        for (FimPerson person : personer) {
            if (compareNavn(utvidetPersonsok, person)) {
                personList.add(person);
            }
        }

        rawResponse.setTotaltAntallTreff(personList.size());
    }

    private boolean compareNavn(UtvidetPersonsok utvidetPersonsok, FimPerson person) {
        return (isNotBlank(utvidetPersonsok.getFornavn())
                && utvidetPersonsok.getFornavn().equalsIgnoreCase(person.getPersonnavn().getFornavn()));
    }


}

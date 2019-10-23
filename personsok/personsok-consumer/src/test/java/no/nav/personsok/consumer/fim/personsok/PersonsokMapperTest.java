package no.nav.personsok.consumer.fim.personsok;

import no.nav.personsok.consumer.fim.kodeverk.support.MockKodeverkManager;
import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.personsok.mock.PersonsokMockFactory;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonResponse;
import no.nav.personsok.domain.Adresse;
import no.nav.personsok.domain.Person;
import no.nav.personsok.domain.enums.AdresseType;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonRequest;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonResponse;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PersonsokMapperTest {

    public static final int TOTALT_ANTALL_TREFF = 100;
    private FIMMapper mapper;

    @Before
    public void setUp() {
        mapper = new FIMMapper(new MockKodeverkManager());
    }

    @Test
    public void finnPersonRequestMapperTest() {
        FinnPersonRequest finnPersonRequest = new FinnPersonRequest();
        finnPersonRequest.setUtvidetPersonsok(PersonsokMockFactory.getUtvidetPersonsok());

        FimFinnPersonRequest rawRequest = mapper.map(finnPersonRequest, FimFinnPersonRequest.class);

        checkRequestMapping(finnPersonRequest, rawRequest);
    }

    @Test
    public void finnPersonResponseMapperPersonTest() {
        FimFinnPersonResponse rawResponse =
                new FimFinnPersonResponse();

        rawResponse.setTotaltAntallTreff(TOTALT_ANTALL_TREFF);
        rawResponse.getPersonListe().add(PersonsokMockFactory.getPerson());
        rawResponse.getPersonListe().add(PersonsokMockFactory.getPerson());

        FinnPersonResponse finnPersonResponse = mapper.map(rawResponse, FinnPersonResponse.class);

        checkResponseMapperFellesFelt(rawResponse, finnPersonResponse);

        for (int i = 0; i < finnPersonResponse.getPersonListe().size(); i++) {
            checkResponseMapperPersonFelt(rawResponse.getPersonListe().get(i), finnPersonResponse.getPersonListe().get(i));
        }
    }

    @Test
    public void finnPersonResponseMapperBrukerTest() {
        FimFinnPersonResponse rawResponse =
                new FimFinnPersonResponse();

        rawResponse.setTotaltAntallTreff(TOTALT_ANTALL_TREFF);
        rawResponse.getPersonListe().add(PersonsokMockFactory.getBruker());
        rawResponse.getPersonListe().add(PersonsokMockFactory.getBruker2());
        rawResponse.getPersonListe().add(PersonsokMockFactory.getBruker3());
        rawResponse.getPersonListe().add(PersonsokMockFactory.getBruker4());
        rawResponse.getPersonListe().add(PersonsokMockFactory.getBruker5());

        FinnPersonResponse finnPersonResponse = mapper.map(rawResponse, FinnPersonResponse.class);

        checkResponseMapperFellesFelt(rawResponse, finnPersonResponse);

        for (int i = 0; i < finnPersonResponse.getPersonListe().size(); i++) {
            checkResponseMapperPersonFelt(rawResponse.getPersonListe().get(i), finnPersonResponse.getPersonListe().get(i));
            checkResponseMapperBrukerFelt(rawResponse.getPersonListe().get(i), finnPersonResponse.getPersonListe().get(i));
        }
    }

    @Test
    public void finnPersonResponseMapperBrukerBranchesTest() {
        FimFinnPersonResponse rawResponse =
                new FimFinnPersonResponse();

        rawResponse.getPersonListe().add(PersonsokMockFactory.getBrukerForBranchTest());

        FinnPersonResponse finnPersonResponse = mapper.map(rawResponse, FinnPersonResponse.class);

        for (int i = 0; i < finnPersonResponse.getPersonListe().size(); i++) {
            assertEquals(null, finnPersonResponse.getPersonListe().get(i).getFodselsnummer());
            assertEquals(0, finnPersonResponse.getPersonListe().get(i).getAdresser().size());
        }
    }

    @Test
    public void datoer() {
        LocalDate from = new LocalDate().withYear(2013).withMonthOfYear(8).withDayOfMonth(29);

        XMLGregorianCalendar to = mapper.map(from, XMLGregorianCalendar.class);

        assertEquals(from.getYear(), to.getYear());
        assertEquals(from.getMonthOfYear(), to.getMonth());
        assertEquals(from.getDayOfMonth(), to.getDay());
    }

    @Test
    public void xmlGregorianTilLocalDate () {
        String dato = "2015-02-01";
        XMLGregorianCalendar xmlGregorianCalendar = getMockDato(dato);

        LocalDate localDate = mapper.map(xmlGregorianCalendar, LocalDate.class);

        assertThat(localDate.toString(), is(dato));
    }

    private static XMLGregorianCalendar getMockDato(String dato) {
        try {
            GregorianCalendar cal = new GregorianCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            cal.setTime(dateFormat.parse(dato));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (ParseException | DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    private void checkRequestMapping(FinnPersonRequest finnPersonRequest, FimFinnPersonRequest rawRequest) {
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getAlderFra(), rawRequest.getPersonFilter().getAlderFra());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getAlderTil(), rawRequest.getPersonFilter().getAlderTil());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getKommunenr(), rawRequest.getPersonFilter().getEnhetId());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getFodselsdatoFra(),
                new LocalDate(rawRequest.getPersonFilter().getFoedselsdatoFra().toGregorianCalendar().getTime()));
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getFodselsdatoTil(),
                new LocalDate(rawRequest.getPersonFilter().getFoedselsdatoTil().toGregorianCalendar().getTime()));
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getKjonn().toString(), rawRequest.getPersonFilter().getKjoenn());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getHusbokstav(), rawRequest.getAdresseFilter().getHusbokstav());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getHusnummer(), rawRequest.getAdresseFilter().getGatenummer().toString());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getPostnummer(), rawRequest.getAdresseFilter().getPostnummer());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getFornavn(), rawRequest.getSoekekriterie().getFornavn());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getEtternavn(), rawRequest.getSoekekriterie().getEtternavn());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getGatenavn(), rawRequest.getSoekekriterie().getGatenavn());
        assertEquals(finnPersonRequest.getUtvidetPersonsok().getKontonummer(), rawRequest.getSoekekriterie().getBankkontoNorge());
    }

    private void checkResponseMapperFellesFelt(FimFinnPersonResponse rawResponse, FinnPersonResponse finnPersonResponse) {
        assertEquals(rawResponse.getTotaltAntallTreff(), finnPersonResponse.getTotaltAntallTreff());
        assertEquals(rawResponse.getPersonListe().size(), finnPersonResponse.getPersonListe().size());
    }

    private void checkResponseMapperPersonFelt(FimPerson rawResponsePerson, Person finnPersonResponsePerson) {
        assertEquals(rawResponsePerson.getPersonstatus().getPersonstatus().getValue(),
                finnPersonResponsePerson.getPersonstatus().getKode());
        assertEquals(rawResponsePerson.getPersonnavn().getFornavn(),
                finnPersonResponsePerson.getFornavn());
        assertEquals(rawResponsePerson.getPersonnavn().getMellomnavn(),
                finnPersonResponsePerson.getMellomnavn());
        assertEquals(rawResponsePerson.getPersonnavn().getEtternavn(),
                finnPersonResponsePerson.getEtternavn());

        checkNorskIdent(rawResponsePerson, finnPersonResponsePerson);
        checkPersonAdresser(rawResponsePerson, finnPersonResponsePerson);
    }

    private void checkResponseMapperBrukerFelt(FimPerson rawResponsePerson, Person finnPersonResponsePerson) {
        assertEquals(rawResponsePerson.getDiskresjonskode().getValue(),
                finnPersonResponsePerson.getDiskresjonskodePerson().getKode());
        assertEquals(((FimBruker) rawResponsePerson).getHarAnsvarligEnhet().getEnhet().getOrganisasjonselementID(),
                finnPersonResponsePerson.getKommunenr());

        checkBrukerAdresser(rawResponsePerson, finnPersonResponsePerson);
    }

    private void checkNorskIdent(FimPerson rawResponsePerson, Person finnPersonResponsePerson) {
        FimNorskIdent norskIdent = rawResponsePerson.getIdent();
        if (norskIdent.getType().getValue().equals("F")) {
            assertEquals(norskIdent.getIdent(),
                    finnPersonResponsePerson.getFodselsnummer());
        }

    }

    private void checkPersonAdresser(FimPerson rawResponsePerson, Person finnPersonResponsePerson) {
        FimUstrukturertAdresse postadresse = rawResponsePerson.getPostadresse().getUstrukturertAdresse();
        FimStrukturertAdresse bostedsadresse = rawResponsePerson.getBostedsadresse().getStrukturertAdresse();

        for (Adresse adresse : finnPersonResponsePerson.getAdresser()) {
            if (postadresse != null && adresse.getAdresseType() == AdresseType.POSTADRESSE) {
                checkUstrukturertAdresse(postadresse, adresse);
            } else if (bostedsadresse != null && adresse.getAdresseType() == AdresseType.BOLIGADRESSE) {
                checkStrukturertAdresse(bostedsadresse, adresse);
            }
        }
    }

    private void checkBrukerAdresser(FimPerson rawResponsePerson, Person finnPersonResponsePerson) {
        FimUstrukturertAdresse midlertidigPostadresseUtland = null;
        FimUstrukturertAdresse midlertidigPostadresseNorge = null;
        FimBruker rawResponseBruker = (FimBruker) rawResponsePerson;
        FimStrukturertAdresse bostedsadresse = rawResponseBruker.getBostedsadresse().getStrukturertAdresse();
        FimUstrukturertAdresse poststedsadresse = rawResponseBruker.getPostadresse().getUstrukturertAdresse();

        if (rawResponseBruker.getMidlertidigPostadresse() instanceof FimMidlertidigPostadresseUtland) {
            midlertidigPostadresseUtland = (((FimMidlertidigPostadresseUtland) rawResponseBruker.getMidlertidigPostadresse()).getUstrukturertAdresse());
        } else if (rawResponseBruker.getMidlertidigPostadresse() instanceof FimMidlertidigPostadresseNorge) {
            midlertidigPostadresseNorge = (((FimMidlertidigPostadresseNorge) rawResponseBruker.getMidlertidigPostadresse()).getUstrukturertAdresse());
        }

        assertEquals(rawResponseBruker.getGjeldendePostadresseType().getValue(), finnPersonResponsePerson.getAdresser().get(0).getAdresseType().name());

        for (Adresse adresse : finnPersonResponsePerson.getAdresser()) {
            if (adresse.getAdresseType() == AdresseType.BOLIGADRESSE && bostedsadresse != null) {
                checkStrukturertAdresse(bostedsadresse, adresse);
            } else if (adresse.getAdresseType() == AdresseType.POSTADRESSE && poststedsadresse != null) {
                checkUstrukturertAdresse(poststedsadresse, adresse);
            } else if (adresse.getAdresseType() == AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND && midlertidigPostadresseUtland != null) {
                checkUstrukturertAdresse(midlertidigPostadresseUtland, adresse);
            } else if (adresse.getAdresseType() == AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE && midlertidigPostadresseNorge != null) {
                checkUstrukturertAdresse(midlertidigPostadresseNorge, adresse);
            }
        }
    }

    private void checkUstrukturertAdresse(FimUstrukturertAdresse rawPostadresseUtland, Adresse adresse) {
        assertEquals((blankIfNull(rawPostadresseUtland.getAdresselinje1()) + " "
                + blankIfNull(rawPostadresseUtland.getAdresselinje2()) + " "
                + blankIfNull(rawPostadresseUtland.getAdresselinje3()) + "  ,").trim(), adresse.getAdresseString());
    }

    private String blankIfNull(String text) {
        return text == null ? "" : text;
    }

    private void checkStrukturertAdresse(FimStrukturertAdresse rawGateAdresse, Adresse adresse) {
        if (rawGateAdresse.getClass().equals(FimStedsadresseNorge.class)) {
            FimStedsadresseNorge adresseNorge = (FimStedsadresseNorge) rawGateAdresse;
            FimPostnummer poststed = adresseNorge.getPoststed();
            String poststedValue = poststed == null ? "" : ", " + poststed.getValue();
            assertEquals(adresseNorge.getTilleggsadresse() + " " + adresseNorge.getBolignummer()
                    + poststedValue, adresse.getAdresseString());
        } else if (rawGateAdresse.getClass().equals(FimGateadresse.class)) {
            FimGateadresse gateadresse = (FimGateadresse) rawGateAdresse;
            FimPostnummer poststed = gateadresse.getPoststed();
            String poststedValue = poststed == null ? "" : ", " + poststed.getValue();
            assertEquals(gateadresse.getGatenavn() + " " + gateadresse.getHusnummer()
                    + gateadresse.getHusbokstav() + poststedValue, adresse.getAdresseString());
        } else if (rawGateAdresse.getClass().equals(FimMatrikkeladresse.class)) {
            assertEquals(((FimMatrikkeladresse) rawGateAdresse).getEiendomsnavn() + " " + ((FimMatrikkeladresse) rawGateAdresse).getMatrikkelnummer().getBruksnummer(), adresse.getAdresseString());
        } else if (rawGateAdresse.getClass().equals(FimPostboksadresseNorsk.class)) {
            FimPostnummer poststed = ((FimPostboksadresseNorsk) rawGateAdresse).getPoststed();
            String poststedValue = poststed == null ? "" : ", " + poststed.getValue();
            assertEquals(((FimPostboksadresseNorsk) rawGateAdresse).getPostboksanlegg() + poststedValue, adresse.getAdresseString());
        }
    }
}

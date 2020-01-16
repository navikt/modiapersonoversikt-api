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
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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

        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest rawRequest = mapper.map(finnPersonRequest, no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest.class);

        checkRequestMapping(finnPersonRequest, rawRequest);
    }

    @Test
    public void finnPersonResponseMapperPersonTest() {
        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse rawResponse =
                new no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse();

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
        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse rawResponse =
                new no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse();

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
        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse rawResponse =
                new no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse();

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
    private void checkRequestMapping(FinnPersonRequest finnPersonRequest, no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest rawRequest) {
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

    private void checkResponseMapperFellesFelt(no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse rawResponse, FinnPersonResponse finnPersonResponse) {
        assertEquals(rawResponse.getTotaltAntallTreff(), finnPersonResponse.getTotaltAntallTreff());
        assertEquals(rawResponse.getPersonListe().size(), finnPersonResponse.getPersonListe().size());
    }

    private void checkResponseMapperPersonFelt(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person rawResponsePerson, Person finnPersonResponsePerson) {
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

    private void checkResponseMapperBrukerFelt(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person rawResponsePerson, Person finnPersonResponsePerson) {
        assertEquals(rawResponsePerson.getDiskresjonskode().getValue(),
                finnPersonResponsePerson.getDiskresjonskodePerson().getKode());
        assertEquals(((Bruker) rawResponsePerson).getHarAnsvarligEnhet().getEnhet().getOrganisasjonselementID(),
                finnPersonResponsePerson.getKommunenr());

        checkBrukerAdresser(rawResponsePerson, finnPersonResponsePerson);
    }

    private void checkNorskIdent(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person rawResponsePerson, Person finnPersonResponsePerson) {
        NorskIdent norskIdent = rawResponsePerson.getIdent();
        if (norskIdent.getType().getValue().equals("F")) {
            assertEquals(norskIdent.getIdent(),
                    finnPersonResponsePerson.getFodselsnummer());
        }

    }

    private void checkPersonAdresser(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person rawResponsePerson, Person finnPersonResponsePerson) {
        UstrukturertAdresse postadresse = rawResponsePerson.getPostadresse().getUstrukturertAdresse();
        StrukturertAdresse bostedsadresse = rawResponsePerson.getBostedsadresse().getStrukturertAdresse();

        for (Adresse adresse : finnPersonResponsePerson.getAdresser()) {
            if (postadresse != null && adresse.getAdresseType() == AdresseType.POSTADRESSE) {
                checkUstrukturertAdresse(postadresse, adresse);
            } else if (bostedsadresse != null && adresse.getAdresseType() == AdresseType.BOLIGADRESSE) {
                checkStrukturertAdresse(bostedsadresse, adresse);
            }
        }
    }

    private void checkBrukerAdresser(no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person rawResponsePerson, Person finnPersonResponsePerson) {
        UstrukturertAdresse midlertidigPostadresseUtland = null;
        UstrukturertAdresse midlertidigPostadresseNorge = null;
        Bruker rawResponseBruker = (Bruker) rawResponsePerson;
        StrukturertAdresse bostedsadresse = rawResponseBruker.getBostedsadresse().getStrukturertAdresse();
        UstrukturertAdresse poststedsadresse = rawResponseBruker.getPostadresse().getUstrukturertAdresse();

        if (rawResponseBruker.getMidlertidigPostadresse() instanceof MidlertidigPostadresseUtland) {
            midlertidigPostadresseUtland = (((MidlertidigPostadresseUtland) rawResponseBruker.getMidlertidigPostadresse()).getUstrukturertAdresse());
        } else if (rawResponseBruker.getMidlertidigPostadresse() instanceof MidlertidigPostadresseNorge) {
            midlertidigPostadresseNorge = (((MidlertidigPostadresseNorge) rawResponseBruker.getMidlertidigPostadresse()).getUstrukturertAdresse());
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

    private void checkUstrukturertAdresse(UstrukturertAdresse rawPostadresseUtland, Adresse adresse) {
        assertEquals((blankIfNull(rawPostadresseUtland.getAdresselinje1()) + " "
                + blankIfNull(rawPostadresseUtland.getAdresselinje2()) + " "
                + blankIfNull(rawPostadresseUtland.getAdresselinje3()) + "  ,").trim(), adresse.getAdresseString());
    }

    private String blankIfNull(String text) {
        return text == null ? "" : text;
    }

    private void checkStrukturertAdresse(StrukturertAdresse rawGateAdresse, Adresse adresse) {
        if (rawGateAdresse.getClass().equals(StedsadresseNorge.class)) {
            StedsadresseNorge adresseNorge = (StedsadresseNorge) rawGateAdresse;
            Postnummer poststed = adresseNorge.getPoststed();
            String poststedValue = poststed == null ? "" : ", " + poststed.getValue();
            assertEquals(adresseNorge.getTilleggsadresse() + " " + adresseNorge.getBolignummer()
                    + poststedValue, adresse.getAdresseString());
        } else if (rawGateAdresse.getClass().equals(Gateadresse.class)) {
            Gateadresse gateadresse = (Gateadresse) rawGateAdresse;
            Postnummer poststed = gateadresse.getPoststed();
            String poststedValue = poststed == null ? "" : ", " + poststed.getValue();
            assertEquals(gateadresse.getGatenavn() + " " + gateadresse.getHusnummer()
                    + gateadresse.getHusbokstav() + poststedValue, adresse.getAdresseString());
        } else if (rawGateAdresse.getClass().equals(Matrikkeladresse.class)) {
            assertEquals(((Matrikkeladresse) rawGateAdresse).getEiendomsnavn() + " " + ((Matrikkeladresse) rawGateAdresse).getMatrikkelnummer().getBruksnummer(), adresse.getAdresseString());
        } else if (rawGateAdresse.getClass().equals(PostboksadresseNorsk.class)) {
            Postnummer poststed = ((PostboksadresseNorsk) rawGateAdresse).getPoststed();
            String poststedValue = poststed == null ? "" : ", " + poststed.getValue();
            assertEquals(((PostboksadresseNorsk) rawGateAdresse).getPostboksanlegg() + poststedValue, adresse.getAdresseString());
        }
    }
}

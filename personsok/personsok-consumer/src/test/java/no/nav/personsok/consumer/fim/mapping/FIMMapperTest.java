package no.nav.personsok.consumer.fim.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import no.nav.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.domain.Kjonn;
import no.nav.personsok.domain.UtvidetPersonsok;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.AdresseFilter;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.PersonFilter;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.Soekekriterie;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class FIMMapperTest {

    public static final File snapshot = new File("src/test/resources/FIMMapperTest-snapshot.json");

    enum BostedsType { POSTBOKS, GATEADRESSE, MATRIKKELADRESSE }
    enum MidlertidigadresseType { UTLAND, NORGE }

    private static final ObjectMapper json = new ObjectMapper().activateDefaultTyping(new DefaultBaseTypeLimitingValidator());
    private KodeverkManager kodeverkManager = mock(KodeverkManager.class);
    private FIMMapper mapper = new FIMMapper(kodeverkManager);
    private static final LocalDate FIXED_DATE = LocalDate.parse("2020-10-12");
    private static XMLGregorianCalendar FIXED_CALENDAR;
    static {
        try {
            FIXED_CALENDAR = DatatypeFactory
                    .newInstance()
                    .newXMLGregorianCalendarDate(2020, 10, 12, 0);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void map_request_should_handle_null() {
        FinnPersonRequest request1 = new FinnPersonRequest();
        FinnPersonRequest request2 = new FinnPersonRequest();
        UtvidetPersonsok utvidetPersonsok = new UtvidetPersonsok();
        request2.setUtvidetPersonsok(utvidetPersonsok);

        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest wsRequest1 = mapper.map(request1, no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest.class);
        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest wsRequest2 = mapper.map(request2, no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest.class);

        assertNotNull(wsRequest1);
        assertNotNull(wsRequest2);
        assertNull(wsRequest1.getAdresseFilter());
        assertNull(wsRequest1.getPersonFilter());
        assertNull(wsRequest1.getSoekekriterie());
    }

    @Test
    public void map_request_should_convert_all_field() {
        FinnPersonRequest request = new FinnPersonRequest();
        UtvidetPersonsok utvidetPersonsok = new UtvidetPersonsok();
        utvidetPersonsok.setFornavn("fornavn");
        utvidetPersonsok.setEtternavn("etternavn");
        utvidetPersonsok.setGatenavn("gatenavn");
        utvidetPersonsok.setHusnummer("10");
        utvidetPersonsok.setHusbokstav("husbokstav");
        utvidetPersonsok.setPostnummer("postnummer");
        utvidetPersonsok.setKontonummer("kontonummer");
        utvidetPersonsok.setKommunenr("kommunenr");
        utvidetPersonsok.setFodselsdatoFra(FIXED_DATE);
        utvidetPersonsok.setFodselsdatoTil(FIXED_DATE);
        utvidetPersonsok.setAlderFra(10);
        utvidetPersonsok.setAlderTil(20);
        utvidetPersonsok.setKjonn(Kjonn.K);


        request.setUtvidetPersonsok(utvidetPersonsok);
        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest wsRequest = mapper.map(request, no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest.class);

        AdresseFilter adresseFilter = wsRequest.getAdresseFilter();
        assertNotNull(adresseFilter);
        assertEquals("husbokstav", adresseFilter.getHusbokstav());
        assertEquals("postnummer", adresseFilter.getPostnummer());
        assertEquals((Integer) 10, adresseFilter.getGatenummer());

        PersonFilter personFilter = wsRequest.getPersonFilter();
        assertNotNull(personFilter);
        assertEquals((Integer)10, personFilter.getAlderFra());
        assertEquals((Integer)20, personFilter.getAlderTil());
        assertEquals("kommunenr", personFilter.getEnhetId());
        assertEquals(FIXED_CALENDAR, personFilter.getFoedselsdatoFra());
        assertEquals(FIXED_CALENDAR, personFilter.getFoedselsdatoTil());
        assertEquals("K", personFilter.getKjoenn());

        Soekekriterie soekekriterie = wsRequest.getSoekekriterie();
        assertNotNull(soekekriterie);
        assertNull(soekekriterie.getNavn());
        assertNull(soekekriterie.getNavnFTE());
        assertEquals("fornavn", soekekriterie.getFornavn());
        assertNull(soekekriterie.getFornavnFTE());
        assertEquals("etternavn", soekekriterie.getEtternavn());
        assertNull(soekekriterie.getEtternavnFTE());
        assertEquals("gatenavn", soekekriterie.getGatenavn());
        assertEquals("kontonummer", soekekriterie.getBankkontoNorge());
    }

    @Test
    public void map_response_should_map_all_field() throws IOException {
        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse wsResponse = new no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse();
        wsResponse.setTotaltAntallTreff(1);
        wsResponse.getPersonListe().addAll(asList(
                lagPerson(MidlertidigadresseType.UTLAND, BostedsType.POSTBOKS),
                lagPerson(MidlertidigadresseType.NORGE, BostedsType.GATEADRESSE),
                lagPerson(MidlertidigadresseType.NORGE, BostedsType.MATRIKKELADRESSE)
        ));

        assertEquals(readSnapshot(snapshot), createSnapshot(wsResponse));
    }

    @Test
    @Ignore
    public  void writeSnapshots() {
        no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse wsResponse = new no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse();
        wsResponse.setTotaltAntallTreff(1);
        wsResponse.getPersonListe().addAll(asList(
                lagPerson(MidlertidigadresseType.UTLAND, BostedsType.POSTBOKS),
                lagPerson(MidlertidigadresseType.NORGE, BostedsType.GATEADRESSE),
                lagPerson(MidlertidigadresseType.NORGE, BostedsType.MATRIKKELADRESSE)
        ));

        writeSnapshot(snapshot, wsResponse);
    }

    private static String readSnapshot(File file) {
        try {
            return new String(Files.readAllBytes(snapshot.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeSnapshot(File file, Object object) {
        try {
            json.writer().writeValue(file, object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createSnapshot(Object object) {
        try {
            return json.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Bruker lagPerson(MidlertidigadresseType midlertidigAdresseUtland, BostedsType bostedPostboks) {
        Bruker person = new Bruker();
        Diskresjonskoder diskresjonskode = new Diskresjonskoder();
        diskresjonskode.setKodeverksRef("kodeverkref");
        diskresjonskode.setKodeRef("SPSF");
        diskresjonskode.setValue("SPSF");
        Bostedsadresse bostedsadresse = lagBostedAdresse(bostedPostboks);

        Postadresse postadresse = new Postadresse();
        UstrukturertAdresse ustrukturertAdresse = new UstrukturertAdresse();
        ustrukturertAdresse.setAdresselinje1("adresselinje1");
        ustrukturertAdresse.setAdresselinje2("adresselinje2");
        ustrukturertAdresse.setAdresselinje3("adresselinje3");
        ustrukturertAdresse.setAdresselinje4("adresselinje4");
        Landkoder landkode = new Landkoder();
        landkode.setKodeverksRef("landkodeRef");
        landkode.setKodeRef("kodeRef");
        landkode.setValue("value");
        ustrukturertAdresse.setLandkode(landkode);
        postadresse.setUstrukturertAdresse(ustrukturertAdresse);

        Kjoenn kjoenn = new Kjoenn();
        Kjoennstyper kjoennstyper = new Kjoennstyper();
        kjoennstyper.setKodeverksRef("kjoennKodeverkRef");
        kjoennstyper.setKodeRef("kjoennKodeRef");
        kjoennstyper.setValue("M");
        kjoenn.setKjoenn(kjoennstyper);

        Personnavn personnavn = new Personnavn();
        personnavn.setFornavn("fornavn");
        personnavn.setEtternavn("etternavn");
        personnavn.setMellomnavn("mellomnavn");
        personnavn.setSammensattNavn("fornavn mellomnavn etternavn");

        Personstatus personstatus = new Personstatus();
        Personstatuser personstatuser = new Personstatuser();
        personstatuser.setKodeverksRef("personstatusKodeverkRef");
        personstatuser.setKodeRef("personstatusKodeRef");
        personstatuser.setValue("DOD");
        personstatus.setPersonstatus(personstatuser);

        NorskIdent ident = new NorskIdent();
        Personidenter type = new Personidenter();
        type.setKodeverksRef("personidenterKodeverkRef");
        type.setKodeRef("personidenterKodeRef");
        type.setValue("FNR");
        ident.setIdent("ident");
        ident.setType(type);

        Postadressetyper gjeldendePostadresseType = new Postadressetyper();
        gjeldendePostadresseType.setKodeverksRef("postadressetyperKodeverkRef");
        gjeldendePostadresseType.setKodeRef("postadressetyperKodeRef");
        gjeldendePostadresseType.setValue("NA");

        MidlertidigPostadresse midlertidigPostadresse = lagMidlertidigAdresse(midlertidigAdresseUtland);

        person.setDiskresjonskode(diskresjonskode);
        person.setPostadresse(postadresse);
        person.setBostedsadresse(bostedsadresse);
        person.setKjoenn(kjoenn);
        person.setPersonnavn(personnavn);
        person.setPersonstatus(personstatus);
        person.setIdent(ident);
        person.setGjeldendePostadresseType(gjeldendePostadresseType);
        person.setMidlertidigPostadresse(midlertidigPostadresse);

        return person;
    }

    private static Bostedsadresse lagBostedAdresse(BostedsType type) {
        StrukturertAdresse adresse;
        Postnummer postnummer = new Postnummer();
        postnummer.setKodeverksRef("postnummerKodeverkRef");
        postnummer.setKodeverksRef("postnummerKodeRef");
        postnummer.setValue("1234");
        if (type == BostedsType.POSTBOKS) {
            adresse = new PostboksadresseNorsk();
            PostboksadresseNorsk local = (PostboksadresseNorsk)adresse;
            local.setPoststed(postnummer);
            local.setPostboksanlegg("postboksanlegg");
            local.setPostboksnummer("postboksnummer");

        } else {
            if (type == BostedsType.GATEADRESSE) {
                adresse = new Gateadresse();
                Gateadresse local = (Gateadresse)adresse;
                local.setGatenavn("gatenummer");
                local.setGatenavn("gatenavn");
                local.setHusnummer(BigInteger.ONE);
                local.setHusbokstav("husbokstav");
            } else {
                adresse = new Matrikkeladresse();
                Matrikkeladresse local = (Matrikkeladresse)adresse;
                local.setEiendomsnavn("eiendomsnavn");
                Matrikkelnummer matrikkelnummer = new Matrikkelnummer();
                matrikkelnummer.setGaardsnummer("gaardsnummer");
                matrikkelnummer.setBruksnummer("bruksnummer");
                matrikkelnummer.setFestenummer("festenummer");
                matrikkelnummer.setSeksjonsnummer("seksjonsnummer");
                matrikkelnummer.setUndernummer("undernummer");
                local.setMatrikkelnummer(matrikkelnummer);
            }
            StedsadresseNorge local = (StedsadresseNorge)adresse;
            local.setPoststed(postnummer);
            local.setBolignummer("bolignummer");
            local.setKommunenummer("kommunenr");
        }
        adresse.setTilleggsadresse("tilleggsadresse");
        adresse.setTilleggsadresseType("tilleggsadresseType");
        Landkoder landkode = new Landkoder();
        landkode.setKodeverksRef("landkodeRef");
        landkode.setKodeRef("kodeRef");
        landkode.setValue("value");
        adresse.setLandkode(landkode);

        Bostedsadresse bosted = new Bostedsadresse();
        bosted.setStrukturertAdresse(adresse);
        return bosted;
    }

    private static MidlertidigPostadresse lagMidlertidigAdresse(MidlertidigadresseType type) {
        MidlertidigPostadresse adresse;
        Landkoder landkode = new Landkoder();
        landkode.setKodeverksRef("landkodeRef");
        landkode.setKodeRef("kodeRef");
        landkode.setValue("value");
        UstrukturertAdresse ustrukturertAdresse = new UstrukturertAdresse();
        ustrukturertAdresse.setAdresselinje1("adresselinje1");
        ustrukturertAdresse.setAdresselinje2("adresselinje2");
        ustrukturertAdresse.setAdresselinje3("adresselinje3");
        ustrukturertAdresse.setAdresselinje4("adresselinje4");
        ustrukturertAdresse.setLandkode(landkode);

        if (type == MidlertidigadresseType.UTLAND) {
            adresse = new MidlertidigPostadresseUtland();
            MidlertidigPostadresseUtland local = (MidlertidigPostadresseUtland)adresse;
            local.setUstrukturertAdresse(ustrukturertAdresse);
        } else {
            adresse = new MidlertidigPostadresseNorge();
            MidlertidigPostadresseNorge local = (MidlertidigPostadresseNorge)adresse;
            local.setUstrukturertAdresse(ustrukturertAdresse);
        }
        Gyldighetsperiode periode = new Gyldighetsperiode();
        periode.setFom(FIXED_CALENDAR);
        periode.setTom(FIXED_CALENDAR);
        adresse.setPostleveringsPeriode(periode);

        return adresse;
    }
}

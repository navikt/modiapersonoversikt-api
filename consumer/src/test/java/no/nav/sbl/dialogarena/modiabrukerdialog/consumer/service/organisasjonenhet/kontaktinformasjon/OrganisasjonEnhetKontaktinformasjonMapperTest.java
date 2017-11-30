package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.Apningstid;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.Apningstider;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.Ukedag;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonMapper;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class OrganisasjonEnhetKontaktinformasjonMapperTest {

    public static final int ANTALL_PUBLIKUMSMOTTAK = 2;
    public static final String GATENAVN = "Islands Gate";
    public static final String ENHET_NAVN = "NAV Eidsvoll";
    public static final String ENHET_ID = "1337";
    public static final String HUSNUMMER = "47";
    public static final String HUSBOKSTAV = "A";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final String APNER_TIRSDAG_TIME = "9";
    public static final String APNER_TIRSDAG_MINUTT = "15";
    public static final String APNER_TIRSDAG_SEKUND = "17";
    public static final String STENGER_TIRSDAG_TIME = "18";
    public static final String STENGER_TIRSDAG_MINUTT = "0";
    public static final String STENGER_TIRSDAG_SEKUND = "17";


    @Test
    @DisplayName("Mapping av organisasjonsenhet")
    void mapperTest() {
        Organisasjonsenhet organisasjonsenhetWS = mockOrganisasjonsenhet();

        OrganisasjonEnhetKontaktinformasjon organisasjonEnhetKontaktinformasjon = OrganisasjonEnhetKontaktinformasjonMapper.map(organisasjonsenhetWS);

        assertAll("Organisasjonsenhet mapping",
                () -> assertEquals(ENHET_ID, organisasjonEnhetKontaktinformasjon.getEnhetId()),
                () -> assertEquals(ENHET_NAVN, organisasjonEnhetKontaktinformasjon.getEnhetNavn())
        );
    }

    @Test
    @DisplayName("Mapping av kontaktinformasjon")
    void kontaktinformasjonsMapping() {
        Organisasjonsenhet organisasjonsenhetWS = mockOrganisasjonsenhet();

        OrganisasjonEnhetKontaktinformasjon organisasjonEnhetKontaktinformasjon = OrganisasjonEnhetKontaktinformasjonMapper.map(organisasjonsenhetWS);

        assertAll("Kontaktinformasjon",
                () -> assertEquals(ANTALL_PUBLIKUMSMOTTAK, organisasjonEnhetKontaktinformasjon.getKontaktinformasjon()
                        .getPublikumsmottak().size())
        );
    }

    @Test
    @DisplayName("Mapping av adresse for publikumsmottak")
    void adresseMapping() {
        Organisasjonsenhet organisasjonsenhetWS = mockOrganisasjonsenhet();

        OrganisasjonEnhetKontaktinformasjon organisasjonEnhetKontaktinformasjon = OrganisasjonEnhetKontaktinformasjonMapper.map(organisasjonsenhetWS);

        no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.Gateadresse besoeksadresse = organisasjonEnhetKontaktinformasjon.getKontaktinformasjon().getPublikumsmottak().get(0).getBesoeksadresse();

        assertAll("Gateadressemapping",
                () -> assertEquals(HUSNUMMER, besoeksadresse.getHusnummer()),
                () -> assertEquals(HUSBOKSTAV, besoeksadresse.getHusbokstav()),
                () -> assertEquals(GATENAVN, besoeksadresse.getGatenavn())
        );
    }

    @Test
    @DisplayName("Mapping av adresse når adresse er null")
    void adresseMappingNaarAdresseErNull() {
        Organisasjonsenhet organisasjonsenhetWS = mockOrganisasjonsenhet();
        organisasjonsenhetWS.getKontaktinformasjon().getPublikumsmottakListe().get(0).setBesoeksadresse(null);

        OrganisasjonEnhetKontaktinformasjon organisasjonEnhetKontaktinformasjon = OrganisasjonEnhetKontaktinformasjonMapper.map(organisasjonsenhetWS);

        no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.Gateadresse besoeksadresse = organisasjonEnhetKontaktinformasjon.getKontaktinformasjon().getPublikumsmottak().get(0).getBesoeksadresse();

        assertNull(besoeksadresse);
    }

    @Test
    @DisplayName("Mapping av åpningstider for publikumsmottak")
    void apningstiderMapping() {
        Organisasjonsenhet organisasjonsenhetWS = mockOrganisasjonsenhet();

        OrganisasjonEnhetKontaktinformasjon organisasjonEnhetKontaktinformasjon = OrganisasjonEnhetKontaktinformasjonMapper.map(organisasjonsenhetWS);

        Apningstider apningstider = organisasjonEnhetKontaktinformasjon.getKontaktinformasjon().getPublikumsmottak().get(0).getApningstider();
        Apningstid apningstidTirsdag = getApningstid(apningstider, Ukedag.TIRSDAG);

        assertEquals(Ukedag.TIRSDAG, apningstidTirsdag.getUkedag());
        assertAll("Tidspunkt åpent fra",
                () -> assertEquals(APNER_TIRSDAG_TIME, apningstidTirsdag.getApentFra().getTime()),
                () -> assertEquals(APNER_TIRSDAG_MINUTT, apningstidTirsdag.getApentFra().getMinutt()),
                () -> assertEquals(APNER_TIRSDAG_SEKUND, apningstidTirsdag.getApentFra().getSekund())
        );
        assertAll("Tidspunkt åpent til",
                () -> assertEquals(STENGER_TIRSDAG_TIME, apningstidTirsdag.getApentTil().getTime()),
                () -> assertEquals(STENGER_TIRSDAG_MINUTT, apningstidTirsdag.getApentTil().getMinutt()),
                () -> assertEquals(STENGER_TIRSDAG_SEKUND, apningstidTirsdag.getApentTil().getSekund())
        );
    }

    private Apningstid getApningstid(Apningstider apningstider, Ukedag ukedag) {
        return apningstider.getApningstider().stream()
                .filter(apningstid -> apningstid.getUkedag() == ukedag)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private Organisasjonsenhet mockOrganisasjonsenhet() {
        Organisasjonsenhet organisasjonsenhetWS = new Organisasjonsenhet();
        organisasjonsenhetWS.setEnhetId(ENHET_ID);
        organisasjonsenhetWS.setEnhetNavn(ENHET_NAVN);
        organisasjonsenhetWS.setKontaktinformasjon(mockKontaktinformasjon());
        return organisasjonsenhetWS;
    }

    private KontaktinformasjonForOrganisasjonsenhet mockKontaktinformasjon() {
        KontaktinformasjonForOrganisasjonsenhet kontaktinformasjonForOrganisasjonsenhetWS = new KontaktinformasjonForOrganisasjonsenhet();
        kontaktinformasjonForOrganisasjonsenhetWS.getPublikumsmottakListe()
                .addAll(Arrays.asList(mockPublikumsmottak(), mockPublikumsmottak()));
        return kontaktinformasjonForOrganisasjonsenhetWS;
    }

    private Publikumsmottak mockPublikumsmottak() {
        Publikumsmottak publikumsmottak = new Publikumsmottak();
        publikumsmottak.setAapningstider(mockApningstider());
        publikumsmottak.setBesoeksadresse(mockGateadresse());
        return publikumsmottak;
    }

    private Gateadresse mockGateadresse() {
        Gateadresse gateadresse = new Gateadresse();
        gateadresse.setGatenavn(GATENAVN);
        gateadresse.setHusnummer(HUSNUMMER);
        gateadresse.setHusbokstav(HUSBOKSTAV);
        return gateadresse;
    }

    private Aapningstider mockApningstider() {
        Aapningstider aapningstider = new Aapningstider();
        Aapningstid aapningstidTirsdag = new Aapningstid();
        aapningstidTirsdag.setAapentFra(lagTidPaDagen(String.valueOf(APNER_TIRSDAG_TIME), String.valueOf(APNER_TIRSDAG_MINUTT), String.valueOf(APNER_TIRSDAG_SEKUND)));
        aapningstidTirsdag.setAapentTil(lagTidPaDagen(String.valueOf(STENGER_TIRSDAG_TIME), String.valueOf(STENGER_TIRSDAG_MINUTT), String.valueOf(STENGER_TIRSDAG_SEKUND)));
        aapningstider.setTirsdag(aapningstidTirsdag);
        return aapningstider;
    }

    private XMLGregorianCalendar lagTidPaDagen(String time, String minutt, String sekund) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(DATE_FORMAT.format(lagTidspunkt(time, minutt, sekund)));
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Date lagTidspunkt(String time, String minutt, String sekund) {
        try {
            return DATE_FORMAT.parse(time + ":" + minutt + ":" + sekund);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
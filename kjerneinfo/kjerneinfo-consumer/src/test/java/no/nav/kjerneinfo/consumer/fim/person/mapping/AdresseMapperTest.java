package no.nav.kjerneinfo.consumer.fim.person.mapping;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.kjerneinfo.domain.person.*;
import no.nav.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdresseMapperTest {

    private static final String ADR1 = "linje 1";
    private static final String ADR2 = "linje 2";
    private static final String ADR3 = "linje 3";
    private static final String ADR4 = "linje 4";
    private static final String LANDKODE = "SWE";
    private static final String GATENAVN = "GATENAVN";
    private static final int GATENUMMER = 213412;
    private static final String POSTNUMMER = "2070";
    private static final String EIENDOMSNAVN = "EIENDOMSNAVN";
    private static final String TILLEGGSADRESSE = "MittTillegg";
    private static final String TILLEGGSADRESSE_TYPE = "C/O";
    private static final String TILLEGGSADRESSE_TYPE_MATRIKKELADRESSE = "Matrikkeladresse";
    private static final String MIDLERTIDIG_POSTADRESSE_NORGE = "MIDLERTIDIG_POSTADRESSE_NORGE";
    private static final String POSTLEVERINGSPERIODE_TOM = "2018-02-02";
    private static final String ENDRET_AV = "Tom Jones";
    private static final String ENDRINGSTIDSPUNKT = "2015-02-02";
    private static final String POSTBOKSANLEGG = "Ryen Postanlegg";
    private static final String POSTBOKSNUMMER = "13337";
    private static final String LANDKODE_BESKRIVELSE = "Sverige";

    private KjerneinfoMapper mapper;
    private final DefaultKodeverkmanager kodeverk = mock(DefaultKodeverkmanager.class);

    @Before
    public void setUp() {
        mapper = new KjerneinfoMapper(kodeverk);
    }

    @Test
    public void bostedsadresseToAdresse() {
        WSGateadresse wsGateadresse = getGateadresse();
        WSBostedsadresse wsBostedsadresse = new WSBostedsadresse().withStrukturertAdresse(wsGateadresse);

        Adresselinje adresselinje = mapper.map(wsBostedsadresse, Adresselinje.class);
        Adresse adresse = (Adresse) adresselinje;

        assertThat(adresse.getGatenavn(), is(wsGateadresse.getGatenavn()));
    }

    @Test
    public void matrikkeladresseTilAdresse() {
        WSMatrikkeladresse wsMatrikkeladresse = getMatrikkelAdresse();
        WSBostedsadresse wsBostedsadresse = new WSBostedsadresse().withStrukturertAdresse(wsMatrikkeladresse);

        Adresselinje adresse = mapper.map(wsBostedsadresse, Adresselinje.class);
        Matrikkeladresse matrikkeladresse = (Matrikkeladresse) adresse;

        assertThat(matrikkeladresse.getEiendomsnavn(), is(wsMatrikkeladresse.getEiendomsnavn()));
        assertThat(matrikkeladresse.getPostnummer(), is(wsMatrikkeladresse.getPoststed().getValue()));
        assertThat(matrikkeladresse.getTilleggsadresseMedType(),
                is(wsMatrikkeladresse.getTilleggsadresseType() + " " + wsMatrikkeladresse.getTilleggsadresse()));
    }

    @Test
    public void matrikkeladresseIgnorererMatrikkeladresseSomTilleggsadressetype(){
        WSMatrikkeladresse wsMatrikkeladresse = getMatrikkelAdresse();
        wsMatrikkeladresse.withTilleggsadresseType(TILLEGGSADRESSE_TYPE_MATRIKKELADRESSE);
        WSBostedsadresse wsBostedsadresse = new WSBostedsadresse().withStrukturertAdresse(wsMatrikkeladresse);

        Adresselinje adresse = mapper.map(wsBostedsadresse, Adresselinje.class);
        Matrikkeladresse matrikkeladresse = (Matrikkeladresse) adresse;

        assertThat(matrikkeladresse.getTilleggsadresseMedType(), is(wsMatrikkeladresse.getTilleggsadresse()));
    }

    @Test
    public void postadresseTilAdresseLinje() {
        WSPostadresse wsPostadresse = new WSPostadresse()
                .withEndretAv("AdresseEndrer")
                .withEndringstidspunkt(getCurrentXmlGregorianCalendar())
                .withUstrukturertAdresse(new WSUstrukturertAdresse().withAdresselinje1(ADR1));

        Adresselinje adresselinje = mapper.map(wsPostadresse, Adresselinje.class);

        assertThat(adresselinje.getEndringsinformasjon().getEndretAv(), is(wsPostadresse.getEndretAv()));
    }

    @Test
    public void brukerMedUtenlandskAdresse() {
        when(kodeverk.getBeskrivelseForKode(any(), any(), any())).thenReturn(LANDKODE_BESKRIVELSE);
        WSBruker wsBruker = new WSBruker().withMidlertidigPostadresse(utenlandskAdresse());

        Person person = mapper.map(wsBruker, Person.class);

        assertMidlertidigAdresse((WSMidlertidigPostadresseUtland) wsBruker.getMidlertidigPostadresse(), person);
    }

    @Test
    public void brukerMedUtenlandskAdresseSomHarLandkodeNull() {
        WSUstrukturertAdresse wsUstrukturertAdresse = getUstrukturertAdresse().withLandkode(null);
        WSBruker wsBruker = new WSBruker()
                .withMidlertidigPostadresse(utenlandskAdresse().withUstrukturertAdresse(wsUstrukturertAdresse));

        Person person = mapper.map(wsBruker, Person.class);

        String expected = wsUstrukturertAdresse.getAdresselinje1() + " " + wsUstrukturertAdresse.getAdresselinje2() +
                " " + wsUstrukturertAdresse.getAdresselinje3() + " " + wsUstrukturertAdresse.getAdresselinje4();
        assertEquals(expected, person.getPersonfakta().getAlternativAdresse().getAdresselinje());
    }

    @Test
    public void wSPostboksadresseNorskMapping() {
        WSPostboksadresseNorsk wsPostboksadresseNorsk = new WSPostboksadresseNorsk()
                .withPostboksanlegg("Daudbilbakken")
                .withPostboksnummer("1")
                .withPoststed(new WSPostnummer()
                        .withKodeRef("9999"))
                .withTilleggsadresse(TILLEGGSADRESSE)
                .withTilleggsadresseType(TILLEGGSADRESSE_TYPE);

        Postboksadresse to = mapper.map(wsPostboksadresseNorsk, Postboksadresse.class);

        assertEquals(wsPostboksadresseNorsk.getPostboksanlegg(), to.getPostboksanlegg());
        assertEquals(wsPostboksadresseNorsk.getPostboksnummer(), to.getPostboksnummer());
        assertThat(to.getTilleggsadresseMedType(), is(TILLEGGSADRESSE_TYPE + " " + TILLEGGSADRESSE));
    }

    @Test
    public void gjeldendePostadressetypeMapping() {
        WSBruker from = new WSBruker().withGjeldendePostadressetype(new WSPostadressetyper().withValue(MIDLERTIDIG_POSTADRESSE_NORGE));

        Person to = mapper.map(from, Person.class);
        Kodeverdi gjeldendePostadressetype = to.getPersonfakta().getGjeldendePostadressetype();

        assertThat(gjeldendePostadressetype.getKodeRef(), is(MIDLERTIDIG_POSTADRESSE_NORGE));
    }

    @Test
    public void midlertidigPostadresseNorgeMapping() {
        WSBruker from = new WSBruker().withMidlertidigPostadresse(getMidlertidigPostadresseNorge());

        Person to = mapper.map(from, Person.class);
        Adresselinje alternativAdresse = to.getPersonfakta().getAlternativAdresse();

        assertThat(alternativAdresse, instanceOf(Adresse.class));
        Adresse adresse = (Adresse) alternativAdresse;
        assertThat(adresse.getPostleveringsPeriode().getTo().toString(),
                is(POSTLEVERINGSPERIODE_TOM));
        assertThat(alternativAdresse.getEndringsinformasjon().getEndretAv(), is(ENDRET_AV));
        assertThat(alternativAdresse.getEndringsinformasjon().getSistOppdatert().toLocalDate().toString(), is(ENDRINGSTIDSPUNKT));
        assertThat(adresse.getPostnummer(), is(POSTNUMMER));
        assertThat(adresse.getTilleggsadresseMedType(), is(TILLEGGSADRESSE_TYPE + " " + TILLEGGSADRESSE));
    }

    @Test
    public void midlertidigMatrikkelPostadresse() {
        WSBruker from = new WSBruker();
        from.setMidlertidigPostadresse(new WSMidlertidigPostadresseNorge()
                .withEndretAv(ENDRET_AV)
                .withEndringstidspunkt(getMockDato(ENDRINGSTIDSPUNKT))
                .withPostleveringsPeriode(new WSGyldighetsperiode()
                        .withFom(getMockDato("2015-02-02"))
                        .withTom(getMockDato(POSTLEVERINGSPERIODE_TOM)))
                .withStrukturertAdresse(new WSMatrikkeladresse()
                        .withEiendomsnavn(EIENDOMSNAVN).withPoststed(new WSPostnummer().withValue(POSTNUMMER))));


        Person to = mapper.map(from, Person.class);
        Matrikkeladresse matrikkeladresse = (Matrikkeladresse) to.getPersonfakta().getAlternativAdresse();

        assertThat(matrikkeladresse.getEiendomsnavn(), is(EIENDOMSNAVN));
        assertThat(matrikkeladresse.getPostnummer(), is(POSTNUMMER));
        assertThat(matrikkeladresse.getEndringsinformasjon().getEndretAv(), is(ENDRET_AV));
        assertThat(matrikkeladresse.getEndringsinformasjon().getSistOppdatert().toLocalDate().toString(), is(ENDRINGSTIDSPUNKT));
        assertThat(matrikkeladresse.getPostleveringsPeriode().getTo().toString(),
                is(POSTLEVERINGSPERIODE_TOM));

    }

    @Test
    public void midlertidigPostadresseUtlandMapping() {
        WSBruker from = new WSBruker();
        from.setMidlertidigPostadresse(new WSMidlertidigPostadresseUtland()
                .withUstrukturertAdresse(new WSUstrukturertAdresse()
                        .withAdresselinje1("Test")
                        .withLandkode(new WSLandkoder()
                                .withValue(LANDKODE)))
                .withPostleveringsPeriode(new WSGyldighetsperiode()
                        .withTom(getMockDato(POSTLEVERINGSPERIODE_TOM)))
                .withEndretAv(ENDRET_AV)
                .withEndringstidspunkt(getMockDato(POSTLEVERINGSPERIODE_TOM)));

        Person to = mapper.map(from, Person.class);
        AlternativAdresseUtland alternativAdresseUtland = (AlternativAdresseUtland) to.getPersonfakta().getAlternativAdresse();

        assertThat(alternativAdresseUtland.getPostleveringsPeriode().getTo().toString(),
                is(POSTLEVERINGSPERIODE_TOM));
        assertThat(alternativAdresseUtland.getAdresselinje1(), is("Test"));
        assertThat(alternativAdresseUtland.getLandkode().getKodeRef(), is(LANDKODE));
        assertThat(alternativAdresseUtland.getEndringsinformasjon().getEndretAv(), is(ENDRET_AV));
        assertThat(alternativAdresseUtland.getEndringsinformasjon().getSistOppdatert().toLocalDate().toString(),
                is(POSTLEVERINGSPERIODE_TOM));
    }

    @Test
    public void postboksadresseMapping() {
        WSBruker from = new WSBruker();
        WSMidlertidigPostadresseNorge postboksadresse = new WSMidlertidigPostadresseNorge()
                .withEndretAv(ENDRET_AV)
                .withEndringstidspunkt(getMockDato(ENDRINGSTIDSPUNKT));
        postboksadresse.setStrukturertAdresse(new WSPostboksadresseNorsk()
                .withLandkode(new WSLandkoder().withValue(LANDKODE))
                .withPostboksanlegg(POSTBOKSANLEGG)
                .withPostboksnummer(POSTBOKSNUMMER)
                .withPoststed(new WSPostnummer().withValue(POSTNUMMER).withKodeRef(POSTNUMMER))
                .withTilleggsadresse(TILLEGGSADRESSE));
        from.setMidlertidigPostadresse(postboksadresse);

        Person to = mapper.map(from, Person.class);
        Postboksadresse alternativAdresse = (Postboksadresse) to.getPersonfakta().getAlternativAdresse();

        assertThat(alternativAdresse.getPostboksanlegg(), is(POSTBOKSANLEGG));
        assertThat(alternativAdresse.getPostboksnummer(), is(POSTBOKSNUMMER));
        assertThat(alternativAdresse.getPoststed(), is(POSTNUMMER));
        assertThat(alternativAdresse.getEndringsinformasjon().getEndretAv(), is(ENDRET_AV));
        assertThat(alternativAdresse.getEndringsinformasjon().getSistOppdatert().toLocalDate().toString(), is(ENDRINGSTIDSPUNKT));
        assertThat(alternativAdresse.getTilleggsadresseMedType(), is(TILLEGGSADRESSE));
    }

    @Test
    public void adresseToWSGateadresse() {
        Adresse from = getAdresse();

        WSGateadresse to = mapper.map(from, WSGateadresse.class);

        assertEquals(from.getBolignummer(), to.getBolignummer());
    }


    private WSGateadresse getGateadresse() {
        WSGateadresse wsGateadresse = new WSGateadresse()
                .withHusbokstav("B")
                .withHusnummer(2)
                .withGatenavn(GATENAVN)
                .withGatenummer(GATENUMMER);
        setStedsadresseinfo(wsGateadresse);
        return wsGateadresse;
    }

    private WSMatrikkeladresse getMatrikkelAdresse() {
        WSMatrikkeladresse matrikkeladresse = new WSMatrikkeladresse()
                .withEiendomsnavn("Pengebingen")
                .withMatrikkelnummer(getMockMatrikkelnummer());
        setStedsadresseinfo(matrikkeladresse);
        return matrikkeladresse;
    }

    private WSMidlertidigPostadresseNorge getMidlertidigPostadresseNorge() {
        return new WSMidlertidigPostadresseNorge()
                .withStrukturertAdresse(getGateadresse())
                .withEndretAv(ENDRET_AV)
                .withEndringstidspunkt(getMockDato(ENDRINGSTIDSPUNKT))
                .withPostleveringsPeriode(new WSGyldighetsperiode().withTom(getMockDato(POSTLEVERINGSPERIODE_TOM)));
    }

    private WSStedsadresseNorge setStedsadresseinfo(WSStedsadresseNorge adresse) {
        adresse.setBolignummer("H001");
        adresse.setKommunenummer("1234");
        adresse.setPoststed(new WSPostnummer().withValue(POSTNUMMER));
        adresse.setTilleggsadresse(TILLEGGSADRESSE);
        adresse.setTilleggsadresseType(TILLEGGSADRESSE_TYPE);
        adresse.setLandkode(new WSLandkoder().withKodeRef("NO"));
        return adresse;
    }

    private WSMatrikkelnummer getMockMatrikkelnummer() {
        return new WSMatrikkelnummer()
                .withBruksnummer("Brnr. 1")
                .withFestenummer("Festenr. 1")
                .withGaardsnummer("Gaardsnr. 1")
                .withSeksjonsnummer("Seksjnr. 1")
                .withUndernummer("Undernr. 1");
    }

    private WSMidlertidigPostadresseUtland utenlandskAdresse() {
        return new WSMidlertidigPostadresseUtland()
                .withUstrukturertAdresse(getUstrukturertAdresse());
    }

    private WSUstrukturertAdresse getUstrukturertAdresse() {
        return new WSUstrukturertAdresse()
                .withAdresselinje1(ADR1)
                .withAdresselinje2(ADR2)
                .withAdresselinje3(ADR3)
                .withAdresselinje4(ADR4)
                .withLandkode(new WSLandkoder().withValue(LANDKODE));
    }

    private void assertMidlertidigAdresse(WSMidlertidigPostadresseUtland from, Person person) {
        WSUstrukturertAdresse fromAdresse = from.getUstrukturertAdresse();
        String expected = fromAdresse.getAdresselinje1() + " " + fromAdresse.getAdresselinje2() + " " +
                fromAdresse.getAdresselinje3() + " " + fromAdresse.getAdresselinje4() + " " + LANDKODE_BESKRIVELSE;

        assertEquals(expected, person.getPersonfakta().getAlternativAdresse().getAdresselinje());
    }

    private Adresse getAdresse() {
        Adresse adresse = new Adresse();
        adresse.setBolignummer("H001");
        adresse.setGatenavn("Apalveien");
        adresse.setGatenummer("1");
        adresse.setPostnummer("1234");
        adresse.setPoststednavn("Andeby");
        adresse.setTilleggsadresse("Ingen");
        Endringsinformasjon endringsinformasjon = new Endringsinformasjon();
        endringsinformasjon.setEndretAv("AdresseEndrer");
        endringsinformasjon.setSistOppdatert(LocalDateTime.now());
        adresse.setEndringsinformasjon(endringsinformasjon);
        return adresse;
    }

    private XMLGregorianCalendar getMockDato(String date) {
        GregorianCalendar cal = GregorianCalendar.from((java.time.LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault())));
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static XMLGregorianCalendar getCurrentXmlGregorianCalendar() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}

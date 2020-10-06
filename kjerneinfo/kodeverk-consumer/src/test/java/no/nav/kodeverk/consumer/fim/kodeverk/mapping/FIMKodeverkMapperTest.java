package no.nav.kodeverk.consumer.fim.kodeverk.mapping;

import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.kodeverk.consumer.fim.kodeverk.mock.KodeverkMockFactory;
import no.nav.kodeverk.consumer.utils.KodeverkMapper;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLPeriode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.finnkodeverkliste.Kodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class FIMKodeverkMapperTest {

    private KodeverkMapper mapper;
    private KodeverkMockFactory mockFactory;

    @Before
    public void setUp() throws Exception {
        mapper = KodeverkMapper.getInstance();
        mockFactory = new KodeverkMockFactory();
    }

    @Test
    public void testFinnkodeverklisteResponse() {
        XMLFinnKodeverkListeResponse from = new XMLFinnKodeverkListeResponse();
        from.getKodeverkListe().add(mockFactory.getMockFinnKodeverk("navn1", "eier1", "1", 1));
        from.getKodeverkListe().add(mockFactory.getMockFinnKodeverk("navn2", "eier2", "2", 2));

        no.nav.kodeverk.consumer.fim.kodeverk.to.meldinger.FinnKodeverkListeResponse to = mapper.map(from);

        assertEquals(from.getKodeverkListe().size(), to.getKodeverkListe().size());
    }

    @Test
    public void hentKodeverkRequest() {
        no.nav.kodeverk.consumer.fim.kodeverk.to.meldinger.HentKodeverkRequest from = new no.nav.kodeverk.consumer.fim.kodeverk.to.meldinger.HentKodeverkRequest();
        from.setNavn("navn1");
        from.setSpraak("NO_nb");
        from.setVersjonsnummer("56");

        XMLHentKodeverkRequest to = mapper.map(from);

        assertEquals(from.getNavn(), to.getNavn());
        assertEquals(from.getSpraak(), to.getSpraak());
        assertEquals(from.getVersjonsnummer(), to.getVersjonsnummer());

    }

    @Test
    public void hentKodeverkResponse() {
        XMLHentKodeverkResponse from = new XMLHentKodeverkResponse();
        from.setKodeverk(new XMLEnkeltKodeverk());
        from.getKodeverk().setEier("Eier1");
        from.getKodeverk().setVersjonsnummer("23");
        from.getKodeverk().setNavn("navn1");

        no.nav.kodeverk.consumer.fim.kodeverk.to.meldinger.HentKodeverkResponse to = mapper.map(from);

        assertEquals(from.getKodeverk().getNavn(), to.getKodeverk().getNavn());
        assertEquals(from.getKodeverk().getEier(), to.getKodeverk().getEier());
        assertEquals(from.getKodeverk().getVersjonsnummer(), to.getKodeverk().getVersjonsnummer());
    }

    @Test
    public void kode() {
        XMLKode from = mockFactory.getMockKode("#MockKode");

        no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kode to = mapper.map(from);

        assertEquals(from.getTerm().size(), to.getTerm().size());
        assertEquals(from.getGyldighetsperiode().size(), to.getGyldighetsperiode().size());
    }

    /**
     * Tester mapping av Kodeverk-klassen brukt i finnkodeverk-operasjonen
     */
    @Test
    public void finnKodeverk() {
        Kodeverk from = mockFactory.getMockFinnKodeverk("Testkodeverk", "Eier", "1", 3);

        no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kodeverk to = mapper.map(from);

        assertEquals(from.getNavn(), to.getNavn());
        assertEquals(from.getEier(), to.getEier());
        assertEquals(from.getVersjonsnummer(), to.getVersjonsnummer());
    }

    @Test
    public void testPeriodeMapping() {
        XMLPeriode periodeDto = mockFactory.getMockPeriode();
        Periode domainPeriode = mapper.map(periodeDto);

        compareDateFields(periodeDto.getFom().toGregorianCalendar(), domainPeriode.getFrom());
        compareDateFields(periodeDto.getTom().toGregorianCalendar(), domainPeriode.getTo());
    }

    /**
     * Tester mapping av Kodeverk-klassen brukt i hentkodeverk-operasjonen
     */
    @Test
    public void hentkodeverk() {
        XMLKodeverk from = mockFactory.getMockHentKodeverk("Testkodeverk", "Eier", "1", 3);

        no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kodeverk to = mapper.map(from);

        assertEquals(from.getNavn(), to.getNavn());
        assertEquals(from.getEier(), to.getEier());
        assertEquals(from.getVersjonsnummer(), to.getVersjonsnummer());
    }

    @Test
    public void enkeltKodeverk() {
        XMLEnkeltKodeverk from = new XMLEnkeltKodeverk();
        from.setEier("EierEnkeltKodeverk");
        from.getKode().add(mockFactory.getMockKode("#1"));
        from.getKode().add(mockFactory.getMockKode("#2"));

        no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.EnkeltKodeverk to = mapper.map(from);

        assertEquals(from.getEier(), to.getEier());
        assertEquals(from.getKode().size(), to.getKode().size());
    }

    private void compareDateFields(GregorianCalendar from, LocalDate to) {
        assertEquals(from.get(Calendar.YEAR), to.getYear());
        assertEquals(from.get(Calendar.MONTH) + 1, to.getMonthOfYear());
        assertEquals(from.get(Calendar.DAY_OF_MONTH), to.getDayOfMonth());
    }
}

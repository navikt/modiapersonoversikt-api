package no.nav.kodeverk.consumer.fim.kodeverk.support;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kodeverk.consumer.fim.kodeverk.mock.KodeverkMockFactory;
import no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.EnkeltKodeverk;
import no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kode;
import no.nav.kodeverk.consumer.utils.KodeverkMapper;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DefaultKodeverkmanagerTest {
    private KodeverkMockFactory mockFactory;
    private DefaultKodeverkmanager kodeverkManager;
    private KodeverkMapper mapper;
    private final String FIKTIV_LOCALE = "FI_fi";

    @Before
    public void setUp() {
        kodeverkManager = new DefaultKodeverkmanager(mock(KodeverkPortType.class));
        mockFactory = new KodeverkMockFactory();
        mapper = KodeverkMapper.getInstance();
    }

    @Test
    public void testGetBeskrivelseForKode() {
        EnkeltKodeverk kodeverk = mapper.map(mockFactory.getMockHentKodeverk("MockKodeverk", "tester", "1", 1));
        Kode kode1 = mapper.map(mockFactory.getMockKode("Mock 1"));
        kodeverk.put(kode1.getNavn(), kode1);
        Kode kode2 = mapper.map(mockFactory.getMockKode("Mock 2"));
        kodeverk.put(kode2.getNavn(), kode2);
        kodeverkManager.kodeverkMap.put(kodeverk.getNavn(), kodeverk);

        assertEquals("TekstekstMock 1FI_fi", kodeverkManager.getBeskrivelseForKode("Mock 1", "MockKodeverk", FIKTIV_LOCALE));

    }

    @Test
    public void testGetKodeverkList() {
        final String kodeverksref = "MockKodeverk";
        EnkeltKodeverk kodeverk = mapper.map(mockFactory.getMockHentKodeverk(kodeverksref, "tester", "1", 1));
        int antallKoder = 4;
        for (int i = 0; i < antallKoder; i++) {
            Kode kode = mapper.map(mockFactory.getMockKode("Mock " + i));
            kodeverk.put(kode.getNavn(), kode);
        }
        kodeverkManager.kodeverkMap.put(kodeverk.getNavn(), kodeverk);

        List<Kodeverdi> kodeverdiList = kodeverkManager.getKodeverkList(kodeverksref, FIKTIV_LOCALE);
        assertEquals(4, kodeverdiList.size());
        assertEquals(kodeverk.get("Mock 1").getTermForSpraak(FIKTIV_LOCALE).getNavn(), kodeverkManager.getBeskrivelseForKode("Mock 1", kodeverksref, FIKTIV_LOCALE));
    }
}

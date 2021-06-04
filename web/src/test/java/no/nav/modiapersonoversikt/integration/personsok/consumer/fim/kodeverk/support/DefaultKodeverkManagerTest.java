package no.nav.modiapersonoversikt.integration.personsok.consumer.fim.kodeverk.support;

import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLTerm;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DefaultKodeverkManagerTest {

    private static final String CODE_NAME = "KODE";
    private static final String CODE_DESCRIPTION = "VALUE";
    private static final String KODEVERK_NAVN = "KODEVERK";
    private static final String SPRAAK = "NO";
    @Mock
    private KodeverkServiceDelegate mockServiceDelegate;
    private KodeverkManager kodeverkManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        kodeverkManager = new DefaultKodeverkManager(mockServiceDelegate);
    }

    @Test
    public void testGetBeskrivelseForKodeValid() {
        when(mockServiceDelegate.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenReturn(getKodeverk(createCode(CODE_NAME, CODE_DESCRIPTION)));
        String decodeValue = kodeverkManager.getBeskrivelseForKode(CODE_NAME, KODEVERK_NAVN, SPRAAK);
        assertEquals(CODE_DESCRIPTION, decodeValue);
    }

    @Test
    public void testGetBeskrivelseForKodeEmptyCode() {
        String decodeValue = kodeverkManager.getBeskrivelseForKode(null, KODEVERK_NAVN, SPRAAK);
        assertEquals(EMPTY, decodeValue);
    }

    @Test
    public void testGetBeskrivelseForKodeEmptyKodeverknavn() {
        String decodeValue = kodeverkManager.getBeskrivelseForKode(CODE_NAME, null, SPRAAK);
        assertEquals(EMPTY, decodeValue);
    }

    private XMLHentKodeverkResponse getKodeverk(XMLKode... codes) {
        XMLEnkeltKodeverk kodeverk = new XMLEnkeltKodeverk();
        kodeverk.getKode().addAll(Arrays.asList(codes));
        XMLHentKodeverkResponse kodeverkResponseWrapper = new XMLHentKodeverkResponse();
        kodeverkResponseWrapper.setKodeverk(kodeverk);
        return kodeverkResponseWrapper;
    }

    private XMLKode createCode(String code, String value) {
        XMLTerm term = new XMLTerm();
        term.setNavn(value);
        term.setSpraak(SPRAAK);

        XMLKode kode = new XMLKode();
        kode.setNavn(code);
        kode.getTerm().add(term);

        return kode;
    }

    private XMLHentKodeverkRequest getHentKodeverkRequest() {
        XMLHentKodeverkRequest request = new XMLHentKodeverkRequest();
        request.setNavn(KODEVERK_NAVN);
        request.setSpraak(SPRAAK);
        return request;
    }
}

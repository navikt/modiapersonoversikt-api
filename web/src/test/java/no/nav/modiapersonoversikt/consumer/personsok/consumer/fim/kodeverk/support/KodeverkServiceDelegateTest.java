package no.nav.modiapersonoversikt.consumer.personsok.consumer.fim.kodeverk.support;

import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class KodeverkServiceDelegateTest {

    private static final String CODE_NAME = "XMLKode";
    private static final String CODE_DESCRIPTION = "VALUE";
    private static final String XMLKodeVERK_NAVN = "XMLKodeVERK";
    private static final String SPRAAK = "NO";
    @Mock
    private KodeverkPortType mockXMLKodeverkPortType;
    private KodeverkServiceDelegate serviceDelegate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        serviceDelegate = new KodeverkServiceDelegate();
        serviceDelegate.setKodeverkPortType(mockXMLKodeverkPortType);
    }

    @Test
    public void testGetBeskrivelseForXMLKodeValid() throws Exception {
        when(mockXMLKodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenReturn(getKodeverkResponse(createCode(CODE_NAME, CODE_DESCRIPTION)));
        XMLEnkeltKodeverk XMLKodeverk = (XMLEnkeltKodeverk) serviceDelegate.hentKodeverk(getHentKodeverkRequest()).getKodeverk();
        assertEquals("Should have 1 element.", 1, XMLKodeverk.getKode().size());
    }

    @Test
    public void testGetBeskrivelseForXMLKodeXMLKodeverkIkkeFunnetException() throws Exception {
        when(mockXMLKodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenThrow(new HentKodeverkHentKodeverkKodeverkIkkeFunnet(XMLKodeVERK_NAVN, new RuntimeException()));
        XMLEnkeltKodeverk XMLKodeverk = (XMLEnkeltKodeverk) serviceDelegate.hentKodeverk(getHentKodeverkRequest()).getKodeverk();
        assertTrue("Should be empty.", XMLKodeverk.getKode().isEmpty());
    }

    @Test
    public void testGetBeskrivelseForXMLKodeFeilITjeneste() throws Exception {
        when(mockXMLKodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenThrow(new RuntimeException());
        XMLEnkeltKodeverk XMLKodeverk = (XMLEnkeltKodeverk) serviceDelegate.hentKodeverk(getHentKodeverkRequest()).getKodeverk();
        assertTrue("Should be empty.", XMLKodeverk.getKode().isEmpty());
    }

    private XMLHentKodeverkResponse getKodeverkResponse(XMLKode... codes) {
        XMLHentKodeverkResponse response = new XMLHentKodeverkResponse();
        XMLEnkeltKodeverk XMLKodeverk = new XMLEnkeltKodeverk();
        XMLKodeverk.getKode().addAll(Arrays.asList(codes));
        response.setKodeverk(XMLKodeverk);
        return response;
    }

    private XMLKode createCode(String code, String value) {
        XMLTerm term = new XMLTerm();
        term.setNavn(value);

        XMLKode XMLKode = new XMLKode();
        XMLKode.setNavn(code);
        XMLKode.getTerm().add(term);

        return XMLKode;
    }

    private XMLHentKodeverkRequest getHentKodeverkRequest() {
        XMLHentKodeverkRequest request = new XMLHentKodeverkRequest();
        request.setNavn(XMLKodeVERK_NAVN);
        request.setSpraak(SPRAAK);
        return request;
    }
}
package no.nav.modiapersonoversikt.consumer.kodeverk2;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import no.nav.modiapersonoversikt.consumer.kodeverk2.exception.KodeverkIkkeFunnetException;
import no.nav.modiapersonoversikt.consumer.kodeverk2.exception.KodeverkTjenesteFeiletException;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLPeriode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLTerm;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class DefaultKodeverkClientTest {

    private KodeverkPortType kodeverkPortType;
    private KodeverkClient defaultKodeverkClient;

    @Before
    public void setUp() throws Exception {
        kodeverkPortType = createKodeverkPortType();
        defaultKodeverkClient = new DefaultKodeverkClient(kodeverkPortType);
    }

    @Test
    public void skalHenteKodeverk() {
        XMLEnkeltKodeverk enkeltKodeverk = (XMLEnkeltKodeverk) defaultKodeverkClient.hentKodeverk("test");
        assertThat(enkeltKodeverk, notNullValue());
    }

    @Test
    public void skalhenteFoersteTermnavnForKode() {
        String term = defaultKodeverkClient.hentFoersteTermnavnForKode("navn", "test");

        assertThat(term, is("term"));
    }

    @Test
    public void skalhenteRiktigTermnavnForMultiTermKode() throws Exception {
        when(kodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenReturn(getXmlKodeverkMultiTermResponse());
        XMLEnkeltKodeverk xmlEnkeltKodeverk = (XMLEnkeltKodeverk) defaultKodeverkClient.hentKodeverk("multiTermKode");
        assertThat(defaultKodeverkClient.hentFoersteTermnavnForKode("multiTermKode", ""), equalTo("term2"));
        assertThat((xmlEnkeltKodeverk.getKode().get(0)).getTerm().size(), is(1));
    }

    @Test(expected = KodeverkIkkeFunnetException.class)
    public void skalKasteExceptionHvisKodeverkIkkeFinnes() throws Exception {
        when(kodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenThrow(new HentKodeverkHentKodeverkKodeverkIkkeFunnet());

        defaultKodeverkClient.hentKodeverk("test");
    }

    @Test(expected = KodeverkTjenesteFeiletException.class)
    public void skalKasteExceptionHvisTjenestenFeiler() throws Exception {
        when(kodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenThrow(new RuntimeException());

        defaultKodeverkClient.hentKodeverk("test");
    }

    @Test(expected = ApplicationException.class)
    public void skalKasteExceptionForKoderMedGammelGyldighetsPeriode() {
        defaultKodeverkClient.hentFoersteTermnavnForKode("navn2", "test");
    }

    private KodeverkPortType createKodeverkPortType() throws Exception {
        kodeverkPortType = mock(KodeverkPortType.class);
        when(kodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenReturn(getXmlKodeverkResponse());

        return kodeverkPortType;
    }

    private XMLHentKodeverkResponse getXmlKodeverkResponse() {
        XMLHentKodeverkResponse response = new XMLHentKodeverkResponse();
        response.withKodeverk(new XMLEnkeltKodeverk()
                .withKode(createXmlKode("navn", "term", getGyldighetsperiode()))
                .withKode(createXmlKode("navn2", "term2", getGammelGyldighetsperiode()))
                .withKode(createXmlKode("navn3", "term3", getGyldighetsperiode())));

        return response;
    }

    private XMLHentKodeverkResponse getXmlKodeverkMultiTermResponse() {
        XMLHentKodeverkResponse response = new XMLHentKodeverkResponse();
        response.withKodeverk(new XMLEnkeltKodeverk()
                .withKode(createMultiTermXmlKode("multiTermKode", getGammelOgNyTerm(), getGyldighetsperiode())));

        return response;
    }

    private XMLKode createXmlKode(String navn, String term, XMLPeriode gyldighetsperiode) {
        return new XMLKode().withGyldighetsperiode(gyldighetsperiode).withNavn(navn).withTerm(new XMLTerm().withNavn(term).withGyldighetsperiode(gyldighetsperiode));
    }

    private XMLKode createMultiTermXmlKode(String navn, List<XMLTerm> termer, XMLPeriode gyldighetsperiode) {
        return new XMLKode().withGyldighetsperiode(gyldighetsperiode).withNavn(navn).withTerm(termer);
    }

    private XMLPeriode getGyldighetsperiode() {
        return new XMLPeriode().withFom(DateMidnight.now().minusDays(1)).withTom(DateMidnight.now().plusDays(1));
    }

    private List<XMLTerm> getGammelOgNyTerm() {
        List<XMLTerm> termList = new ArrayList<>();
        termList.add(new XMLTerm().withNavn("term1").withGyldighetsperiode(getGammelGyldighetsperiode()));
        termList.add(new XMLTerm().withNavn("term2").withGyldighetsperiode(getGyldighetsperiode()));
        return termList;
    }

    private XMLPeriode getGammelGyldighetsperiode() {
        return new XMLPeriode().withFom(DateMidnight.now().minusDays(200)).withTom(DateMidnight.now().minusDays(50));
    }
}

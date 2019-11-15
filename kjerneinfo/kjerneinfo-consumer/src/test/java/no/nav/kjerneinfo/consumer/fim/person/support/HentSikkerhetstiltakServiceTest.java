package no.nav.kjerneinfo.consumer.fim.person.support;

import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.tjeneste.virksomhet.person.v3.HentSikkerhetstiltakPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSSikkerhetstiltak;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentSikkerhetstiltakRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentSikkerhetstiltakResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class HentSikkerhetstiltakServiceTest {
    private static final String IDENT = "11223344556";

    private HentSikkerhetstiltakService service;

    @Mock
    private PersonV3 portType;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new HentSikkerhetstiltakService(portType);
    }

    @Test
    public void hentSikkerhetstiltak() throws Exception {
        String sikkerhetsBeskrivelse = "Farlig person.";
        WSHentSikkerhetstiltakResponse response = new WSHentSikkerhetstiltakResponse()
                .withSikkerhetstiltak(new WSSikkerhetstiltak()
                        .withSikkerhetstiltaksbeskrivelse(sikkerhetsBeskrivelse));
        when(portType.hentSikkerhetstiltak(any(WSHentSikkerhetstiltakRequest.class))).thenReturn(response);

        Sikkerhetstiltak sikkerhetstiltak = service.hentSikkerhetstiltak(IDENT);

        assertEquals(sikkerhetsBeskrivelse, sikkerhetstiltak.getSikkerhetstiltaksbeskrivelse());
    }

    @Test
    public void hentSikkerhetstiltakSjekkPeriode() throws Exception {
        String sikkerhetsBeskrivelse = "Farlig person.";
        WSHentSikkerhetstiltakResponse response = new WSHentSikkerhetstiltakResponse()
                .withSikkerhetstiltak(new WSSikkerhetstiltak()
                        .withSikkerhetstiltaksbeskrivelse(sikkerhetsBeskrivelse).withPeriode(new WSPeriode()
                                .withTom(lagDato()).withFom(lagDato())));
        when(portType.hentSikkerhetstiltak(any(WSHentSikkerhetstiltakRequest.class))).thenReturn(response);

        Sikkerhetstiltak sikkerhetstiltak = service.hentSikkerhetstiltak(IDENT);

        assertNotNull(sikkerhetstiltak.getPeriode().getFrom());
    }

    @Test
    public void hentSikkerhetstiltakThatDontExist() throws Exception {
        when(portType.hentSikkerhetstiltak(any(WSHentSikkerhetstiltakRequest.class)))
                .thenThrow(new HentSikkerhetstiltakPersonIkkeFunnet());

        Sikkerhetstiltak sikkerhetstiltak = service.hentSikkerhetstiltak(IDENT);

        assertEquals(null, sikkerhetstiltak.getSikkerhetstiltaksbeskrivelse());
    }

    private XMLGregorianCalendar lagDato() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date);
        try {
            XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(strDate);
            return xmlDate;
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

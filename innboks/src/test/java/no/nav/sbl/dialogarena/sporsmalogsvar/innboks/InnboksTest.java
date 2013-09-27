package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.sporsmalogsvar.TestApplication;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.TestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.DummyHomePage;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.Henvendelsetype.SPORSMAL;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContext.class)
public class InnboksTest {
    private static final String FODSELSNUMMER = "14108643790";
    public static final String STANDARD_OVERSKRIFT = "overskrift";
    public static final String STANDARD_TEMA = "tema";
    public static final String STANDARD_FRITEKST = "fritekst";
    public static final DateTime STANDARD_DATO = DateTime.parse("2013-08-28T16:00Z");
    public static final String INNBOKS_ID = "innboks";
    private FluentWicketTester<? extends WebApplication> tester;

    HenvendelseService meldingService;

    @Mock
    HenvendelsePortType henvendelsetjeneste;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        HenvendelseService henvendeslseService = new HenvendelseService.Default(henvendelsetjeneste);
        tester = new FluentWicketTester<>(new TestApplication() {
            @Override
            protected void init() {
                super.init();
                mountPage("/innboks", DummyHomePage.class);
            }
        });
    }

    @Test
    public void testIngenting() {
    }

    private static WSHenvendelse lagSporsmal(String tema, String overskrift) {
        return lagSporsmalMedFritekst(tema, overskrift, "fritekst");
    }

    private static WSHenvendelse lagSporsmalMedFritekst(String tema, String overskrift, String fritekst) {
        return new WSHenvendelse().withHenvendelseType(SPORSMAL.name())
                .withTema(tema)
                .withBehandlingsresultat(lagBehandlingsresultat(overskrift, fritekst))
                .withTraad("1")
                .withOpprettetDato(DateTime.now());
    }

    private static WSHenvendelse lagSporsmalMedOpprettetDato(DateTime opprettet) {
        return new WSHenvendelse()
                .withHenvendelseType(SPORSMAL.name())
                .withTema(STANDARD_TEMA)
                .withTraad("1")
                .withBehandlingsresultat(lagBehandlingsresultat("overskrift", "fritekst"))
                .withOpprettetDato(opprettet);
    }

    private static String lagBehandlingsresultat(String overskrift, String fritekst) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> behandlingsresultat = new HashMap<>();
        behandlingsresultat.put("overskrift", overskrift);
        behandlingsresultat.put("fritekst", fritekst);
        String json = null;
        try {
            json = mapper.writeValueAsString(behandlingsresultat);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}

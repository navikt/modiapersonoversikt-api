package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.sporsmalogsvar.TestApplication;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.TestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.AlleMeldingerPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingsHeader;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingstraadPanel;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.DummyHomePage;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.AlleMeldingerPanel.INGEN_MELDINGER_ID;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

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

    MeldingService meldingService;

    @Mock
    HenvendelsePortType henvendelsetjeneste;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        meldingService = new MeldingService.Default(henvendelsetjeneste);
        tester = new FluentWicketTester<>(new TestApplication() {
            @Override
            protected void init() {
                super.init();
                mountPage("/innboks", DummyHomePage.class);
            }
        });
    }

    @Test
    public void skalViseInfotekstNaarInnboksErTom() {
        when(henvendelsetjeneste.hentHenvendelseListe(FODSELSNUMMER)).thenReturn(new ArrayList<WSHenvendelse>());

        tester.goToPageWith(new Innboks(INNBOKS_ID, FODSELSNUMMER, meldingService));

        tester.should().containComponent(
                ofType(Label.class).withId(INGEN_MELDINGER_ID).and(containedInComponent(ofType(AlleMeldingerPanel.class))));
    }

    @Test
    public void skalViseMeldingsoverskrifter() {
        when(henvendelsetjeneste.hentHenvendelseListe(FODSELSNUMMER))
                .thenReturn(asList(lagSporsmalMedOpprettetDato(STANDARD_DATO), lagSporsmalMedOpprettetDato(STANDARD_DATO)));

        tester.goToPageWith(new Innboks(INNBOKS_ID, FODSELSNUMMER, meldingService));

        tester.should().inComponent(AlleMeldingerPanel.Meldingsliste.class).containComponents(2, ofType(MeldingsHeader.class));

        tester.should().inComponent(MeldingsHeader.class).containLabelsSaying(STANDARD_OVERSKRIFT);
        tester.should()
                .inComponent(MeldingsHeader.class)
                .containLabelsSaying(MeldingVM.DATE_FORMATTER_DEFAULT.print(STANDARD_DATO));
    }

    @Test
    public void skalViseMeldingstekst() {
        when(henvendelsetjeneste.hentHenvendelseListe(FODSELSNUMMER))
                .thenReturn(asList(lagSporsmalMedFritekst(STANDARD_TEMA, STANDARD_OVERSKRIFT, STANDARD_FRITEKST)));

        tester.goToPageWith(new Innboks(INNBOKS_ID, FODSELSNUMMER, meldingService));

        tester.should()
                .inComponent(AlleMeldingerPanel.Meldingsliste.class)
                .containLabelsSaying(STANDARD_FRITEKST);
    }

    @Test
    public void skalViseIngenMeldingValgt() {
        when(henvendelsetjeneste.hentHenvendelseListe(FODSELSNUMMER))
                .thenReturn(asList(lagSporsmalMedFritekst(STANDARD_TEMA, STANDARD_OVERSKRIFT, STANDARD_FRITEKST)));
        tester.goToPageWith(new Innboks(INNBOKS_ID, FODSELSNUMMER, meldingService));

        tester.should()
                .containComponent(ofType(Label.class).withId(DetaljvisningPanel.INGEN_VALGT_ID)
                        .and(containedInComponent(ofType(DetaljvisningPanel.class))));
    }

    @Test
    public void skalViseDetaljerForValgtMelding() {
        when(henvendelsetjeneste.hentHenvendelseListe(FODSELSNUMMER))
                .thenReturn(asList(lagSporsmalMedFritekst(STANDARD_TEMA, STANDARD_OVERSKRIFT, STANDARD_FRITEKST)));
        tester.goToPageWith(new Innboks(INNBOKS_ID, FODSELSNUMMER, meldingService));

        tester.tester.executeAjaxEvent(tester.get()
                .component(containedInComponent(ofType(AlleMeldingerPanel.Meldingsliste.class))
                        .and(ofType(ListItem.class))), "click");

        tester.should().inComponent(MeldingstraadPanel.class).containLabelsSaying(STANDARD_OVERSKRIFT, STANDARD_FRITEKST);
    }

    @Test
    public void skalViseMeldingeneIOmvendtKronologiskRekkefolge() {
        when(henvendelsetjeneste.hentHenvendelseListe(FODSELSNUMMER))
                .thenReturn(
                        asList(lagSporsmalMedOpprettetDato(STANDARD_DATO),
                                lagSporsmalMedOpprettetDato(STANDARD_DATO.plusDays(3)),
                                lagSporsmalMedOpprettetDato(STANDARD_DATO.plusDays(1))));

        tester.goToPageWith(new Innboks(INNBOKS_ID, FODSELSNUMMER, meldingService));

        List<MeldingsHeader> overskrifter = tester.get()
                .components(ofType(MeldingsHeader.class)
                        .and(containedInComponent(ofType(AlleMeldingerPanel.Meldingsliste.class))));

        for (int i = 0; i < overskrifter.size() - 2; i++) {
            // Ikke så lett å komme oss tilbake til DateTime basert på label outputen, pga. hardkodet datoformatering.
            // Vi sammenligner dermed bare dag-komponenten i tidspunktet, siden testdataene varierer kun i dager.
            int opprettet1 = Integer.parseInt(overskrifter.get(i).getOpprettetDato().getDefaultModelObjectAsString().substring(0, 2));
            int opprettet2 = Integer.parseInt(overskrifter.get(i + 1).getOpprettetDato().getDefaultModelObjectAsString().substring(0, 2));
            assertThat(opprettet1, Matchers.greaterThan(opprettet2));
        }
    }

    private static WSHenvendelse lagSporsmal(String tema, String overskrift) {
        return lagSporsmalMedFritekst(tema, overskrift, "fritekst");
    }

    private static WSHenvendelse lagSporsmalMedFritekst(String tema, String overskrift, String fritekst) {
        return new WSHenvendelse().withHenvendelseType(Meldingstype.SPORSMAL.name())
                .withTema(tema)
                .withBehandlingsresultat(lagBehandlingsresultat(overskrift, fritekst))
                .withTraad("1")
                .withOpprettetDato(DateTime.now());
    }

    private static WSHenvendelse lagSporsmalMedOpprettetDato(DateTime opprettet) {
        return new WSHenvendelse()
                .withHenvendelseType(Meldingstype.SPORSMAL.name())
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

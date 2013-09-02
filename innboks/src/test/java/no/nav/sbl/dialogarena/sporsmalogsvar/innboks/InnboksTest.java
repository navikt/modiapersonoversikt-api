package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.sporsmalogsvar.TestApplication;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.TestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.AlleMeldingerPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingsHeader;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMeldingstype;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import org.apache.wicket.markup.html.basic.Label;
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

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.AlleMeldingerPanel.INGEN_MELDINGER_ID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContext.class)
public class InnboksTest {
    private static final String FODSELSNUMMER = "14108643790";
    public static final String STANDARD_OVERSKRIFT = "overskrift";
    public static final String STANDARD_TEMA = "tema";
    public static final String STANDARD_FRITEKST = "fritekst";
    public static final DateTime STANDARD_DATO = DateTime.parse("20130828");
    private FluentWicketTester<? extends WebApplication> tester;

    MeldingService meldingService;

    @Mock
    HenvendelsePortType henvendelsetjeneste;

    @Mock
    SporsmalOgSvarPortType sporsmalstjeneste;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        meldingService = new MeldingService.Default(henvendelsetjeneste, sporsmalstjeneste);
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

        tester.goToPageWith(new Innboks("innboks", FODSELSNUMMER, meldingService));

        tester.should()
                .containComponent(ofType(Label.class).withId(INGEN_MELDINGER_ID)
                        .and(containedInComponent(ofType(AlleMeldingerPanel.class))));
    }

    @Test
    public void skalViseMeldingsoverskrifter() {
        when(henvendelsetjeneste.hentHenvendelseListe(FODSELSNUMMER))
                .thenReturn(asList(lagSporsmalMedOpprettetDato(STANDARD_DATO), lagSporsmalMedOpprettetDato(STANDARD_DATO)));

        tester.goToPageWith(new Innboks("innboks", FODSELSNUMMER, meldingService));

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

        tester.goToPageWith(new Innboks("innboks", FODSELSNUMMER, meldingService));

        tester.should()
                .inComponent(AlleMeldingerPanel.Meldingsliste.class)
                .containLabelsSaying(STANDARD_FRITEKST);
    }

    @Test
    public void skalViseMeldingeneKronologisk() {
        when(henvendelsetjeneste.hentHenvendelseListe(FODSELSNUMMER))
                .thenReturn(
                        asList(lagSporsmalMedOpprettetDato(STANDARD_DATO),
                                lagSporsmalMedOpprettetDato(STANDARD_DATO.plusDays(3)),
                                lagSporsmalMedOpprettetDato(STANDARD_DATO.plusDays(1))));

        tester.goToPageWith(new Innboks("innboks", FODSELSNUMMER, meldingService));

        List<MeldingsHeader> overskrifter = tester.get()
                .components(ofType(MeldingsHeader.class)
                        .and(containedInComponent(ofType(AlleMeldingerPanel.Meldingsliste.class))));

        for (int i = overskrifter.size() - 1; i >= 0; i--) {
            String opprettetdato = overskrifter.get(i).getOpprettetDato().getDefaultModelObjectAsString();
            assertThat(opprettetdato, is(equalTo(STANDARD_DATO.plus(i).toString())));
        }
    }

    private static WSHenvendelse lagSporsmal(String tema, String overskrift) {
        return new WSMelding().withType(WSMeldingstype.SPORSMAL).withTema(tema).withOverskrift(overskrift).withTraadId("1").withSistEndretDato(DateTime.now());
    }

    private static WSHenvendelse lagSporsmalMedOpprettetDato(DateTime opprettet) {
        return new WSMelding()
                .withType(WSMeldingstype.SPORSMAL)
                .withTema(STANDARD_TEMA)
                .withOverskrift(STANDARD_OVERSKRIFT)
                .withTraadId("1")
                .withSistEndretDato(opprettet);
    }

    private static WSHenvendelse lagSporsmalMedFritekst(String tema, String overskrift, String fritekst) {
        WSHenvendelse sporsmal = lagSporsmal(tema, overskrift);
        sporsmal.setBeskrivelse(fritekst);
        return sporsmal;
    }
}

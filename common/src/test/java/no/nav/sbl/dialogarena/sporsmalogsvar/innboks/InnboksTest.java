package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.sporsmalogsvar.TestApplication;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.TestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.AlleMeldingerPanel;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMeldingstype;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.DummyHomePage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.AlleMeldingerPanel.INGEN_MELDINGER_ID;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContext.class)
public class InnboksTest {
    private static final String FODSELSNUMMER = "14108643790";
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
        tester.should().containComponent(ofType(Label.class).withId(INGEN_MELDINGER_ID).and(containedInComponent(ofType(AlleMeldingerPanel.class))));
    }

    @Test
    public void skalViseMeldingerIInnboksen() {
        when(henvendelsetjeneste.hentHenvendelseListe(FODSELSNUMMER)).thenReturn(lagMockMeldinger());
        tester.goToPageWith(new Innboks("innboks", FODSELSNUMMER, meldingService));

        ListItem meldingILista = tester.get().component(ofType(ListItem.class).and(containedInComponent(ofType(ListView.class).withId("meldinger"))));


//        Component fritekst = meldingILista.get("fritekst");
//
        tester.tester.executeAjaxEvent(meldingILista, "onclick");
//
//        tester.should().containComponent()

//        tester.get().component(ofType(ListItem.class));
    }



    private static List<WSHenvendelse> lagMockMeldinger() {
        List<WSHenvendelse> liste = new ArrayList<>();
        liste.add(
                lagSporsmal("tema", "overskrift")
        );
        return liste;

    }

    private static WSMelding lagSporsmal(String tema, String overskrift) {
        return new WSMelding().withType(WSMeldingstype.SPORSMAL).withTema(tema).withOverskrift(overskrift).withTraadId("1");
    }
}

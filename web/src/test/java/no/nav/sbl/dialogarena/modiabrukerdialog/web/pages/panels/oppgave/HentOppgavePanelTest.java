package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
public class HentOppgavePanelTest extends TestSecurityBaseClass {

    @Inject
    private FluentWicketTester<?> fluentWicketTester;

    @Before
    public void hentOppgave() {
        fluentWicketTester.goTo(Intern.class).click().link(withId("plukk-oppgave"));
    }

    @Test
    public void skalViseTemavelgerNaarDuPlukkerOppgave() {
        fluentWicketTester.should().containComponent(both(ofType(HentOppgavePanel.class)).and(thatIsVisible()));
    }

//    @Test
//    public void besvarePanelSkalHaVerdierNaarManHarValgtTema() {
//        fluentWicketTester.tester.executeAjaxEvent(fluentWicketTester.get().components(ofType(ListItem.class).and(containedInComponent(ofType(VelgTemaPanel.class)))).get(0), "click");
//
//        PageParameters pageParameters = fluentWicketTester.tester.getLastRenderedPage().getPageParameters();
//        assertThat(pageParameters.get("fnr").toString(), equalTo(HentOppgaveConfig.OppgavebehandlingTest.FODESELSNR));
//
//        BesvareModell modell = (BesvareModell) fluentWicketTester.get().component(ofType(BesvareSporsmalPanel.class)).getDefaultModel();
//        SporsmalVM sporsmal = modell.getObject().sporsmal;
//        assertNotNull(sporsmal.fritekst);
//        assertNotNull(sporsmal.opprettetDato);
//        assertNotNull(sporsmal.overskrift);
//
//        SvarVM svar = modell.getObject().svar;
//        assertNotNull(svar.behandlingsId);
//    }

}

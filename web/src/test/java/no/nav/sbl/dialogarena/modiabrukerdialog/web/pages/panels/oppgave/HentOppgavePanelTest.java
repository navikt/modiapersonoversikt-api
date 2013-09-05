package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import javax.inject.Inject;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.HentOppgaveConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare.BesvareSporsmalPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.modell.BesvareModell;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.modell.SporsmalVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.modell.SvarVM;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

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
    public void skalAapneBesvarePanelNaarDuPlukkerOppgaver() {
        fluentWicketTester.should().containComponent(both(ofType(BesvareSporsmalPanel.class)).and(thatIsVisible()));
    }

    @Test
    public void skalAapneInternsideTilRiktigBruker() {
        PageParameters pageParameters = fluentWicketTester.tester.getLastRenderedPage().getPageParameters();
        assertThat(pageParameters.get("fnr").toString(), equalTo(HentOppgaveConfig.OppgavebehandlingTest.FODESELSNR));
    }

    @Test
    public void besvarePanelSkalHaVerdierNaarManPlukkerOppgave() {
        BesvareModell modell = (BesvareModell) fluentWicketTester.get().component(ofType(BesvareSporsmalPanel.class)).getDefaultModel();
        SporsmalVM sporsmal = modell.getObject().sporsmal;
        assertNotNull(sporsmal.behandlingsId);
        assertNotNull(sporsmal.fritekst);
        assertNotNull(sporsmal.opprettetDato);
        assertNotNull(sporsmal.overskrift);

        SvarVM svar = modell.getObject().svar;
        assertNotNull(svar.behandlingsId);
    }

}

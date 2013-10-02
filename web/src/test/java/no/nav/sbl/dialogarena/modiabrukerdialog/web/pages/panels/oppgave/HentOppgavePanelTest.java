package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Sporsmal;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Svar;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare.BesvareSporsmalPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
public class HentOppgavePanelTest extends TestSecurityBaseClass {

    @Inject
    private FluentWicketTester<?> wicket;

    @Before
    public void hentOppgave() {
        wicket.goTo(Intern.class).click().link(withId("plukk-oppgave"));
    }

    @Test
    public void skalViseTemavelgerNaarDuPlukkerOppgave() {
        wicket.should().containComponent(both(ofType(HentOppgavePanel.class)).and(thatIsVisible()));
    }

    @Test
    public void besvarePanelSkalHaVerdierNaarManHarValgtTema() {
        wicket.tester.executeAjaxEvent(wicket.get().components(ofType(ListItem.class).and(containedInComponent(ofType(HentOppgavePanel.class)))).get(0), "click");

//        PageParameters pageParameters = wicket.tester.getLastRenderedPage().getPageParameters();
//        assertThat(pageParameters.get("fnr").toString(), equalTo(OppgavebehandlingConfig.Test.FODESELSNR));

        Sporsmal sporsmal = (Sporsmal) wicket.get()
                .component(both(containedInComponent(ofType(BesvareSporsmalPanel.class))).and(withId("sporsmal")))
                .getDefaultModelObject();
        assertThat(sporsmal.getFritekst(), notNullValue());
        assertThat(sporsmal.getSendtDato(), notNullValue());

        Svar svar = (Svar) wicket.get()
                .component(both(containedInComponent(ofType(BesvareSporsmalPanel.class))).and(withId("svar"))).getDefaultModelObject();

        assertThat(svar.getBehandlingId(), notNullValue());
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.OppgavebehandlingConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.HenvendelseinnsynConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.SoknaderConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.BesvareHenvendelseMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.BesvareSporsmalPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareServiceConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Sporsmal;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Svar;
import org.apache.wicket.markup.html.list.ListItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.CombinableMatcher.both;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        KjerneinfoPepMockContext.class,
        WicketTesterConfig.class,
        HenvendelseinnsynConfig.Test.class,
        SykepengerWidgetMockContext.class,
        SoknaderConfig.Test.class,
        OppgavebehandlingConfig.Test.class,
        BesvareServiceConfig.Default.class,
        BesvareHenvendelseMockContext.class
})
public class HentOppgavePanelTest {

    @Inject
    private FluentWicketTester<?> wicket;

    @Before
    public void cleanSession() {
        wicket.tester.getSession().replaceSession();
    }

    @Test
    public void skalViseTemavelgerNaarDuPlukkerOppgave() {
        wicket.goTo(Intern.class)
            .should().containComponent(ofType(HentOppgavePanel.Temaliste.class).and(thatIsInvisible()))
            .click().link(withId("plukk-oppgave"))
            .should().containComponent(ofType(HentOppgavePanel.Temaliste.class).and(thatIsVisible()));
    }

    @Test
    public void besvarePanelSkalHaVerdierNaarManHarValgtTema() {
        wicket.goTo(Intern.class).click().link(withId("plukk-oppgave"));
        wicket.tester.executeAjaxEvent(wicket.get().components(ofType(ListItem.class).and(containedInComponent(ofType(HentOppgavePanel.class)))).get(0), "click");

        Sporsmal sporsmal = (Sporsmal) wicket.get()
                .component(both(containedInComponent(ofType(BesvareSporsmalPanel.class))).and(withId("sporsmal")))
                .getDefaultModelObject();
        assertThat(sporsmal.fritekst, notNullValue());
        assertThat(sporsmal.sendtDato, notNullValue());

        Svar svar = (Svar) wicket.get()
                .component(both(containedInComponent(ofType(BesvareSporsmalPanel.class))).and(withId("svar"))).getDefaultModelObject();

        assertThat(svar.behandlingId, notNullValue());
    }

}

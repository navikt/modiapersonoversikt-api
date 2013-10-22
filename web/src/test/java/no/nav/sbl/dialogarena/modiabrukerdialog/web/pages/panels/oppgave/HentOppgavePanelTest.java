package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints.BesvareHenvendelseEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints.OppgavebehandlingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints.SakOgBehandlingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.HentPersonPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykepengerWidgetMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.BesvareSporsmalPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Traad;
import org.apache.wicket.markup.html.list.ListItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HentPersonPanelMockContext.class,
        HenvendelsePortTypeContext.class,
        SykepengerWidgetMockContext.class,
        SakOgBehandlingEndpointConfig.Test.class,
        OppgavebehandlingEndpointConfig.Test.class,
        BesvareHenvendelseEndpointConfig.Test.class
})
public class HentOppgavePanelTest extends WicketPageTest {

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

        Melding sporsmal = getTraad().getSisteMelding();

        assertThat(sporsmal.fritekst, notNullValue());
        assertThat(sporsmal.sendtDato, notNullValue());

        assertThat(getTraad().getSvar().behandlingId, notNullValue());
    }


    private Traad getTraad() {
        return (Traad) wicket.get().component(ofType(BesvareSporsmalPanel.class)).getDefaultModelObject();
    }

}

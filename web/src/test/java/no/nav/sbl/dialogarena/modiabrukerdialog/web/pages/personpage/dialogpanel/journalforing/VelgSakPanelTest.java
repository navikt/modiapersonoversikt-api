package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.CompoundPropertyModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerServicesMockContext.class})
public class VelgSakPanelTest extends WicketPageTest {

    @Inject
    private SakerService sakerService;

    @Test
    public void viserTekniskFeilHvisSakerServiceTryner() {
        doThrow(Exception.class).when(sakerService).hentSaker(anyString());

        VelgSakPanel velgSakPanel = new VelgSakPanel("id", "", new CompoundPropertyModel<>(new HenvendelseVM()));
        velgSakPanel.togglePanel(mock(AjaxRequestTarget.class));

        wicket.goToPageWith(velgSakPanel)
                .should().containComponent(thatIsVisible().and(withId("tekniskFeil")))
                .should().containComponent(thatIsInvisible().and(withId("plukkSakForm")))
                .should().containComponent(thatIsInvisible().and(withId("feedback")))
                .should().containComponent(thatIsInvisible().and(withId("ingenSaker")));
    }

}
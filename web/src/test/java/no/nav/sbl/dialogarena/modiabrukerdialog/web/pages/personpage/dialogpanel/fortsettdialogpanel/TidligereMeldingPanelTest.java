package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.FMLI;

@RunWith(SpringJUnit4ClassRunner.class)
public class TidligereMeldingPanelTest extends WicketPageTest {

    private static final String ID = "id";
    private static final String TYPE = "type";

    @Test
    public void skalAapnesMedFritekstUsynlig() {
        wicket.goToPageWith(new TidligereMeldingPanel(ID, TYPE, FMLI.name(), DateTime.now(), "fritekst", true))
                .should().containComponent(thatIsInvisible().withId("fritekst"));
    }

    @Test
    public void skalAapnesMedFritekstSynlig() {
        wicket.goToPageWith(new TidligereMeldingPanel(ID, TYPE, FMLI.name(), DateTime.now(), "fritekst", false))
                .should().containComponent(thatIsVisible().withId("fritekst"));
    }

    @Test
    public void skalToggleFritekstSynligVedKlikkPaaOverskriftContainer() {
        wicket.goToPageWith(new TidligereMeldingPanel(ID, TYPE, FMLI.name(), DateTime.now(), "fritekst", false))
                .should().containComponent(thatIsVisible().withId("fritekst"))
                .onComponent(withId("overskriftContainer")).executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsInvisible().withId("fritekst"));
    }

}

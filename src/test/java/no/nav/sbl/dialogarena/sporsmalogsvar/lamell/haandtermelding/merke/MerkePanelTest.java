package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class MerkePanelTest extends WicketPageTest {

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Test
    public void merkeTraadSomKontorsperret() {
        String fnr = "fnr";

        InnboksVM innboksVM = new InnboksVM(fnr);

        MerkePanel merkePanel = new MerkePanel("panel", innboksVM);
        merkePanel.setVisibilityAllowed(true);
        wicket.goToPageWith(merkePanel)
                .inForm("panel:merkForm")
                .select("merkType", 1)
                .submitWithAjaxButton(withId("merk"));
        verify(henvendelseBehandlingService).merkSomKontorsperret(eq(fnr), any(TraadVM.class));
    }
}

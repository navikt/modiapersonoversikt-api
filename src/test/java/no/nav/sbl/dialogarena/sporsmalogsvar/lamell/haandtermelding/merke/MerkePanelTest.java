package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class MerkePanelTest extends WicketPageTest {

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;
    private static final String FNR = "fnr";

    private MerkePanel merkePanel;

    @Before
    public void setUp() {
        InnboksVM innboksVM = new InnboksVM(FNR);
        merkePanel = new MerkePanel("panel", innboksVM);
        merkePanel.setVisibilityAllowed(true);
    }

    @Test
    public void skalGiFeilmeldingDersomManProverAaMarkereUtenAaVelgeKontorsperretEllerFeilsendt() {
        wicket.goToPageWith(merkePanel)
                .inForm("panel:merkForm")
                .submitWithAjaxButton(withId("merk"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
        assertThat(errorMessages, contains(wicket.get().component(ofType(RadioGroup.class)).getString("merkType.Required")));
    }

    @Test
    public void skalMerkeTraadSomKontorsperret() {
        wicket.goToPageWith(merkePanel)
                .inForm("panel:merkForm")
                .select("merkType", 1)
                .submitWithAjaxButton(withId("merk"));

        verify(henvendelseBehandlingService).merkSomKontorsperret(eq(FNR), any(TraadVM.class));
    }

    @Test
    public void skalMerkeTraadSomFeilsendt() {
        wicket.goToPageWith(merkePanel)
                .inForm("panel:merkForm")
                .select("merkType", 0)
                .submitWithAjaxButton(withId("merk"));

        verify(henvendelseBehandlingService).merkSomFeilsendt(any(TraadVM.class));
    }


}

package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.JournalforingPanelVelgSakTestConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.apache.wicket.model.CompoundPropertyModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {JournalforingPanelVelgSakTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class JournalforingsPanelVelgSakTest extends WicketPageTest {

    @Inject
    private MeldingService meldingService;

    private final static String FODSELSNR = "52765236723";

    private CompoundPropertyModel<InnboksVM> innboksVMModel;

    @Before
    public void setUp(){
        innboksVMModel = new CompoundPropertyModel<>(new InnboksVM(meldingService, FODSELSNR));
    }

    @Test
    public void skalStarteJournalforingsPanelVelgSakUtenFeil() {
        wicket.goToPageWith(new JournalforingsPanelVelgSak("panel", innboksVMModel));
    }

    @Test
    public void skalJournalforeVedSubmit() {
        wicket
                .goToPageWith(new JournalforingsPanelVelgSak("panel", innboksVMModel))
                .inForm("panel:plukkSakForm")
                .select("valgtTraad.journalfortSak", 0)
                .submitWithAjaxButton(withId("journalforTraad"));

        verify(meldingService).journalforTraad(any(TraadVM.class), any(Sak.class), eq(FODSELSNR));
    }

    @Test
    public void skalKreveAtMinstEnSakErValgt() {
        JournalforingsPanelVelgSak journalforingsPanel = new JournalforingsPanelVelgSak("panel", innboksVMModel);

        wicket
                .goToPageWith(journalforingsPanel)
                .inForm("panel:plukkSakForm")
                .submitWithAjaxButton(withId("journalforTraad"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, contains(journalforingsPanel.get("plukkSakForm:valgtTraad.journalfortSak").getString("valgtTraad.journalfortSak.Required")));
    }

}

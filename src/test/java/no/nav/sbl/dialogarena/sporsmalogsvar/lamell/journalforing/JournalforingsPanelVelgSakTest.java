package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.MeldingServiceTestContext;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMockSaksliste;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.opprettMeldingEksempel;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MeldingServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class JournalforingsPanelVelgSakTest extends WicketPageTest {

    @Inject
    private MeldingService meldingService;

    private CompoundPropertyModel<InnboksVM> innboksVMModel;

    @Before
    public void setUp(){
        reset(meldingService);
        when(meldingService.hentSakerForBruker(anyString())).thenReturn(createMockSaksliste());
        when(meldingService.hentMeldinger(anyString())).thenReturn(new ArrayList<>(Arrays.asList(opprettMeldingEksempel())));

        innboksVMModel = new CompoundPropertyModel<>(new InnboksVM(meldingService, "fnr"));
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

        verify(meldingService).journalforTraad(any(TraadVM.class), any(Sak.class));
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

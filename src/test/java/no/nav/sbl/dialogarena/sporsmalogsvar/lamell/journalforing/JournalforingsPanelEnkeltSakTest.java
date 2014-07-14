package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.MeldingServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.apache.wicket.model.CompoundPropertyModel;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@ContextConfiguration(classes = {MeldingServiceTestContext.class})
@DirtiesContext(classMode = AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
public class JournalforingsPanelEnkeltSakTest extends WicketPageTest {

    @Inject
    private MeldingService meldingService;

    private final static String JOURNALFORT_SAKSID = "123123123";

    private CompoundPropertyModel<InnboksVM> innboksVMModel;

    @Before
    public void setUp(){
        innboksVMModel = new CompoundPropertyModel<>(new InnboksVM(meldingService, "fnr"));
        List<Sak> sakerForBruker = meldingService.hentSakerForBruker(innboksVMModel.getObject().getFnr());
        sakerForBruker.get(0).opprettetDato = DateTime.now();
        sakerForBruker.get(0).saksId = JOURNALFORT_SAKSID;
        innboksVMModel.getObject().getValgtTraad().getEldsteMelding().melding.journalfortSaksId = JOURNALFORT_SAKSID;
    }

    @Test
    public void skalStarteJournalforingsPanelEnkeltSakUtenFeil() {
        wicket.goToPageWith(new JournalforingsPanelEnkeltSak("panel", innboksVMModel));
    }

    @Test
    public void skalJournalforeVedSubmit() {
        wicket
                .goToPageWith(new JournalforingsPanelEnkeltSak("panel", innboksVMModel))
                .click().link(withId("journalforTraad"));

        verify(meldingService).journalforTraad(any(TraadVM.class), any(Sak.class));
    }

}
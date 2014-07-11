package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.MeldingServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.model.CompoundPropertyModel;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

@ContextConfiguration(classes = {MeldingServiceTestContext.class})
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

}
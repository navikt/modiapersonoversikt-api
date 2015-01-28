package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class JournalforingsPanelEnkeltSakTest extends WicketPageTest {

    private final static String FODSELSNR = "52765236723";
    private final static String JOURNALFORT_SAKSID = "123123123";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;
    @Inject
    private BehandleHenvendelsePortType behandleHenvendelsePortType;

    private InnboksVM innboksVM;

    @Before
    public void setUp() {
        innboksVM = new InnboksVM(FODSELSNR, henvendelseBehandlingService);
        List<Sak> sakerForBruker = TestUtils.createMockSaksliste();
        sakerForBruker.get(0).opprettetDato = DateTime.now();
        sakerForBruker.get(0).saksId = JOURNALFORT_SAKSID;
        innboksVM.getValgtTraad().getEldsteMelding().melding.journalfortSaksId = JOURNALFORT_SAKSID;
    }

    @Test
    public void skalStarteJournalforingsPanelEnkeltSakUtenFeil() {
        wicket.goToPageWith(new JournalforingsPanelEnkeltSak("panel", innboksVM));
    }

    @Test
    public void skalJournalforeVedSubmit() {
        JournalforingsPanelEnkeltSak journalforingsPanel = new JournalforingsPanelEnkeltSak("panel", innboksVM);
        journalforingsPanel.oppdater();
        wicket
                .goToPageWith(journalforingsPanel)
                .click().link(withId("journalforTraad"));

        verify(behandleHenvendelsePortType).knyttBehandlingskjedeTilSak(anyString(), anyString(), anyString(), anyString());
    }

}
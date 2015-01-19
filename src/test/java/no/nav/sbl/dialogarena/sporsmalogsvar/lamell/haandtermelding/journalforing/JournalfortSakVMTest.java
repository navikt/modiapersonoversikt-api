package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.config.InnboksTestConfig;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.SAKS_ID_3;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ServiceTestContext.class, InnboksTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class JournalfortSakVMTest {

    @Mock
    private InnboksVM innboksVM;

    @Inject
    private SakerService sakerService;

    @Before
    public void setUp() {
        TraadVM traadVM = mock(TraadVM.class);
        initMocks(this);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        MeldingVM eldsteMeldingVM = opprettMeldingVMogSetJournalfortSaksId(SAKS_ID_3);
        when(traadVM.getEldsteMelding()).thenReturn(eldsteMeldingVM);

        Sak sak = new Sak();
        sak.saksId = SAKS_ID_3;

        when(sakerService.hentListeAvSaker(anyString())).thenReturn(asList(sak));
    }

    @Test
    public void sjekkAtGetSakMetodenReturnererKorrektSakBasertPaJournalforingsId() {
        JournalfortSakVM journalfortSakVM = new JournalfortSakVM(innboksVM, sakerService);
        journalfortSakVM.oppdater();

        assertThat(journalfortSakVM.getSak().saksId, is(SAKS_ID_3));
    }

    private MeldingVM opprettMeldingVMogSetJournalfortSaksId(String journalfortSaksId) {
        Melding melding = new Melding("", SPORSMAL_SKRIFTLIG, DateTime.now());
        melding.journalfortSaksId = journalfortSaksId;
        return new MeldingVM(melding, 1);
    }

}
package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.SAKS_ID_3;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ServiceTestContext.class, WicketTesterConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class JournalfortSakVMTest {

    @Mock
    private InnboksVM innboksVM;

    @Before
    public void setUp() {
        TraadVM traadVM = mock(TraadVM.class);
        initMocks(this);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        MeldingVM eldsteMeldingVM = opprettMeldingVMogSetJournalfortSaksId(SAKS_ID_3);
        when(traadVM.getEldsteMelding()).thenReturn(eldsteMeldingVM);
    }

    @Test
    public void sjekkAtGetSakMetodenReturnererKorrektSakBasertPaJournalforingsId() {
        JournalfortSakVM journalfortSakVM = new JournalfortSakVM(innboksVM);
        journalfortSakVM.oppdater();

        assertThat(journalfortSakVM.getSak().saksId, is(SAKS_ID_3));
    }

    private MeldingVM opprettMeldingVMogSetJournalfortSaksId(String journalfortSaksId) {
        Melding melding = new Melding("", Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now());
        melding.journalfortSaksId = journalfortSaksId;
        return new MeldingVM(melding, 1);
    }

}
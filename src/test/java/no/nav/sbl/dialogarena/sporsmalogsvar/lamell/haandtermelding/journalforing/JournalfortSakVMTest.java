package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.SAKS_ID_3;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JournalfortSakVMTest {

    private SakerService sakerService = mock(SakerService.class);
    private InnboksVM innboksVM = mock(InnboksVM.class);

    @Before
    public void setUp() {
        TraadVM traadVM = mock(TraadVM.class);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        MeldingVM eldsteMeldingVM = opprettMeldingVMogSetJournalfortSaksId(SAKS_ID_3);
        when(traadVM.getEldsteMelding()).thenReturn(eldsteMeldingVM);

        Sak sak = new Sak();
        sak.saksId = optional(SAKS_ID_3);

        when(sakerService.hentListeAvSaker(anyString())).thenReturn(asList(sak));
    }

    @Test
    public void sjekkAtGetSakMetodenReturnererKorrektSakBasertPaJournalforingsId() {
        JournalfortSakVM journalfortSakVM = new JournalfortSakVM(innboksVM, sakerService);
        journalfortSakVM.oppdater();

        assertThat(journalfortSakVM.getSak().saksId.get(), is(SAKS_ID_3));
    }

    private MeldingVM opprettMeldingVMogSetJournalfortSaksId(String journalfortSaksId) {
        Melding melding = new Melding("", SPORSMAL_SKRIFTLIG, DateTime.now());
        melding.journalfortSaksId = journalfortSaksId;
        return new MeldingVM(melding, 1);
    }

}
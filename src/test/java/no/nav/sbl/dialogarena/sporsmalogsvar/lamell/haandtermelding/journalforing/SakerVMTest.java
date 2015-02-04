package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMockSaker;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SakerVMTest {

    private final InnboksVM innboksVM = mock(InnboksVM.class);
    private final TraadVM traadVM = mock(TraadVM.class);
    private SakerService sakerService = mock(SakerService.class);

    private SakerVM sakerVM;

    @Before
    public void setUp() {
        MeldingVM meldingVM = opprettMeldingVM("temagruppe");

        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        when(traadVM.getEldsteMelding()).thenReturn(meldingVM);
        when(sakerService.hentSaker(anyString())).thenReturn(createMockSaker());

        sakerVM = new SakerVM(innboksVM, sakerService);
    }

    @Test
    public void oppdaterSkalHenteSaker() {
        sakerVM.oppdater();

        verify(sakerService).hentSaker(anyString());
        assertThat(sakerVM.visFagsaker.getObject(), is(true));
        assertThat(sakerVM.visGenerelleSaker.getObject(), is(false));
    }

    @Test
    public void skalReturnereOmSakerFinnes() {
        sakerVM.oppdater();

        assertThat(sakerVM.sakerFinnes().getObject(), is(true));

        when(sakerService.hentSaker(anyString())).thenReturn(new Saker());
        sakerVM.oppdater();

        assertThat(sakerVM.sakerFinnes().getObject(), is(false));
    }

    @Test
    public void henterSakerSortertBasertPaaEldsteMeldingITraad() {
        sakerVM.oppdater();

        sakerVM.getGenerelleSakerGruppertPaaTema();
        sakerVM.getFagsakerGruppertPaaTema();

        verify(traadVM, times(2)).getEldsteMelding();
    }

    private MeldingVM opprettMeldingVM(String temagruppe) {
        Melding melding = new Melding("", Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now());
        melding.temagruppe = temagruppe;
        return new MeldingVM(melding, 1);
    }

}
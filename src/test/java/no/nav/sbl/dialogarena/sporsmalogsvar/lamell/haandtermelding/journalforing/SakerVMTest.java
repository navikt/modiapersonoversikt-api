package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.JournalforingPanelVelgSakTestConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Saker;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.*;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.config.InnboksTestConfig;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JournalforingPanelVelgSakTestConfig.class, InnboksTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SakerVMTest {


    private final InnboksVM innboksVM = mock(InnboksVM.class);

    private final TraadVM traadVM = mock(TraadVM.class);

    @Inject
    private SakerService sakerService;

    private MeldingVM meldingVM;
    private SakerVM sakerVM;

    @Before
    public void setUp() {
        meldingVM = opprettMeldingVM("temagruppe");

        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        when(traadVM.getEldsteMelding()).thenReturn(meldingVM);

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
package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaMedSaker;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.TEMA_1;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMockSaksliste;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createSak;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SakerVMTest {

    private ArrayList<Sak> mockSaksliste;
    private MeldingService meldingService;
    private InnboksVM innboksVM;
    private TraadVM traadVM;

    @Before
    public void setUp(){
        mockSaksliste = createMockSaksliste();
        meldingService = mock(MeldingService.class);
        innboksVM = mock(InnboksVM.class);
        traadVM = mock(TraadVM.class);
        when(meldingService.hentSakerForBruker(anyString())).thenReturn(mockSaksliste);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        MeldingVM meldingVM = opprettMockMeldingVM();
        when(traadVM.getEldsteMelding()).thenReturn(meldingVM);
    }

    private MeldingVM opprettMockMeldingVM() {
        Melding melding = new Melding("", Meldingstype.SPORSMAL, DateTime.now());
        melding.temagruppe = "temagruppe";
        return new MeldingVM(melding, 1);
    }

    @Test
    public void gittHverSakHarUniktTemaReturnerKorrektSakstemaliste() {
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.size(), is(3));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(1).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(2).saksliste.size(), is(1));
    }

    @Test
    public void gittToSakerMedLiktTemaReturnerKorrektSakstemaliste() {
        Sak sak4 = createSak("44444444", TEMA_1, "Fagsak 4", DateTime.now().minusDays(5));
        mockSaksliste.add(sak4);
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.size(), is(3));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(2));
        assertThat(sakstemaliste.get(1).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(2).saksliste.size(), is(1));
    }

}
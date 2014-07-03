package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.DATE_4;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.ID_4;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.TRAAD_LENGDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMeldingVMer;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TraadVMTest {

    private List<MeldingVM> meldinger;
    private TraadVM traadVM;

    @Before
    public void setUp(){
        meldinger = createMeldingVMer();
        traadVM = new TraadVM(meldinger);
    }

    @Test
    public void skalFinneRiktigTraadlengde() {
        int traadLengde = traadVM.getTraadLengde();

        assertThat(traadLengde, is(TRAAD_LENGDE));
    }

    @Test
    public void gittSortertMeldingsListeFinnNyesteMelding () {
        MeldingVM nyesteMeldingVM = meldinger.get(0);

        assertSame(traadVM.getNyesteMelding(), nyesteMeldingVM);
    }

    @Test
    public void gittSortertMeldingsListeFinnEldsteMelding () {
        MeldingVM eldsteMeldingVM = meldinger.get(2);

        assertSame(traadVM.getEldsteMelding(), eldsteMeldingVM);
    }

    @Test
    public void gittSortertListeFinnTidligereMeldinger () {
        MeldingVM melding3VM = meldinger.get(2);
        MeldingVM melding2VM = meldinger.get(1);

        List<MeldingVM> tidligereMeldinger = traadVM.getTidligereMeldinger();

        assertSame(tidligereMeldinger.get(0), melding2VM);
        assertSame(tidligereMeldinger.get(1), melding3VM);
    }

    @Test
    public void gittSortertListeFinnNyesteMeldingTemagruppe() {
        MeldingVM nyesteMeldingVM = meldinger.get(0);
        String nyesteMeldingTema = nyesteMeldingVM.melding.temagruppe;

        assertSame(traadVM.getNyesteMeldingsTemagruppe(), nyesteMeldingTema);
    }

    @Test
    public void gittMeldingstypeIkkeSpørsmålIEldsteMeldingReturnererBleInitiertAvBrukerFalse () {
        MeldingVM eldsteMeldingVM = new MeldingVM(new Melding(ID_4, Meldingstype.SAMTALEREFERAT, DATE_4), 4);
        traadVM.getMeldinger().add(eldsteMeldingVM);

        assertFalse(traadVM.bleInitiertAvBruker());
    }

    @Test
    public void gittMeldingstypeSpørsmålIEldsteMeldingReturnererBleInitiertAvBrukerTrue () {
        MeldingVM eldsteMeldingVM = new MeldingVM(new Melding(ID_4, Meldingstype.SPORSMAL, DATE_4), 4);
        traadVM.getMeldinger().add(eldsteMeldingVM);

        assertTrue(traadVM.bleInitiertAvBruker());
    }

}

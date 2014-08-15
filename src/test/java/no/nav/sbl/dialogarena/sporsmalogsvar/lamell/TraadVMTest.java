package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM.grupperMeldingerPaaJournalfortdato;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.DATE_1;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.DATE_2;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.DATE_3;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.DATE_4;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_1;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_2;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_3;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_4;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.TEMAGRUPPE_1;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.TRAAD_LENGDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMeldingMedJournalfortDato;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMeldingVMer;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TraadVMTest {

    private List<MeldingVM> meldinger;
    private TraadVM traadVM;

    @Before
    public void setUp() {
        meldinger = createMeldingVMer();
        traadVM = new TraadVM(meldinger);
    }

    @Test
    public void skalFinneRiktigTraadlengde() {
        int traadLengde = traadVM.getTraadLengde();

        assertThat(traadLengde, is(TRAAD_LENGDE));
    }

    @Test
    public void gittSortertMeldingsListeFinnNyesteMelding() {
        MeldingVM nyesteMeldingVM = meldinger.get(0);

        assertSame(traadVM.getNyesteMelding(), nyesteMeldingVM);
    }

    @Test
    public void gittSortertMeldingsListeFinnEldsteMelding() {
        MeldingVM eldsteMeldingVM = meldinger.get(2);

        assertSame(traadVM.getEldsteMelding(), eldsteMeldingVM);
    }

    @Test
    public void gittSortertListeFinnTidligereMeldinger() {
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
    public void gittMeldingstypeIkkeSpørsmålIEldsteMeldingReturnererBleInitiertAvBrukerFalse() {
        MeldingVM eldsteMeldingVM = new MeldingVM(new Melding(ID_4, Meldingstype.SAMTALEREFERAT, DATE_4), 4);
        traadVM.getMeldinger().add(eldsteMeldingVM);

        assertFalse(traadVM.bleInitiertAvBruker());
    }

    @Test
    public void gittMeldingstypeSpørsmålIEldsteMeldingReturnererBleInitiertAvBrukerTrue() {
        MeldingVM eldsteMeldingVM = new MeldingVM(new Melding(ID_4, Meldingstype.SPORSMAL, DATE_4), 4);
        traadVM.getMeldinger().add(eldsteMeldingVM);

        assertTrue(traadVM.bleInitiertAvBruker());
    }

    @Test
    public void settFlaggPaaDenNyesteMeldingenInneforEnJournalfortgruppe() {
        Melding melding1 = createMeldingMedJournalfortDato(ID_1, Meldingstype.SAMTALEREFERAT, DATE_1, TEMAGRUPPE_1, "Traad Id", DateTime.now());
        Melding melding2 = createMeldingMedJournalfortDato(ID_2, Meldingstype.SAMTALEREFERAT, DATE_2, TEMAGRUPPE_1, "Traad Id", DateTime.now());
        Melding melding3 = createMeldingMedJournalfortDato(ID_3, Meldingstype.SAMTALEREFERAT, DATE_3, TEMAGRUPPE_1, "Traad Id", DateTime.now().minusDays(2));
        Melding melding4 = createMeldingMedJournalfortDato(ID_4, Meldingstype.SAMTALEREFERAT, DATE_4, TEMAGRUPPE_1, "Traad Id", DateTime.now().minusDays(2));
        List<MeldingVM> meldinger = new ArrayList<>(Arrays.asList(
                new MeldingVM(melding1, 4),
                new MeldingVM(melding2, 4),
                new MeldingVM(melding3, 4),
                new MeldingVM(melding4, 4)));

        for (MeldingVM meldingVM : grupperMeldingerPaaJournalfortdato(meldinger)) {
            if (meldingVM.melding.id.equals(ID_1) || meldingVM.melding.id.equals(ID_3)) {
                assertTrue(meldingVM.nyesteMeldingISinJournalfortgruppe);
            } else {
                assertFalse(meldingVM.nyesteMeldingISinJournalfortgruppe);
            }
        }

    }

}

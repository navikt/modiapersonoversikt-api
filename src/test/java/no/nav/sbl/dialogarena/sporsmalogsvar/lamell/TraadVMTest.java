package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TraadVMTest {

    private final static String ID_1 = "id1";
    private final static String ID_2 = "id2";
    private final static String ID_3 = "id3";
    private final static String ID_4 = "id4";

    private final static DateTime DATE_1 = new DateTime(2014, 06, 21, 0, 0);
    private final static DateTime DATE_2 = new DateTime(2014, 05, 21, 0, 0);
    private final static DateTime DATE_3 = new DateTime(2014, 04, 21, 0, 0);
    private final static DateTime DATE_4 = new DateTime(2014, 03, 21, 0, 0);

    private final static String TEMA_1 = "Dagpenger";
    private final static String TEMA_2 = "Barnebidrag";
    private final static String TEMA_3 = "Familie og barn";

    private final static int TRAAD_LENGDE = 3;

    private List<MeldingVM> meldinger;
    private TraadVM traadVM;

    @Before
    public void setUp(){
        meldinger = createMeldingEksempler();
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
    public void gittSortertListeFinnNyesteMeldingTema () {
        MeldingVM nyesteMeldingVM = meldinger.get(0);
        String nyesteMeldingTema = nyesteMeldingVM.melding.tema;

        assertSame(traadVM.getNyesteMeldingsTema(), nyesteMeldingTema);
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

    private List<MeldingVM> createMeldingEksempler() {
        MeldingVM melding3VM = new MeldingVM(new Melding(ID_3, Meldingstype.SAMTALEREFERAT, DATE_3), TRAAD_LENGDE);
        melding3VM.melding.tema = TEMA_3;
        MeldingVM melding2VM = new MeldingVM(new Melding(ID_2, Meldingstype.SAMTALEREFERAT, DATE_2), TRAAD_LENGDE);
        melding2VM.melding.tema = TEMA_2;
        MeldingVM melding1VM = new MeldingVM(new Melding(ID_1, Meldingstype.SPORSMAL, DATE_1), TRAAD_LENGDE);
        melding1VM.melding.tema = TEMA_1;
        return new ArrayList<>(Arrays.asList(melding1VM, melding2VM, melding3VM));
    }



}

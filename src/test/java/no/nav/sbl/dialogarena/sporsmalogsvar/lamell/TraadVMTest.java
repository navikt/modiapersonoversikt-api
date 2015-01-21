package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SAMTALEREFERAT_OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM.grupperMeldingerPaaJournalfortdato;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

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
    public void gittMeldingstypeIkkeSporsmalIEldsteMeldingReturnererBleInitiertAvBrukerFalse() {
        MeldingVM eldsteMeldingVM = new MeldingVM(new Melding(ID_4, SAMTALEREFERAT_OPPMOTE, DATE_4), 4);
        traadVM.getMeldinger().add(eldsteMeldingVM);

        assertThat(traadVM.bleInitiertAvEtSporsmal(), is(false));
    }

    @Test
    public void gittMeldingstypeSporsmalSkriftligIEldsteMeldingReturnererBleInitiertAvEtSporsmal() {
        MeldingVM eldsteMeldingVMSporsmalSkriftlig = new MeldingVM(new Melding(ID_4, SPORSMAL_SKRIFTLIG, DATE_4), 4);
        traadVM.getMeldinger().add(eldsteMeldingVMSporsmalSkriftlig);

        assertThat(traadVM.bleInitiertAvEtSporsmal(), is(true));
    }

    @Test
    public void gittMeldingstypeSporsmalModiaUtgaaendeIEldsteMeldingReturnererBleInitiertAvEtSporsmal() {
        MeldingVM eldsteMeldingVMSporsmalModiaUtgaaende = new MeldingVM(new Melding(ID_4, SPORSMAL_MODIA_UTGAAENDE, DATE_4), 4);
        traadVM.getMeldinger().add(eldsteMeldingVMSporsmalModiaUtgaaende);

        assertThat(traadVM.bleInitiertAvEtSporsmal(), is(true));
    }

    @Test
    public void erBehandletDersomTraadenHarFlereMeldinger() {
        assertThat(traadVM.erBehandlet(), is(true));
    }

    @Test
    public void erBehandletDersomTraadensMeldingErFraNav() {
        MeldingVM meldingFraNav = new MeldingVM(new Melding(ID_4, SPORSMAL_MODIA_UTGAAENDE, DATE_4), 4);
        traadVM.getMeldinger().clear();
        traadVM.getMeldinger().add(meldingFraNav);

        assertThat(traadVM.erBehandlet(), is(true));
    }

    @Test
    public void erIkkeBehandletDersomTraadensMeldingErFraBruker() {
        MeldingVM meldingTilNav = new MeldingVM(new Melding(ID_4, SPORSMAL_SKRIFTLIG, DATE_4), 4);
        traadVM.getMeldinger().clear();
        traadVM.getMeldinger().add(meldingTilNav);

        assertThat(traadVM.erBehandlet(), is(false));
    }

    @Test
    public void traadenErIkkeKontorsperretDersomEldsteMeldingIkkeErKontorsperret() {
        MeldingVM meldingVM = new MeldingVM(new Melding(ID_4, SPORSMAL_SKRIFTLIG, DATE_4), 4);
        traadVM.getMeldinger().clear();
        traadVM.getMeldinger().add(meldingVM);

        assertThat(traadVM.erKontorsperret(), is(false));
    }

    @Test
    public void traadenErKontorsperretDersomEldsteMeldingErKontorsperret() {
        MeldingVM meldingVM = new MeldingVM(new Melding(ID_4, SPORSMAL_SKRIFTLIG, DATE_4), 4);
        meldingVM.melding.kontorsperretEnhet = "enhetId";
        traadVM.getMeldinger().clear();
        traadVM.getMeldinger().add(meldingVM);

        assertThat(traadVM.erKontorsperret(), is(true));
    }

    @Test
    public void erIkkeFeilsendtDersomIngenAvMeldingeneErFeilsendt() {
        assertThat(traadVM.erFeilsendt(), is(false));
    }

    @Test
    public void erFeilsendtDersomEnAvMeldingeneErFeilsendt() {
        MeldingVM meldingVM = new MeldingVM(new Melding(ID_4, SPORSMAL_SKRIFTLIG, DATE_4), 4);
        meldingVM.melding.markertSomFeilsendtAv = "feilSendtAv";
        traadVM.getMeldinger().clear();
        traadVM.getMeldinger().add(meldingVM);

        assertThat(traadVM.erFeilsendt(), is(true));
    }

    @Test
    public void settFlaggPaaDenNyesteMeldingenInneforEnJournalfortgruppe() {
        Melding melding1 = createMeldingMedJournalfortDato(ID_1, SAMTALEREFERAT_OPPMOTE, DATE_1, TEMAGRUPPE_1, "Traad Id", DateTime.now());
        Melding melding2 = createMeldingMedJournalfortDato(ID_2, SAMTALEREFERAT_OPPMOTE, DATE_2, TEMAGRUPPE_1, "Traad Id", DateTime.now());
        Melding melding3 = createMeldingMedJournalfortDato(ID_3, SAMTALEREFERAT_OPPMOTE, DATE_3, TEMAGRUPPE_1, "Traad Id", DateTime.now().minusDays(2));
        Melding melding4 = createMeldingMedJournalfortDato(ID_4, SAMTALEREFERAT_OPPMOTE, DATE_4, TEMAGRUPPE_1, "Traad Id", DateTime.now().minusDays(2));
        List<MeldingVM> meldinger = new ArrayList<>(Arrays.asList(
                new MeldingVM(melding1, 4),
                new MeldingVM(melding2, 4),
                new MeldingVM(melding3, 4),
                new MeldingVM(melding4, 4)));

        for (MeldingVM meldingVM : grupperMeldingerPaaJournalfortdato(meldinger)) {
            if (meldingVM.melding.id.equals(ID_1) || meldingVM.melding.id.equals(ID_3)) {
                assertThat(meldingVM.nyesteMeldingISinJournalfortgruppe, is(true));
            } else {
                assertThat(meldingVM.nyesteMeldingISinJournalfortgruppe, is(false));
            }
        }

    }

}

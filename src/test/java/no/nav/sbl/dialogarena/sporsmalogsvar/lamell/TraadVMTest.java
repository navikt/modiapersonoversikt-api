package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SAMTALEREFERAT_OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.DATE_4;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.ID_4;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.TEMAGRUPPE_1;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.TRAAD_LENGDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMeldingVMer;
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
    public void dersomEldsteMeldingITraadErEtSporsmalOgIkkeKassertSkalBrukerKunneBesvare() {
        MeldingVM eldsteMeldingVMSporsmalModiaUtgaaende = new MeldingVM(new Melding(ID_4, SPORSMAL_MODIA_UTGAAENDE, DATE_4).withTemagruppe(TEMAGRUPPE_1), 4);
        traadVM.getMeldinger().add(eldsteMeldingVMSporsmalModiaUtgaaende);

        assertThat(traadVM.traadKanBesvares(), is(true));
    }

    @Test
    public void dersomEldsteMeldingITraadIkkeErEtSporsmalOgIkkeKassertSkalBrukerIkkeKunneBesvare() {
        MeldingVM eldsteMeldingVMSporsmalModiaUtgaaende = new MeldingVM(new Melding(ID_4, SAMTALEREFERAT_OPPMOTE, DATE_4).withTemagruppe(TEMAGRUPPE_1), 4);
        traadVM.getMeldinger().add(eldsteMeldingVMSporsmalModiaUtgaaende);

        assertThat(traadVM.traadKanBesvares(), is(false));
    }

    @Test
    public void dersomEldsteMeldingITraadErEtSporsmalMenKassertSkalBrukerIkkeKunneBesvare() {
        Melding melding = new Melding(ID_4, SPORSMAL_MODIA_UTGAAENDE, DATE_4).withTemagruppe(null);
        melding.kassert = true;

        MeldingVM eldsteMeldingVMSporsmalModiaUtgaaende = new MeldingVM(melding, 4);
        traadVM.getMeldinger().add(eldsteMeldingVMSporsmalModiaUtgaaende);

        assertThat(traadVM.traadKanBesvares(), is(false));
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

}

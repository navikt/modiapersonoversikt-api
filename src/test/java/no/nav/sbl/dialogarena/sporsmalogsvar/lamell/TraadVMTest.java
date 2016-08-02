package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class TraadVMTest {

    private List<MeldingVM> meldinger;
    private TraadVM traadVM;

    private EnforcementPoint pepMock;
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerServiceMock;

    @Before
    public void setUp() {
        pepMock = mock(EnforcementPoint.class);
        saksbehandlerInnstillingerServiceMock = mock(SaksbehandlerInnstillingerService.class);

        meldinger = createMeldingVMer();
        traadVM = new TraadVM(meldinger, pepMock, saksbehandlerInnstillingerServiceMock);
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
        MeldingVM eldsteMeldingVMSporsmalModiaUtgaaende = new MeldingVM(new Melding(ID_4, SPORSMAL_MODIA_UTGAAENDE, DATE_4).withTemagruppe(TEMAGRUPPE_1.toString()), 4);
        traadVM.getMeldinger().add(eldsteMeldingVMSporsmalModiaUtgaaende);

        assertThat(traadVM.traadKanBesvares(), is(true));
    }

    @Test
    public void dersomEldsteMeldingITraadIkkeErEtSporsmalOgIkkeKassertSkalBrukerIkkeKunneBesvare() {
        MeldingVM eldsteMeldingVMSporsmalModiaUtgaaende = new MeldingVM(new Melding(ID_4, SAMTALEREFERAT_OPPMOTE, DATE_4).withTemagruppe(TEMAGRUPPE_1.toString()), 4);
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

    @Test
    public void traadOKSOSKanBesvaresDersomSaksbehandlerHarOKSOSTilgang() throws Exception {
        testOKSOSKanBesvares(true);
    }

    @Test
    public void traadOKSOSKanIkkeBesvaresDersomSaksbehandlerIkkeHarOKSOSTilgang() throws Exception {
        testOKSOSKanBesvares(false);
    }

    private void testOKSOSKanBesvares(final boolean kanBesvares) {
        when(saksbehandlerInnstillingerServiceMock.getSaksbehandlerValgtEnhet()).thenReturn("1100");
        when(pepMock.hasAccess((PolicyRequest) anyObject())).thenReturn(kanBesvares);

        final Melding melding = new Melding(ID_4, SPORSMAL_MODIA_UTGAAENDE, DATE_4);
        melding.gjeldendeTemagruppe = Temagruppe.OKSOS;
        MeldingVM eldsteMeldingVMSporsmalModiaUtgaaende = new MeldingVM(melding.withTemagruppe(Temagruppe.OKSOS.toString()), 4);
        traadVM.getMeldinger().add(eldsteMeldingVMSporsmalModiaUtgaaende);

        assertThat(traadVM.traadKanBesvares(), is(kanBesvares));
    }
}

package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.*;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.*;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InnboksVMTest {

    public final static DateTime DATE_1 = new DateTime().minusDays(1);
    public final static DateTime DATE_2 = new DateTime().minusDays(2);
    public final static DateTime DATE_3 = new DateTime().minusDays(3);
    public final static DateTime DATE_4 = new DateTime().minusDays(4);
    public static final String DELVIS_SVAR_FRITEKST = "Jeg svarer deg delvis";
    public static final String SVAR_FRITEKST = "Jeg fullfører svaret og deler med deg";
    public static final Saksbehandler SAKSBEHANDLER_1 = new Saksbehandler("Sissel", "Saksbehandler", "ident1");
    public static final Saksbehandler SAKSBEHANDLER_2 = new Saksbehandler("Sigurd", "Saksbehandler", "ident2");
    public static final Person BRUKER = new Person("Bjarne", "Bruker");

    @Inject
    HenvendelseBehandlingService henvendelseBehandlingService;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private EnforcementPoint pep;

    private InnboksVM innboksVM;

    @Before
    public void setUp() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(createMeldingerIToTraader());

        innboksVM = new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);
        innboksVM.oppdaterMeldinger();
    }

    @Test
    public void skalDeleOppIToTraader() {
        assertThat(innboksVM.getTraader().size(), is(2));
    }

    @Test
    public void skalKunneHenteUtTraadlengdeBasertPaaTraadId() {
        assertThat(innboksVM.getTraadLengde(ID_1), is(1));
        assertThat(innboksVM.getTraadLengde(ID_2), is(3));
    }

    @Test
    public void skalHaRiktigAntallMeldingIHverTraad() {
        Map<String, TraadVM> traader = innboksVM.getTraader();

        assertThat(traader.get(ID_1).getTraadLengde(), is(1));
        assertThat(traader.get(ID_2).getTraadLengde(), is(3));
    }

    @Test
    public void skalHenteNyesteMeldingINyesteTraad() {
        assertThat(innboksVM.getNyesteMeldingINyesteTraad().melding.id, is(ID_4));
    }

    @Test
    public void nyesteMeldingINyesteTraadSkalVaereDefaultValgt() {
        innboksVM.settForsteSomValgtHvisIkkeSatt();
        assertThat(innboksVM.getValgtTraad().getNyesteMelding(), is(innboksVM.getNyesteMeldingINyesteTraad()));
    }

    @Test
    public void skalSetteValgtTraadBasertPaaMeldingId() {
        innboksVM.setValgtMelding(ID_1);

        assertThat(innboksVM.getValgtTraad().getNyesteMelding().melding.traadId, is(ID_1));
    }

    @Test
    public void skalSetteValgtTraadBasertPaaMeldingVM() {
        MeldingVM nyesteMeldingITraad1 = innboksVM.getTraader().get(ID_1).getNyesteMelding();

        innboksVM.setValgtMelding(nyesteMeldingITraad1);

        assertThat(innboksVM.getValgtTraad().getNyesteMelding().melding.traadId, is(ID_1));
    }

    @Test
    public void skalFinneUtOmGittMeldingVMErIValgtMelding() {
        MeldingVM nyesteMeldingITraad1 = innboksVM.getTraader().get(ID_1).getNyesteMelding();
        MeldingVM nyesteMeldingITraad2 = innboksVM.getTraader().get(ID_2).getNyesteMelding();

        innboksVM.setValgtMelding(nyesteMeldingITraad1);

        assertTrue(innboksVM.erValgtMelding(nyesteMeldingITraad1).getObject());
        assertFalse(innboksVM.erValgtMelding(nyesteMeldingITraad2).getObject());

        innboksVM.setValgtMelding(nyesteMeldingITraad2);

        assertFalse(innboksVM.erValgtMelding(nyesteMeldingITraad1).getObject());
        assertTrue(innboksVM.erValgtMelding(nyesteMeldingITraad2).getObject());
    }

    @Test
    public void skalFungereUtenMeldinger() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(Collections.<Melding>emptyList());
        innboksVM = new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);

        assertThat(innboksVM.getTraader().size(), is(0));
        assertThat(innboksVM.getValgtTraad().getMeldinger().size(), is(0));
    }

    @Test
    public void henterNyesteMeldingITraad() {
        String fnr = "fnr";
        String traadId = "traadId";

        when(henvendelseBehandlingService.hentMeldinger(fnr)).thenReturn(asList(createMelding(traadId, SPORSMAL_SKRIFTLIG, DateTime.now(), Temagruppe.ARBD, traadId)));
        innboksVM = new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);
        innboksVM.oppdaterMeldinger();
        innboksVM.settForsteSomValgtHvisIkkeSatt();

        Optional<MeldingVM> nyesteMeldingITraad = innboksVM.getNyesteMeldingITraad(traadId);
        assertTrue(nyesteMeldingITraad.isPresent());
    }

    @Test
    public void henterNyesteMeldingITraadMedUkjentTraadId() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(Collections.<Melding>emptyList());
        innboksVM = new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);

        Optional<MeldingVM> nyesteMeldingITraad = innboksVM.getNyesteMeldingITraad("traadId");
        assertFalse(nyesteMeldingITraad.isPresent());
    }

    @Test
    public void slettGamleMeldinger() {
        assertThat(innboksVM.getTraader().size(), is(2));
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(createMeldingerIEnTraad());

        innboksVM.oppdaterMeldinger();

        assertThat(innboksVM.getTraader().size(), is(1));
    }

    @Test
    public void skalKombinereSkrevetAvForDelviseSvar() {
        when(henvendelseBehandlingService.hentMeldinger(anyString()))
                .thenReturn(mockMeldingskjedeMedDelviseSvar());

        innboksVM.oppdaterMeldinger();

        List<Fritekst> fritekster = innboksVM.getNyesteMeldingINyesteTraad().melding.getFriteksterMedEldsteForst();
        assertThat(fritekster.stream().map(fritekst -> fritekst.getSaksbehandler().get().navn)
                .collect(Collectors.toList()), Matchers.contains(SAKSBEHANDLER_1.navn, SAKSBEHANDLER_2.navn));
    }

    @Test
    public void skalKombinereFritekstForDelviseSvar() {
        when(henvendelseBehandlingService.hentMeldinger(anyString()))
                .thenReturn(mockMeldingskjedeMedDelviseSvar());

        innboksVM.oppdaterMeldinger();

        List<String> fritekster = innboksVM.getNyesteMeldingINyesteTraad().melding.getFriteksterMedEldsteForst().stream()
                .map(Fritekst::getFritekst).collect(Collectors.toList());
        assertThat(fritekster.size(), is(2));
        assertThat(fritekster.get(0), is(DELVIS_SVAR_FRITEKST));
        assertThat(fritekster.get(1), is(SVAR_FRITEKST));
    }

    private List<Melding> mockMeldingskjedeMedDelviseSvar() {
        return asList(
                createMelding(ID_1, Meldingstype.SPORSMAL_SKRIFTLIG, DATE_4, TEMAGRUPPE_1, ID_1)
                        .withFritekst(new Fritekst("Jeg stiller et spørsmål", BRUKER, DATE_4))
                        .withSkrevetAv(BRUKER),
                createMelding(ID_2, Meldingstype.DELVIS_SVAR_SKRIFTLIG, DATE_3, TEMAGRUPPE_1, ID_1)
                        .withFritekst(new Fritekst(DELVIS_SVAR_FRITEKST, SAKSBEHANDLER_1, DATE_3))
                        .withSkrevetAv(SAKSBEHANDLER_1),
                createMelding(ID_3, Meldingstype.SVAR_SKRIFTLIG, DATE_2, TEMAGRUPPE_2, ID_1)
                        .withFritekst(new Fritekst(SVAR_FRITEKST, SAKSBEHANDLER_2, DATE_2))
                        .withSkrevetAv(SAKSBEHANDLER_2));
    }

    public static List<Melding> createMeldingerIToTraader() {
        Melding melding1 = createMelding(ID_1, SPORSMAL_SKRIFTLIG, DATE_4, TEMAGRUPPE_1, ID_1);
        Melding melding2 = createMelding(ID_2, SPORSMAL_SKRIFTLIG, DATE_3, TEMAGRUPPE_2, ID_2);
        Melding melding3 = createMelding(ID_3, SAMTALEREFERAT_OPPMOTE, DATE_2, TEMAGRUPPE_2, ID_2);
        Melding melding4 = createMelding(ID_4, SVAR_SKRIFTLIG, DATE_1, TEMAGRUPPE_2, ID_2);
        return asList(melding1, melding2, melding3, melding4);
    }

    private static List<Melding> createMeldingerIEnTraad() {
        return asList(
                createMelding(ID_1, SPORSMAL_SKRIFTLIG, DATE_4, TEMAGRUPPE_1, ID_1)
        );
    }

}

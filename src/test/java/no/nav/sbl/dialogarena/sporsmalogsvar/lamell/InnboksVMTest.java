package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.config.InnboksTestConfig;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_1;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_2;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_3;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_4;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.TEMAGRUPPE_1;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.TEMAGRUPPE_2;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ServiceTestContext.class, InnboksTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InnboksVMTest {

    public final static DateTime DATE_1 = new DateTime().minusDays(1);
    public final static DateTime DATE_2 = new DateTime().minusDays(2);
    public final static DateTime DATE_3 = new DateTime().minusDays(3);
    public final static DateTime DATE_4 = new DateTime().minusDays(4);

    @Inject
    HenvendelseBehandlingService henvendelseBehandlingService;

    private InnboksVM innboksVM;

    @Before
    public void setUp() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(createMeldingerIToTraader());

        innboksVM = new InnboksVM("fnr");
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
        innboksVM = new InnboksVM("fnr");

        assertThat(innboksVM.getTraader().size(), is(0));
        assertThat(innboksVM.getValgtTraad().getMeldinger().size(), is(0));
    }

    @Test
    public void henterNyesteMeldingITraad() {
        String fnr = "fnr";
        String traadId = "traadId";

        when(henvendelseBehandlingService.hentMeldinger(fnr)).thenReturn(asList(createMelding(traadId, SPORSMAL, DateTime.now(), "temagruppe", traadId)));
        innboksVM = new InnboksVM(fnr);

        Optional<MeldingVM> nyesteMeldingITraad = innboksVM.getNyesteMeldingITraad(traadId);
        assertTrue(nyesteMeldingITraad.isSome());
    }

    @Test
    public void henterNyesteMeldingITraadMedUkjentTraadId() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(Collections.<Melding>emptyList());
        innboksVM = new InnboksVM("fnr");

        Optional<MeldingVM> nyesteMeldingITraad = innboksVM.getNyesteMeldingITraad("traadId");
        assertFalse(nyesteMeldingITraad.isSome());
    }

    @Test
    public void slettGamleMeldinger() {
        assertThat(innboksVM.getTraader().size(), is(2));
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(createMeldingerIEnTraad());

        innboksVM.oppdaterMeldinger();

        assertThat(innboksVM.getTraader().size(), is(1));
    }

    public static List<Melding> createMeldingerIToTraader() {
        Melding melding1 = createMelding(ID_1, SPORSMAL, DATE_4, TEMAGRUPPE_1, ID_1);
        Melding melding2 = createMelding(ID_2, SPORSMAL, DATE_3, TEMAGRUPPE_2, ID_2);
        Melding melding3 = createMelding(ID_3, SAMTALEREFERAT, DATE_2, TEMAGRUPPE_2, ID_2);
        Melding melding4 = createMelding(ID_4, SVAR, DATE_1, TEMAGRUPPE_2, ID_2);
        return asList(melding1, melding2, melding3, melding4);
    }

    private static List<Melding> createMeldingerIEnTraad() {
        return asList(
                createMelding(ID_1, SPORSMAL, DATE_4, TEMAGRUPPE_1, ID_1)
        );
    }

}

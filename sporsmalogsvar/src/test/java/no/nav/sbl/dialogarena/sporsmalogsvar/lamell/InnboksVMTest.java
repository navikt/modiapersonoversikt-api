package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {MockServiceTestContext.class, ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InnboksVMTest {

    public final static DateTime DATE_1 = new DateTime().minusDays(1);
    public final static DateTime DATE_2 = new DateTime().minusDays(2);
    public final static DateTime DATE_3 = new DateTime().minusDays(3);
    public final static DateTime DATE_4 = new DateTime().minusDays(4);

    @Inject
    HenvendelsePortType henvendelsePortType;

    @Inject
    @Named("henvendelseBehandlingServiceProd")
    HenvendelseBehandlingService henvendelseBehandlingService;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private EnforcementPoint pep;

    private InnboksVM innboksVM;

    @Before
    public void setUp() {
        when(henvendelsePortType.hentHenvendelseListe(any())).thenReturn(createMeldingerIToTraader());

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
        when(henvendelsePortType.hentHenvendelseListe(any())).thenReturn(new WSHentHenvendelseListeResponse());

        innboksVM = new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);

        assertThat(innboksVM.getTraader().size(), is(0));
        assertThat(innboksVM.getValgtTraad().getMeldinger().size(), is(0));
    }

    @Test
    public void henterNyesteMeldingITraad() {
        String traadId = "traadId";
        when(henvendelsePortType.hentHenvendelseListe(any())).thenReturn(new WSHentHenvendelseListeResponse()
                .withAny(Arrays.asList(createMelding(traadId, SPORSMAL_SKRIFTLIG, DateTime.now(), Temagruppe.ARBD, traadId))));

        innboksVM = new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);
        innboksVM.oppdaterMeldinger();
        innboksVM.settForsteSomValgtHvisIkkeSatt();

        Optional<MeldingVM> nyesteMeldingITraad = innboksVM.getNyesteMeldingITraad(traadId);
        assertTrue(nyesteMeldingITraad.isPresent());
    }

    @Test
    public void henterNyesteMeldingITraadMedUkjentTraadId() {
        when(henvendelsePortType.hentHenvendelseListe(any())).thenReturn(new WSHentHenvendelseListeResponse());

        innboksVM = new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);

        Optional<MeldingVM> nyesteMeldingITraad = innboksVM.getNyesteMeldingITraad("traadId");
        assertFalse(nyesteMeldingITraad.isPresent());
    }

    @Test
    public void slettGamleMeldinger() {
        assertThat(innboksVM.getTraader().size(), is(2));
        when(henvendelsePortType.hentHenvendelseListe(any())).thenReturn(createMeldingerIEnTraad());

        innboksVM.oppdaterMeldinger();

        assertThat(innboksVM.getTraader().size(), is(1));
    }

    private static WSHentHenvendelseListeResponse createMeldingerIToTraader() {
        XMLHenvendelse melding1 = createMelding(ID_1, SPORSMAL_SKRIFTLIG, DATE_4, TEMAGRUPPE_1, ID_1);
        XMLHenvendelse melding2 = createMelding(ID_2, SPORSMAL_SKRIFTLIG, DATE_3, TEMAGRUPPE_2, ID_2);
        XMLHenvendelse melding3 = createMelding(ID_3, REFERAT_OPPMOTE, DATE_2, TEMAGRUPPE_2, ID_2);
        XMLHenvendelse melding4 = createMelding(ID_4, SVAR_SKRIFTLIG, DATE_1, TEMAGRUPPE_2, ID_2);
        return new WSHentHenvendelseListeResponse().withAny(Arrays.asList(melding1, melding2, melding3, melding4));
    }

    private static WSHentHenvendelseListeResponse createMeldingerIEnTraad() {
        return new WSHentHenvendelseListeResponse().withAny(Arrays.asList(createMelding(ID_1, SPORSMAL_SKRIFTLIG, DATE_4, TEMAGRUPPE_1, ID_1)));
    }

}

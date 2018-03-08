package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {MockServiceTestContext.class, ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AlleMeldingerPanelTest extends WicketPageTest {

    @Inject
    @Named("henvendelseBehandlingServiceProd")
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Inject
    HenvendelsePortType henvendelsePortType;

    @Inject
    private EnforcementPoint pep;

    @Before
    public void setUp() {
        when(henvendelsePortType.hentHenvendelseListe(any())).thenReturn(new WSHentHenvendelseListeResponse()
                .withAny(Arrays.asList(createMelding("id1", XMLHenvendelseType.SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, "id1"),
                        createMelding("id2", XMLHenvendelseType.SPORSMAL_SKRIFTLIG, now().minusDays(2), Temagruppe.ARBD, "id2"))));
    }

    @Test
    public void starterAlleMeldingerPanelUtenFeil() {
        wicket.goToPageWith(new AlleMeldingerPanel("id", new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService)));
    }

    @Test
    public void setterValgtMeldingDersomManTrykkerPaaDen() {
        InnboksVM innboksVM = new InnboksVM("fnr", henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);
        innboksVM.oppdaterMeldinger();
        innboksVM.setValgtMelding("id1");

        wicket.goToPageWith(new AlleMeldingerPanel("id", innboksVM))
                .executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class));

        assertThat(innboksVM.getValgtTraad().getNyesteMelding().melding.id, is("id2"));
    }
}

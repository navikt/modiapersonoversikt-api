package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

@DirtiesContext(classMode = BEFORE_CLASS)
@ContextConfiguration(classes = {MockServiceTestContext.class})
@ExtendWith(SpringExtension.class)
public class InnboksTest extends WicketPageTest {

    private static final String ELDSTE_MELDING_ID_TRAAD1 = "eldsteIdTraad1";
    private static final String NYESTE_MELDING_ID_TRAAD1 = "nyesteIdTraad1";
    private static final String ENESTE_MELDING_ID_TRAAD2 = "enesteIdTraad2";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Inject
    private EnforcementPoint pep;

    @BeforeEach
    public void setUp() {
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(
                createMelding(ELDSTE_MELDING_ID_TRAAD1, SPORSMAL_SKRIFTLIG, now().minusDays(1), Temagruppe.ARBD, ELDSTE_MELDING_ID_TRAAD1),
                createMelding(NYESTE_MELDING_ID_TRAAD1, SVAR_SKRIFTLIG, now(), Temagruppe.ARBD, ELDSTE_MELDING_ID_TRAAD1),
                createMelding(ENESTE_MELDING_ID_TRAAD2, SPORSMAL_SKRIFTLIG, now().minusDays(2), Temagruppe.ARBD, ENESTE_MELDING_ID_TRAAD2))));
    }

    @Test
    public void inneholderRiktigeKomponenter() {
        wicket.goToPageWith(new Innboks("innboks", innboksVM()))
                .should().containComponent(ofType(AlleMeldingerPanel.class))
                .should().containComponent(ofType(TraaddetaljerPanel.class));
    }

    @Test
    public void velgerTraadMedNyesteMeldingSomDefault() {
        Innboks testInnboks = new Innboks("innboks", innboksVM());
        wicket.goToPageWith(testInnboks);

        assertThat(getValgtTraad(testInnboks).getNyesteMelding().melding.id, is(NYESTE_MELDING_ID_TRAAD1));
    }

    @Test
    public void setterValgtMeldingVedEvent() {
        Innboks testInnboks = new Innboks("innboks", innboksVM());
        wicket.goToPageWith(testInnboks);

        wicket.sendEvent(new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(FEED_ITEM_CLICKED, new FeedItemPayload("", ENESTE_MELDING_ID_TRAAD2, ""));
            }
        }).should().inAjaxResponse().haveComponents(ofType(Innboks.class));

        assertThat(getValgtTraad(testInnboks).getNyesteMelding().melding.id, is(ENESTE_MELDING_ID_TRAAD2));
    }

    @Test
    public void oppdatererMeldingeneVedKlikkPaaSok() {
        String fnr = "fnr";
        Innboks testInnboks = new Innboks("innboks", innboksVM(fnr));
        wicket.goToPageWith(testInnboks)
                .click().link(withId("meldingerSokToggle"));

        verify(henvendelseBehandlingService, atLeast(2)).hentMeldinger(eq(fnr), anyString());
    }

    private InnboksVM innboksVM() {
        return innboksVM("1234578910");
    }

    private InnboksVM innboksVM(String fnr) {
        return new InnboksVM(fnr, henvendelseBehandlingService, pep, saksbehandlerInnstillingerService);
    }

    private TraadVM getValgtTraad(Innboks testInnboks) {
        return ((InnboksVM) testInnboks.getDefaultModelObject()).getValgtTraad();
    }
}
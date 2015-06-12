package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.InnboksProps;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InnboksTest extends WicketPageTest {

    private static final String ELDSTE_MELDING_ID_TRAAD1 = "eldsteIdTraad1";
    private static final String NYESTE_MELDING_ID_TRAAD1 = "nyesteIdTraad1";
    private static final String ENESTE_MELDING_ID_TRAAD2 = "enesteIdTraad2";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Before
    public void setUp() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding(ELDSTE_MELDING_ID_TRAAD1, SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", ELDSTE_MELDING_ID_TRAAD1),
                createMelding(NYESTE_MELDING_ID_TRAAD1, SVAR_SKRIFTLIG, now(), "TEMA", ELDSTE_MELDING_ID_TRAAD1),
                createMelding(ENESTE_MELDING_ID_TRAAD2, SPORSMAL_SKRIFTLIG, now().minusDays(2), "TEMA", ENESTE_MELDING_ID_TRAAD2)));
    }

    @Test
    public void inneholderRiktigeKomponenter() {
        wicket.goToPageWith(new Innboks("innboks", "fnr", tomInnboksProps()))
                .should().containComponent(ofType(AlleMeldingerPanel.class))
                .should().containComponent(ofType(TraaddetaljerPanel.class));
    }

    @Test
    public void velgerTraadMedNyesteMeldingSomDefault() {
        Innboks testInnboks = new Innboks("innboks", "fnr", tomInnboksProps());
        wicket.goToPageWith(testInnboks);

        assertThat(getValgtTraad(testInnboks).getNyesteMelding().melding.id, is(NYESTE_MELDING_ID_TRAAD1));
    }

    @Test
    public void setterValgtMeldingVedEvent() {
        Innboks testInnboks = new Innboks("innboks", "fnr", tomInnboksProps());
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
        Innboks testInnboks = new Innboks("innboks", fnr, tomInnboksProps());
        wicket.goToPageWith(testInnboks)
                .click().link(withId("meldingerSokToggle"));

        verify(henvendelseBehandlingService, atLeast(2)).hentMeldinger(fnr);
    }

    @Test
    public void setterTraadSomErReferertISessionTilValgtTraadIInnboks() {
        Innboks testInnboks = new Innboks("innboks", "fnr", new InnboksProps(optional(ENESTE_MELDING_ID_TRAAD2), Optional.<String>none(), Optional.<String>none(), Optional.<Boolean>none()));
        wicket.goToPageWith(testInnboks);

        assertThat(getValgtTraad(testInnboks).getNyesteMelding().melding.id, is(ENESTE_MELDING_ID_TRAAD2));
    }

    private TraadVM getValgtTraad(Innboks testInnboks) {
        return ((InnboksVM) testInnboks.getDefaultModelObject()).getValgtTraad();
    }

    private InnboksProps tomInnboksProps() {
        return new InnboksProps(Optional.<String>none(), Optional.<String>none(), Optional.<String>none(), Optional.<Boolean>none());
    }

}
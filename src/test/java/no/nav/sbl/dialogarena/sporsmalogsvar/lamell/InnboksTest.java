package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.config.InnboksTestConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.TRAAD_ID_PARAMETER_NAME;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServiceTestContext.class, InnboksTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InnboksTest {

    private static final String ELDSTE_MELDING_ID_TRAAD1 = "eldsteIdTraad1";
    private static final String NYESTE_MELDING_ID_TRAAD1 = "nyesteIdTraad1";
    private static final String ENESTE_MELDING_ID_TRAAD2 = "enesteIdTraad2";

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;
    @Inject
    private FluentWicketTester wicket;

    @Before
    public void setUp() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding(ELDSTE_MELDING_ID_TRAAD1, Meldingstype.SPORSMAL, now().minusDays(1), "TEMA", ELDSTE_MELDING_ID_TRAAD1),
                createMelding(NYESTE_MELDING_ID_TRAAD1, Meldingstype.SVAR, now(), "TEMA", ELDSTE_MELDING_ID_TRAAD1),
                createMelding(ENESTE_MELDING_ID_TRAAD2, Meldingstype.SPORSMAL, now().minusDays(2), "TEMA", ENESTE_MELDING_ID_TRAAD2)));
    }

    @Test
    public void skalInneholdeRiktigeKomponenter() {
        wicket.goToPageWith(new TestInnboks("innboks", "fnr"))
                .should().containComponent(ofType(AlleMeldingerPanel.class))
                .should().containComponent(ofType(TraaddetaljerPanel.class));
    }

    @Test
    public void skalVelgeTraadMedNyesteMeldingSomDefault() {
        TestInnboks innboks = new TestInnboks("innboks", "fnr");
        wicket.goToPageWith(innboks);

        assertThat(((InnboksVM) innboks.getDefaultModelObject()).getValgtTraad().getNyesteMelding().melding.id, is(NYESTE_MELDING_ID_TRAAD1));
    }

    @Test
    public void skalSetteValgtMeldingVedEvent() {
        TestInnboks innboks = new TestInnboks("innboks", "fnr");
        wicket.goToPageWith(innboks);

        wicket.sendEvent(new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(FEED_ITEM_CLICKED, new FeedItemPayload("", ENESTE_MELDING_ID_TRAAD2, ""));
            }
        }).should().inAjaxResponse().haveComponents(ofType(Innboks.class));

        assertThat(((InnboksVM) innboks.getDefaultModelObject()).getValgtTraad().getNyesteMelding().melding.id, is(ENESTE_MELDING_ID_TRAAD2));
    }

    @Test
    public void skalSetteTraadSomErReferertITraadIdPageParameterSomValgtTraad() {
        wicket.tester.getRequest().setParameter(TRAAD_ID_PARAMETER_NAME, ENESTE_MELDING_ID_TRAAD2);

        TestInnboks innboks = new TestInnboks("innboks", "fnr");
        wicket.goToPageWith(innboks);

        assertThat(((InnboksVM) innboks.getDefaultModelObject()).getValgtTraad().getNyesteMelding().melding.id, is(ENESTE_MELDING_ID_TRAAD2));
    }

}
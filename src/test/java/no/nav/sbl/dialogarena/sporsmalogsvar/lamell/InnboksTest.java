package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.MeldingServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.config.InnboksTestConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createMelding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MeldingServiceTestContext.class, InnboksTestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InnboksTest {

    private static final String NYESTE_MELDING_ID = "nyesteId";
    private static final String ANDRE_TRAAD_HENVENDELSE_ID = "traad2Id";

    @Inject
    private MeldingService meldingService;

    @Inject
    private FluentWicketTester wicket;

    @Test
    public void skalInneholdeRiktigeKomponenter() {
        when(meldingService.hentMeldinger(anyString())).thenReturn(asList(createMelding("id1", Meldingstype.SPORSMAL, now().minusDays(1), "TEMA", "1")));
        wicket.goToPageWith(new TestInnboks("innboks", "fnr"))
                .should().containComponent(ofType(AlleMeldingerPanel.class))
                .should().containComponent(ofType(TraaddetaljerPanel.class));
    }

    @Test
    public void skalSetteValgtMeldingVedEvent() {
        when(meldingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("id1", Meldingstype.SPORSMAL, now().minusDays(1), "TEMA", "1"),
                createMelding(NYESTE_MELDING_ID, Meldingstype.SVAR, now(), "TEMA", "1"),
                createMelding(ANDRE_TRAAD_HENVENDELSE_ID, Meldingstype.SPORSMAL, now().minusDays(2), "TEMA", "2")));

        TestInnboks innboks = new TestInnboks("innboks", "fnr");
        wicket.goToPageWith(innboks);

        assertThat(((InnboksVM) innboks.getDefaultModelObject()).getValgtTraad().getNyesteMelding().melding.id, is(NYESTE_MELDING_ID));

        wicket.sendEvent(new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(FEED_ITEM_CLICKED, new FeedItemPayload("", ANDRE_TRAAD_HENVENDELSE_ID, ""));
            }
        }).should().inAjaxResponse().haveComponents(ofType(Innboks.class));

        assertThat(((InnboksVM) innboks.getDefaultModelObject()).getValgtTraad().getNyesteMelding().melding.id, is(ANDRE_TRAAD_HENVENDELSE_ID));
    }
}
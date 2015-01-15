package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.config.InnboksTestConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.constants.URLParametere.HENVENDELSEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
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
                createMelding(ELDSTE_MELDING_ID_TRAAD1, SPORSMAL_SKRIFTLIG, now().minusDays(1), "TEMA", ELDSTE_MELDING_ID_TRAAD1),
                createMelding(NYESTE_MELDING_ID_TRAAD1, SVAR_SKRIFTLIG, now(), "TEMA", ELDSTE_MELDING_ID_TRAAD1),
                createMelding(ENESTE_MELDING_ID_TRAAD2, SPORSMAL_SKRIFTLIG, now().minusDays(2), "TEMA", ENESTE_MELDING_ID_TRAAD2)));
        when(henvendelseBehandlingService.hentInnboks(anyString())).thenAnswer(new Answer<InnboksVM>(){
            @Override
            public InnboksVM answer(InvocationOnMock invocation) throws Throwable {
                return new InnboksVM(((String) invocation.getArguments()[0]), henvendelseBehandlingService);
            }
        });
    }

    @Test
    public void inneholderRiktigeKomponenter() {
        wicket.goToPageWith(new TestInnboks("innboks", "fnr"))
                .should().containComponent(ofType(AlleMeldingerPanel.class))
                .should().containComponent(ofType(TraaddetaljerPanel.class));
    }

    @Test
    public void velgerTraadMedNyesteMeldingSomDefault() {
        TestInnboks testInnboks = new TestInnboks("innboks", "fnr");
        wicket.goToPageWith(testInnboks);

        assertThat(getValgtTraad(testInnboks).getNyesteMelding().melding.id, is(NYESTE_MELDING_ID_TRAAD1));
    }

    @Test
    public void setterValgtMeldingVedEvent() {
        TestInnboks testInnboks = new TestInnboks("innboks", "fnr");
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
    public void setterTraadSomErReferertISessionTilValgtTraadIInnboks() {
        wicket.tester.getSession().setAttribute(HENVENDELSEID, ENESTE_MELDING_ID_TRAAD2);

        TestInnboks testInnboks = new TestInnboks("innboks", "fnr");
        wicket.goToPageWith(testInnboks);

        assertThat(getValgtTraad(testInnboks).getNyesteMelding().melding.id, is(ENESTE_MELDING_ID_TRAAD2));
    }

    private TraadVM getValgtTraad(TestInnboks testInnboks) {
        return ((InnboksVM) testInnboks.getDefaultModelObject()).getValgtTraad();
    }

}
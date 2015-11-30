package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller;


import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.JacksonMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.LamellServicesAndLoaders;
import org.apache.wicket.event.IEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer.*;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.FORELDREPENGER;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.SYKEPENGER;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {LamellServicesAndLoaders.class, JacksonMockContext.class})
public class LamellContainerTest extends WicketPageTest {

    private LamellContainer lamellContainer;

    @Before
    public void setup() {
        lamellContainer = new LamellContainer("lameller", "22222222222", wicket.tester.getSession());
    }

    @Test(expected = ApplicationException.class)
    public void handleFeedItemEventshouldThrowWhenUnknownEventhappens() {
        lamellContainer.handleFeedItemEvent(mock(IEvent.class), new FeedItemPayload("widgetid", "itemId", "type"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoForeldrePengerLamellWhenForeeldrePengerEventHappens() {
        lamellContainer.handleFeedItemEvent(createEvent(), new FeedItemPayload("widgetid", "itemId", FORELDREPENGER));
        assertThat(getSelectedLamell(), equalTo(LAMELL_FORELDREPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoSykePengerLamellWhenSykePengerEventHappens() {
        lamellContainer.handleFeedItemEvent(createEvent(), new FeedItemPayload("widgetid", "itemId", SYKEPENGER));
        assertThat(getSelectedLamell(), equalTo(LAMELL_SYKEPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldReuseFactory() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SYKEPENGER);
        lamellContainer.handleFeedItemEvent(event, payload);
        String selectedLamell = getSelectedLamell();

        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
        lamellContainer.handleFeedItemEvent(event, payload);
        selectedLamell = getSelectedLamell();
        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoDifferentLammelWhenDifferentItemIsClicked() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SYKEPENGER);
        lamellContainer.handleFeedItemEvent(event, payload);
        String selectedLamell = getSelectedLamell();

        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
        payload = new FeedItemPayload("widgetid", "itemId2", SYKEPENGER);
        lamellContainer.handleFeedItemEvent(event, payload);
        selectedLamell = getSelectedLamell();
        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId2"));
    }

    @Test(expected = ApplicationException.class)
    public void handleWidgetItemEventshouldThrowWhenUnknownEventhappens() {
        lamellContainer.handleWidgetItemEvent("ukjent");
    }

    @Test
    public void hasUnchangedChangesReturnFalseWhenNoChanges() {
        assertThat(lamellContainer.hasUnsavedChanges(), equalTo(false));
    }

    @Test
    public void handleWidgetItemEventshouldGotoKontrakterLamellWhenKontrakterEventHappens() {
        lamellContainer.handleWidgetItemEvent(LAMELL_KONTRAKTER);
        assertThat(getSelectedLamell(), equalTo(LAMELL_KONTRAKTER));
    }

    private IEvent<String> createEvent() {
        @SuppressWarnings("unchecked")
        IEvent<String> event = mock(IEvent.class);
        when(event.getPayload()).thenReturn("payload");
        return event;
    }

    private String getSelectedLamell() {
        return lamellContainer.getSelectedLamell();
    }

}

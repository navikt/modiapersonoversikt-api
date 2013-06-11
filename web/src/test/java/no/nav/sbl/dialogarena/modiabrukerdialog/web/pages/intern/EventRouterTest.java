package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;


import no.nav.dialogarena.modiabrukerdialog.example.component.ExamplePanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.LamellPanel;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl;
import org.apache.wicket.event.IEvent;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class EventRouterTest {

    @Test(expected = ApplicationException.class)
    public void handleFeedItemEventshouldThrowWhenUnknownEventhappens() {
        IEvent event = mock(IEvent.class);
        LamellPanel panel = mock(LamellPanel.class);
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", "type");
        EventRouter.handleFeedItemEvent(panel, event, payload);
    }

    @Test
    public void handleFeedItemEventsShouldGotoForeldrePengerLamellWhenForeeldrePengerEventHappens(){
        IEvent<String> event = mock(IEvent.class);
        Mockito.when(event.getPayload()).thenReturn("payload");
        LamellPanel panel = mock(LamellPanel.class);
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SykepengerWidgetServiceImpl.FORELDREPENGER);
        EventRouter.handleFeedItemEvent(panel, event, payload);
        Mockito.verify(panel).goToLamell(Intern.LAMELL_FORELDREPENGER);
        Mockito.verify(panel).sendToLamell(Intern.LAMELL_FORELDREPENGER, "payload");
    }

    @Test
    public void handleFeedItemEventsShouldGotoSykePengerLamellWhenSykePengerEventHappens(){
        IEvent<String> event = mock(IEvent.class);
        Mockito.when(event.getPayload()).thenReturn("payload");
        LamellPanel panel = mock(LamellPanel.class);
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SykepengerWidgetServiceImpl.SYKEPENGER);
        EventRouter.handleFeedItemEvent(panel, event, payload);
        Mockito.verify(panel).goToLamell(Intern.LAMELL_SYKEPENGER);
        Mockito.verify(panel).sendToLamell(Intern.LAMELL_SYKEPENGER, "payload");
    }

    @Test
    public void handleFeedItemEventsShouldGotoExampleLamellWhenExampleEventHappens(){
        IEvent<String> event = mock(IEvent.class);
        Mockito.when(event.getPayload()).thenReturn("payload");
        LamellPanel panel = mock(LamellPanel.class);
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", ExamplePanel.EXAMPLE_TYPE);
        EventRouter.handleFeedItemEvent(panel, event, payload);
        Mockito.verify(panel).goToLamell(Intern.LAMELL_EXAMPLE);
        Mockito.verify(panel).sendToLamell(Intern.LAMELL_EXAMPLE, "payload");
    }

    @Test(expected = ApplicationException.class)
    public void handleWidgetItemEventshouldThrowWhenUnknownEventhappens() {
        LamellPanel panel = mock(LamellPanel.class);
        EventRouter.handleWidgetItemEvent(panel,"ukjent");
    }

    @Test
    public void handleWidgetItemEventshouldGotoKontrakterLamellWhenKontrakterEventHappens() {
        LamellPanel panel = mock(LamellPanel.class);
        EventRouter.handleWidgetItemEvent(panel,Intern.LAMELL_KONTRAKTER);
        Mockito.verify(panel).goToLamell(Intern.LAMELL_KONTRAKTER);
    }



}

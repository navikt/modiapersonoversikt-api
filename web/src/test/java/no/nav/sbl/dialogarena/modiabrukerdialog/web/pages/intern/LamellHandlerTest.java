package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;


import no.nav.modig.common.MDCOperations;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ApplicationContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl;
import org.apache.wicket.event.IEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LamellHandlerTest extends TestSecurityBaseClass {

    private LamellHandler lamellHandler;

    @Before
    public void setup() {
        lamellHandler = new LamellHandler();
        lamellHandler.createLamellPanel("lameller", "22222222222");
        final String callID = MDCOperations.generateCallId();
        MDCOperations.putToMDC(MDCOperations.MDC_CALL_ID, callID);
    }

    @Test(expected = ApplicationException.class)
    public void handleFeedItemEventshouldThrowWhenUnknownEventhappens() {
        IEvent<?> event = mock(IEvent.class);
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", "type");
        lamellHandler.handleFeedItemEvent(event, payload);
    }

    @Test
    public void handleFeedItemEventsShouldGotoForeldrePengerLamellWhenForeeldrePengerEventHappens() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SykepengerWidgetServiceImpl.FORELDREPENGER);
        lamellHandler.handleFeedItemEvent(event, payload);
        final String selectedLamell = getSelectedLamell();
        Assert.assertThat(selectedLamell, equalTo(LamellHandler.LAMELL_FORELDREPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoSykePengerLamellWhenSykePengerEventHappens() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SykepengerWidgetServiceImpl.SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload);
        final String selectedLamell = getSelectedLamell();
        Assert.assertThat(selectedLamell, equalTo(LamellHandler.LAMELL_SYKEPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldReuseFactory() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SykepengerWidgetServiceImpl.SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload);
        String selectedLamell = getSelectedLamell();
        Assert.assertThat(selectedLamell, equalTo(LamellHandler.LAMELL_SYKEPENGER + "itemId"));
        lamellHandler.handleFeedItemEvent(event, payload);
        selectedLamell = getSelectedLamell();
        Assert.assertThat(selectedLamell, equalTo(LamellHandler.LAMELL_SYKEPENGER + "itemId"));

    }

    @Test
    public void handleFeedItemEventsShouldGotoDiffenrentLammelWhenDifferentItemIsClicked() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SykepengerWidgetServiceImpl.SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload);
        String selectedLamell = getSelectedLamell();
        Assert.assertThat(selectedLamell, equalTo(LamellHandler.LAMELL_SYKEPENGER + "itemId"));
        payload = new FeedItemPayload("widgetid", "itemId2", SykepengerWidgetServiceImpl.SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload);
        selectedLamell = getSelectedLamell();
        Assert.assertThat(selectedLamell, equalTo(LamellHandler.LAMELL_SYKEPENGER + "itemId2"));

    }

    @Test(expected = ApplicationException.class)
    public void handleWidgetItemEventshouldThrowWhenUnknownEventhappens() {
        lamellHandler.handleWidgetItemEvent("ukjent");
    }

    @Test
    public void handleWidgetItemEventshouldGotoKontrakterLamellWhenKontrakterEventHappens() {
        TokenLamellPanel panel = createPanel();
        lamellHandler.handleWidgetItemEvent(LamellHandler.LAMELL_KONTRAKTER);
        Mockito.verify(panel).goToLamell(LamellHandler.LAMELL_KONTRAKTER);
    }

    private TokenLamellPanel createPanel() {
        TokenLamellPanel panel = mock(TokenLamellPanel.class);
        Whitebox.setInternalState(lamellHandler, "lamellPanel", panel);
        return panel;
    }

    private IEvent<String> createEvent() {
        @SuppressWarnings("unchecked")
        IEvent<String> event = mock(IEvent.class);
        Mockito.when(event.getPayload()).thenReturn("payload");
        return event;
    }

    private String getSelectedLamell() {
        TokenLamellPanel panel = (TokenLamellPanel) Whitebox.getInternalState(lamellHandler, "lamellPanel");
        return panel.getSelectedLamell();
    }

}

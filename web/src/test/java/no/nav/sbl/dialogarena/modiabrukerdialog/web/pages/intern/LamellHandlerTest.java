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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modig.common.MDCOperations.generateCallId;
import static no.nav.modig.common.MDCOperations.putToMDC;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.LamellHandler.LAMELL_KONTRAKTER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.LamellHandler.LAMELL_SYKEPENGER;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.SYKEPENGER;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@ContextConfiguration(classes = {ApplicationContext.class, WicketTesterConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LamellHandlerTest extends TestSecurityBaseClass {

    private LamellHandler lamellHandler;
    private String fnr = "22222222222";

    @Before
    public void setup() {
        lamellHandler = new LamellHandler();
        lamellHandler.createLamellPanel("lameller", "22222222222");
        putToMDC(MDCOperations.MDC_CALL_ID, generateCallId());
    }

    @Test(expected = ApplicationException.class)
    public void handleFeedItemEventshouldThrowWhenUnknownEventhappens() {
        lamellHandler.handleFeedItemEvent(mock(IEvent.class), new FeedItemPayload("widgetid", "itemId", "type"), fnr);
    }

    @Test
    public void handleFeedItemEventsShouldGotoForeldrePengerLamellWhenForeeldrePengerEventHappens() {
        lamellHandler.handleFeedItemEvent(createEvent(), new FeedItemPayload("widgetid", "itemId", SykepengerWidgetServiceImpl.FORELDREPENGER), fnr);
        assertThat(getSelectedLamell(), equalTo(LamellHandler.LAMELL_FORELDREPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoSykePengerLamellWhenSykePengerEventHappens() {
        lamellHandler.handleFeedItemEvent(createEvent(), new FeedItemPayload("widgetid", "itemId", SYKEPENGER), fnr);
        assertThat(getSelectedLamell(), equalTo(LAMELL_SYKEPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldReuseFactory() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload, fnr);
        String selectedLamell = getSelectedLamell();

        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
        lamellHandler.handleFeedItemEvent(event, payload, fnr);
        selectedLamell = getSelectedLamell();
        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoDiffenrentLammelWhenDifferentItemIsClicked() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload, fnr);
        String selectedLamell = getSelectedLamell();

        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
        payload = new FeedItemPayload("widgetid", "itemId2", SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload, fnr);
        selectedLamell = getSelectedLamell();
        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId2"));
    }

    @Test(expected = ApplicationException.class)
    public void handleWidgetItemEventshouldThrowWhenUnknownEventhappens() {
        lamellHandler.handleWidgetItemEvent("ukjent", fnr);
    }

    @Test
    public void handleWidgetItemEventshouldGotoKontrakterLamellWhenKontrakterEventHappens() {
        TokenLamellPanel panel = createPanel();
        lamellHandler.handleWidgetItemEvent(LAMELL_KONTRAKTER, fnr);
        verify(panel).goToLamell(LAMELL_KONTRAKTER);
    }

    private TokenLamellPanel createPanel() {
        TokenLamellPanel panel = mock(TokenLamellPanel.class);
        Map<String, TokenLamellPanel> lamellPanelMap = new HashMap<>();
        lamellPanelMap.put(fnr, panel);
        setInternalState(lamellHandler, "lamellPanelMap", lamellPanelMap);
        return panel;
    }

    private IEvent<String> createEvent() {
        @SuppressWarnings("unchecked")
        IEvent<String> event = mock(IEvent.class);
        when(event.getPayload()).thenReturn("payload");
        return event;
    }

    private String getSelectedLamell() {
        Map<String, TokenLamellPanel> lamellPanelMap = (Map<String, TokenLamellPanel>) getInternalState(lamellHandler, "lamellPanelMap");
        TokenLamellPanel panel = lamellPanelMap.get(fnr);
        return panel.getSelectedLamell();
    }

}

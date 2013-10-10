package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;


import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ForeldrepengerPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.KjerneinfoPepMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.SykmeldingsperiodePanelMockContext;
import org.apache.wicket.event.IEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.LamellHandler.LAMELL_FORELDREPENGER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.LamellHandler.LAMELL_KONTRAKTER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.LamellHandler.LAMELL_SYKEPENGER;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.FORELDREPENGER;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.SYKEPENGER;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@ContextConfiguration(classes = {
        KjerneinfoPepMockContext.class,
        WicketTesterConfig.class,
        SykmeldingsperiodePanelMockContext.class,
        ForeldrepengerPanelMockContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LamellHandlerTest {

    private LamellHandler lamellHandler;

    @Before
    public void setup() {
        lamellHandler = new LamellHandler();
        lamellHandler.createLamellPanel("lameller", "22222222222");
    }

    @Test(expected = ApplicationException.class)
    public void handleFeedItemEventshouldThrowWhenUnknownEventhappens() {
        lamellHandler.handleFeedItemEvent(mock(IEvent.class), new FeedItemPayload("widgetid", "itemId", "type"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoForeldrePengerLamellWhenForeeldrePengerEventHappens() {
        lamellHandler.handleFeedItemEvent(createEvent(), new FeedItemPayload("widgetid", "itemId", FORELDREPENGER));
        assertThat(getSelectedLamell(), equalTo(LAMELL_FORELDREPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoSykePengerLamellWhenSykePengerEventHappens() {
        lamellHandler.handleFeedItemEvent(createEvent(), new FeedItemPayload("widgetid", "itemId", SYKEPENGER));
        assertThat(getSelectedLamell(), equalTo(LAMELL_SYKEPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldReuseFactory() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload);
        String selectedLamell = getSelectedLamell();

        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
        lamellHandler.handleFeedItemEvent(event, payload);
        selectedLamell = getSelectedLamell();
        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoDiffenrentLammelWhenDifferentItemIsClicked() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload);
        String selectedLamell = getSelectedLamell();

        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
        payload = new FeedItemPayload("widgetid", "itemId2", SYKEPENGER);
        lamellHandler.handleFeedItemEvent(event, payload);
        selectedLamell = getSelectedLamell();
        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId2"));
    }

    @Test(expected = ApplicationException.class)
    public void handleWidgetItemEventshouldThrowWhenUnknownEventhappens() {
        lamellHandler.handleWidgetItemEvent("ukjent");
    }

    @Test
    public void hasUnchangedChangesReturnFalseWhenNoChanges() {
        assertThat(lamellHandler.hasUnsavedChanges(), equalTo(false));
    }

    @Test
    public void handleWidgetItemEventshouldGotoKontrakterLamellWhenKontrakterEventHappens() {
        TokenLamellPanel panel = createPanel();
        lamellHandler.handleWidgetItemEvent(LAMELL_KONTRAKTER);
        verify(panel).goToLamell(LAMELL_KONTRAKTER);
    }

    private TokenLamellPanel createPanel() {
        TokenLamellPanel panel = mock(TokenLamellPanel.class);
        setInternalState(lamellHandler, "lamellPanel", panel);
        return panel;
    }

    private IEvent<String> createEvent() {
        @SuppressWarnings("unchecked")
        IEvent<String> event = mock(IEvent.class);
        when(event.getPayload()).thenReturn("payload");
        return event;
    }

    private String getSelectedLamell() {
        TokenLamellPanel panel = (TokenLamellPanel) getInternalState(lamellHandler, "lamellPanel");
        return panel.getSelectedLamell();
    }

}

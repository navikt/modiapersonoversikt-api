package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller;


import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.JacksonMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.LamellServicesAndLoaders;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mock.PleiepengerMockFactory;
import org.apache.wicket.event.IEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer.*;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LamellServicesAndLoaders.class, JacksonMockContext.class})
public class LamellContainerTest extends WicketPageTest {

    private LamellContainer lamellContainer;

    private GrunnInfo getMockGrunnInfo() {
        GrunnInfo.Bruker bruker = new GrunnInfo.Bruker("10108000398", "test", "testesen", "navKontorX", "1234", "", "kjonn");
        GrunnInfo.Saksbehandler saksbehandler = new GrunnInfo.Saksbehandler("enhetX", "fornavn", "etternavn");
        return new GrunnInfo(bruker, saksbehandler);
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        lamellContainer = new LamellContainer("lameller", wicket.tester.getSession(), getMockGrunnInfo(), false, false, false, false);
    }

    @Test
    public void handleFeedItemEventshouldThrowWhenUnknownEventhappens() {
        assertThrows(ApplicationException.class, () ->
                lamellContainer.handleFeedItemEvent(mock(IEvent.class), new FeedItemPayload("widgetid", "itemId", "type"))
        );
    }

    @Test
    public void handleFeedItemEventsShouldGotoForeldrePengerLamellWhenForeeldrePengerEventHappens() {
        lamellContainer.handleFeedItemEvent(createEvent(), new FeedItemPayload("widgetid", "itemId", FORELDREPENGER_TYPE));
        assertThat(getSelectedLamell(), equalTo(LAMELL_FORELDREPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoSykePengerLamellWhenSykePengerEventHappens() {
        lamellContainer.handleFeedItemEvent(createEvent(), new FeedItemPayload("widgetid", "itemId", SYKEPENGER_TYPE));
        assertThat(getSelectedLamell(), equalTo(LAMELL_SYKEPENGER + "itemId"));
    }

    @Test
    public void handleFeedItemEventsShouldGotoPleiePengerLamellWhenPleiePengerEventHappens() {
        String itemId = PleiepengerMockFactory.BARN_FNR;
        lamellContainer.handleFeedItemEvent(createEvent(), new FeedItemPayload("widgetid", itemId , PLEIEPENGER_TYPE));
        assertThat(getSelectedLamell(), equalTo(LAMELL_PLEIEPENGER + itemId));
    }

    @Test
    public void handleFeedItemEventsShouldReuseFactory() {
        IEvent<String> event = createEvent();
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SYKEPENGER_TYPE);
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
        FeedItemPayload payload = new FeedItemPayload("widgetid", "itemId", SYKEPENGER_TYPE);
        lamellContainer.handleFeedItemEvent(event, payload);
        String selectedLamell = getSelectedLamell();

        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId"));
        payload = new FeedItemPayload("widgetid", "itemId2", SYKEPENGER_TYPE);
        lamellContainer.handleFeedItemEvent(event, payload);
        selectedLamell = getSelectedLamell();
        assertThat(selectedLamell, equalTo(LAMELL_SYKEPENGER + "itemId2"));
    }

    @Test
    public void handleWidgetItemEventshouldThrowWhenUnknownEventhappens() {
        assertThrows(ApplicationException.class, () ->
                lamellContainer.handleWidgetItemEvent("ukjent")
        );
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

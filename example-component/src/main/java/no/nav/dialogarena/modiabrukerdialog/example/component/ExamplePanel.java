package no.nav.dialogarena.modiabrukerdialog.example.component;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;

import java.io.IOException;

import javax.inject.Inject;

import no.nav.dialogarena.modiabrukerdialog.example.Pingable;
import no.nav.dialogarena.modiabrukerdialog.example.service.ExampleService;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.Model;


public class ExamplePanel extends GenericPanel<String> implements Pingable {

    public static final String EXAMPLE_TYPE = "example";

    @Inject
    private ExampleService exampleService;

    private Component content, error;

    public ExamplePanel(String id) {
        super(id, Model.of(""));

        setOutputMarkupId(true);

        content = new Label("content", getModel());
        error = new Label("error") {
            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                throw new RuntimeException();
            }
        }.setVisible(false);


        add(content, error);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void handleEvent(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        if (feedItemPayload.getType().equals(EXAMPLE_TYPE)) {
            updateModel(feedItemPayload.getItemId());
            target.add(this);
        }
    }

	@Override
    public long ping() {
        long startTime = System.currentTimeMillis();
        if (exampleService.isAvailable()) {
            return System.currentTimeMillis() - startTime;
        }
        throw new SystemException("examplePanel unavailable because exampleService cannot be reached", new IOException());
    }

    private void updateModel(String itemId) {
        // setModelObject(exampleService.getContent());
        if ("noerror".equals(itemId)) {
            setModelObject("Fungerer OK");
        }
        if ("renderingerror".equals(itemId)) {
            error.setVisible(true);
        }
        if ("systemerror".equals(itemId)) {
            throw new RuntimeException();
        }
    }

}

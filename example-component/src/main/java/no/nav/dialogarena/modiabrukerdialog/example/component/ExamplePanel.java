package no.nav.dialogarena.modiabrukerdialog.example.component;

import no.nav.dialogarena.modiabrukerdialog.example.Pingable;
import no.nav.dialogarena.modiabrukerdialog.example.service.ExampleService;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;

import javax.inject.Inject;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;

public class ExamplePanel extends Lerret implements Pingable {

    public static final String EXAMPLE_EVENT = "example";

    @Inject
    private ExampleService exampleService;

    private Label content = new Label("content", new ContentModel());

    public ExamplePanel(String id) {
        super(id);

        add(content);
    }

    @RunOnEvents(EXAMPLE_EVENT)
    public void updateModel(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        if (feedItemPayload.getType().equals(EXAMPLE_EVENT)) {
            target.add(content);
        }
    }

    @Override
    public long ping() throws SystemException {
        long startTime = currentTimeMillis();
        if (exampleService.isAvailable()) {
            return currentTimeMillis() - startTime;
        } throw new SystemException("examplePanel unavailable because exampleService cannot be reached", new IOException());
    }

    private class ContentModel extends AbstractReadOnlyModel<String> {

        @Override
        public String getObject() {
            return exampleService.getContent();
        }

    }

}

package no.nav.dialogarena.modiabrukerdialog.example.component;

import no.nav.dialogarena.modiabrukerdialog.example.service.ExampleService;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;

import javax.inject.Inject;

public class ExamplePanel extends Lerret {

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

    private class ContentModel extends AbstractReadOnlyModel<String> {

        @Override
        public String getObject() {
            return exampleService.getContent();
        }

    }

}

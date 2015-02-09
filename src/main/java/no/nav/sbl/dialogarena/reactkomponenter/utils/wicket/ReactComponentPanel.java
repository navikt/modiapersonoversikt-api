package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.HashMap;
import java.util.Map;

public class ReactComponentPanel extends Panel {

    private static final String JS_REF_REACT = "window.ModiaJS.React";
    private static final String JS_REF_COMPONENTS = "window.ModiaJS.Components";

    private String componentName;
    private Map<String, Object> props;
    private ObjectMapper objectMapper = new ObjectMapper();

    private WebMarkupContainer reactContainer;

    public ReactComponentPanel(String id, String componentName) {
        this(id, componentName, new HashMap<String, Object>());
    }

    public ReactComponentPanel(String id, String componentName, Map<String, Object> props) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        this.componentName = componentName;
        this.props = props;

        reactContainer = new WebMarkupContainer("reactContainer");
        add(reactContainer);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(onDomReadyHeaderItem());
    }

    private OnDomReadyHeaderItem onDomReadyHeaderItem() {
        try {
            String js = String.format(
                    "%s.render(%s.createElement(%s, %s), document.getElementById('%s'));",
                    JS_REF_REACT,
                    JS_REF_REACT,
                    JS_REF_COMPONENTS + "." + componentName,
                    objectMapper.writeValueAsString(props),
                    reactContainer.getMarkupId());

            return OnDomReadyHeaderItem.forScript(js);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Feil ved serialisering av props: " + props, e);
        }
    }
}

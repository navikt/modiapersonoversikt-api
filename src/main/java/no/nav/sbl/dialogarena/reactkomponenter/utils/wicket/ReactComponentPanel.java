package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.HashMap;
import java.util.Map;

public class ReactComponentPanel extends Panel {

    private static final String JS_REF_REACT = "window.ModiaJS.React";
    private static final String JS_REF_COMPONENTS = "window.ModiaJS.Components";
    private static final String JS_REF_INITIALIZED_COMPONENTS = "window.ModiaJS.InitializedComponents";

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

    public void updateState(AjaxRequestTarget target, Map<String, Object> props) {
        this.props = props;
        callFunction(target, "setState", props);
    }

    public void callFunction(AjaxRequestTarget target, String functionName, Object... arguments) {
        try {
            target.appendJavaScript(String.format(
                    "%s.%s.%s(%s);",
                    JS_REF_INITIALIZED_COMPONENTS,
                    reactContainer.getMarkupId(),
                    functionName,
                    prepareArguments(arguments)
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Feil ved serialisering av props: " + props, e);
        }
    }

    private String prepareArguments(Object... arguments) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arguments.length; i++) {
            sb.append(objectMapper.writeValueAsString(arguments[i]));
            if (i < arguments.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private OnDomReadyHeaderItem onDomReadyHeaderItem() {
        try {
            String js = String.format(
                    "%s.%s = %s.render(%s.createElement(%s, %s), document.getElementById('%s'));",
                    JS_REF_INITIALIZED_COMPONENTS,
                    reactContainer.getMarkupId(),
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

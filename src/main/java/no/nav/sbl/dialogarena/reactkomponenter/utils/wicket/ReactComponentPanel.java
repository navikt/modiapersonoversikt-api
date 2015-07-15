package no.nav.sbl.dialogarena.reactkomponenter.utils.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import java.util.*;

import static java.lang.String.format;
import static no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.SerializeUtils.deserialize;
import static no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.SerializeUtils.serialize;
import static org.apache.wicket.markup.head.OnDomReadyHeaderItem.forScript;

public class ReactComponentPanel extends MarkupContainer {

    protected static final String JS_REF_REACT = "window.ModiaJS.React";
    protected static final String JS_REF_COMPONENTS = "window.ModiaJS.Components";
    protected static final String JS_REF_INITIALIZED_COMPONENTS = "window.ModiaJS.InitializedComponents";

    private final transient Map<String, List<CallbackWrapper>> callbacks = new HashMap<>();

    public ReactComponentPanel(String id, String componentName) {
        this(id, componentName, new HashMap<String, Object>());
    }

    public ReactComponentPanel(String id, final String componentName, final Map<String, Object> props) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                WebRequest request = ((WebRequest) RequestCycle.get().getRequest());
                Set<String> paramnames = request.getQueryParameters().getParameterNames();

                for (String paramname : paramnames) {
                    String data = request.getQueryParameters().getParameterValue(paramname).toString();
                    List<CallbackWrapper> callbackWrappers = callbacks.get(paramname);
                    if (callbackWrappers != null) {
                        for (CallbackWrapper callbackWrapper : callbackWrappers) {
                            if (data == null || data.isEmpty() || callbackWrapper.type == Void.class) {
                                callbackWrapper.callback.onCallback(target, null);
                            } else {
                                Object object = deserialize(data, callbackWrapper.type);
                                callbackWrapper.callback.onCallback(target, object);
                            }
                        }
                    }
                }
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                Map<String, Object> augmentedprops = augmentedProps(props, getCallbackUrl());
                response.render(forScript(initializeScript(componentName, augmentedprops)));
                super.renderHead(component, response);
            }
        });
    }

    public <T> void addCallback(String action, Class<T> type, ReactComponentCallback<T> callback) {
        List<CallbackWrapper> callbackList = callbacks.get("action");
        if (callbackList == null) {
            callbackList = new ArrayList<>();
        }

        CallbackWrapper<T> wrapper = new CallbackWrapper<>(type, callback);
        callbackList.add(wrapper);

        callbacks.put(action, callbackList);
    }

    private Map<String, Object> augmentedProps(Map<String, Object> props, CharSequence callbackUrl) {
        props.put("wicketurl", callbackUrl);
        props.put("wicketcomponent", getMarkupId());
        return props;
    }

    public void updateState(Map<String, Object> state) {
        call("setState", state);
    }

    public void call(String method, Object... args) {
        transmit(callScript(method, prepareArguments(args)));
    }

    private void transmit(final String js) {
        AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        target.appendJavaScript(js);
    }

    String initializeScript(String componentName, Map<String, Object> props) {
        return createScript(componentName, props) + renderScript();
    }

    String renderScript() {
        return format("%s.%s = %s.render(%s.%s, document.getElementById('%s'));", JS_REF_INITIALIZED_COMPONENTS, this.getMarkupId(), JS_REF_REACT, JS_REF_INITIALIZED_COMPONENTS, this.getMarkupId(), this.getMarkupId());
    }

    String createScript(String componentName, Map<String, Object> props) {
        String json = SerializeUtils.serialize(props);
        return format("%s.%s = %s.createElement(%s.%s, %s);", JS_REF_INITIALIZED_COMPONENTS, this.getMarkupId(), JS_REF_REACT, JS_REF_COMPONENTS, componentName, json);
    }

    String callScript(String methodName, String arguments) {
        return format("%s.%s.%s(%s);", JS_REF_INITIALIZED_COMPONENTS, this.getMarkupId(), methodName, arguments);
    }


    String prepareArguments(Object... arguments) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arguments.length; i++) {
            sb.append(serialize(arguments[i]));
            if (i < arguments.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private static class CallbackWrapper<T> {
        public final Class<T> type;
        public final ReactComponentCallback<T> callback;

        public CallbackWrapper(Class<T> type, ReactComponentCallback<T> callback) {
            this.type = type;
            this.callback = callback;
        }
    }
}

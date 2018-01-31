package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.function.Consumer;

public class HodeCallbackWrapper<T> {
    public final Class<T> type;
    public final HodeCallback<T> callback;

    public HodeCallbackWrapper(Class<T> type, HodeCallback<T> callback) {
        this(type, callback, null);
    }

    public HodeCallbackWrapper(Class<T> type, HodeCallback<T> callback, Consumer<AjaxRequestTarget> after) {
        this.type = type;
        this.callback = withAfter(callback, after);
    }

    private static <T> HodeCallback<T> withAfter(HodeCallback<T> callback, Consumer<AjaxRequestTarget> after) {
        if (after == null) {
            return callback;
        }
        return (target, component, data) -> {
            callback.onCallback(target, component, data);
            after.accept(target);
        };
    }
}

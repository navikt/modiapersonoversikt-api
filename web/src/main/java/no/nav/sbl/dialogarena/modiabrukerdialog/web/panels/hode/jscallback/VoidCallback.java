package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.Hode;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.function.BiConsumer;

public class VoidCallback extends HodeCallbackWrapper<Void> {
    public VoidCallback(BiConsumer<AjaxRequestTarget, Component> impl) {
        super(Void.class, (AjaxRequestTarget target, Hode component, Void aVoid) -> {
            impl.accept(target, component);
        });
    }
}

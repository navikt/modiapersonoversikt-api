package no.nav.sbl.dialogarena.sak.util;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;


public abstract class ResourceStreamAjaxBehaviour extends AbstractAjaxBehavior {

    public void openInSameWindow(AjaxRequestTarget target) {
        String url = getCallbackUrl().toString();
        // the timeout is needed to let Wicket release the channel
        target.appendJavaScript("setTimeout(\"window.location='" + url + "'\", 100);");
    }


    public void onRequest() {
        //TODO Her må man hente ut navnet til PDF'en. Må avvente tjenesten for å se hvordan det kan løses.
        ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(getResourceStream(), "vedlegg.pdf");
        handler.setContentDisposition(ContentDisposition.INLINE);
        getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
    }
    protected abstract IResourceStream getResourceStream();
}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.timeout;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Åpner en dialogboks ved sesjonstimeout med aktuelle brukervalg.
 */
public class TimeoutBehaviour extends AbstractDefaultAjaxBehavior {
    private static final Logger logger = LoggerFactory.getLogger(TimeoutBehaviour.class);

    @Override
    protected void respond(AjaxRequestTarget target) {
        logger.debug("Session was refreshed");
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(OnLoadHeaderItem.forScript("setSessionTimeoutBox();"));
        super.renderHead(component, response);
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personinfo.timeout;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.slf4j.Logger;

import static org.apache.wicket.markup.head.OnLoadHeaderItem.forScript;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Åpner en dialogboks ved sesjonstimeout med aktuelle brukervalg.
 */
public class TimeoutBehaviour extends AbstractDefaultAjaxBehavior {

    private static final Logger logger = getLogger(TimeoutBehaviour.class);

    @Override
    protected void respond(AjaxRequestTarget target) {
        logger.debug("Session was refreshed");
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(forScript("setSessionTimeoutBox();"));
        super.renderHead(component, response);
    }

}

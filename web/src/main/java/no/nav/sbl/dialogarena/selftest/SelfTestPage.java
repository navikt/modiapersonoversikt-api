package no.nav.sbl.dialogarena.selftest;

import org.apache.wicket.markup.html.WebPage;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class SelfTestPage extends WebPage {

    private static final Logger logger = getLogger(SelfTestPage.class);

    public SelfTestPage() {
        logger.info("entered SelfTestPage!");
    }
}
